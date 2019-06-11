package sportsmanager

import java.time.LocalDate

data class Competition(
    val id: String? = null,
    val name: String,
    val location: String,
    val date: LocalDate,
    val pw: String
)