package sportsmanager

import javafx.beans.InvalidationListener
import javafx.collections.ObservableList
import java.time.LocalDate
import java.util.*

data class Competition(
    val id: String? = null,
    val name: String,
    val location: String,
    val date: LocalDate,
    val pw: String
)

data class Game(
    val id: String,
    val court: Int,
    private var _number: Int,
    private var _state: Int,
    private val _scores: MutableList<Int>,
    val players: List<Player>,
    val competitionId: String
): Observable() {
    val number: Int
        get() = _number

    val state: Int
        get() = _state

    val scores: List<Int>
        get() = _scores

    infix fun replaceTo(other: Game) {
        if(_number != other.number) setChanged()
        _number = other.number

        if(_state != other.state) setChanged()
        _state = other.state

        _scores.forEachIndexed { team, score ->
            if(score != other.scores[team]) {
                setChanged()
                return@forEachIndexed
            }
        }
        _scores.clear()
        _scores.addAll(other.scores)

        notifyObservers()
    }

}

data class Player(
    val id: String,
    val name: String,
    val birth: LocalDate
)