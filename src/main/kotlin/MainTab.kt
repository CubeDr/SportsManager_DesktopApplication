import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.*
import javax.json.Json

class MainTab(tabPane: TabPane, op: Tab.() -> Unit = {}): View("대회 목록") {
    private val controller: MainTabController by inject()
    private val competition = mutableListOf<Competition>().asObservable()

    init {
        competition.addAll(controller.listCompetition())
    }

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
                            val id = controller.createCompetition(it)
                            val competition = it.copy(id = id)
                            CompetitionTab1(competition, tabPane)
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