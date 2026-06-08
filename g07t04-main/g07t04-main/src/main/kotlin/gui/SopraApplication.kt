package gui

import entity.Player
import service.Refreshable
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

/** The main application class that initializes all scenes and the root service. */
class SopraApplication : BoardGameApplication("6 Card Golf"), Refreshable {

    /** The root service instance shared across all scenes. */
    private val rootService = RootService()

    /** The main game scene. */
    private val gameScene = GameScene(rootService)

    /** The main menu scene shown at startup. */
    private val mainMenuScene = MainMenuScene(rootService).apply {
        quitButton.onMouseClicked = {
            exit()
        }
    }

    /** The next player handoff scene shown between turns. */
    private val nextPlayerScene = NextPlayerScene().apply {
        continueButton.onMouseClicked = {
            this@SopraApplication.hideMenuScene()
        }
    }

    /** The end game scene shown when the game ends. */
    private val endGameScene = EndGameScene().apply {
        newGameButton.onMouseClicked = {
            showMenuScene(mainMenuScene)
        }
        quitButton.onMouseClicked = {
            exit()
        }
    }

    /** Sets up refreshables, shows the game scene and main menu on startup. */
    init {
        rootService.gameService.addRefreshable(this)
        rootService.gameService.addRefreshable(mainMenuScene)
        rootService.gameService.addRefreshable(gameScene)
        rootService.gameService.addRefreshable(nextPlayerScene)
        rootService.gameService.addRefreshable(endGameScene)

        rootService.playerActionService.addRefreshable(this)
        rootService.playerActionService.addRefreshable(mainMenuScene)
        rootService.playerActionService.addRefreshable(gameScene)
        rootService.playerActionService.addRefreshable(nextPlayerScene)
        rootService.playerActionService.addRefreshable(endGameScene)

        this.showGameScene(gameScene)
        this.showMenuScene(mainMenuScene)
    }

    /** Hides the menu scene after the game starts. */
    override fun refreshAfterStartGame(currentPlayer: Player) {
        hideMenuScene(500)
    }

    /** Shows the next player scene after the turn advances. */
    override fun refreshAfterNextPlayer(nextPlayer: Player) {
        showMenuScene(nextPlayerScene)
    }

    /** Shows the end game scene when the game ends. */
    override fun refreshAfterGameEnd(players: List<Player>) {
        showMenuScene(endGameScene)
    }
}