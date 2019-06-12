package sportsmanager.tabs

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import javafx.collections.MapChangeListener
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import sportsmanager.*
import sportsmanager.components.GameView
import tornadofx.*

class CompetitionTab(
    competition: Competition,
    managable: Boolean
): Tab(competition.name) {
    private val view = CompetitionView(competition, managable)

    init {
        content = view.root
        setOnClosed {
            view.stop()
        }
    }
}

class CompetitionView(competition: Competition, managable: Boolean): View() {
    private val controller: CompetitionTabController by inject()

    private val gameViewMap = mutableMapOf<String, GameView>()

    private val gameStatus = gridpane {
        prefWidth = 375.0
        paddingAll = 5.0
        hgap = 5.0
        vgap = 5.0
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

    override val root = borderpane {
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
        controller.gameMap.addListener { change: MapChangeListener.Change<out String, out Game> ->
            println("Changed")
            if(change.wasAdded()) {
                addGame(change.valueAdded)
            } else if(change.wasRemoved()) {
                // TODO 연결이 끊겼을 때 처리
            }
        }
        competition.id?.let { controller.startPollingGames(it) }
    }

    fun stop() {
        controller.stopPolling()
    }

    private fun addGame(game: Game) {
        val gameView = GameView(game)
        val columnIndex = gameViewMap.size % 3
        val rowIndex = gameViewMap.size / 3
        gameViewMap[game.id] = gameView
        gameStatus.add(
            gameView.root,
            columnIndex,
            rowIndex)
    }
}

class CompetitionTabController: Controller() {
    internal val gameMap = mutableMapOf<String, Game>().asObservable()
    private val api: Rest by inject()
    private var competitionId: String? = null

    private val polling = Repeat(
        operation = ::listGames,
        postOperation = { list ->
            list.forEach {
                gameMap[it.id] = it
            }
        }
    )

    fun startPollingGames(competitionId: String) {
        this.competitionId = competitionId
        polling.start()
    }

    fun stopPolling() {
        polling.stop()
    }

    private fun listGames(): List<Game> {
        return api.get("$SERVER_URL$GAME/list/$competitionId").list().map {
            println(it.toString())
            with(it.asJsonObject()) {
                Game(
                    getString("_id"),
                    getInt("court"),
                    getInt("number"),
                    getInt("state"),
                    mutableListOf(0, 0),
                    getString("competition_id")
                )
            }
        }
    }
}