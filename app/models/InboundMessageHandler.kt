package models

import com.fasterxml.jackson.databind.JsonNode
import play.Logger
import play.libs.Json
import javax.inject.Inject

class InboundMessageHandler @Inject constructor(val quizMaster: QuizMaster, val pingManager: PingManager) {

    private val requestLogger = Logger.of("requestLogger")

    init {
        pingManager.start()
    }

    fun messageReceived(teamName: String, message: JsonNode) {
        val type = message.get("type").asText()

        if(type != "pong") {
            requestLogger.info("Json:" + Json.stringify(message))
        }

        val team = quizMaster.teamRoster[teamName] ?: Team.nil()

        when(type) {
            "pong" -> pingManager.pong(team)
            "score" -> {
                val teamThatScored = quizMaster.teamRoster[message.get("team").asText()] ?: return
                val delta = message.get("delta").asInt()
                val event = Event.Scored(teamThatScored, delta)
                quizMaster.teamScored(event)
            }
            "confirmation" -> quizMaster.answerConfirmed(Event.AnswerConfirmation(team, message.get("correct").asBoolean()))
            "buzz" -> quizMaster.teamBuzzed(Event.Buzz(team))
            "nextQuestion" -> quizMaster.askNextQuestion()
            "answer" -> quizMaster.teamAnswered(Event.QuestionAnswered(team, message.get("questionNumber").asInt(), message.get("answer").asText()))
            "removal" -> quizMaster.leave(teamName)
            else -> Logger.warn("Unexpected message type:" + message.toString())
        }
    }
}