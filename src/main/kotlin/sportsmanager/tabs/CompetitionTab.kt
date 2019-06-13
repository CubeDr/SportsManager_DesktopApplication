package sportsmanager.tabs

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import javafx.collections.ListChangeListener
import javafx.collections.MapChangeListener
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import sportsmanager.*
import sportsmanager.components.GameView
import tornadofx.*
import java.util.*
import javax.json.Json
import javax.json.JsonValue

class CompetitionTab(
    competition: Competition,
    managable: Boolean
): Tab((if(managable) "*" else "") + competition.name) {
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

    private val competitionView = borderpane {
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

    private var selectedGame: Game? = null
    private val gameViewTitle = label(text = "1코트 1경기", color = Color.WHITE, size = 50.0)
    private val gameViewScores = listOf(
        label(text = "0", color = Color.WHITE, size = 60.0),
        label(text = "0", color = Color.WHITE, size = 60.0)
    )
    private val gameViewPlayers = listOf(
        label(text = "김현이", color = Color.WHITE, size = 30.0).apply { prefWidth = 100.0 },
        label(text = "채상욱", color = Color.WHITE, size = 30.0).apply { prefWidth = 100.0 },
        label(text = "김정모", color = Color.WHITE, size = 30.0).apply { prefWidth = 100.0 },
        label(text = "네프", color = Color.WHITE, size = 30.0).apply { prefWidth = 100.0 }
    )
    private val situationColor = listOf("dodgerblue", "chartreuse", "blueviolet")
    private val gameViewSituations = listOf(
        situationButton("셔틀콕 부족") {
            if(!managable) return@situationButton
            controller.toggleSituation(selectedGame?:return@situationButton, 0)
        },
        situationButton("선수 없음") {
            if(!managable) return@situationButton
            controller.toggleSituation(selectedGame?:return@situationButton, 1)
        },
        situationButton("선수 부상") {
            if(!managable) return@situationButton
            controller.toggleSituation(selectedGame?:return@situationButton, 2)
        }
    )
    private val gameView = anchorpane {
        style = "-fx-background-color: #0009;"
        topAnchor(0.0)
        bottomAnchor(0.0)
        leftAnchor(0.0)
        rightAnchor(0.0)


        vbox {
            alignment = Pos.TOP_CENTER
            style = "-fx-background-color: black; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0, 0, 0);"
            topAnchor(60.0)
            bottomAnchor(60.0)
            leftAnchor(50.0)
            rightAnchor(50.0)
            paddingTop = 5.0
            spacing = 20.0

            add(gameViewTitle)

            hbox {
                alignment = Pos.CENTER
                spacing = 50.0
                if(managable) vbox {
                    spacing = 20.0
                    button("▲") {
                        isFocusTraversable = false
                        action {
                            controller.leftTeamScoreUp(selectedGame?:return@action)
                        }
                    }
                    button("▼") {
                        isFocusTraversable = false
                        action {
                            controller.leftTeamScoreDown(selectedGame?:return@action)
                        }
                    }
                }
                hbox {
                    spacing = 20.0
                    add(gameViewScores[0])
                    add(label(":", Color.WHITE, 60.0))
                    add(gameViewScores[1])
                }
                if(managable) vbox {
                    spacing = 20.0
                    button("▲") {
                        isFocusTraversable = false
                        action {
                            controller.rightTeamScoreUp(selectedGame?:return@action)
                        }
                    }
                    button("▼") {
                        isFocusTraversable = false
                        action {
                            controller.rightTeamScoreDown(selectedGame?:return@action)
                        }
                    }
                }
            }

            hbox {
                alignment = Pos.CENTER
                spacing = 30.0
                hbox {
                    alignment = Pos.CENTER
                    spacing = 10.0
                    add(gameViewPlayers[0])
                    add(gameViewPlayers[1])
                }
                hbox {
                    alignment = Pos.CENTER
                    spacing = 10.0
                    add(gameViewPlayers[2])
                    add(gameViewPlayers[3])
                }
            }

            hbox {
                gameViewSituations.forEach { add(it) }
            }
        } // vbox

        button("X") {
            style = "-fx-background-color: black; -fx-border-radius: 50"
            textFill = Color.WHITE
            topAnchor(60.0)
            rightAnchor(50.0)
            action {
                closeGameView()
            }
        } // button
    }

    override val root = anchorpane {
        add(competitionView)
        add(gameView)
    }

    init {
        closeGameView()

        // 탭이 생성될 시점에 로드되어있던 게임 추가
        controller.gameMap.values
            .filter { it.competitionId == competition.id }
            .forEach { addGame(it) }

        // 탭이 생성된 후에 로드되는 게임 추가
        controller.gameMap.addListener { change: MapChangeListener.Change<out String, out Game> ->
            if(change.wasAdded()) {
                val game = change.valueAdded
                if(game.competitionId == competition.id)
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

    private val onGameChangedListener = Observer { _, _ ->
        val game = selectedGame?:return@Observer
        gameViewTitle.text = "${game.court}코트 ${game.number}경기"
        gameViewScores.forEachIndexed { index, label -> label.text = game.scores[index].toString() }
        gameViewSituations.forEachIndexed { index, button ->
            val color = if(game.state and (1 shl index) > 0) situationColor[index] else "gray"
            button.style = "-fx-background-color: $color; -fx-border-radius: 10;"
        }
    }

    private fun selectGame(game: Game) {
        selectedGame = game
        game.addObserver(onGameChangedListener)
        onGameChangedListener.update(null, null)
        gameView.isVisible = true

    }

    private fun closeGameView() {
        selectedGame?.deleteObserver(onGameChangedListener)
        selectedGame = null
        gameView.isVisible = false
    }

    private fun addGame(game: Game) {
        val gameView = GameView(game)
        val columnIndex = gameViewMap.size % 3
        val rowIndex = gameViewMap.size / 3
        gameViewMap[game.id] = gameView
        gameStatus.add(
            gameView,
            columnIndex,
            rowIndex
        )
        gameView.action {
            selectGame(game)
        }
    }

    private fun situationButton(text: String, onClick: Button.() -> Unit = {}) = Button(text).apply {
        font = Font(20.0)
        maxWidth = Double.MAX_VALUE
        hgrow = Priority.ALWAYS
        alignment = Pos.CENTER
        textFill = Color.WHITE
        isFocusTraversable = false
        action {
            this.onClick()
        }
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
                gameMap.getOrPut(it.id) { it } replaceTo it
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

    fun leftTeamScoreUp(game: Game) {
        val teamA = teamScore(game, 0, 1)
        updateGame(game.id, teamA)
    }
    fun leftTeamScoreDown(game: Game) {
        val teamA = teamScore(game, 0, -1)
        updateGame(game.id, teamA)
    }
    fun rightTeamScoreUp(game: Game) {
        val teamB = teamScore(game, 1, 1)
        updateGame(game.id, teamB)
    }
    fun rightTeamScoreDown(game: Game) {
        val teamB = teamScore(game, 1, -1)
        updateGame(game.id, teamB)
    }
    fun toggleSituation(game: Game, situationCode: Int) {
        val situation = game.state xor (1 shl situationCode)
        val situationObj = Json.createObjectBuilder()
            .add("state", situation)
            .build()
        updateGame(game.id, situationObj)
    }

    private fun teamScore(game: Game, team: Int, scoreDelta: Int) = Json.createObjectBuilder()
        .add(if(team == 0) "team_A" else "team_B",
            Json.createObjectBuilder()
                .add("score", game.scores[team] + scoreDelta)
                .build()
        )
        .build()
    private fun updateGame(id: String, value: JsonValue) {
        val response = api.put("$SERVER_URL$GAME/$id", value)
        if(response.statusCode != 200) {
            println("ERROR: Failed to update game(${response.statusCode})")
            println("\t${response.reason}")
        }
    }

    private fun listGames(): List<Game> {
        return api.get("$SERVER_URL$GAME/list/$competitionId").list().map {
            with(it.asJsonObject()) {
                val teamA = getJsonObject("team_A")
                val teamB = getJsonObject("team_B")
                val scores = mutableListOf(teamA.getInt("score"), teamB.getInt("score"))
                val players = mutableListOf<Player>()

                Game(
                    getString("_id"),
                    getInt("court"),
                    getInt("number"),
                    getInt("state"),
                    scores,
                    players,
                    getString("competition_id")
                )
            }
        }
    }
}