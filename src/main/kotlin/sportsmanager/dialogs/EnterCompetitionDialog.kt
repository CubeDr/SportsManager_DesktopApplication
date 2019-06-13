package sportsmanager.dialogs

import javafx.scene.control.PasswordField
import javafx.scene.text.Font
import sportsmanager.Competition
import sportsmanager.toString
import tornadofx.*

fun UIComponent.enterCompetitionDialog(competition: Competition, listener: (result: Boolean, password: String?) -> Unit) {
    val pf: PasswordField = PasswordField().apply { promptText = "비밀번호" }

    dialog("대회 입장") {
        minWidth = 200.0

        label(competition.name) {
            font = Font(20.0)
        }
        label("장소: " + competition.location)
        label("일시: " + competition.date.toString("yyyy년 MM월 dd일"))
        label("입장하시겠습니까?") {
            paddingTop = 10
        }

        titledpane("고급") {
            add(pf)
            isExpanded = false
        }

        buttonbar {
            paddingTop = 10
            paddingBottom = 5
            button("취소").action {
                listener.invoke(false, null)
                close()
            }
            button("확인") {
                action {
                    val password = if(pf.text.isEmpty()) null else pf.text
                    listener.invoke(true, password)
                    close()
                }
            }
        }
    }
}