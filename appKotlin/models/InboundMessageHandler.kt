package models

import com.fasterxml.jackson.databind.JsonNode
import play.Logger
import play.libs.Json
import javax.inject.Inject

class InboundMessageHandler @Inject constructor(val quizMaster: QuizMaster, nextQuestionHandler: NextQuestionHandler, answerQuestionHandler: AnswerQuestionHandler,
                                                buzzerHandler: BuzzerHandler, scoreHandler: ScoreHandler, pingManager: PingManager) {

    private val requestLogger = Logger.of("requestLogger")

    init {
        pingManager.start()
    }

    private val handlers: Map<String, Handler> = mapOf(
            "nextQuestion" to nextQuestionHandler,
            "answer" to answerQuestionHandler,
            "correctAnswer" to answerQuestionHandler,
            "buzz" to buzzerHandler,
            "score" to scoreHandler,
            "pong" to pingManager
    )

    fun messageReceived(teamName: String, message: JsonNode) {
        requestLogger.info("Json:" + Json.stringify(message))

        val type = message.get("type").asText()

        val handler = handlers[type] ?: NullHandler()
        val team = quizMaster.teamRoster[teamName] ?: Team.nil()

        handler.handle(team, message)
        quizMaster.notifyAdmin()
    }
}