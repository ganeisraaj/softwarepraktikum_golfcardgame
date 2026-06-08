package entity

/**
 * Represents a player in a Card Golf game.
 *
 * @constructor Creates a player with the given name.
 *
 * @param name The name of the player
 *
 * @property deck The 6 cards in front of the player
 * @property turn The last action performed by the player
 */
class Player(val name: String) {
    /** The current score of the player */
    var currentScore: Int = 0

    /** The 6 cards belonging to this player */
    var deck: MutableList<Card> = mutableListOf()

    /** The last action performed by this player */
    // null because there's no action before the game start. even before the reveal 2 cards round.
    var turn: PlayerAction? = null

    var playedFirstRound: Boolean = false

    var playedLastRound: Boolean = false

}