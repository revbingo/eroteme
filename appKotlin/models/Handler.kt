package models

import com.fasterxml.jackson.databind.JsonNode
import play.Logger
import java.util.*

interface Handler {
    fun handle(team: Team, message: JsonNode)
}

class NullHandler : Handler {

    override fun handle(team: Team, message: JsonNode) {
        Logger.warn("Unexpected message type:" + message.toString())
    }
}

class ScoreHandler(private val quizMaster: QuizMaster) : Handler {

    override fun handle(team: Team, message: JsonNode) {
        val teamThatScored = quizMaster.teamRoster.getOrDefault(message.get("team").asText(), Team.nil())
        val delta = message.get("delta").asInt()
        teamThatScored.scored(delta)
        quizMaster.notifyTeam(teamThatScored, Optional.of<Any>(Domain.Scored(teamThatScored.getScore())))
        quizMaster.notifyAdmin()
    }
}

class BuzzerHandler(private val quizMaster: QuizMaster, private val buzzerManager: BuzzerManager) : Handler {

    override fun handle(team: Team, message: JsonNode) {
        val responseOrder = this.buzzerManager.respond(team)
        team.buzzed(responseOrder)
        val ack = Domain.BuzzAck(team.name, responseOrder)
        quizMaster.notifyTeam(team, Optional.of<Any>(ack))
        quizMaster.notifyAdmin()
    }
}


class AnswerQuestionHandler(private val quizMaster: QuizMaster, private val asker: QuestionAsker) : Handler {

    override fun handle(team: Team, message: JsonNode) {
        var correct = false
        try {
            correct = asker.answer(message.get("questionNumber").asInt(), message.get("answer").asText())
            if (correct) {
                team.scored(1)
            }
            quizMaster.notifyTeam(team, Optional.of<Any>(Domain.QuestionAnswerResponse(correct, team.score)))
            quizMaster.notifyAdmin()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}