package service

import entity.LogMessage
import entity.PlayerAction

/** service class responsible for handling player actions during the game. */
class PlayerActionService(private val rootService: RootService) : AbstractRefreshingService() {

    /** flips the cards at the given [cardIndices] of the current player face-up. */
    fun flipCard(cardIndices: List<Int>) {
        val game = checkNotNull(rootService.currentGame)
        val player = game.players[game.currentPlayer]


        require(cardIndices.isNotEmpty()){"Must provide atleast one card index"}
        require(cardIndices.size <= 2){"Can flip at most two cards only"}
        require(cardIndices.all{it in player.deck.indices}){"card index out of bounds"}
        require (cardIndices.all{!player.deck[it].isFlipped}){"cannot flip an already face up card"}

        val revealDone = game.players.all{it.playedFirstRound}

        if(revealDone){
            require(cardIndices.size ==1){"Can only flip one card during normal game"}
        }else{
            require(cardIndices.size == 2){"Must flip exactly 2 cards during reveal round"}
            require(!player.playedFirstRound){"Player already completed reveal round"}
        }

        // flip each card at the given indices
        for (i in cardIndices) {
            player.deck[i].isFlipped = true
        }

        if(!revealDone){
            player.playedFirstRound = true
        }

        val flippedCards = cardIndices.map { i -> player.deck[i] }

        // log the action
        rootService.gameService.updateLog(
            LogMessage(game.logMessages.size,
                player, if (cardIndices.size == 2) PlayerAction.FLIP_TWO else PlayerAction.FLIP_ONE, flippedCards.toMutableList())
        )

        onAllRefreshables { refreshAfterFlipCards(flippedCards, cardIndices) }

    }

    /** draws the top card from the discard pile and swaps it with the card at [cardIndex]. */
    fun drawFromDiscardPile(cardIndex: Int) {
        val game = checkNotNull(rootService.currentGame)
        val player = game.players[game.currentPlayer]


        require(game.players.all{it.playedFirstRound}){"reveal round not yet complete"}
        require(game.discardPile.isNotEmpty()){"discard pile is empty"}
        require(cardIndex in player.deck.indices){"card index out of bounds"}


        val newCard = game.discardPile.pop()

        val oldCard = player.deck[cardIndex]

        // set old card face-up before putting it on the discard pile
        oldCard.isFlipped = true
        game.discardPile.push(oldCard)

        // put new card into player's deck and flip it face-up
        player.deck[cardIndex] = newCard
        newCard.isFlipped = true

        // log the action
        rootService.gameService.updateLog(
            LogMessage(game.logMessages.size,
                player, PlayerAction.DRAW_FROM_DISCARD_PILE, mutableListOf(newCard))
        )

        onAllRefreshables { refreshAfterDrawFromDiscardPile(newCard, cardIndex) }

    }

    /**
     * draws the top card from the draw pile.
     * if [isAccepted], swaps it with the card at [cardIndex]. if not, discards it and flips [cardIndex].
     */
    fun drawFromDrawPile(isAccepted: Boolean, cardIndex: Int) {
        val game = checkNotNull(rootService.currentGame)
        val player = game.players[game.currentPlayer]

        require(game.players.all{it.playedFirstRound}){"reveal round not yet complete"}
        require(game.drawPile.isNotEmpty()){"draw pile is empty"}
        require(cardIndex in player.deck.indices){"card index out of bounds"}

        if(!isAccepted){
            require(!player.deck[cardIndex].isFlipped){"must reveal a face-down card when discarding drawn card"}
        }


        val newCard = game.drawPile.pop()

        if (isAccepted) {
            val oldCard = player.deck[cardIndex]

            // set old card face-up before putting it on the discard pile
            oldCard.isFlipped = true
            game.discardPile.push(oldCard)

            // put new card into player's deck and flip it face-up
            player.deck[cardIndex] = newCard
            newCard.isFlipped = true

        } else {
            // player rejected the card, just discard it
            game.discardPile.push(newCard)

            // reveal the card at cardIndex in the player's deck
            player.deck[cardIndex].isFlipped = true
        }

        // log the action
        rootService.gameService.updateLog(
            LogMessage(game.logMessages.size,
                player, PlayerAction.DRAW_FROM_DRAW_PILE, mutableListOf(newCard))
        )

        onAllRefreshables { refreshAfterDrawFromDrawPile(isAccepted, newCard, cardIndex) }

    }
}