import tornadofx.*
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class CompetitionProperty {
    var name: String by property("")
    var location: String by property("")
    var date: LocalDate by property(LocalDate.now())
    var pw: String by property("")

    fun build() = Competition(name, location, Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()), pw)
}

fun UIComponent.newCompetitionDialog(action: (Competition?) -> Unit) {
    dialog("대회 개최") {
        val comp = CompetitionProperty()

        fun validate(): Boolean {
            comp.name = comp.name.trim()
            comp.location = comp.location.trim()
            return comp.name.isNotEmpty() && comp.location.isNotEmpty() && comp.pw.isNotEmpty()
        }

        field("대회명") {
            textfield { promptText = "대회명" }.apply {
                bind(comp.getProperty(CompetitionProperty::name))
            }
        }
        field("장소") {
            textfield { promptText = "장소" }.apply {
                bind(comp.getProperty(CompetitionProperty::location))
            }
        }
        field("일시") {
            datepicker().apply {
                bind(comp.getProperty(CompetitionProperty::date))
            }
        }
        field("비밀번호") {
            passwordfield().apply {
                bind(comp.getProperty(CompetitionProperty::pw))
            }
        }

        buttonbar {
            button("취소").action {
                close()
                action(null)
            }
            button("확인").action {
                if(validate()) {
                    close()
                    action(comp.build())
                } else {

                }
            }
        }
    }
}