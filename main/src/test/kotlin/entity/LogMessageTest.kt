package entity

import kotlin.test.*

/**
 * Tests for the [LogMessage] class.
 */
class LogMessageTest {

    /** Helper to create a default player for testing. */
    private fun testPlayer() = Player("Alice")

    /**
     * Tests that a log message is created with the correct id.
     */
    @Test
    fun testLogId() {
        val log = LogMessage(1, testPlayer(), PlayerAction.FLIP_ONE)
        assertEquals(1, log.logId)
    }

    /**
     * Tests that a log message is created with the correct player action.
     */
    @Test
    fun testLogPlayerAction() {
        val log = LogMessage(1, testPlayer(), PlayerAction.DRAW_FROM_DRAW_PILE)
        assertEquals(PlayerAction.DRAW_FROM_DRAW_PILE, log.playerAction)
    }


    /**
     * Tests that all four player actions can be stored in a log message.
     */
    @Test
    fun testAllPlayerActions() {
        val player = testPlayer()
        assertEquals(PlayerAction.FLIP_ONE,
            LogMessage(1, player, PlayerAction.FLIP_ONE).playerAction)
        assertEquals(PlayerAction.FLIP_TWO,
            LogMessage(2, player, PlayerAction.FLIP_TWO).playerAction)
        assertEquals(PlayerAction.DRAW_FROM_DRAW_PILE,
            LogMessage(3, player, PlayerAction.DRAW_FROM_DRAW_PILE).playerAction)
        assertEquals(PlayerAction.DRAW_FROM_DISCARD_PILE,
            LogMessage(4, player, PlayerAction.DRAW_FROM_DISCARD_PILE).playerAction)
    }
}