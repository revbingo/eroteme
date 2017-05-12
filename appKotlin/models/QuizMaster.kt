package models

import play.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizMaster @Inject constructor(val buzzerManager: BuzzerManager) {

    private val requestLogger = Logger.of("requestLogger")

    val teamRoster = mutableMapOf<String, Team>()
    private var admin: Admin? = null

    fun join(teamName: String, out: JsonWebSocket) {
        requestLogger.info("Join:" + teamName)

        val theTeam = teamRoster.getOrPut(teamName, { Team(teamName, out) })
        theTeam.rebind(out)

        statusChange(theTeam.name, Team.Status.LIVE)
        theTeam.notify(Event.RegistrationResponse(theTeam))
        notifyAdmin()
    }

    fun registerAdmin(outSocket: JsonWebSocket) {
        requestLogger.info("Admin join")
        admin?.destroy()
        admin = Admin(outSocket)
        notifyAdmin()
    }

    fun deregisterAdmin() {
        requestLogger.info("Admin leave")
        admin = null
    }

    fun statusChange(teamName: String, status: Team.Status) {
        val team = teamRoster[teamName] ?: return
        team.status = status

        if (status == Team.Status.GONE) {
            teamRoster.remove(teamName)
        }

        requestLogger.info("$teamName is $status")
        notifyAdmin()
    }

    fun reset() {
        eachTeam { it.resetBuzzer() }
        notifyAllTeams(Event.Reset())
        notifyAdmin()
    }

    fun askNextQuestion(questionEvent: Event.Question) {
        buzzerManager.reset()

        eachTeam { it.resetBuzzer() }

        notifyAllTeams(questionEvent)
        notifyAdmin(questionEvent)
    }

    fun teamBuzzed(buzzEvent: Event.Buzz) {
        val team = buzzEvent.team
        val responseOrder = buzzerManager.respond(team)
        team.buzzed(responseOrder)
        val ack = Event.BuzzAck(team.name, responseOrder)
        notifyTeam(team, ack)
        notifyAdmin()
    }

    fun teamScored(scoreEvent: Event.Scored) {
        scoreEvent.team.scored(scoreEvent.delta)
        notifyTeam(scoreEvent.team, Event.Scored(scoreEvent.team, scoreEvent.delta))
        notifyAdmin()
    }

    fun teamAnswered(answerEvent: Event.QuestionAnswered) {
        with(answerEvent) {
            if (correct) {
                teamScored(Event.Scored(team, 1))
            }

            if(oneAnswerOnly) {
                if(correct) {
                    reset()
                } else  {
                    team.resetBuzzer()
                }
            }

            notifyAllTeams(answerEvent)
        }
        notifyAdmin()
    }

    fun eachTeam(callback: (Team) -> Unit) = teamRoster.values.forEach(callback)

    fun notifyAllTeams(msg: Event) = eachTeam { it.notify(msg) }

    fun notifyTeam(team: Team, event: Event) = team.notify(event)

    fun notifyAdmin() = admin?.notify(Event.TeamListResponse(teamRoster.values))

    fun notifyAdmin(event: Event) = admin?.notify(event)
}
