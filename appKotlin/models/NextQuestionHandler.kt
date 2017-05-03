package models

import com.fasterxml.jackson.databind.JsonNode

import java.util.Optional

class NextQuestionHandler(private val quizMaster: QuizMaster, private val asker: QuestionAsker, private val buzzerManager: BuzzerManager) : Handler {

    override fun handle(team: Team, message: JsonNode) {
        val question = asker.nextQuestion()
        buzzerManager.reset()
        quizMaster.teamRoster.values
                .stream()
                .forEach { t -> t.resetBuzzer() }

        quizMaster.notifyTeams(Optional.of<Any>(question))
        quizMaster.notifyAdmin()
    }
}