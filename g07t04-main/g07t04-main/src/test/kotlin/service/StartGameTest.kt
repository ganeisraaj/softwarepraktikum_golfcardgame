package service

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

/** Tests for the [GameService.startGame] method of the [GameService]. */
class StartGameTest {

    private lateinit var rootService: RootService
    private lateinit var testRefreshable: RefreshableTest

    /** Sets up a new [RootService] before each test. */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = RefreshableTest()
        rootService.gameService.addRefreshable(testRefreshable)
    }

    /** Tests that startGame correctly starts the game. */
    @Test
    fun testStartGame() {
        assertDoesNotThrow {
            rootService.gameService.startGame(listOf("Alice", "Bob"), isTestGame = false, isRandom = false)
        }

        val game = rootService.currentGame
        checkNotNull(game)

        assertEquals(2, game.players.size)
        assertEquals("Alice", game.players[0].name)

        for (player in game.players) {
            assertEquals(6, player.deck.size)
        }

        assertEquals(39, game.drawPile.size)
        assertEquals(1, game.discardPile.size)
        assertTrue(game.discardPile.peek().isFlipped)
        assertTrue(testRefreshable.refreshAfterStartGameCalled)
    }
}