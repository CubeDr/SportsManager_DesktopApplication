import javafx.scene.control.Tab
import tornadofx.*

class SportsManager : App(SportsManagerView::class)

class SportsManagerView: View("스포츠 대회 관리 시스템") {
    override val root = anchorpane {
        tabpane {
            fun newTab(v: UIComponent, op: Tab.() -> Unit = {}) = tab(v.title) { add(v) }.also(op)

            prefWidth = 600.0
            prefHeight = 400.0

            newTab(MainTab(this@tabpane))

            tab<NewTab> {
                setOnSelectionChanged {
                    newTab(MainTab(this@tabpane)) {
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