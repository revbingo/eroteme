package models

import com.fasterxml.jackson.databind.JsonNode
import play.Logger
import play.libs.Json
import javax.inject.Inject

class InboundMessageHandler @Inject constructor(val quizMaster: QuizMaster,
                                                val pingManager: PingManager, val asker: QuestionAsker) {

    private val requestLogger = Logger.of("requestLogger")

    init {
        pingManager.start()
    }

    fun messageReceived(teamName: String, message: JsonNode) {
        requestLogger.info("Json:" + Json.stringify(message))

        val type = message.get("type").asText()

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
            "nextQuestion" -> {
                val question = asker.nextQuestion()
                quizMaster.askNextQuestion(question)

            }
            "answer" -> {
                val answerType = message.get("answerType").asText() ?: "text"
                when(answerType) {
                    "text" -> {
                        val correct = asker.answer(message.get("questionNumber").asInt(), message.get("answer").asText())
                        quizMaster.teamAnswered(Event.QuestionAnswered(team, correct, false))

                    }
                    "voice" -> {
                        val correct = message.get("answerCorrect").asBoolean()
                        quizMaster.teamAnswered(Event.QuestionAnswered(team, correct, true))
                    }
                }
            }

            else -> Logger.warn("Unexpected message type:" + message.toString())
        }
    }
}