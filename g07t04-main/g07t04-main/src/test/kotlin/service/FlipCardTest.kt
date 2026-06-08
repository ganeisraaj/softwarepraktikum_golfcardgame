package service

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*
import org.junit.jupiter.api.assertThrows

/** Tests for the [PlayerActionService.flipCard] method of the [PlayerActionService]. */
class FlipCardTest {

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

    /** Tests that flipCard sets the specif cards of the current player to isFlipped = true. */
    @Test
    fun testFlipCard() {
        val game = rootService.currentGame
        checkNotNull(game)

        game.currentPlayer = 0
        val player = game.players[0]

        assertFalse(player.deck[0].isFlipped)
        assertFalse(player.deck[1].isFlipped)

        assertDoesNotThrow { rootService.playerActionService.flipCard(listOf(0, 1)) }

        assertTrue(player.deck[0].isFlipped)
        assertTrue(player.deck[1].isFlipped)
        assertTrue(testRefreshable.refreshAfterFlipCardsCalled)
    }

    /** Tests that flipCard throws when cardIndices is empty. */
    @Test
    fun testFlipCardEmptyList() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.players.forEach { it.playedFirstRound = true }
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.flipCard(listOf())
        }
    }

    /** Tests that flipCard throws when more than 2 indices are provided. */
    @Test
    fun testFlipCardTooManyIndices() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.players.forEach { it.playedFirstRound = true }
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.flipCard(listOf(0, 1, 2))
        }
    }

    /** Tests that flipCard throws when index is out of bounds. */
    @Test
    fun testFlipCardOutOfBounds() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.players.forEach { it.playedFirstRound = true }
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.flipCard(listOf(99))
        }
    }

    /** Tests that flipCard throws when card is already flipped. */
    @Test
    fun testFlipCardAlreadyFlipped() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.players.forEach { it.playedFirstRound = true }
        game.players[0].deck[0].isFlipped = true
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.flipCard(listOf(0))
        }
    }

    /** Tests that flipCard throws when flipping only 1 card during reveal round. */
    @Test
    fun testFlipCardOneCardDuringReveal() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.currentPlayer = 0
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.flipCard(listOf(0))
        }
    }

    /** Tests that flipCard throws when player already completed reveal round. */
    @Test
    fun testFlipCardAlreadyRevealedRound() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.currentPlayer = 0
        game.players[0].playedFirstRound = true
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.flipCard(listOf(0, 1))
        }
    }

    /** Tests that flipCard throws when flipping 2 cards during normal play. */
    @Test
    fun testFlipCardTwoCardsDuringNormalPlay() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.players.forEach { it.playedFirstRound = true }
        assertThrows<IllegalArgumentException> {
            rootService.playerActionService.flipCard(listOf(0, 1))
        }
    }
}