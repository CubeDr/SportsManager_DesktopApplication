package sportsmanager.tabs

import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TableView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import sportsmanager.*
import sportsmanager.dialogs.enterCompetitionDialog
import sportsmanager.dialogs.newCompetitionDialog
import tornadofx.*
import javax.json.Json

class MainTab(tabPane: TabPane, op: Tab.() -> Unit = {}): View("대회 목록") {
    private val controller: MainTabController by inject()
    private val competition = mutableListOf<Competition>().asObservable()
    private lateinit var competitionListView: TableView<Competition>
    private val polling = Repeat(
        operation = controller::listCompetition,
        postOperation = {
            val item = competitionListView.selectedItem
            competition.clear()
            competition.addAll(it)

            competitionListView.selectWhere { competition ->  competition == item }
        }
    )

    init {
        polling.start()
    }

    override val root = anchorpane {
        prefWidth = 600.0
        prefHeight = 400.0

        competitionListView = tableview(competition) {
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

                action {
                    competitionListView.selectedItem.notNull {
                        enterCompetitionDialog(it) { result, _ ->
                            if(!result) return@enterCompetitionDialog
                            tabPane.addToLast(CompetitionTab(it, false))
                        }
                    }
                }

                competitionListView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                    this.isDisable = newValue == null
                }
            }
            button("대회 개최") {
                layoutY = 340.0
                prefWidth = 230.0

                action {
                    newCompetitionDialog {
                        if(it != null) {
                            // get id from server
                            val id = controller.createCompetition(it)
                            val competition = it.copy(id = id)
                            tabPane.addToLast(CompetitionTab(competition, true))
                        }
                    }
                }
            }
        }
    }
}

class MainTabController: Controller() {
    private val api: Rest by inject()

    fun listCompetition() = api.get(SERVER_URL + COMPETITION).list().map {
        val jo = it.asJsonObject()
        Competition(
            jo.getString("_id"),
            jo.getString("name"),
            jo.getString("location"),
            jo.getDate("date"),
            jo.getString("password")
        )
    }

    fun createCompetition(competition: Competition): String {
        val jsonValue = Json.createObjectBuilder()
            .add("name", competition.name)
            .add("location", competition.location)
            .add("password", competition.pw)
            .build()
        val response = api.post(SERVER_URL + COMPETITION, jsonValue)
        if(response.statusCode != 200) return "-${response.statusCode}"
        return response.list()[0].asJsonObject().getString("_id")
    }
}