package models

import controllers.forms.CreateQuizForm
import play.Logger
import play.inject.Injector
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizMaster @Inject constructor(val buzzerManager: BuzzerManager, val soundAllocator: SoundAllocator, val injector: Injector) {

    val teamRoster = mutableMapOf<String, Team>()
    private var admin: Admin? = null

    var answerType = Event.AnswerType.VOICE
    var firstAnswerScores = true
    var questionCount = 20
    var currentQuestionNumber = 0;

    var questionSource: QuestionSource = FreeQuestionSource()

    enum class QuizState {
        NOT_STARTED, READY, IN_PROGRESS, FINISHED
    }

    var quizState: QuizState = QuizState.NOT_STARTED

    fun startQuiz(quizConfig: CreateQuizForm) {
        firstAnswerScores = quizConfig.singleAnswer
        questionCount = quizConfig.questionCount
        currentQuestionNumber = 0

        questionSource = when (quizConfig.questionSource) {
            "byo" -> FreeQuestionSource()
            "opentrivia" -> injector.instanceOf(OpenTriviaQuestionSource::class.java)
            else -> FixedQuestionSource()
        }

        answerType = when (quizConfig.questionType) {
            "voice" -> Event.AnswerType.VOICE
            else -> Event.AnswerType.TEXT
        }
        quizState = QuizState.READY
    }

    fun join(teamName: String, out: JsonWebSocket) {
        Logger.info("Team $teamName joined")

        val theTeam = teamRoster.getOrPut(teamName, { Team(teamName, out).apply {
            sound = soundAllocator.allocateSound()
        } })
        theTeam.rebind(out)

        statusChange(theTeam.name, Team.Status.LIVE)
        theTeam.notify(Event.RegistrationResponse(theTeam))
        sendTeamStateToAdmin()
    }

    fun leave(teamName: String) {
        Logger.info("Team $teamName left or was removed")
        teamRoster.remove(teamName)
        sendTeamStateToAdmin()
    }

    fun registerAdmin(outSocket: JsonWebSocket) {
        Logger.info("Admin joined")
        admin?.destroy()
        admin = Admin(outSocket)
        sendTeamStateToAdmin()
    }

    fun deregisterAdmin() {
        Logger.info("Admin left")
        admin = null
    }

    fun statusChange(teamName: String, status: Team.Status) {
        val team = teamRoster[teamName] ?: return
        team.status = status

        Logger.info("$teamName is $status")
        sendTeamStateToAdmin()
    }

    fun reset() {
        eachTeam { it.resetBuzzer() }
        notifyAllTeams(Event.Reset())
        sendTeamStateToAdmin()
    }

    fun askNextQuestion() {
        if(quizState == QuizState.READY) quizState = QuizState.IN_PROGRESS
        currentQuestionNumber++;
        if(currentQuestionNumber > questionCount) {
            val endQuiz = Event.EndQuiz()
            quizState = QuizState.FINISHED
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
            val responseOrder = buzzerManager.respond(team)
            notifyAdmin(Event.BuzzAck(team, responseOrder))
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

    fun sendTeamStateToAdmin() = admin?.notify(Event.QuizState(teamRoster.values))

    fun notifyAdmin(event: Event) = admin?.notify(event)

}
