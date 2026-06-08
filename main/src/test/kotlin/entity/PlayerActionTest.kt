package entity

import kotlin.test.*

/**
 * Tests for the [PlayerAction] enum class.
 */
class PlayerActionTest {

    /**
     * Tests that the enum contains exactly 4 values as per game rules.
     */
    @Test
    fun testExactlyFourActions() {
        assertEquals(4, PlayerAction.entries.size)
    }

    /**
     * Tests that an invalid action name throws an exception.
     */
    @Test
    fun testInvalidActionThrows() {
        assertFailsWith<IllegalArgumentException> {
            PlayerAction.valueOf("INVALID_ACTION")
        }
    }
}