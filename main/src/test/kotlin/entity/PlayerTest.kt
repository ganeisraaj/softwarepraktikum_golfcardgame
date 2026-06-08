package entity

import kotlin.test.*

/**
 * Tests for the [Player] class.
 */
class PlayerTest {

    /**
     * Tests that a newly created player has a score of 0.
     */
    @Test
    fun testInitialScore() {
        val player = Player("Alice")
        assertEquals(0, player.currentScore)
    }

    /**
     * Tests that a newly created player has an empty deck.
     */
    @Test
    fun testInitialDeckIsEmpty() {
        val player = Player("Alice")
        assertTrue(player.deck.isEmpty())
    }

    /**
     * Tests that a newly created player has no turn action (null).
     */
    @Test
    fun testInitialTurnIsNull() {
        val player = Player("Alice")
        assertNull(player.turn)
    }

    /**
     * Tests that cards can be added to the player's deck.
     */
    @Test
    fun testAddCardToDeck() {
        val player = Player("Alice")
        val card = Card(Value.ACE, Suit.HEARTS)
        player.deck.add(card)
        assertEquals(1, player.deck.size)
        assertEquals(card, player.deck[0])
    }

    /**
     * Tests that a player's deck can hold exactly 6 cards as per game rules.
     */
    @Test
    fun testDeckHoldsSixCards() {
        val player = Player("Alice")
        repeat(6) {
            player.deck.add(Card(Value.TWO, Suit.HEARTS))
        }
        assertEquals(6, player.deck.size)
    }

    /**
     * Tests that [Player.turn] can be set to a [PlayerAction].
     */
    @Test
    fun testTurnCanBeSet() {
        val player = Player("Alice")
        player.turn = PlayerAction.FLIP_TWO
        assertEquals(PlayerAction.FLIP_TWO, player.turn)
    }

    /**
     * Tests that [Player.currentScore] can be updated.
     */
    @Test
    fun testScoreCanBeUpdated() {
        val player = Player("Alice")
        player.currentScore = 9
        assertEquals(9, player.currentScore)
    }
}