package entity

/**
 * Represents a card in a Card Golf game.
 *
 * @constructor Creates a card with the given suit and value.
 *
 * @param suit The [Suit] of the card
 * @param value The [Value] of the card

 * @property isFlipped Whether the card is face-up (true) or face-down (false)
 */

data class Card(val value: Value, val suit: Suit, var isFlipped: Boolean = false)