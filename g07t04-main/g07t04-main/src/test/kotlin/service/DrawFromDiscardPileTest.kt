package service

import entity.Card
import entity.Suit
import entity.Value
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*
import org.junit.jupiter.api.assertThrows

/** Tests for the [PlayerActionService.drawFromDiscardPile] method of the [PlayerActionService]. */
class DrawFromDiscardPileTest {

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

    /** Tests that drawFromDiscardPile swaps the top discard card into the player's deck. */
    @Test
    fun testDrawFromDiscardPile() {
        val game = rootService.currentGame
        checkNotNull(game)

        game.players.forEach { it.playedFirstRound = true }

        game.currentPlayer = 0
        val player = game.players[0]
        val knownCard = Card(Value.KING, Suit.CLUBS)
        game.discardPile.push(knownCard)

        val oldCard = player.deck[3]
        val sizeBefore = game.discardPile.size

        assertDoesNotThrow { rootService.playerActionService.drawFromDiscardPile(cardIndex = 3) }

        assertEquals(knownCard, player.deck[3])
        assertTrue(player.deck[3].isFlipped)
        assertEquals(oldCard, game.discardPile.peek())
        assertEquals(sizeBefore, game.discardPile.size)
        assertTrue(testRefreshable.refreshAfterDrawFromDiscardPileCalled)
    }

    /** Tests that drawFromDiscardPile throws when reveal round is not done. */
    @Test
    fun testDrawFromDiscardPileRevealNotDone() {
        val game = rootService.currentGame
        checkNotNull(game)
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.drawFromDiscardPile(cardIndex = 0)
        }
    }

    /** Tests that drawFromDiscardPile throws when card index is out of bounds. */
    @Test
    fun testDrawFromDiscardPileOutOfBounds() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.players.forEach { it.playedFirstRound = true }
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.drawFromDiscardPile(cardIndex = 99)
        }
    }
}