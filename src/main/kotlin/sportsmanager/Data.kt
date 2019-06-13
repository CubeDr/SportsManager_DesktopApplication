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
    private var _scores: ObservableList<Int>,
    val players: List<Player>,
    val competitionId: String
): Observable() {
    var number: Int
        get() = _number
        set(value) {
            _number = value
            notifyObservers()
        }
    var state: Int
        get() = _state
        set(value) {
            if(_state != value) {
                _state = value
                notifyObservers()
            }
        }
    val scores: List<Int>
        get() = _scores

    fun setScore(team: Int, score: Int) {
        while(_scores.size <= team) _scores.add(0)
        _scores[team] = score
        notifyObservers()
    }

    override fun notifyObservers() {
        setChanged()
        super.notifyObservers()
    }
}

data class Player(
    val id: String,
    val name: String,
    val birth: LocalDate
)