package models

import play.inject.ApplicationLifecycle
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

class PingManager @Inject constructor(val quizMaster: QuizMaster, val lifecycle: ApplicationLifecycle) {

    val pingCount = mutableMapOf<String, Int>()

    fun start() {
        val timer = fixedRateTimer("ping", daemon = true, period = 5000) {
            ping()
        }

        lifecycle.addStopHook {
            timer.cancel()
            CompletableFuture.completedFuture(null)
        }
    }

    fun ping() {
        checkTimeouts()

        quizMaster.eachTeam { team ->
            pingCount.compute(team.name, { _, count -> count?.inc() ?: 1 })
            team.notify(Event.Ping())
        }
    }

    fun checkTimeouts() {
        pingCount.filter { it.value > 2 }.forEach { teamName, _ ->
            quizMaster.statusChange(teamName, Team.Status.AWOL)
        }

        pingCount.filter { it.value > 10 }.forEach { teamName, _ ->
            play.Logger.info("Team $teamName have been removed - timeout")

            quizMaster.statusChange(teamName, Team.Status.GONE)
        }
    }

    fun pong(teamName: String) {
        pingCount[teamName] = 0
    }
}