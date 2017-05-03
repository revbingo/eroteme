package models.questions

class SimpleQuestion(questionNumber: Int, question: String, private val answer: String) : models.questions.Question(models.questions.Question.AnswerType.SIMPLE, questionNumber, question) {

    override fun checkAnswer(answer: String): Boolean {
        play.Logger.debug("checking answer " + answer + " against " + this.answer)
        return answer.equals(this.answer, ignoreCase = true)
    }

}
