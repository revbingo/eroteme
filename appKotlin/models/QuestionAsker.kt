package models

import play.Logger
import java.util.*

class QuestionAsker {

    private val questions = ArrayList<Domain.Question>()
    private var questionCount = 0

    fun nextQuestion(): Domain.Question {
        Logger.debug("Next question!")
        questionCount++
        val question: Domain.Question = Domain.BuzzerQuestion(questionCount, "What time is it?")
        questions.add(question)
        return question
    }

    fun answer(questionNumber: Int, answer: String): Boolean {
        return questions[questionNumber - 1].checkAnswer(answer)
    }
}
