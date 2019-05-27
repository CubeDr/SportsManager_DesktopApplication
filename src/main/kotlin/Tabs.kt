import javafx.scene.Parent
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

        anchorpane {
            layoutX = 360.0
            prefWidth = 250.0
            prefHeight = 400.0

            text("경기 정보") {
                layoutY = 20.0
            }
            pane {
                layoutY = 40.0
                prefWidth = 230.0
                prefHeight = 250.0
                style="-fx-background-color: lightgray;"
            }
            button("대회 입장") {
                layoutY = 300.0
                prefWidth = 230.0
                isDisable = true
            }
            button("대회 개최") {
                layoutY = 340.0
                prefWidth = 230.0

                action {
                    newCompetitionDialog {
                        println(it)
                    }
                }
            }
        }
    }
}

class NewTab: View("+") {
    override val root: Parent = pane()
}