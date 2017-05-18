package models

sealed class Event(val type: String) {

    class QuestionAnswered(val team: Team, val questionNumber: Int, val response: String): Event("questionAnswered")

    class Registered(val team: Team): Event("registered")

    class QuizState(val teams: Collection<Team>): Event("quizState")

    class Buzz(val team: Team)

    class BuzzAck(val team: Team, val responseOrder: Int): Event("buzzAck")

    class Scored(val team: Team, val delta: Int): Event("scored")

    class Ping : Event("ping")

    class Reset : Event("reset")

    class AskQuestion(val answerType: AnswerType, val question: Question): Event("askQuestion")

    class AnswerConfirmation(val team: Team, val correct: Boolean): Event("confirmation")

    class EndQuiz: Event("endQuiz")

    abstract class Question(val questionNumber: Int, val question: String, val answer: String): Event("question") {
        abstract fun checkAnswer(answer: String): Boolean
    }

    class SimpleQuestion(questionNumber: Int, question: String, answer: String) : Question(questionNumber, question, answer) {

        override fun checkAnswer(answer: String): Boolean {
            play.Logger.debug("checking answer " + answer + " against " + this.answer)
            return answer.equals(this.answer, ignoreCase = true)
        }
    }

    class FreeQuestion(questionNumber: Int) : Question(questionNumber, "", "") {

        override fun checkAnswer(answer: String): Boolean {
            return answer.toBoolean()
        }
    }

    enum class AnswerType {
        TEXT, VOICE
    }
}