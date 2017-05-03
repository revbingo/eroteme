package models.questions

class BuzzerQuestion(questionNumber: Int, question: String) : Question(AnswerType.BUZZER, questionNumber, question) {

    override fun checkAnswer(answer: String): Boolean {
        return false
    }
}
