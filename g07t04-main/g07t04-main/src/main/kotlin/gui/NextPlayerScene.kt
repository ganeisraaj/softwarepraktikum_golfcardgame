package gui

import entity.Player
import service.Refreshable
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/** The scene shown between turns, displaying the next player's name. */
class NextPlayerScene : MenuScene(1920, 1080), Refreshable {

    /** Label displaying the next player's name. */
    private val playerNameLabel = Label(
        text = "",
        width = 700,
        height = 180,
        posX = 1920 / 2 - 700 / 2,
        posY = 1080 / 2 - 150,
        alignment = Alignment.CENTER,
        font = Font(60, Color(0xFFFFFF), "JetBrains Mono ExtraBold")
    )

    /** Button the next player clicks to continue. */
    val continueButton = Button(
        text = "CONTINUE",
        width = 400,
        height = 70,
        posX = 1920 / 2 - 200,
        posY = 1080 / 2 + 60,
        font = Font(26, Color(0xFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x2E7D32))
    )

    /** Sets up the background and adds all components directly — no Pane. */
    init {
        background = ColorVisual(Color(27, 94, 32, 240))
        addComponents(playerNameLabel, continueButton)
    }

    /** Updates the player name label when the next player's turn starts. */
    override fun refreshAfterNextPlayer(nextPlayer: Player) {
        playerNameLabel.text = nextPlayer.name
    }
}