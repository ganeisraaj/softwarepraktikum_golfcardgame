package service

import entity.Card
import entity.Player

/** A [Refreshable] implementation for testing which refresh methods were called. */
class RefreshableTest : Refreshable {
    /** True if [refreshAfterStartGame] was called. */
    var refreshAfterStartGameCalled = false
    /** True if [refreshAfterLastRoundStarted] was called. */
    var refreshAfterLastRoundStartedCalled = false
    /** True if [refreshAfterFlipCards] was called. */
    var refreshAfterFlipCardsCalled = false
    /** True if [refreshAfterDrawFromDiscardPile] was called. */
    var refreshAfterDrawFromDiscardPileCalled = false
    /** True if [refreshAfterDrawFromDrawPile] was called. */
    var refreshAfterDrawFromDrawPileCalled = false
    /** True if [refreshAfterNextPlayer] was called. */
    var refreshAfterNextPlayerCalled = false
    /** True if [refreshAfterGameEnd] was called. */
    var refreshAfterGameEndCalled = false

    /** Sets [refreshAfterStartGameCalled] to true. */
    override fun refreshAfterStartGame(currentPlayer: Player) {
        refreshAfterStartGameCalled = true
    }
    /** Sets [refreshAfterLastRoundStartedCalled] to true. */
    override fun refreshAfterLastRoundStarted() {
        refreshAfterLastRoundStartedCalled = true
    }
    /** Sets [refreshAfterFlipCardsCalled] to true. */
    override fun refreshAfterFlipCards(cards: List<Card>, indices: List<Int>) {
        refreshAfterFlipCardsCalled = true
    }
    /** Sets [refreshAfterDrawFromDiscardPileCalled] to true. */
    override fun refreshAfterDrawFromDiscardPile(newCard: Card, cardIndex: Int) {
        refreshAfterDrawFromDiscardPileCalled = true
    }
    /** Sets [refreshAfterDrawFromDrawPileCalled] to true. */
    override fun refreshAfterDrawFromDrawPile(isAccepted: Boolean, newCard: Card, cardIndex: Int) {
        refreshAfterDrawFromDrawPileCalled = true
    }
    /** Sets [refreshAfterNextPlayerCalled] to true. */
    override fun refreshAfterNextPlayer(nextPlayer: Player) {
        refreshAfterNextPlayerCalled = true


    }
    /** Sets [refreshAfterGameEndCalled] to true. */
    override fun refreshAfterGameEnd(players: List<Player>) {
        refreshAfterGameEndCalled = true
        }

}