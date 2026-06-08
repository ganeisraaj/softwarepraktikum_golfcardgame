package service

import entity.Card
import entity.Suit
import entity.Value
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*
import org.junit.jupiter.api.assertThrows

/** Tests for the [PlayerActionService.drawFromDrawPile] method of the [PlayerActionService]. */
class DrawFromDrawPileTest {

    private lateinit var rootService: RootService
    private lateinit var testRefreshable: RefreshableTest

    /** Sets up a new game with two players before each test. */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = RefreshableTest()
        rootService.playerActionService.addRefreshable(testRefreshable)
        rootService.gameService.startGame(listOf("Alice", "Bob"), isTestGame = false, isRandom = false)
    }

    /** Tests that accepting the drawn card swaps it into the deck and puts the old card on the discard pile. */
    @Test
    fun testDrawFromDrawPileAccepted() {
        val game = rootService.currentGame
        checkNotNull(game)

        game.players.forEach { it.playedFirstRound = true }

        game.currentPlayer = 0
        val player = game.players[0]
        val knownCard = Card(Value.ACE, Suit.HEARTS)
        game.drawPile.push(knownCard)

        val oldCard = player.deck[0]

        assertDoesNotThrow { rootService.playerActionService.drawFromDrawPile(isAccepted = true, cardIndex = 0) }

        assertEquals(knownCard, player.deck[0])
        assertTrue(player.deck[0].isFlipped)
        assertEquals(oldCard, game.discardPile.peek())
        assertTrue(testRefreshable.refreshAfterDrawFromDrawPileCalled)
    }

    /** Tests that rejecting the drawn card puts it on the discard pile and flips the card at cardIndex. */
    @Test
    fun testDrawFromDrawPileRejected() {
        val game = rootService.currentGame
        checkNotNull(game)

        game.players.forEach { it.playedFirstRound = true }

        game.currentPlayer = 0
        val player = game.players[0]
        val knownCard = Card(Value.ACE, Suit.SPADES)
        game.drawPile.push(knownCard)

        val cardAtIndex = player.deck[2]

        assertDoesNotThrow { rootService.playerActionService.drawFromDrawPile(isAccepted = false, cardIndex = 2) }

        assertEquals(knownCard, game.discardPile.peek())
        assertEquals(cardAtIndex, player.deck[2])
        assertTrue(player.deck[2].isFlipped)
        assertTrue(testRefreshable.refreshAfterDrawFromDrawPileCalled)
    }

    /** Tests that drawFromDrawPile throws when reveal round is not done. */
    @Test
    fun testDrawFromDrawPileRevealNotDone() {
        val game = rootService.currentGame
        checkNotNull(game)
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.drawFromDrawPile(isAccepted = true, cardIndex = 0)
        }
    }

    /** Tests that drawFromDrawPile throws when draw pile is empty. */
    @Test
    fun testDrawFromDrawPileEmptyPile() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.players.forEach { it.playedFirstRound = true }
        while (game.drawPile.isNotEmpty()) game.drawPile.pop()
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.drawFromDrawPile(isAccepted = true, cardIndex = 0)
        }
    }

    /** Tests that drawFromDrawPile throws when card index is out of bounds. */
    @Test
    fun testDrawFromDrawPileOutOfBounds() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.players.forEach { it.playedFirstRound = true }
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.drawFromDrawPile(isAccepted = true, cardIndex = 99)
        }
    }

    /** Tests that drawFromDrawPile throws when rejecting but card is already flipped. */
    @Test
    fun testDrawFromDrawPileRejectAlreadyFlipped() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.players.forEach { it.playedFirstRound = true }
        game.players[0].deck[0].isFlipped = true
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.drawFromDrawPile(isAccepted = false, cardIndex = 0)
        }
    }
}