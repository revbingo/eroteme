package models

import java.util.*

object TeamTracker {

    private val teams = HashMap<String, Int>()

    fun addTeam(teamName: String) {
        teams.put(teamName, 0)
    }

    val teamList: Set<String>
        get() = teams.keys
}
