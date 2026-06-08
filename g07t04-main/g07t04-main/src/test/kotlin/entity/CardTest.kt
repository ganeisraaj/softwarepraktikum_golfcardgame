package entity

import kotlin.test.*

class CardTest {

    @Test
    fun testCardIsNotFlippedByDefault() {
        val card = Card(Value.ACE, Suit.HEARTS)
        assertFalse(card.isFlipped)
    }

    @Test
    fun testCardCanBeFlipped() {
        val card = Card(Value.ACE, Suit.HEARTS)
        card.isFlipped = true
        assertTrue(card.isFlipped)
    }

    @Test
    fun testCardProperties() {
        val card = Card(Value.KING, Suit.SPADES)
        assertEquals(Suit.SPADES, card.suit)
        assertEquals(Value.KING, card.value)
    }

    @Test
    fun testEqualsTrue() {
        val card1 = Card(Value.TEN, Suit.CLUBS)
        val card2 = Card(Value.TEN, Suit.CLUBS)
        assertEquals(card1, card2)
    }


}