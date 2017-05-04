package models

import com.fasterxml.jackson.databind.JsonNode
import play.Logger
import play.libs.Json
import javax.inject.Inject

class QuizMaster @Inject constructor(val questionAsker: OpenTriviaQuestionAsker) {

    private val requestLogger = Logger.of("requestLogger")

    private val pingManager = PingManager(this)

    private val buzzerManager = BuzzerManager()

    private val handlers: Map<String, Handler> = mapOf(
            "nextQuestion" to NextQuestionHandler(this, questionAsker, buzzerManager),
            "answer" to AnswerQuestionHandler(this, questionAsker),
            "buzz" to BuzzerHandler(this, buzzerManager),
            "score" to ScoreHandler(this),
            "pong" to pingManager
    )

    val teamRoster = mutableMapOf<String, Team>()
    private var admin: Admin? = null

    init {
        pingManager.start()
    }

    fun join(teamName: String, out: JsonWebSocket) {
        requestLogger.info("Join:" + teamName)

        val theTeam = teamRoster.getOrPut(teamName, { Team(teamName, out) })
        theTeam.rebind(out)

        statusChange(theTeam.name, Team.Status.LIVE)
        theTeam.notify(Message.RegistrationResponse(theTeam))
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

    fun statusChange(teamName: String, status: Team.Status) {
        val team = teamRoster[teamName] ?: return
        team.status = status

        if(status == Team.Status.GONE) {
            teamRoster.remove(teamName)
        }

        requestLogger.info("$teamName is $status")
        notifyAdmin()
    }

    fun messageReceived(teamName: String, message: JsonNode) {
        requestLogger.info("Json:" + Json.stringify(message))

        val type = message.get("type").asText()

        val handler = handlers[type] ?: NullHandler()
        val team = teamRoster[teamName] ?: Team.nil()

        handler.handle(team, message)
        notifyAdmin()
    }

    fun eachTeam(callback: (Team) -> Unit) {
        teamRoster.values.forEach(callback)
    }

    fun notifyAllTeams(msg: Message) = eachTeam { it.notify(msg) }

    fun notifyTeam(team: Team, response: Message) {
        team.notify(response)
    }

    fun notifyAdmin() {
        admin?.notify(Message.TeamListResponse(teamRoster.values))
    }

    fun notifyAdmin(message: Message) {
        admin?.notify(message)
    }
}
