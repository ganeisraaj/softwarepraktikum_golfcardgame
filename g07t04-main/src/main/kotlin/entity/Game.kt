package entity

import tools.aqua.bgw.util.Stack

/**
 * Represents a 6-Card Golf game.
 *
 * @constructor Creates a game with the given players and game mode.
 *
 * @param players The players of the game
 * @param isTestGame Whether the game is in test mode (3 values instead of 13)
 *
 */
class Game(val players: MutableList<Player>, val isTestGame: Boolean) {

    /** Index of the current player in [players] */
    var currentPlayer: Int = 0

    /** The face-down draw pile */
    var drawPile: Stack<Card> = Stack()

    /** The face-up discard pile */
    var discardPile: Stack<Card> = Stack()

    /** The log of moves made during the game */
    var logMessages: MutableList<LogMessage> = mutableListOf()

    var lastRoundStarted: Boolean = false
}
