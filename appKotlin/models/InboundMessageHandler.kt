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
            "buzz" -> quizMaster.teamBuzzed(Event.Buzz(team))
            "nextQuestion" -> quizMaster.askNextQuestion()
            "answer" -> quizMaster.teamAnswered(Event.QuestionAnswered(team, message.get("questionNumber").asInt(), message.get("answer").asText()))

            else -> Logger.warn("Unexpected message type:" + message.toString())
        }
    }
}