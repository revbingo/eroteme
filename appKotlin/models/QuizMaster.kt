package models

import com.fasterxml.jackson.databind.JsonNode
import play.Logger
import play.libs.Json
import java.util.*

typealias TeamRoster = HashMap<String, Team>

class QuizMaster {

    private val handlers: MutableMap<String, Handler>
    val teamRoster = TeamRoster()
    private var admin = Admin(null)

    private val requestLogger = Logger.of("requestLogger")

    init {
        handlers = HashMap<String, Handler>()
        handlers.put("nextQuestion", NextQuestionHandler(this, questionAsker, buzzerManager))
        handlers.put("answer", AnswerQuestionHandler(this, questionAsker))
        handlers.put("buzz", BuzzerHandler(this, buzzerManager))
        handlers.put("score", ScoreHandler(this))
    }

    fun join(teamName: String, out: JsonWebSocket) {
        requestLogger.info("Join:" + teamName)

        var theTeam: Team? = teamRoster[teamName]
        if (theTeam == null) {
            theTeam = Team(teamName, out)
        } else {
            theTeam.setOut(out)
        }

        teamRoster.put(teamName, theTeam)
        theTeam.notify(Optional.of<Any>(Domain.RegistrationResponse(theTeam)))
        requestLogger.debug("Roster has ${teamRoster.size} teams now")
        notifyAdmin()
    }

    fun registerAdmin(outSocket: JsonWebSocket) {
        requestLogger.info("Admin join")
        admin.destroy()
        admin = Admin(outSocket)
        notifyAdmin()
    }

    fun deregisterAdmin() {
        requestLogger.info("Admin leave")
        admin = Admin(null)
    }

    fun leave(teamName: String) {
        requestLogger.info("Leave:" + teamName)
        teamRoster.remove(teamName)
        notifyAdmin()
    }

    @Throws(Exception::class)
    fun messageReceived(teamName: String, message: JsonNode) {
        val jsonMessage = message
        requestLogger.info("Json:" + Json.stringify(jsonMessage))

        val type = jsonMessage.get("type").asText()
        val targetTeam = jsonMessage.get("team").asText()
        val handler = handlers[type] ?: NullHandler()
        requestLogger.debug("Using handler ${handler.javaClass.name} for type ${type} and team ${teamName}")

        requestLogger.debug("The current roster (${teamRoster.size}):")
        teamRoster.keys.forEach { println(it) }
        val team = teamRoster[targetTeam] ?: return
        requestLogger.debug("Found ${team} in roster")

        handler.handle(team, jsonMessage)
    }

    fun notifyTeam(team: Team, response: Optional<Any>) {
        if (response.isPresent) {
            team.notify(response)
        }
    }

    fun notifyTeams(obj: Optional<Any>) {
        teamRoster.forEach { name, team -> team.notify(obj) }
    }

    fun notifyAdmin() {
        admin.notify(Optional.of<Any>(Domain.TeamListResponse(teamRoster.values)))
    }

    fun notifyAdmin(obj: Optional<Any>) {
        admin.notify(obj)
    }

    companion object {

        private val questionAsker = QuestionAsker()
        private val buzzerManager = BuzzerManager()
    }
}
