package models

import com.fasterxml.jackson.databind.JsonNode
import play.Logger
import play.libs.Json
import javax.inject.Inject

class InboundMessageHandler @Inject constructor(val quizMaster: QuizMaster, nextQuestionHandler: NextQuestionHandler, answerQuestionHandler: AnswerQuestionHandler,
                                                val buzzerManager: BuzzerManager, val pingManager: PingManager) {

    private val requestLogger = Logger.of("requestLogger")

    init {
        pingManager.start()
    }

    private val handlers: Map<String, Handler> = mapOf(
            "nextQuestion" to nextQuestionHandler,
            "answer" to answerQuestionHandler
    )

    fun messageReceived(teamName: String, message: JsonNode) {
        requestLogger.info("Json:" + Json.stringify(message))

        val type = message.get("type").asText()

        val handler = handlers[type] ?: NullHandler()
        val team = quizMaster.teamRoster[teamName] ?: Team.nil()

        when(type) {
            "pong" -> pingManager.pong(teamName)
            "score" -> {
                val teamThatScored = quizMaster.teamRoster[message.get("team").asText()] ?: return
                val delta = message.get("delta").asInt()
                val event = Event.Scored(teamThatScored, delta)
                quizMaster.teamScored(event)
            }
            "buzz" -> {
                quizMaster.teamBuzzed(Event.Buzz(team))
            }

            else -> handler.handle(team, message)
        }

        quizMaster.notifyAdmin()
    }
}

interface Handler {
    fun handle(team: Team?, message: JsonNode)
}

abstract class TeamSpecificHandler: Handler {
    override fun handle(team: Team?, message: JsonNode) {
        team ?: return
        handleTeamMessage(team, message)
    }

    abstract fun handleTeamMessage(team: Team, message: JsonNode)
}

abstract class AdminHandler: Handler {
    override fun handle(team: Team?, message: JsonNode) {
        handleAdminMessage(message)
    }

    abstract fun handleAdminMessage(message: JsonNode)
}

class NullHandler : Handler {

    override fun handle(team: Team?, message: JsonNode) {
        Logger.warn("Unexpected message type:" + message.toString())
    }
}

class NextQuestionHandler @Inject constructor(private val quizMaster: QuizMaster, private val asker: QuestionAsker, private val buzzerManager: BuzzerManager) : AdminHandler() {

    override fun handleAdminMessage(message: JsonNode) {
        val question = asker.nextQuestion()
        buzzerManager.reset()

        quizMaster.eachTeam { it.resetBuzzer() }

        quizMaster.notifyAllTeams(question)
        quizMaster.notifyAdmin(question)
    }
}

class AnswerQuestionHandler @Inject constructor(private val quizMaster: QuizMaster, private val asker: QuestionAsker) : TeamSpecificHandler() {

    override fun handleTeamMessage(team: Team, message: JsonNode) {
        val answerType = message.get("answerType").asText() ?: "text"
        when(answerType) {
            "text" -> {
                val correct = asker.answer(message.get("questionNumber").asInt(), message.get("answer").asText())
                if (correct) {
                    team.scored(1)
                }
                quizMaster.notifyTeam(team, Event.Scored(team, team.score))
                quizMaster.notifyAllTeams(Event.QuestionAnswered(team, correct))
            }
            "voice" -> {
                val correct = message.get("answerCorrect").asBoolean()
                if(correct) {
                    team.scored(1)
                    quizMaster.notifyTeam(team, Event.Scored(team, team.score))
                    quizMaster.reset()
                } else {
                    team.resetBuzzer()
                }
            }
        }
    }
}