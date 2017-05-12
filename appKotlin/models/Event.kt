package models

sealed class Event(val type: String) {

    class QuestionAnswered(val team: Team, val correct: Boolean): Event("questionAnswered")

    class RegistrationResponse(val team: Team): Event("registrationResponse")

    class TeamListResponse(val teams: Collection<Team>): Event("teamList")

    class BuzzAck(val teamName: String, val responseOrder: Int): Event("buzzAck")

    class Scored(val score: Int): Event("scored")

    class Ping : Event("ping")

    class Reset : Event("reset")

    abstract class Question(val answerType: AnswerType, val questionNumber: Int, val question: String, val answer: String): Event("question") {
        enum class AnswerType {
            SIMPLE, BUZZER
        }

        abstract fun checkAnswer(answer: String): Boolean
    }

    class SimpleQuestion(questionNumber: Int, question: String, answer: String) : Question(Question.AnswerType.SIMPLE, questionNumber, question, answer) {

        override fun checkAnswer(answer: String): Boolean {
            play.Logger.debug("checking answer " + answer + " against " + this.answer)
            return answer.equals(this.answer, ignoreCase = true)
        }
    }

    class BuzzerQuestion(questionNumber: Int, question: String, answer: String) : Question(AnswerType.BUZZER, questionNumber, question, answer) {

        override fun checkAnswer(answer: String): Boolean {
            return false
        }
    }
}