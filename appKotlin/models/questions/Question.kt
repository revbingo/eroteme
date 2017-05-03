package models.questions

abstract class Question(val answerType: models.questions.Question.AnswerType, val questionNumber: Int, val question: String) {
    enum class AnswerType {
        SIMPLE, BUZZER
    }

    abstract fun checkAnswer(answer: String): Boolean
}