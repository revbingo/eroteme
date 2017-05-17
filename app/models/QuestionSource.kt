package models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import play.Logger
import play.libs.Json
import play.libs.ws.WSClient
import play.libs.ws.WSResponse
import java.util.concurrent.CompletionStage
import javax.inject.Inject
import javax.inject.Singleton

abstract class QuestionSource {

    var questions: List<Event.Question> = emptyList()

    abstract fun nextQuestion(questionNumber: Int): Event.Question

    open fun answer(questionNumber: Int, answer: String): Boolean {
        return questions[questionNumber].checkAnswer(answer)
    }
}

data class OpenTriviaResponse(@JsonProperty("response_code") val response_code: Int, @JsonProperty("results") val results: List<OpenTriviaQuestion>)
data class OpenTriviaQuestion(@JsonProperty("category") val category: String, @JsonProperty("type") val type: String, @JsonProperty("difficulty") val difficulty: String, @JsonProperty("question") val question: String, @JsonProperty("correct_answer") val correct_answer: String)

class OpenTriviaQuestionSource @Inject constructor(val ws: WSClient): QuestionSource() {

    private fun loadQuestions(): List<Event.Question> {
        Logger.info("Fetching questions from OpenTrivia")
        val request = ws.url("http://opentdb.com/api.php").setQueryParameter("amount", "50")
        val responsePromise: CompletionStage<OpenTriviaResponse> = request.get().thenApply<JsonNode>(WSResponse::asJson).thenApply { jsonNode ->
            Json.fromJson(jsonNode, OpenTriviaResponse::class.java)
        }

        val response = responsePromise.toCompletableFuture().get()
        return response.results.mapIndexed { index, otquestion ->
            Event.SimpleQuestion(index, otquestion.question, otquestion.correct_answer)
        }
    }

    override fun nextQuestion(questionNumber: Int): Event.Question {
        Logger.info("Getting next question")
        if(questions.isEmpty()) questions = loadQuestions()
        return questions.get(questionNumber)
    }
}

class FixedQuestionSource : QuestionSource() {

    fun loadQuestions(): List<Event.Question> {
       return listOf(Event.SimpleQuestion(1, "what's the first number?", "one"),
               Event.SimpleQuestion(2, "what's the second number?", "two"),
               Event.SimpleQuestion(3, "what's the third number?", "three"),
               Event.SimpleQuestion(4, "what's the fourth number?", "four"),
               Event.SimpleQuestion(5, "what's the fifth number?", "five"))
    }

    override fun nextQuestion(questionNumber: Int): Event.Question {
        Logger.info("Getting next question")
        if(questions.isEmpty()) questions = loadQuestions()
        return questions.get(questionNumber)
    }
}

@Singleton
class FreeQuestionSource : QuestionSource() {

    override fun nextQuestion(questionNumber: Int): Event.Question {
        return Event.FreeQuestion(questionNumber);
    }

    override fun answer(questionNumber: Int, answer: String): Boolean {
        return answer.toBoolean()
    }
}