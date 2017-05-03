package models

import com.fasterxml.jackson.databind.JsonNode

class PingManager(private val quizMaster: QuizMaster): Handler {

    val pingCount = mutableMapOf<String, Int>()

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
            quizMaster.awol(teamName)
        }

        pingCount.filter { it.value > 10 }.forEach { teamName, _ ->
            play.Logger.info("Team $teamName have been removed - timeout")

            pingCount.remove(teamName)
            quizMaster.leave(teamName)
        }
    }

    fun pong(teamName: String) {
        pingCount[teamName] = 0
    }

    override fun handle(team: Team?, message: JsonNode) {
        pong(team!!.name)
    }

}