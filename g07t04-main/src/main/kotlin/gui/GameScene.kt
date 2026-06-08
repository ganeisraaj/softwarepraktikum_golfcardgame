package gui

import entity.Card
import entity.Player
import entity.PlayerAction
import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/** The main game scene displaying all players' card grids, piles, and action buttons. */
class GameScene(private val rootService: RootService) :
    BoardGameScene(1920, 1080, background = ColorVisual(Color(0x1B5E20))), Refreshable {

    /** Card image loader for loading card visuals from the spritesheet. */
    private val cardImageLoader = CardImageLoader()

    private val cardWidth = 90
    private val cardHeight = 130

    // -------------------------------------------------------------------------
    // pending drawn card from draw pile
    // -------------------------------------------------------------------------

    /** Holds the card drawn from the draw pile until the player decides to swap or discard. */
    private var drawnCard: Card? = null
    private var actionTaken = false

    // -------------------------------------------------------------------------
    // Game log
    // -------------------------------------------------------------------------

    /** Five labels for the last 5 log entries, stacked vertically. */
    private val logLabels: List<Label> = List(5) { i ->
        Label(
            text = "",
            width = 350,
            height = 30,
            posX = 20,
            posY = 20 + i * 32,
            alignment = Alignment.TOP_LEFT,
            font = Font(13, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
            visual = ColorVisual(Color(0x145214))
        )
    }

    /** Label showing the current player's name at the bottom-right. */
    private val currentPlayerLabel = Label(
        text = "",
        width = 250,
        height = 50,
        posX = 1920 - 270,
        posY = 1080 - 70,
        alignment = Alignment.CENTER,
        font = Font(22, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x145214))
    )

    // -------------------------------------------------------------------------
    // Draw pile and discard pile
    // -------------------------------------------------------------------------

    /** The face-down draw pile shown in the center. */
    private val drawPileView = CardView(
        posX = 1920 / 2 - cardWidth - 20,
        posY = 1080 / 2 - cardHeight / 2,
        width = cardWidth,
        height = cardHeight,
        front = cardImageLoader.backImage,
        back = cardImageLoader.backImage
    )

    /** Label below the draw pile. */
    private val drawPileLabel = Label(
        text = "DRAW",
        width = cardWidth,
        height = 30,
        posX = 1920 / 2 - cardWidth - 20,
        posY = 1080 / 2 + cardHeight / 2 + 5,
        alignment = Alignment.CENTER,
        font = Font(14, Color(0xFFFFFF), "JetBrains Mono ExtraBold")
    )

    /** The top card of the discard pile shown face-up in the center. */
    private val discardPileView = CardView(
        posX = 1920 / 2 + 20,
        posY = 1080 / 2 - cardHeight / 2,
        width = cardWidth,
        height = cardHeight,
        front = cardImageLoader.blankImage,
        back = cardImageLoader.backImage
    )

    /** Label below the discard pile. */
    private val discardPileLabel = Label(
        text = "DISCARD",
        width = cardWidth,
        height = 30,
        posX = 1920 / 2 + 20,
        posY = 1080 / 2 + cardHeight / 2 + 5,
        alignment = Alignment.CENTER,
        font = Font(14, Color(0xFFFFFF), "JetBrains Mono ExtraBold")
    )

    /** Shows the drawn card from the draw pile while the player decides. */
    private val drawnCardView = CardView(
        posX = 1920 / 2 - cardWidth * 3 - 40,
        posY = 1080 / 2 - cardHeight / 2,
        width = cardWidth,
        height = cardHeight,
        front = cardImageLoader.blankImage,
        back = cardImageLoader.backImage
    ).apply { isVisible = false }

    /** Label below the drawn card. */
    private val drawnCardLabel = Label(
        text = "DRAWN",
        width = cardWidth,
        height = 30,
        posX = 1920 / 2 - cardWidth * 3 - 40,
        posY = 1080 / 2 + cardHeight / 2 + 5,
        alignment = Alignment.CENTER,
        font = Font(14, Color(0xFFFFFF), "JetBrains Mono ExtraBold")
    ).apply { isVisible = false }

    // -------------------------------------------------------------------------
    // Action buttons
    // -------------------------------------------------------------------------

    /** Button to flip one card during normal play. */
    private val flipOneButton = Button(
        text = "FLIP ONE",
        width = 180,
        height = 55,
        posX = 1150,
        posY = 720,
        font = Font(18, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    ).apply {
        onMouseClicked = {
            if (selectedIndices.size == 1) {
                rootService.playerActionService.flipCard(selectedIndices.toList())
            }
        }
    }

    /** Button to flip two cards during the reveal phase. */
    private val flipTwoButton = Button(
        text = "FLIP TWO",
        width = 180,
        height = 55,
        posX = 1150,
        posY = 720,
        font = Font(18, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    ).apply {
        onMouseClicked = {
            if (selectedIndices.size == 2) {
                rootService.playerActionService.flipCard(selectedIndices.toList())
            }
        }
    }

    /**
     * Player clicks DRAW to peek the top card from the draw pile.
     * The card is shown and the player must then choose to swap or discard+flip.
     */
    private val drawFromDrawPileButton = Button(
        text = "DRAW",
        width = 120,
        height = 40,
        posX = 1920 / 2 - cardWidth - 20,
        posY = 350,
        font = Font(16, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    ).apply {
        onMouseClicked = {
            val game = rootService.currentGame
            if (game != null && game.drawPile.isNotEmpty() && drawnCard == null) {
                drawnCard = game.drawPile.peek()
                drawnCardView.frontVisual = cardImageLoader.frontImageFor(
                    drawnCard!!.suit, drawnCard!!.value
                )
                drawnCardView.showFront()
                drawnCardView.isVisible = true
                drawnCardLabel.isVisible = true
                this.isVisible = false
                drawFromDiscardPileButton.isVisible = false
                swapButton.isVisible = true
                discardAndFlipButton.isVisible = true
                flipOneButton.isVisible = false
                flipTwoButton.isVisible = false
            }
        }
    }

    /**
     * Player clicks SWAP to swap the drawn card with a selected card.
     * Only visible after drawing from the draw pile.
     */
    private val swapButton = Button(
        text = "SWAP",
        width = 180,
        height = 55,
        posX = 1150,
        posY = 720,
        font = Font(18, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    ).apply {
        isVisible = false
        onMouseClicked = {
            if (selectedIndices.size == 1 && drawnCard != null) {
                rootService.playerActionService.drawFromDrawPile(
                    isAccepted = true,
                    cardIndex = selectedIndices[0]
                )
            }
        }
    }

    /**
     * Player clicks DISCARD & FLIP to discard the drawn card and flip a face-down card.
     * Only visible after drawing from the draw pile.
     */
    private val discardAndFlipButton = Button(
        text = "DISCARD + FLIP",
        width = 180,
        height = 55,
        posX = 1150,
        posY = 790,
        font = Font(18, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    ).apply {
        isVisible = false
        onMouseClicked = {
            if (selectedIndices.size == 1 && drawnCard != null) {
                rootService.playerActionService.drawFromDrawPile(
                    isAccepted = false,
                    cardIndex = selectedIndices[0]
                )
            }
        }
    }

    /** Button to draw from the discard pile and swap with a selected card. */
    private val drawFromDiscardPileButton = Button(
        text = "DRAW",
        width = 120,
        height = 40,
        posX = 1920 / 2 + 20,
        posY = 350,
        font = Font(16, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    ).apply {
        onMouseClicked = {
            if (selectedIndices.size == 1) {
                rootService.playerActionService.drawFromDiscardPile(cardIndex = selectedIndices[0])
            }
        }
    }

    /** Button to end the current player's turn. */
    private val endTurnButton = Button(
        text = "END TURN",
        width = 200,
        height = 60,
        posX = 20,
        posY = 1080 - 80,
        font = Font(22, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0xB71C1C))
    ).apply {
        onMouseClicked = {
            actionTaken = false
            val game = rootService.currentGame
            if (game != null) {
                val currentPlayer = game.players[game.currentPlayer]
                if (currentPlayer.playedFirstRound) {
                    val cp = game.currentPlayer
                    rootService.gameService.clearRow()
                    // Refresh card views after row clear
                    for (ci in 0 until 6) {
                        if (ci < game.players[cp].deck.size) {
                            val card = game.players[cp].deck[ci]
                            playerCardViews[cp][ci].isVisible = true
                            if (card.isFlipped) {
                                playerCardViews[cp][ci].frontVisual =
                                    cardImageLoader.frontImageFor(card.suit, card.value)
                                playerCardViews[cp][ci].showFront()
                            } else {
                                playerCardViews[cp][ci].backVisual = cardImageLoader.backImage
                                playerCardViews[cp][ci].showBack()
                            }
                        } else {
                            playerCardViews[cp][ci].isVisible = false
                        }
                    }
                    rootService.gameService.nextPlayer()
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Player card grids
    // -------------------------------------------------------------------------

    /** Card views for each player: playerCardViews[playerIndex][cardIndex 0..5]. */
    private val playerCardViews: List<List<CardView>> = List(4) { playerIndex ->
        List(6) { cardIndex ->
            val (px, py) = cardPosition(playerIndex, cardIndex)
            CardView(
                posX = px,
                posY = py,
                width = cardWidth,
                height = cardHeight,
                front = cardImageLoader.blankImage,
                back = cardImageLoader.backImage
            ).apply {
                showBack()
                onMouseClicked = {
                    handleCardClick(playerIndex, cardIndex)
                }
            }
        }
    }

    /** Player name labels shown next to each player's grid. */
    private val playerNameLabels: List<Label> = List(4) { playerIndex ->
        val (lx, ly) = nameLabelPosition(playerIndex)
        Label(
            text = "",
            width = 200,
            height = 40,
            posX = lx,
            posY = ly,
            alignment = Alignment.CENTER,
            font = Font(18, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
            visual = ColorVisual(Color(0x145214))
        )
    }

    /** Currently selected card indices of the current player. */
    private val selectedIndices = mutableListOf<Int>()

    // -------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------

    /** Adds all components to the scene. */
    init {
        val allComponents = mutableListOf(
            currentPlayerLabel,
            drawPileView, drawPileLabel,
            discardPileView, discardPileLabel,
            drawnCardView, drawnCardLabel,
            flipOneButton, flipTwoButton,
            drawFromDrawPileButton, drawFromDiscardPileButton,
            swapButton, discardAndFlipButton,
            endTurnButton
        )
        logLabels.forEach { allComponents.add(it) }
        playerCardViews.flatten().forEach { allComponents.add(it) }
        playerNameLabels.forEach { allComponents.add(it) }
        addComponents(*allComponents.toTypedArray())
    }

    // -------------------------------------------------------------------------
    // Layout helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the (x, y) position for a card based on its slot around the table.
     * Slot 0 = bottom (current player), 1 = left, 2 = top, 3 = right.
     */
    private fun cardPosition(slot: Int, cardIndex: Int): Pair<Int, Int> {
        val col = cardIndex % 3
        val row = cardIndex / 3
        val spacingX = cardWidth + 10
        val spacingY = cardHeight + 10
        return when (slot) {
            0 -> {
                val startX = 1920 / 2 - 150 + col * spacingX
                val startY = 720 + row * spacingY
                startX to startY
            }
            1 -> {
                val startX = 20 + col * spacingX
                val startY = 1080 / 2 - 140 + row * spacingY
                startX to startY
            }
            2 -> {
                val startX = 1920 / 2 - 150 + col * spacingX
                val startY = 30 + row * spacingY
                startX to startY
            }
            3 -> {
                val startX = 1920 - 320 + col * spacingX
                val startY = 1080 / 2 - 140 + row * spacingY
                startX to startY
            }
            else -> 0 to 0
        }
    }

    private fun nameLabelPosition(slot: Int): Pair<Int, Int> = when (slot) {
        0 -> 1920 / 2 - 100 to 975
        1 -> 20 to 1080 / 2 + 150
        2 -> 1920 / 2 - 100 to 290
        3 -> 1920 - 320 to 1080 / 2 + 150
        else -> 0 to 0
    }

    /**
     * Repositions all card grids and name labels so the current player is always at the bottom.
     */
    private fun updateLayout(currentPlayerIndex: Int, playerCount: Int) {
        for (i in 0 until playerCount) {
            val slot = (i - currentPlayerIndex + playerCount) % playerCount
            for (cardIndex in 0 until 6) {
                val (px, py) = cardPosition(slot, cardIndex)
                playerCardViews[i][cardIndex].posX = px.toDouble()
                playerCardViews[i][cardIndex].posY = py.toDouble()
            }
            val (lx, ly) = nameLabelPosition(slot)
            playerNameLabels[i].posX = lx.toDouble()
            playerNameLabels[i].posY = ly.toDouble()
        }
    }

    /** Resets all buttons and state back to normal mode after a draw decision is made. */
    private fun exitDecisionMode() {
        val game = rootService.currentGame
        drawnCard = null
        drawnCardView.isVisible = false
        drawnCardLabel.isVisible = false
        swapButton.isVisible = false
        discardAndFlipButton.isVisible = false
        selectedIndices.clear()
        // Restore all card visuals — face-up cards need frontVisual restored
        if (game != null) {
            game.players.forEachIndexed { pi, player ->
                player.deck.forEachIndexed { ci, card ->
                    if (card.isFlipped) {
                        playerCardViews[pi][ci].frontVisual =
                            cardImageLoader.frontImageFor(card.suit, card.value)
                        playerCardViews[pi][ci].showFront()
                    } else {
                        playerCardViews[pi][ci].backVisual = cardImageLoader.backImage
                        playerCardViews[pi][ci].showBack()
                    }
                }
            }
        } else {
            playerCardViews.flatten().forEach { it.backVisual = cardImageLoader.backImage }
        }
    }

    // -------------------------------------------------------------------------
    // Card click handler
    // -------------------------------------------------------------------------

    /**
     * Handles a click on a card — only for the current player.
     * Caps selection at 2 cards.
     */
    private fun handleCardClick(playerIndex: Int, cardIndex: Int) {
        val game = rootService.currentGame
        if (game != null && playerIndex == game.currentPlayer) {
            val revealDone = game.players.all { it.playedFirstRound }
            if (!revealDone && game.players[game.currentPlayer].playedFirstRound) return
            val cardView = playerCardViews[playerIndex][cardIndex]
            val card = game.players[playerIndex].deck[cardIndex]
            if (selectedIndices.contains(cardIndex)) {
                // deselect — restore correct visual
                selectedIndices.remove(cardIndex)
                if (card.isFlipped) {
                    cardView.frontVisual = cardImageLoader.frontImageFor(card.suit, card.value)
                    cardView.showFront()
                } else {
                    cardView.backVisual = cardImageLoader.backImage
                    cardView.showBack()
                }
            } else {
                if (selectedIndices.size < 2) {
                    selectedIndices.add(cardIndex)
                    // highlight with gold regardless of face-up or face-down
                    if (card.isFlipped) {
                        cardView.frontVisual = ColorVisual(Color(0xFFD700))
                    } else {
                        cardView.backVisual = ColorVisual(Color(0xFFD700))
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Refreshable implementations
    // -------------------------------------------------------------------------

    /** Initializes the scene after a new game starts. */
    override fun refreshAfterStartGame(currentPlayer: Player) {
        val game = rootService.currentGame ?: return

        game.players.forEachIndexed { index, player ->
            playerNameLabels[index].text = player.name
            playerNameLabels[index].isVisible = true
        }
        for (i in game.players.size until 4) {
            playerNameLabels[i].isVisible = false
            playerCardViews[i].forEach { it.isVisible = false }
        }

        playerCardViews.flatten().forEach { it.showBack() }
        currentPlayerLabel.text = currentPlayer.name

        if (game.discardPile.isNotEmpty()) {
            val top = game.discardPile.peek()
            discardPileView.frontVisual = cardImageLoader.frontImageFor(top.suit, top.value)
            discardPileView.showFront()
        }
        actionTaken = false
        exitDecisionMode()
        updateButtonVisibility()
        updateLayout(game.currentPlayer, game.players.size)
        updateLog()
    }

    /** Updates card visuals after cards are flipped. */
    override fun refreshAfterFlipCards(cards: List<Card>, indices: List<Int>) {
        val game = rootService.currentGame ?: return
        val cp = game.currentPlayer

        indices.forEachIndexed { i, cardIndex ->
            val card = cards[i]
            playerCardViews[cp][cardIndex].frontVisual =
                cardImageLoader.frontImageFor(card.suit, card.value)
            playerCardViews[cp][cardIndex].showFront()
        }

        exitDecisionMode()
        val revealDone = game.players.all { it.playedFirstRound }
        if (revealDone) actionTaken = true
        updateButtonVisibility()
        updateLog()
    }

    /** Updates card visuals after drawing from the discard pile. */
    override fun refreshAfterDrawFromDiscardPile(newCard: Card, cardIndex: Int) {
        val game = rootService.currentGame ?: return
        val cp = game.currentPlayer

        playerCardViews[cp][cardIndex].frontVisual =
            cardImageLoader.frontImageFor(newCard.suit, newCard.value)
        playerCardViews[cp][cardIndex].showFront()

        if (game.discardPile.isNotEmpty()) {
            val top = game.discardPile.peek()
            discardPileView.frontVisual = cardImageLoader.frontImageFor(top.suit, top.value)
            discardPileView.showFront()
        }

        exitDecisionMode()
        actionTaken = true
        updateButtonVisibility()
        updateLog()
    }

    /** Updates card visuals after drawing from the draw pile (swap or discard+flip). */
    override fun refreshAfterDrawFromDrawPile(isAccepted: Boolean, newCard: Card, cardIndex: Int) {
        val game = rootService.currentGame ?: return
        val cp = game.currentPlayer

        if (isAccepted) {
            playerCardViews[cp][cardIndex].frontVisual =
                cardImageLoader.frontImageFor(newCard.suit, newCard.value)
            playerCardViews[cp][cardIndex].showFront()
        } else {
            val flippedCard = game.players[cp].deck[cardIndex]
            playerCardViews[cp][cardIndex].frontVisual =
                cardImageLoader.frontImageFor(flippedCard.suit, flippedCard.value)
            playerCardViews[cp][cardIndex].showFront()
        }

        if (game.discardPile.isNotEmpty()) {
            val top = game.discardPile.peek()
            discardPileView.frontVisual = cardImageLoader.frontImageFor(top.suit, top.value)
            discardPileView.showFront()
        }

        exitDecisionMode()
        actionTaken = true
        updateButtonVisibility()
        updateLog()
    }

    /** Updates the current player label and layout after the turn advances. */
    override fun refreshAfterNextPlayer(nextPlayer: Player) {
        val game = rootService.currentGame ?: return
        currentPlayerLabel.text = nextPlayer.name
        actionTaken = false
        exitDecisionMode()
        updateButtonVisibility()
        updateLayout(game.currentPlayer, game.players.size)
        updateLog()
    }

    /** Shows FLIP TWO during reveal round, FLIP ONE during normal play. */
    private fun updateButtonVisibility() {
        if (actionTaken) {
            flipOneButton.isVisible = false
            flipTwoButton.isVisible = false
            drawFromDrawPileButton.isVisible = false
            drawFromDiscardPileButton.isVisible = false
            return
        }
        val game = rootService.currentGame ?: return
        val revealDone = game.players.all { it.playedFirstRound }
        flipOneButton.isVisible = revealDone
        flipTwoButton.isVisible = !revealDone
        drawFromDrawPileButton.isVisible = revealDone
        drawFromDiscardPileButton.isVisible = revealDone
    }

    /** Updates the log labels with the last 5 actions in a readable format. */
    private fun updateLog() {
        val game = rootService.currentGame ?: return
        val entries = game.logMessages.takeLast(5).map { msg ->
            val action = when (msg.playerAction) {
                PlayerAction.FLIP_ONE, PlayerAction.FLIP_TWO ->
                    "flipped ${msg.cards.size}"
                PlayerAction.DRAW_FROM_DRAW_PILE -> "drew from deck"
                PlayerAction.DRAW_FROM_DISCARD_PILE -> "drew from discard"
            }
            val cardStr = msg.cards.joinToString(", ") { "${it.value} ${it.suit}" }
            "${msg.player.name}: $action" + if (cardStr.isNotEmpty()) " ($cardStr)" else ""
        }
        logLabels.forEachIndexed { i, label ->
            label.text = if (i < entries.size) entries[i] else ""
        }
    }
}
