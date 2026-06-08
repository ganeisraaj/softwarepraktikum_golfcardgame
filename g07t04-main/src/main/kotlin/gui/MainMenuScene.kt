package gui

import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/** The main menu scene where players enter their names and start the game. */
class MainMenuScene(private val rootService: RootService) : MenuScene(1920, 1080), Refreshable {

    /** Centered pane that holds all UI components. */
    private val contentPane = Pane<UIComponent>(
        width = 700,
        height = 900,
        posX = 1920 / 2 - 700 / 2,
        posY = 1080 / 2 - 900 / 2,
        visual = ColorVisual(Color(0x1B5E20))
    )

    /** Title label showing the game name. */
    private val titleLabel = Label(
        text = "6 CARD GOLF",
        width = 700,
        height = 80,
        posX = 0,
        posY = 20,
        alignment = Alignment.CENTER,
        font = Font(50, Color(0xFFFFFF), "JetBrains Mono ExtraBold")
    )

    /** Subtitle label prompting name entry. */
    private val subtitleLabel = Label(
        text = "ENTER NAMES",
        width = 700,
        height = 50,
        posX = 0,
        posY = 100,
        alignment = Alignment.CENTER,
        font = Font(22, Color(0xFFFFFF), "JetBrains Mono ExtraBold")
    )

    /** Text field for the first player (always present). */
    private val playerDefaultInput = TextField(
        prompt = "Name",
        width = 575,
        height = 75,
        posX = 50,
        posY = 150,
        font = Font(26, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32)),
    )

    /** Remove button for the first player. */
    private val playerRemove = Button(
        text = "×",
        width = 75,
        height = 75,
        posX = 575,
        posY = 150,
        font = Font(35, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0xB71C1C))
    ).apply {
        onMouseClicked = { removePlayer(0) }
    }

    /** Button to add a new player row. */
    private val playerAdd = Button(
        text = "+ ADD PLAYER",
        width = 600,
        height = 60,
        posX = 50,
        posY = 275,
        font = Font(22, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    ).apply {
        onMouseClicked = { addPlayer() }
    }

    /** Lists holding all player input fields and their remove buttons. */
    private val playerInputs = mutableListOf(playerDefaultInput)
    private val playerRemoves = mutableListOf(playerRemove)

    /** Tracks whether random player order is enabled. */
    private var isRandom = false

    /** Tracks whether test mode is enabled. */
    private var isTest = false

    /** Toggle button for random player order. */
    private val randomButton = Button(
        text = "RANDOM ORDER: OFF",
        width = 600,
        height = 60,
        posX = 50,
        posY = 680,
        font = Font(20, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    ).apply {
        onMouseClicked = {
            isRandom = !isRandom
            text = if (isRandom) "RANDOM ORDER: ON" else "RANDOM ORDER: OFF"
        }
    }

    /** Toggle button for test mode. */
    private val testButton = Button(
        text = "TEST MODE: OFF",
        width = 600,
        height = 60,
        posX = 50,
        posY = 760,
        font = Font(20, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    ).apply {
        onMouseClicked = {
            isTest = !isTest
            text = if (isTest) "TEST MODE: ON" else "TEST MODE: OFF"
        }
    }

    /** Button to start the game. */
    private val startButton = Button(
        text = "START GAME",
        width = 280,
        height = 60,
        posX = 370,
        posY = 840,
        font = Font(22, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    ).apply {
        onMouseClicked = {
            val playerNames = playerInputs.filter { it.text.isNotBlank() }.map { it.text }
            if (playerNames.size in 2..4) {
                rootService.gameService.startGame(playerNames, isTestGame = isTest, isRandom = isRandom)
            }
        }
    }

    /** Button to quit the application. */
    val quitButton = Button(
        text = "QUIT GAME",
        width = 280,
        height = 60,
        posX = 50,
        posY = 840,
        font = Font(22, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0xB71C1C))
    )

    /** Sets up the background and adds all components. */
    init {
        background = ColorVisual(Color(27, 94, 32, 240))
        contentPane.addAll(
            titleLabel, subtitleLabel,
            playerDefaultInput, playerRemove,
            playerAdd,
            randomButton, testButton,
            startButton, quitButton
        )
        addComponents(contentPane)
    }

    /** Adds a new player input field and remove button below the existing rows. */
    private fun addPlayer() {
        if (playerInputs.size >= 4) return

        val currentI = playerInputs.size

        val newPlayerInput = TextField(
            prompt = "Name",
            width = 575,
            height = 75,
            posX = 50,
            posY = 150 + 100 * playerInputs.size,
            font = Font(26, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
            visual = ColorVisual(Color(0x2E7D32)),
        )

        val newPlayerRemove = Button(
            text = "×",
            width = 75,
            height = 75,
            posX = 575,
            posY = 150 + 100 * playerInputs.size,
            font = Font(35, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
            visual = ColorVisual(Color(0xB71C1C))
        ).apply {
            onMouseClicked = { removePlayer(currentI) }
        }

        contentPane.add(newPlayerInput)
        contentPane.add(newPlayerRemove)
        playerInputs.add(newPlayerInput)
        playerRemoves.add(newPlayerRemove)

        playerAdd.posY += 100
    }

    /**
     * Removes the player input field and remove button at the given index.
     *
     * @param index Index of the player row to remove.
     */
    private fun removePlayer(index: Int) {
        if (playerInputs.size > 1) {
            contentPane.remove(playerInputs[index])
            contentPane.remove(playerRemoves[index])
            playerInputs.removeAt(index)
            playerRemoves.removeAt(index)

            for (i in index until playerInputs.size) {
                playerInputs[i].posY -= 100
                playerRemoves[i].posY -= 100
                playerRemoves[i].onMouseClicked = { removePlayer(i) }
            }

            playerAdd.posY -= 100
        }
    }




}
