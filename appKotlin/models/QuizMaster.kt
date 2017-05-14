package models

import play.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizMaster @Inject constructor(val buzzerManager: BuzzerManager, val soundAllocator: SoundAllocator) {

    private val requestLogger = Logger.of("requestLogger")

    val teamRoster = mutableMapOf<String, Team>()
    private var admin: Admin? = null

    var answerType = Event.AnswerType.VOICE
    var firstAnswerScores = true
    var questionCount = 20
    var currentQuestionNumber = 0;

    var questionSource: QuestionSource = FreeQuestionSource()

    fun join(teamName: String, out: JsonWebSocket) {
        requestLogger.info("Join:" + teamName)

        val theTeam = teamRoster.getOrPut(teamName, { Team(teamName, out).apply {
            sound = soundAllocator.allocateSound()
        } })
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
        currentQuestionNumber++;
        if(currentQuestionNumber > questionCount) {
            val endQuiz = Event.EndQuiz()
            notifyAllTeams(endQuiz)
            notifyAdmin(endQuiz)
            return
        }

        buzzerManager.reset()

        eachTeam { it.resetBuzzer() }

        val question = questionSource.nextQuestion(currentQuestionNumber)

        val askQuestionEvent = Event.AskQuestion(answerType, question)
        notifyAllTeams(askQuestionEvent)
        notifyAdmin(askQuestionEvent)
    }

    fun teamBuzzed(buzzEvent: Event.Buzz) {
        val team = buzzEvent.team
        val responseOrder = buzzerManager.respond(team)
        val ack = Event.BuzzAck(team, responseOrder)
        notifyTeam(team, ack)
        notifyAdmin(ack)
        sendTeamStateToAdmin()
    }

    fun teamScored(scoreEvent: Event.Scored) {
        scoreEvent.team.scored(scoreEvent.delta)
        scoreEvent.team.latestResponse = null
        notifyTeam(scoreEvent.team, Event.Scored(scoreEvent.team, scoreEvent.delta))
        sendTeamStateToAdmin()
    }

    fun answerConfirmed(confirmation: Event.AnswerConfirmation) {
        with(confirmation) {
            if (correct) {
                teamScored(Event.Scored(team, 1))
                if(firstAnswerScores) {
                    eachTeam { it.latestResponse = null }
                    reset()
                }
            } else {
                team.latestResponse = null
                team.resetBuzzer()
            }
        }
        notifyTeam(confirmation.team, confirmation)
        sendTeamStateToAdmin()
    }

    fun teamAnswered(answerEvent: Event.QuestionAnswered) {
        with(answerEvent) {
            val correct = questionSource.answer(questionNumber, response)

            if(correct) {
                answerConfirmed(Event.AnswerConfirmation(team, correct = true))
            } else {
                team.latestResponse = response
                sendTeamStateToAdmin()
            }
        }
    }

    fun eachTeam(callback: (Team) -> Unit) = teamRoster.values.forEach(callback)

    fun notifyAllTeams(msg: Event) = eachTeam { it.notify(msg) }

    fun notifyTeam(team: Team, event: Event) = team.notify(event)

    fun sendTeamStateToAdmin() = admin?.notify(Event.TeamListResponse(teamRoster.values))

    fun notifyAdmin(event: Event) = admin?.notify(event)

}
