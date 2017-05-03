package models

import java.util.*

class BuzzerManager {

    private var respondedTeams: MutableList<Team> = ArrayList()

    fun reset() {
        this.respondedTeams = ArrayList<Team>()
    }

    fun getRespondedTeams(): List<Team> {
        return respondedTeams
    }

    @Synchronized fun respond(team: Team): Int {
        respondedTeams.add(team)
        return respondedTeams.size
    }
}
