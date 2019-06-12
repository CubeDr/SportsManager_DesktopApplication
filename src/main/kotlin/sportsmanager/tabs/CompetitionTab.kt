package sportsmanager.tabs

import sportsmanager.Competition
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import sportsmanager.Game
import sportsmanager.components.GameView
import sportsmanager.toString
import tornadofx.*
import kotlin.random.Random

class CompetitionTab(
    private val competition: Competition,
    private val managable: Boolean
): Tab(competition.name) {
    private val gameList = mutableListOf<GameView>()

    private val gameStatus = gridpane {
        prefWidth = 375.0
        paddingAll = 5.0
        hgap = 5.0
        vgap = 5.0
    }
    private fun addGame(gameView: GameView) {
        val columnIndex = gameList.size % 3
        val rowIndex = gameList.size / 3
        gameList.add(gameView)
        gameStatus.add(gameView.root, columnIndex, rowIndex)
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
            prefHeight = if(managable) 200.0 else 340.0
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
                    text(competition.date.toString("yyyy년 M월 d일"))
                }
            }
        }
    }

    private val root = borderpane {
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
                if(managable) {
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
    }

    init {
        content = root

        repeat(30) {
            val state = Random.nextInt(8)
            addGame(GameView(Game(
                (it + 1).toString(),
                it,
                1,
                state,
                mutableListOf(Random.nextInt(25), Random.nextInt(25)),
                ""
            )))
        }
    }
}