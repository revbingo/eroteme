package models

import com.fasterxml.jackson.databind.JsonNode
import play.Logger
import play.libs.Json

class QuizMaster {

    private val requestLogger = Logger.of("requestLogger")

    private val handlers: Map<String, Handler> = mapOf(
            "nextQuestion" to NextQuestionHandler(this, questionAsker, buzzerManager),
            "answer" to AnswerQuestionHandler(this, questionAsker),
            "buzz" to BuzzerHandler(this, buzzerManager),
            "score" to ScoreHandler(this)
    )

    val teamRoster = mutableMapOf<String, Team>()
    private var admin: Admin? = null

    fun join(teamName: String, out: JsonWebSocket) {
        requestLogger.info("Join:" + teamName)

        val theTeam = teamRoster.getOrPut(teamName, { Team(teamName, out) })
        theTeam.rebind(out)

        theTeam.notify(Domain.RegistrationResponse(theTeam))
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

    fun leave(teamName: String) {
        requestLogger.info("Leave:" + teamName)
        teamRoster.remove(teamName)
        notifyAdmin()
    }

    fun messageReceived(teamName: String, message: JsonNode) {
        val jsonMessage = message
        requestLogger.info("Json:" + Json.stringify(jsonMessage))

        val type = jsonMessage.get("type").asText()

        val handler = handlers[type] ?: NullHandler()
        val team = teamRoster[teamName] ?: Team.nil()

        handler.handle(team, jsonMessage)
    }

    fun notifyTeam(team: Team, response: Domain) {
        team.notify(response)
    }

    fun notifyTeams(msg: Domain) {
        teamRoster.values.forEach { it.notify(msg) }
    }

    fun notifyAdmin() {
        admin?.notify(Domain.TeamListResponse(teamRoster.values))
    }

    fun notifyAdmin(msg: Domain) {
        admin?.notify(msg)
    }

    companion object {

        private val questionAsker = QuestionAsker()
        private val buzzerManager = BuzzerManager()
    }
}
