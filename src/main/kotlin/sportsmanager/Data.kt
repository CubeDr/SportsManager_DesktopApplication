package sportsmanager

import java.time.LocalDate

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
    var number: Int,
    var state: Int,
    var scores: MutableList<Int>,
    val competitionId: String
)