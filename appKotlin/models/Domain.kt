package models

sealed class Domain(val type: String) {

    class QuestionAnswerResponse(var correct: Boolean, var score: Int): Domain("answerResponse")

    class RegistrationResponse(var team: Team): Domain("registrationResponse") {
        val statusCode = 200
    }

    class ErrorResponse(var message: String): Domain("error")

    class TeamListResponse(var teams: Collection<Team>): Domain("teamList")

    class QuestionAdminResponse(var question: Question): Domain("currentQuestion")

    class BuzzAck(var teamName: String, var responseOrder: Int): Domain("buzzAck")

    class Scored(var score: Int): Domain("scored")

    abstract class Question(val answerType: AnswerType, val questionNumber: Int, val question: String): Domain("question") {
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