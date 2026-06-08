package entity

/**
 * Represents a log entry of a player's action during the game.
 *
 * @constructor Creates a log message with the given id, player and action.
 *
 * @param logId The unique identifier of this log message
 * @param player The [Player] who performed the action
 * @param playerAction The [PlayerAction] the player performed
 *
 * @property cards The cards involved in this action (0 to 2 cards)
 */
class LogMessage(
    val logId: Int,
    val player: Player,
    val playerAction: PlayerAction,
    var cards: MutableList<Card> = mutableListOf()
)