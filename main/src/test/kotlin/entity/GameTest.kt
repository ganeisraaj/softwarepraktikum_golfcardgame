package entity

import kotlin.test.*

/**
 * Tests for the [Game] class.
 */
class GameTest {

    /** Helper to create a default list of two players for testing. */
    private fun twoPlayers() = mutableListOf(Player("Alice"), Player("Bob"))

    /**
     * Tests that a game is created with the correct players.
     */
    @Test
    fun testGamePlayers() {
        val players = twoPlayers()
        val game = Game(players, false)
        assertEquals(2, game.players.size)
        assertEquals("Alice", game.players[0].name)
        assertEquals("Bob", game.players[1].name)
    }

    /**
     * Tests that [Game.currentPlayer] starts at 0.
     */
    @Test
    fun testInitialCurrentPlayer() {
        val game = Game(twoPlayers(), false)
        assertEquals(0, game.currentPlayer)
    }

    /**
     * Tests that [Game.drawPile] is empty on construction.
     */
    @Test
    fun testInitialDrawPileIsEmpty() {
        val game = Game(twoPlayers(), false)
        assertEquals(0, game.drawPile.size)
    }

    /**
     * Tests that [Game.discardPile] is empty on construction.
     */
    @Test
    fun testInitialDiscardPileIsEmpty() {
        val game = Game(twoPlayers(), false)
        assertEquals(0, game.discardPile.size)
    }


    /**
     * Tests that log messages can be added to [Game.logMessages].
     */
    @Test
    fun testLogMessagesCanBeAdded() {
        val game = Game(twoPlayers(), false)
        val player = game.players[0]
        val log = LogMessage(0, player, PlayerAction.FLIP_TWO)
        game.logMessages.add(log)
        assertEquals(1, game.logMessages.size)
    }
}