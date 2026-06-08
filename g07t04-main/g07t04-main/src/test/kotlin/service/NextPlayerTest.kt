package service

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

/** Tests for the [GameService.nextPlayer] method of the [GameService]. */
class NextPlayerTest {

    private lateinit var rootService: RootService
    private lateinit var testRefreshable: RefreshableTest

    /** Sets up a new game with three players before each test. */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = RefreshableTest()
        rootService.gameService.addRefreshable(testRefreshable)
        rootService.gameService.startGame(listOf("Alice", "Bob", "Charlie"), isTestGame = false, isRandom = false)
    }

    /** Tests that nextPlayer advances the index and wraps around after the last player. */
    @Test
    fun testNextPlayerWrapsAround() {
        val game = rootService.currentGame
        checkNotNull(game)

        game.currentPlayer = 0
        rootService.gameService.nextPlayer()
        assertEquals(1, game.currentPlayer)

        game.currentPlayer = 2
        assertDoesNotThrow { rootService.gameService.nextPlayer() }
        assertEquals(0, game.currentPlayer)
    }
}