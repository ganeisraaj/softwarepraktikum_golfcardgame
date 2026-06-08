package entity

import java.util.*

/**
 * Enum to distinguish between the 13 possible values in a french-suited card game:
 * 2-10, Jack, Queen, King, and Ace.
 *
 * The values are ordered according to their most common ordering:
 * 2 < 3 < ... < 10 < Jack < Queen < King < Ace
 *
 */
enum class Value {
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING,
    ACE,
    ;

    /**
     * provide a single character to represent this value.
     * Returns one of: 2/3/4/5/6/7/8/9/10/J/Q/K/A
     */
    override fun toString() =
        when(this) {
            TWO -> "2"
            THREE -> "3"
            FOUR -> "4"
            FIVE -> "5"
            SIX -> "6"
            SEVEN -> "7"
            EIGHT -> "8"
            NINE -> "9"
            TEN -> "10"
            JACK -> "J"
            QUEEN -> "Q"
            KING -> "K"
            ACE -> "A"
        }

    /**
     * Returns the point value of this card according to 6-Card Golf rules:
     * Ace = 1, 2 = -2, 3-10 = face value, Jack/Queen = 10, King = 0
     */
    fun toPoints(): Int =
        when(this) {
            ACE -> 1
            TWO -> -2
            THREE -> 3
            FOUR -> 4
            FIVE -> 5
            SIX -> 6
            SEVEN -> 7
            EIGHT -> 8
            NINE -> 9
            TEN -> 10
            JACK -> 10
            QUEEN -> 10
            KING -> 0
        }

    /**
     * Provides utility functions for working with [Value].
     */
    companion object {

        /**
         * A set of values used in test game mode {2, Queen, King}
         */
        fun testDeck(): Set<Value> {
            return EnumSet.of(TWO, QUEEN, KING)
        }

    }
}