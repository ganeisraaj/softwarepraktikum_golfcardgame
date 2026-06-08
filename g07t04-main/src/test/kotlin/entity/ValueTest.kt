package entity

import kotlin.test.*

/**
 * Tests for the [Value] enum class.
 */
class ValueTest {

    /**
     * Tests that [Value.toPoints] returns the correct point value for Ace (1 point).
     */
    @Test
    fun testAcePoints() {
        assertEquals(1, Value.ACE.toPoints())
    }

    /**
     * Tests that [Value.toPoints] returns -2 for TWO (special negative value).
     */
    @Test
    fun testTwoPoints() {
        assertEquals(-2, Value.TWO.toPoints())
    }

    /**
     * Tests that [Value.toPoints] returns the face value for cards THREE through TEN.
     */
    @Test
    fun testNumericCardPoints() {
        assertEquals(3, Value.THREE.toPoints())
        assertEquals(4, Value.FOUR.toPoints())
        assertEquals(5, Value.FIVE.toPoints())
        assertEquals(6, Value.SIX.toPoints())
        assertEquals(7, Value.SEVEN.toPoints())
        assertEquals(8, Value.EIGHT.toPoints())
        assertEquals(9, Value.NINE.toPoints())
        assertEquals(10, Value.TEN.toPoints())
    }

    /**
     * Tests that [Value.toPoints] returns 10 for JACK.
     */
    @Test
    fun testJackPoints() {
        assertEquals(10, Value.JACK.toPoints())
    }

    /**
     * Tests that [Value.toPoints] returns 10 for QUEEN.
     */
    @Test
    fun testQueenPoints() {
        assertEquals(10, Value.QUEEN.toPoints())
    }

    /**
     * Tests that [Value.toPoints] returns 0 for KING.
     */
    @Test
    fun testKingPoints() {
        assertEquals(0, Value.KING.toPoints())
    }

    /**
     * Tests that [Value.toString] returns the correct single character representation.
     */
    @Test
    fun testToString() {
        assertEquals("2", Value.TWO.toString())
        assertEquals("J", Value.JACK.toString())
        assertEquals("Q", Value.QUEEN.toString())
        assertEquals("K", Value.KING.toString())
        assertEquals("A", Value.ACE.toString())
    }

    /**
     * Tests that [Value.testDeck] returns exactly the three test mode values {2, King, Queen}.
     */
    @Test
    fun testTestDeck() {
        val deck = Value.testDeck()
        assertEquals(3, deck.size)
        assertTrue(Value.TWO in deck)
        assertTrue(Value.KING in deck)
        assertTrue(Value.QUEEN in deck)
    }
}