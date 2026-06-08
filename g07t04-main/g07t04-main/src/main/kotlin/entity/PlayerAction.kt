package entity

/**
 * Represents the possible actions a player can perform during their turn.
 */
enum class PlayerAction {
    /** The player reveals one face-down card from their grid */
    FLIP_ONE,

    /** The player reveals two face-down cards from their grid (also used in the reveal round) */
    FLIP_TWO,

    /** The player draws the top card from the draw pile */
    DRAW_FROM_DRAW_PILE,

    /** The player draws the top card from the discard pile */
    DRAW_FROM_DISCARD_PILE
}