package models

import com.fasterxml.jackson.databind.JsonNode
import kotlin.concurrent.fixedRateTimer

class PingManager(private val quizMaster: QuizMaster): TeamSpecificHandler() {

    val pingCount = mutableMapOf<String, Int>()

    fun start() {
        fixedRateTimer("ping", daemon = false, period = 5000) {
            ping()
        }
    }

    fun ping() {
        checkTimeouts()
        quizMaster.notifyAdmin()

        quizMaster.eachTeam { team ->
            pingCount.compute(team.name, { _, count -> count?.inc() ?: 1 })
            team.notify(Message.Ping())
        }
    }

    fun checkTimeouts() {
        pingCount.filter { it.value > 2 }.forEach { teamName, _ ->
            quizMaster.statusChange(teamName, Team.Status.AWOL)
        }

        pingCount.filter { it.value > 10 }.forEach { teamName, _ ->
            play.Logger.info("Team $teamName have been removed - timeout")

            pingCount.remove(teamName)
            quizMaster.statusChange(teamName, Team.Status.GONE)
        }
    }

    fun pong(teamName: String) {
        pingCount[teamName] = 0
    }

    override fun handleTeamMessage(team: Team, message: JsonNode) {
        pong(team.name)
    }

}