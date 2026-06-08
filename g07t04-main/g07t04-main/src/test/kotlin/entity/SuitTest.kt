package entity

import kotlin.test.*

/**
 * Tests for the [Suit] enum class.
 */
class SuitTest {

    /**
     * Tests that the CLUBS suit has the correct string representation.
     */
    @Test
    fun testClubsToString() {
        assertEquals("♣", Suit.CLUBS.toString())
    }

    /**
     * Tests that the SPADES suit has the correct string representation.
     */
    @Test
    fun testSpadesToString() {
        assertEquals("♠", Suit.SPADES.toString())
    }

    /**
     * Tests that the HEARTS suit has the correct string representation.
     */
    @Test
    fun testHeartsToString() {
        assertEquals("♥", Suit.HEARTS.toString())
    }

    /**
     * Tests that the DIAMONDS suit has the correct string representation.
     */
    @Test
    fun testDiamondsToString() {
        assertEquals("♦", Suit.DIAMONDS.toString())
    }

    /**
     * Tests that the enum contains exactly 4 suits.
     */
    @Test
    fun testExactlyFourSuits() {
        assertEquals(4, Suit.entries.size)
    }

    /**
     * Tests that all four suits can be retrieved by name.
     */
    @Test
    fun testValueOf() {
        assertEquals(Suit.CLUBS, Suit.valueOf("CLUBS"))
        assertEquals(Suit.SPADES, Suit.valueOf("SPADES"))
        assertEquals(Suit.HEARTS, Suit.valueOf("HEARTS"))
        assertEquals(Suit.DIAMONDS, Suit.valueOf("DIAMONDS"))
    }

    /**
     * Tests that an invalid suit name throws an exception.
     */
    @Test
    fun testInvalidSuitThrows() {
        assertFailsWith<IllegalArgumentException> {
            Suit.valueOf("INVALID_SUIT")
        }
    }
}