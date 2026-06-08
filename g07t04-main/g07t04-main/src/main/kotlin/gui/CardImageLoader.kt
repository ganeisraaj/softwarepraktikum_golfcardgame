package gui

import entity.Suit
import entity.Value
import tools.aqua.bgw.visual.ImageVisual

/**
 * The full raster image containing the suits as rows (plus one special row for blank/back)
 * and values as columns (starting with the ace). As the ordering in the image is not the same
 * as the order in which the suits are declared in [CardSuit], mappings via [row] and [column]
 * are required.
 */
private const val CARDS_FILE = "card_deck.png"

private const val IMG_HEIGHT = 200
private const val IMG_WIDTH = 130

/**
 * Provides access to the src/main/resources/card_deck.png file that contains all card images
 * in a raster. The returned [ImageVisual] objects of [frontImageFor], [blankImage],
 * and [backImage] are 130x200 pixels.
 */
class CardImageLoader {

    /**
     * Provides a blank (white) card
     */
    val blankImage : ImageVisual get() = getImageByCoordinates(0, 4)

    /**
     * Provides the back side image of the card deck
     */
    val backImage: ImageVisual get() = getImageByCoordinates(2, 4)

    /**
     * Provides the card image for the given [CardSuit] and [CardValue]
     */
    fun frontImageFor(suit: Suit, value: Value) =
        getImageByCoordinates(value.column, suit.row)

    /**
     * retrieves from the full raster image [CARDS_FILE] the corresponding sub-image
     * for the given column [x] and row [y]
     *
     * @param x column in the raster image, starting at 0
     * @param y row in the raster image, starting at 0
     *
     */
    private fun getImageByCoordinates (x: Int, y: Int) = ImageVisual(
        CARDS_FILE,
        IMG_WIDTH,
        IMG_HEIGHT,
        x * IMG_WIDTH,
        y * IMG_HEIGHT,
    )

}

/**
 * As the [CARDS_FILE] does not have the same ordering of suits
 * as they are in [CardSuit], this extension property provides
 * a corresponding mapping to be used when addressing the row.
 *
 */
private val Suit.row get() = when (this) {
    Suit.CLUBS -> 0
    Suit.DIAMONDS -> 1
    Suit.HEARTS -> 2
    Suit.SPADES -> 3
}


/**
 * As the [CARDS_FILE] does not have the same ordering of values
 * as they are in [CardValue], this extension property provides
 * a corresponding mapping to be used when addressing the column.
 */
private val Value.column get() = when (this) {
    Value.ACE -> 0
    Value.TWO -> 1
    Value.THREE -> 2
    Value.FOUR -> 3
    Value.FIVE -> 4
    Value.SIX -> 5
    Value.SEVEN -> 6
    Value.EIGHT -> 7
    Value.NINE -> 8
    Value.TEN -> 9
    Value.JACK -> 10
    Value.QUEEN -> 11
    Value.KING -> 12
}
