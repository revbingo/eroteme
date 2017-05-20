package models

sealed class Event(val type: String) {

    abstract class TeamEvent(type: String, val team: Team): Event(type)

    class QuestionAnswered(team: Team, val questionNumber: Int, val response: String): TeamEvent("questionAnswered", team)

    class Registered(team: Team): TeamEvent("registered", team)

    class Removed: Event("removed")

    class QuizState(val teams: Collection<Team>): Event("quizState")

    class Buzz(team: Team): TeamEvent("buzz", team)

    class BuzzAck(team: Team, val responseOrder: Int): TeamEvent("buzzAck", team)

    class Scored(team: Team, val delta: Int): TeamEvent("scored", team)

    class Ping : Event("ping")

    class Reset : Event("reset")

    class AskQuestion(val answerType: AnswerType, val question: Question): Event("askQuestion")

    class AnswerConfirmation(team: Team, val correct: Boolean): TeamEvent("confirmation", team)

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