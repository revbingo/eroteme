package models

import models.questions.Question

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
}