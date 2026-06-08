package gui

import entity.Player
import service.Refreshable
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual


/** The end game scene showing final scores and the winner. */
class EndGameScene : MenuScene(1920, 1080), Refreshable {

    /** Centered pane that holds all UI components. */
    private val contentPane = Pane<UIComponent>(
        width = 700,
        height = 600,
        posX = 1920 / 2 - 700 / 2,
        posY = 1080 / 2 - 600 / 2,
        visual = ColorVisual(Color(0x1B5E20))
    )

    /** Title label at the top of the pane. */
    private val titleLabel = Label(
        text = "GAME OVER",
        width = 700,
        height = 80,
        posX = 0,
        posY = 20,
        alignment = Alignment.CENTER,
        font = Font(40, Color(0xFFFFFF), "JetBrains Mono ExtraBold")
    )

    /** Label showing the winner's name with a crown. */
    private val winnerLabel = Label(
        text = "",
        width = 600,
        height = 60,
        posX = 50,
        posY = 110,
        alignment = Alignment.CENTER,
        font = Font(26, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    )

    /** Four score labels — one per player row. */
    private val scoreLabels: List<Label> = List(4) { i ->
        Label(
            text = "",
            width = 600,
            height = 50,
            posX = 50,
            posY = 190 + i * 60,
            alignment = Alignment.CENTER_LEFT,
            font = Font(22, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
            visual = ColorVisual(Color(0x2E7D32))
        )
    }

    /** Button to start a new game — goes back to main menu. */
    val newGameButton = Button(
        text = "NEW GAME",
        width = 280,
        height = 60,
        posX = 50,
        posY = 510,
        font = Font(22, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    )

    /** Button to quit the application. */
    val quitButton = Button(
        text = "QUIT",
        width = 280,
        height = 60,
        posX = 370,
        posY = 510,
        font = Font(22, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0xB71C1C))
    )

    /** Sets up the background and adds all components to the content pane. */
    init {
        background = ColorVisual(Color(27, 94, 32, 240))
        contentPane.addAll(titleLabel, winnerLabel)
        scoreLabels.forEach { contentPane.add(it) }
        contentPane.addAll(newGameButton, quitButton)
        addComponents(contentPane)
    }

    /**
     * Updates the scene with final scores after the game ends.
     *
     * @param players All players with their final scores, sorted by score ascending.
     */
    override fun refreshAfterGameEnd(players: List<Player>) {
        // Sort by score ascending — lowest score wins
        val sorted = players.sortedBy { it.currentScore }

        // Show winner with crown
        winnerLabel.text = "★  ${sorted[0].name}  WINS  ★"

        // Show all scores
        scoreLabels.forEachIndexed { i, label ->
            if (i < sorted.size) {
                label.text = "${sorted[i].name}:  ${sorted[i].currentScore} pts"
            } else {
                label.text = ""
            }
        }
    }
}
