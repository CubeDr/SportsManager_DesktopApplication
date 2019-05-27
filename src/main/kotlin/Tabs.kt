import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.*


class MainTab: View("대회 목록") {
    private val competition = listOf<Competition>().asObservable()

    override val root = anchorpane {
        prefWidth = 600.0
        prefHeight = 400.0

        tableview(competition) {
            prefWidth = 350.0
            prefHeight = 400.0

            readonlyColumn("대회명", Competition::name) {
                prefWidth = 100.0
            }
            readonlyColumn("장소",   Competition::location) {
                prefWidth = 150.0
                isSortable = false
            }
            readonlyColumn("일시",   Competition::date) {
                prefWidth = 100.0
            }
        }

        borderpane {
            layoutX = 350.0
            prefWidth = 250.0
            prefHeight = 400.0
            paddingAll = 30.0

            top = text {
                text="경기 정보"
            }
            bottom = vbox {
                style="-fx-background-color: yellow;"
                button("대회 입장") { hgrow = Priority.ALWAYS }
                button("대회 개최") { hgrow = Priority.ALWAYS }
            }
            center = pane {
                style="-fx-background-color: lightgray;"
            }
        }
    }
}

class NewTab: View("+") {
    override val root: Parent = pane()
}