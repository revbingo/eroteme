package models

import com.fasterxml.jackson.databind.JsonNode
import play.Logger

interface Handler {
    fun handle(team: Team?, message: JsonNode)
}

class NullHandler : Handler {

    override fun handle(team: Team?, message: JsonNode) {
        Logger.warn("Unexpected message type:" + message.toString())
    }
}

class ScoreHandler(private val quizMaster: QuizMaster) : Handler {

    override fun handle(team: Team?, message: JsonNode) {
        val teamThatScored = quizMaster.teamRoster[message.get("team").asText()] ?: return
        val delta = message.get("delta").asInt()
        teamThatScored.scored(delta)
        quizMaster.notifyTeam(teamThatScored, Domain.Scored(teamThatScored.score))
        quizMaster.notifyAdmin()
    }
}

class BuzzerHandler(private val quizMaster: QuizMaster, private val buzzerManager: BuzzerManager) : Handler {

    override fun handle(team: Team?, message: JsonNode) {
        Logger.of("requestLogger").debug("Buzzed! Team ${team?.name}")
        team ?: return
        val responseOrder = this.buzzerManager.respond(team)
        team.buzzed(responseOrder)
        val ack = Domain.BuzzAck(team.name ?: "", responseOrder)
        quizMaster.notifyTeam(team, ack)
        quizMaster.notifyAdmin()
    }
}


class NextQuestionHandler(private val quizMaster: QuizMaster, private val asker: QuestionAsker, private val buzzerManager: BuzzerManager) : Handler {

    override fun handle(team: Team?, message: JsonNode) {
        val question = asker.nextQuestion()
        buzzerManager.reset()
        quizMaster.teamRoster.values
                .stream()
                .forEach { t -> t.resetBuzzer() }

        quizMaster.notifyTeams(question)
        quizMaster.notifyAdmin()
    }
}

class AnswerQuestionHandler(private val quizMaster: QuizMaster, private val asker: QuestionAsker) : Handler {

    override fun handle(team: Team?, message: JsonNode) {
        team ?: return
        var correct = false
        try {
            correct = asker.answer(message.get("questionNumber").asInt(), message.get("answer").asText())
            if (correct) {
                team.scored(1)
            }
            quizMaster.notifyTeam(team, Domain.QuestionAnswerResponse(correct, team.score))
            quizMaster.notifyAdmin()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}