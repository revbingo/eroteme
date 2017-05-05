package models

import com.fasterxml.jackson.databind.JsonNode
import play.Logger
import javax.inject.Inject

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

class ScoreHandler @Inject constructor(private val quizMaster: QuizMaster) : AdminHandler() {

    override fun handleAdminMessage(message: JsonNode) {
        val teamThatScored = quizMaster.teamRoster[message.get("team").asText()] ?: return
        val delta = message.get("delta").asInt()
        teamThatScored.scored(delta)
        quizMaster.notifyTeam(teamThatScored, Message.Scored(teamThatScored.score))
    }
}

class BuzzerHandler @Inject constructor(private val quizMaster: QuizMaster, private val buzzerManager: BuzzerManager) : TeamSpecificHandler() {

    override fun handleTeamMessage(team: Team, message: JsonNode) {
        val responseOrder = this.buzzerManager.respond(team)
        team.buzzed(responseOrder)
        val ack = Message.BuzzAck(team.name, responseOrder)
        quizMaster.notifyTeam(team, ack)
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
        val correct = asker.answer(message.get("questionNumber").asInt(), message.get("answer").asText())
        if (correct) {
            team.scored(1)
        }
        quizMaster.notifyTeam(team, Message.QuestionAnswerResponse(correct, team.score))
    }
}