package service

import entity.Suit
import entity.Value
import entity.Card
import entity.Player
import entity.Game
import entity.LogMessage
import tools.aqua.bgw.util.Stack

/** service class responsible for managing the game state and flow. */
class GameService(private val rootService: RootService) : AbstractRefreshingService() {

    /** creates and returns a shuffled draw stack. in test mode uses only TWO, KING and QUEEN repeated to 52 cards. */
    fun createDrawStack(isTestGame: Boolean): Stack<Card> {
        val drawStack = Stack<Card>()

        if (isTestGame) {
            val testValues = listOf(Value.TWO, Value.KING, Value.QUEEN)
            val baseCards = mutableListOf<Card>()

            // build the base 12 cards (3 values x 4 suits)
            for (value in testValues) {
                for (suit in Suit.entries) {
                    baseCards.add(Card(value, suit))
                }
            }

            val repeated = mutableListOf<Card>()
            // keep adding copies until we have at least 52
            while (repeated.size < 52) {
                // create new Card objects each time so they dont share the same reference
                repeated.addAll(baseCards.map { Card(it.value, it.suit) })
            }

            // take exactly 52 and push onto the stack
            repeated.take(52).forEach { drawStack.push(it) }

        } else {
            // normal mode: all 52 unique cards
            for (value in Value.entries) {
                for (suit in Suit.entries) {
                    drawStack.push(Card(value, suit))
                }
            }
        }

        drawStack.shuffle()
        return drawStack
    }

    /**
     * creates and returns a shuffled draw stack.
     * in test mode uses only TWO, KING and QUEEN repeated to 52 cards.
     *
     */
    /** creates and returns a discard stack by popping the top card from [drawStack] and flipping it. */
    fun createDiscardStack(drawStack: Stack<Card>): Stack<Card> {
        val discardStack = Stack<Card>()
        val card = drawStack.pop()
        card.isFlipped = true
        discardStack.push(card)
        return discardStack
    }

    /** adds the [logMessage] to the current game's log. */
    fun updateLog(logMessage: LogMessage) {
        val game = checkNotNull(rootService.currentGame)
        game.logMessages.add(logMessage)
    }

    /** removes completed rows from the current player's deck and puts them on the discard pile. */
    /** Removes completed rows from the current player's deck and puts them on the discard pile. */
    fun clearRow() {
        val game = checkNotNull(rootService.currentGame)
        val player = game.players[game.currentPlayer]

        // Check bottom row first (indices 3,4,5)
        if (player.deck.size >= 6 &&
            player.deck[3].isFlipped && player.deck[4].isFlipped && player.deck[5].isFlipped &&
            player.deck[3].value == player.deck[4].value &&
            player.deck[4].value == player.deck[5].value
        ) {
            val card3 = player.deck[3]
            player.deck.removeAt(3)
            game.discardPile.push(card3)

            val card4 = player.deck[3]
            player.deck.removeAt(3)
            game.discardPile.push(card4)

            val card5 = player.deck[3]
            player.deck.removeAt(3)
            game.discardPile.push(card5)
        }

        // Check top row (indices 0,1,2)
        if (player.deck.size >= 3 &&
            player.deck[0].isFlipped && player.deck[1].isFlipped && player.deck[2].isFlipped &&
            player.deck[0].value == player.deck[1].value &&
            player.deck[1].value == player.deck[2].value
        ) {
            val card0 = player.deck[0]
            player.deck.removeAt(0)
            game.discardPile.push(card0)

            val card1 = player.deck[0]
            player.deck.removeAt(0)
            game.discardPile.push(card1)

            val card2 = player.deck[0]
            player.deck.removeAt(0)
            game.discardPile.push(card2)
        }
    }
    /**
     * matching column pairs count as 0. removed rows are not counted.
     *
     */
    /** calculates and returns the score of [player] based on their current deck. */

    fun calculateScore(player: Player): Int {
        var score = 0

        if (player.deck.size == 6) {
            for (i in 0..2) {
                if (player.deck[i].value != player.deck[i + 3].value) {
                    score += player.deck[i].value.toPoints()
                    score += player.deck[i + 3].value.toPoints()
                }
            }
        } else {
            for (card in player.deck) {
                score += card.value.toPoints()
            }
        }
        return score
    }

    /** advances to the next player. if current player revealed all cards, triggers last round first. */
    fun nextPlayer() {
        val game = checkNotNull(rootService.currentGame)

        val currentPlayer = game.players[game.currentPlayer]

        // check if the current player (before advancing) has all cards revealed
        val allRevealed = currentPlayer.deck.all { it.isFlipped }
        if (allRevealed && !game.lastRoundStarted) {
            // this player triggered the last round
            startLastRound()
        }

        // mark that this player has finished their turn in the last round
        if (game.lastRoundStarted) {
            currentPlayer.playedLastRound = true
        }

        // check if all other players have also played their last round turn
        //val allPlayedLastRound = game.players.all { it.playedLastRound }
        var allPlayedLastRound = true

        for(player in game.players){
            if(!player.playedLastRound){
                allPlayedLastRound = false
            }
        }
        // go through all player
        // check if all the player has played last round











        if (allPlayedLastRound) {
            // everyone played their final turn, end the game
            endGame()
            return
        }

        // advance to next player
        game.currentPlayer = (game.currentPlayer + 1) % game.players.size

        onAllRefreshables { refreshAfterNextPlayer(game.players[game.currentPlayer]) }
    }

    /** ends the game: reveals all face-down cards, clears rows, calculates scores, notifies GUI. */
    private fun endGame() {
        val game = checkNotNull(rootService.currentGame)

        // reveal all remaining face-down cards for every player
        for (player in game.players) {
            for (card in player.deck) {
                card.isFlipped = true
            }
        }

        // apply clearRow one more time for each player now that all cards are visible
        for (i in game.players.indices) {
            game.currentPlayer = i
            clearRow()
        }

        // calculate final score for each player
        for (player in game.players) {
            player.currentScore = calculateScore(player)
        }

        // notify the GUI that the game is over, pass all players with their scores
        onAllRefreshables { refreshAfterGameEnd(game.players) }
    }

    /** starts a new game with the given player [names], test mode and random order. */
    fun startGame(names: List<String>, isTestGame: Boolean, isRandom: Boolean) {
        require(names.size in 2..4){"Must be 2 to 4 players only "}
        require(names.all { it.isNotBlank() }){"Player name is empty"}
        require(names.distinct().size == names.size){"player names must be unique"}

        val drawStack = createDrawStack(isTestGame)
        val discardStack = createDiscardStack(drawStack)

        val players: MutableList<Player> = mutableListOf()
        for (player in names) {
            players.add(Player(player))
        }

        if (isRandom) {
            players.shuffle()
        }

        val game = Game(players, isTestGame)
        game.drawPile = drawStack
        game.discardPile = discardStack

        rootService.currentGame = game

        for (player in game.players) {
            repeat(6) {
                player.deck.add(game.drawPile.pop())
            }
        }

        onAllRefreshables {
            refreshAfterStartGame(game.players[game.currentPlayer])
        }
    }

    /** marks the last round as started and notifies all refreshables. */
    fun startLastRound() {
        val game = checkNotNull(rootService.currentGame)
        game.lastRoundStarted = true
        onAllRefreshables { refreshAfterLastRoundStarted() }
    }
    
}


