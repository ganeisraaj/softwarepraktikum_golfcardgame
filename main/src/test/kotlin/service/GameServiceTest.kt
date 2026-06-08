package service
import entity.Card
import entity.LogMessage
import entity.PlayerAction
import entity.Suit
import entity.Value
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*


class GameServiceTest {

    private lateinit var rootService: RootService
    private lateinit var testRefreshable: RefreshableTest

    /** Sets up a new game with two players before each test. */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = RefreshableTest()
        rootService.gameService.addRefreshable(testRefreshable)
        rootService.gameService.startGame(listOf("Alice", "Bob"), isTestGame = false, isRandom = false)
    }

    /** Tests that createDrawStack returns 52 cards in normal mode. */
    @Test
    fun testCreateDrawStackNormalMode() {
        val stack = rootService.gameService.createDrawStack(isTestGame = false)
        assertEquals(52, stack.size)
    }

    /** Tests that createDrawStack returns 52 cards in test mode. */
    @Test
    fun testCreateDrawStackTestMode() {
        val stack = rootService.gameService.createDrawStack(isTestGame = true)
        assertEquals(52, stack.size)
    }

    /** Tests that createDrawStack in test mode only contains TWO, KING and QUEEN. */
    @Test
    fun testCreateDrawStackTestModeValues() {
        val stack = rootService.gameService.createDrawStack(isTestGame = true)
        val allowedValues = setOf(Value.TWO, Value.KING, Value.QUEEN)
        val cards = mutableListOf<Card>()
        while (stack.isNotEmpty()) cards.add(stack.pop())
        assertTrue(cards.all { it.value in allowedValues })
    }

    /** Tests that createDiscardStack returns one face-up card and shrinks the draw stack. */
    @Test
    fun testCreateDiscardStack() {
        val drawStack = rootService.gameService.createDrawStack(isTestGame = false)
        val sizeBefore = drawStack.size
        val discardStack = rootService.gameService.createDiscardStack(drawStack)
        assertEquals(1, discardStack.size)
        assertEquals(sizeBefore - 1, drawStack.size)
        assertTrue(discardStack.peek().isFlipped)
    }

    /** Tests calculateScore with no column pairs. */
    @Test
    fun testCalculateScoreNoPairs() {
        val game = rootService.currentGame
        checkNotNull(game)
        val player = game.players[0]

        player.deck[0] = Card(Value.ACE, Suit.HEARTS)
        player.deck[1] = Card(Value.TWO, Suit.HEARTS)
        player.deck[2] = Card(Value.THREE, Suit.HEARTS)
        player.deck[3] = Card(Value.FOUR, Suit.HEARTS)
        player.deck[4] = Card(Value.FIVE, Suit.HEARTS)
        player.deck[5] = Card(Value.SIX, Suit.HEARTS)

        assertEquals(17, rootService.gameService.calculateScore(player))
    }

    /** Tests calculateScore with column pairs counting as 0. */
    @Test
    fun testCalculateScoreWithColumnPairs() {
        val game = rootService.currentGame
        checkNotNull(game)
        val player = game.players[0]

        player.deck[0] = Card(Value.KING, Suit.HEARTS)
        player.deck[3] = Card(Value.KING, Suit.SPADES)
        player.deck[1] = Card(Value.ACE, Suit.HEARTS)
        player.deck[4] = Card(Value.TWO, Suit.HEARTS)
        player.deck[2] = Card(Value.FIVE, Suit.HEARTS)
        player.deck[5] = Card(Value.FIVE, Suit.SPADES)

        assertEquals(-1, rootService.gameService.calculateScore(player))
    }

    /** Tests calculateScore with fewer than 6 cards. */
    @Test
    fun testCalculateScorePartialDeck() {
        val game = rootService.currentGame
        checkNotNull(game)
        val player = game.players[0]

        player.deck.clear()
        player.deck.add(Card(Value.KING, Suit.HEARTS))
        player.deck.add(Card(Value.ACE, Suit.HEARTS))
        player.deck.add(Card(Value.TWO, Suit.HEARTS))

        assertEquals(-1, rootService.gameService.calculateScore(player))
    }

    /** Tests that clearRow removes the bottom row when all 3 cards match and are flipped. */
    @Test
    fun testClearRowBottomRow() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.currentPlayer = 0
        val player = game.players[0]

        player.deck[3] = Card(Value.KING, Suit.HEARTS).apply { isFlipped = true }
        player.deck[4] = Card(Value.KING, Suit.SPADES).apply { isFlipped = true }
        player.deck[5] = Card(Value.KING, Suit.CLUBS).apply { isFlipped = true }

        rootService.gameService.clearRow()
        assertEquals(3, player.deck.size)
    }

    /** Tests that clearRow removes the top row when all 3 cards match and are flipped. */
    @Test
    fun testClearRowTopRow() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.currentPlayer = 0
        val player = game.players[0]

        player.deck[0] = Card(Value.ACE, Suit.HEARTS).apply { isFlipped = true }
        player.deck[1] = Card(Value.ACE, Suit.SPADES).apply { isFlipped = true }
        player.deck[2] = Card(Value.ACE, Suit.CLUBS).apply { isFlipped = true }

        rootService.gameService.clearRow()
        assertEquals(3, player.deck.size)
    }

    /** Tests that clearRow does not remove a row when cards are not flipped. */
    @Test
    fun testClearRowNotFlipped() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.currentPlayer = 0
        val player = game.players[0]

        player.deck[0] = Card(Value.KING, Suit.HEARTS)
        player.deck[1] = Card(Value.KING, Suit.SPADES)
        player.deck[2] = Card(Value.KING, Suit.CLUBS)

        rootService.gameService.clearRow()
        assertEquals(6, player.deck.size)
    }

    /** Tests that clearRow does not remove a row when values do not match. */
    @Test
    fun testClearRowNoMatch() {
        val game = rootService.currentGame
        checkNotNull(game)
        game.currentPlayer = 0
        val player = game.players[0]

        player.deck[0] = Card(Value.KING, Suit.HEARTS).apply { isFlipped = true }
        player.deck[1] = Card(Value.ACE, Suit.SPADES).apply { isFlipped = true }
        player.deck[2] = Card(Value.TWO, Suit.CLUBS).apply { isFlipped = true }

        rootService.gameService.clearRow()
        assertEquals(6, player.deck.size)
    }

    /** Tests that startLastRound sets lastRoundStarted and notifies refreshables. */
    @Test
    fun testStartLastRound() {
        val game = rootService.currentGame
        checkNotNull(game)

        assertFalse(game.lastRoundStarted)
        rootService.gameService.startLastRound()
        assertTrue(game.lastRoundStarted)
        assertTrue(testRefreshable.refreshAfterLastRoundStartedCalled)
    }

    /** Tests that nextPlayer triggers last round when current player reveals all cards. */
    @Test
    fun testNextPlayerTriggersLastRound() {
        val game = rootService.currentGame
        checkNotNull(game)

        game.currentPlayer = 0
        game.players.forEach { it.playedFirstRound = true }
        game.players[0].deck.forEach { it.isFlipped = true }

        assertFalse(game.lastRoundStarted)
        rootService.gameService.nextPlayer()
        assertTrue(game.lastRoundStarted)
    }

    /** Tests that nextPlayer ends the game when all players have played their last round. */
    @Test
    fun testNextPlayerEndsGame() {
        val game = rootService.currentGame
        checkNotNull(game)

        game.lastRoundStarted = true
        game.players.forEach { it.playedLastRound = true }
        game.players.forEach { it.playedFirstRound = true }
        game.players.forEach { player -> player.deck.forEach { it.isFlipped = true } }

        rootService.gameService.nextPlayer()
        assertTrue(testRefreshable.refreshAfterGameEndCalled)
    }

    /** Tests that startGame throws with fewer than 2 players. */
    @Test
    fun testStartGameTooFewPlayers() {
        assertThrows<IllegalArgumentException> {
            rootService.gameService.startGame(listOf("Alice"), isTestGame = false, isRandom = false)
        }
    }

    /** Tests that startGame throws with more than 4 players. */
    @Test
    fun testStartGameTooManyPlayers() {
        assertThrows<IllegalArgumentException> {
            rootService.gameService.startGame(
                listOf("A", "B", "C", "D", "E"), isTestGame = false, isRandom = false
            )
        }
    }

    /** Tests that startGame throws with duplicate player names. */
    @Test
    fun testStartGameDuplicateNames() {
        assertThrows<IllegalArgumentException> {
            rootService.gameService.startGame(
                listOf("Alice", "Alice"), isTestGame = false, isRandom = false
            )
        }
    }

    /** Tests that startGame throws with a blank player name. */
    @Test
    fun testStartGameBlankName() {
        assertThrows<IllegalArgumentException> {
            rootService.gameService.startGame(
                listOf("Alice", ""), isTestGame = false, isRandom = false
            )
        }
    }

    /** Tests that startGame works in test mode. */
    @Test
    fun testStartGameTestMode() {
        assertDoesNotThrow {
            rootService.gameService.startGame(
                listOf("Alice", "Bob"), isTestGame = true, isRandom = false
            )
        }
        assertTrue(rootService.currentGame!!.isTestGame)
    }

    /** Tests that startGame works with random order. */
    @Test
    fun testStartGameRandomOrder() {
        assertDoesNotThrow {
            rootService.gameService.startGame(
                listOf("Alice", "Bob", "Charlie", "Dave"), isTestGame = false, isRandom = true
            )
        }
        assertEquals(4, rootService.currentGame!!.players.size)
    }

    /** Tests that updateLog adds a message to the game log. */
    @Test
    fun testUpdateLog() {
        val game = rootService.currentGame
        checkNotNull(game)
        val player = game.players[0]
        val card = Card(Value.ACE, Suit.HEARTS)
        val msg = LogMessage(0, player, PlayerAction.FLIP_ONE, mutableListOf(card))

        val sizeBefore = game.logMessages.size
        rootService.gameService.updateLog(msg)
        assertEquals(sizeBefore + 1, game.logMessages.size)
    }















}