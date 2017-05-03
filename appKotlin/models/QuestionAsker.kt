package models

import models.questions.BuzzerQuestion
import models.questions.Question
import java.util.ArrayList

import play.Logger

class QuestionAsker {

    private val questions = ArrayList<Question>()
    private var questionCount = 0

    fun nextQuestion(): Question {
        Logger.debug("Next question!")
        questionCount++
        val question: Question = BuzzerQuestion(questionCount, "What time is it?")
        questions.add(question)
        return question
    }

    fun answer(questionNumber: Int, answer: String): Boolean {
        return questions[questionNumber - 1].checkAnswer(answer)
    }
}
