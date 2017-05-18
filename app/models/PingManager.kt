package models

import play.inject.ApplicationLifecycle
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

class PingManager @Inject constructor(val quizMaster: QuizMaster, val lifecycle: ApplicationLifecycle) {

    val pingCount = mutableMapOf<Team, Int>()

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
            pingCount.compute(team, { _, count -> count?.inc() ?: 1 })
            team.notify(Event.Ping())
        }
    }

    fun checkTimeouts() {
        pingCount.filter { it.value > 2 }.forEach { team, _ ->
            quizMaster.statusChange(team.name, Team.Status.AWOL)
        }

        pingCount.filter { it.value > 10 }.forEach { team, _ ->
            play.Logger.info("Team ${team.name} have been removed - timeout")

            quizMaster.statusChange(team.name, Team.Status.GONE)
        }
    }

    fun pong(team: Team) {
        pingCount[team] = 0
    }
}