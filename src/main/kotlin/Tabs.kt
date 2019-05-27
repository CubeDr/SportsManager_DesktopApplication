import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.*
import java.text.SimpleDateFormat
import kotlin.random.Random

class MainTab(tabPane: TabPane, op: Tab.() -> Unit = {}): View("대회 목록") {
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
                        if(it != null) {
                            // get id from server
                            val id = Random.nextInt(123456, 789012)
                            val competition = it.copy(id = id)
                            CompetitionTab1(competition, tabPane)
                        }
                    }
                }
            }
        }
    }
}

class CompetitionTab1(
    private val competition: Competition,
    private val tabPane: TabPane,
    private val op: Tab.() -> Unit = {}
): View(competition.name) {

    private val gameStatus = gridpane {

    }
    private val competitionInfoWindow = anchorpane {
        text("대회 정보") {
            layoutY = 10.0
        }
        scrollpane {
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            layoutX = 0.0
            layoutY = 13.0
            prefWidth = 200.0
            prefHeight = 200.0
            style = "-fx-background-color: transparent; -fx-background: lightgray"

            vbox(10) {
                paddingAll = 5.0

                hbox(5) {
                    text("대회명:")
                    text(competition.name)
                }
                hbox(5) {
                    text("장소:")
                    text(competition.location)
                }
                hbox(5) {
                    text("일시:")
                    text(SimpleDateFormat("yyyy년 M월 d일").format(competition.date))
                }
            }
        }
    }

    override val root: Parent = borderpane {
        paddingAll = 10.0
        center {
            borderpane {
                top {
                    text("경기장 상황")
                }
                center {
                    scrollpane {
                        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                        vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED

                        add(gameStatus)
                    }
                }
            }
        }
        right {
            borderpane {
                prefWidth = 200.0
                paddingBottom = 10.0

                center {
                    // competition info
                    add(competitionInfoWindow)
                }
                bottom {
                    flowpane {
                        prefHeight = 120.0
                        alignment = Pos.BOTTOM_CENTER

                        val qrCodeWriter = QRCodeWriter()
                        val bitMatrix = qrCodeWriter.encode(competition.id.toString(), BarcodeFormat.QR_CODE, 100, 100)
                        val bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix)
                        imageview(SwingFXUtils.toFXImage(bufferedImage, null)) {
                            style = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);"
                        }
                    }
                }
            }
        }
    }

    init {
        tabPane.tab(title) { add(root) }.also {
            tabPane.tabs.move(it, tabPane.tabs.size-2)
            it.select()
            op
        }
    }
}

class NewTab: View("+") {
    override val root: Parent = pane()
}