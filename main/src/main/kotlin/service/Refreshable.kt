package service

import entity.Card
import entity.Player

/**
 * this interface provides a mechanism for the service layer classes to communicate
 * (usually to the GUI classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * default (empty) implementations are provided for all methods, so that implementing
 * classes only need to override the methods relevant to them.
 *
 * @see AbstractRefreshingService
 */
interface Refreshable {

    /** called after a new game has been started. [currentPlayer] is the first player to act. */
    fun refreshAfterStartGame(currentPlayer: Player) {}

    /** called after the last round has been triggered. */
    fun refreshAfterLastRoundStarted() {}

    /** called after the turn advances. [nextPlayer] is the player who acts next. */
    fun refreshAfterNextPlayer(nextPlayer: Player) {}

    /** called after [cards] at [indices] in the current player's deck have been flipped. */
    fun refreshAfterFlipCards(cards: List<Card>, indices: List<Int>) {}

    /** called after the current player swapped a card from the discard pile with their card at [cardIndex]. */
    fun refreshAfterDrawFromDiscardPile(newCard: Card, cardIndex: Int) {}

    /** called after the current player drew [newCard] from the draw pile.
     *[isAccepted] indicates if it was swapped or discarded.
     */
    fun refreshAfterDrawFromDrawPile(isAccepted: Boolean, newCard: Card, cardIndex: Int) {}

    /** called when the game has ended. [players] contains all players with their final scores. */
    fun refreshAfterGameEnd(players: List<Player>) {}
}