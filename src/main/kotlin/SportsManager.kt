import tornadofx.*

class SportsManager : App(SportsManagerView::class)

class SportsManagerView: View("스포츠 대회 관리 시스템") {
    override val root = anchorpane {
        tabpane {
            prefWidth = 600.0
            prefHeight = 400.0
            val tabPane = this

            tab<MainTab> {}
            tab<NewTab> {
                setOnSelectionChanged {
                    tab<MainTab> {
                        tabs.move(this, tabs.size-2)
                        select()
                    }
                }
            }

            selectionModel.select(0)
        }
    }
}

fun main(args: Array<String>) {
    launch<SportsManager>(args)
}