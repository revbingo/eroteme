package models

import play.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizMaster @Inject constructor(val buzzerManager: BuzzerManager, val questionAsker: QuestionAsker) {

    private val requestLogger = Logger.of("requestLogger")

    val teamRoster = mutableMapOf<String, Team>()
    private var admin: Admin? = null

    fun join(teamName: String, out: JsonWebSocket) {
        requestLogger.info("Join:" + teamName)

        val theTeam = teamRoster.getOrPut(teamName, { Team(teamName, out) })
        theTeam.rebind(out)

        statusChange(theTeam.name, Team.Status.LIVE)
        theTeam.notify(Event.RegistrationResponse(theTeam))
        sendTeamStateToAdmin()
    }

    fun registerAdmin(outSocket: JsonWebSocket) {
        requestLogger.info("Admin join")
        admin?.destroy()
        admin = Admin(outSocket)
        sendTeamStateToAdmin()
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
        sendTeamStateToAdmin()
    }

    fun reset() {
        eachTeam { it.resetBuzzer() }
        notifyAllTeams(Event.Reset())
        sendTeamStateToAdmin()
    }

    fun askNextQuestion() {
        buzzerManager.reset()

        eachTeam { it.resetBuzzer() }

        val question = questionAsker.nextQuestion()

        notifyAllTeams(question)
        notifyAdmin(question)
    }

    fun teamBuzzed(buzzEvent: Event.Buzz) {
        val team = buzzEvent.team
        val responseOrder = buzzerManager.respond(team)
        team.buzzed(responseOrder)
        val ack = Event.BuzzAck(team.name, responseOrder)
        notifyTeam(team, ack)
        sendTeamStateToAdmin()
    }

    fun teamScored(scoreEvent: Event.Scored) {
        scoreEvent.team.scored(scoreEvent.delta)
        notifyTeam(scoreEvent.team, Event.Scored(scoreEvent.team, scoreEvent.delta))
        sendTeamStateToAdmin()
    }

    fun teamAnswered(answerEvent: Event.QuestionAnswered) {
        with(answerEvent) {
            val correct = questionAsker.answer(questionNumber, response)
            if (correct) {
                teamScored(Event.Scored(team, 1))
                if(oneAnswerOnly) {
                    reset()
                }
            } else {
                team.resetBuzzer()
            }

            notifyAllTeams(answerEvent)
        }
        sendTeamStateToAdmin()
    }

    fun eachTeam(callback: (Team) -> Unit) = teamRoster.values.forEach(callback)

    fun notifyAllTeams(msg: Event) = eachTeam { it.notify(msg) }

    fun notifyTeam(team: Team, event: Event) = team.notify(event)

    fun sendTeamStateToAdmin() = admin?.notify(Event.TeamListResponse(teamRoster.values))

    fun notifyAdmin(event: Event) = admin?.notify(event)
}
