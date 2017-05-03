package models

sealed class Message(val type: String) {

    class QuestionAnswerResponse(val correct: Boolean, val score: Int): Message("answerResponse")

    class RegistrationResponse(val team: Team): Message("registrationResponse")

    class TeamListResponse(val teams: Collection<Team>): Message("teamList")

    class BuzzAck(val teamName: String, val responseOrder: Int): Message("buzzAck")

    class Scored(val score: Int): Message("scored")

    class ErrorResponse(val message: String): Message("error")

    class QuestionAdminResponse(val question: Question): Message("currentQuestion")

    abstract class Question(val answerType: AnswerType, val questionNumber: Int, val question: String): Message("question") {
        enum class AnswerType {
            SIMPLE, BUZZER
        }

        abstract fun checkAnswer(answer: String): Boolean
    }

    class SimpleQuestion(questionNumber: Int, question: String, private val answer: String) : Question(Question.AnswerType.SIMPLE, questionNumber, question) {

        override fun checkAnswer(answer: String): Boolean {
            play.Logger.debug("checking answer " + answer + " against " + this.answer)
            return answer.equals(this.answer, ignoreCase = true)
        }
    }

    class BuzzerQuestion(questionNumber: Int, question: String) : Question(AnswerType.BUZZER, questionNumber, question) {

        override fun checkAnswer(answer: String): Boolean {
            return false
        }
    }


}