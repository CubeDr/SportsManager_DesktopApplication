package sportsmanager.components

import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.text.Font
import sportsmanager.Game
import tornadofx.*

class GameView(val game: Game): View() {
    // 게임의 상태(점수, 상황) 변경 알림 함수
    fun notifyGameChanged() {
        // 코트번호, 게임 번호 갱신
        lbTitle.text = "${game.court}코트 #${game.number}"
        // 스코어 갱신
        lbScores.forEachIndexed { index, label -> label.text = game.scores[index].toString() }
        // 상황 갱신
        for (i in 0..2) {
            lbSituations[i].isVisible = game.state and (1 shl i) != 0
        }
    }

    private val lbTitle = Label().apply { textFill = Color.WHITE }
    private val lbScores = listOf(scoreLabel(), scoreLabel())
    private val lbSituations = listOf(
        situationLabel("dodgerblue"),
        situationLabel("chartreuse"),
        situationLabel("blueviolet")
    )

    override val root: Parent = vbox {
        style="-fx-background-color: black; -fx-background-radius: 5 5 0 0;"
        prefWidth = 115.0
        prefHeight = 54.0
        paddingTop = 5.0

        hbox {
            alignment = Pos.CENTER
            add(lbTitle)
        }

        hbox {
            alignment = Pos.CENTER
            add(lbScores[0])
            add(scoreLabel(" : "))
            add(lbScores[1])
        }

        hbox {
            maxHeight = 3.0
            lbSituations.forEach { add(it) }
        }
    }

    init {
        notifyGameChanged()
    }

    private fun scoreLabel(text: String? = null) = Label(text).apply {
        textFill = Color.WHITE
        font = Font(28.0)
    }

    private fun situationLabel(color: String) = Label().apply {
        style="-fx-background-color: $color;"
        prefWidth = 115.0 / 3
    }

}