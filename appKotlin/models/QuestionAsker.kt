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

abstract class QuestionAsker {

    private var questions = listOf<Message.Question>()
    private var questionCount = 0

    fun nextQuestion(): Message.Question {
        Logger.info("Getting next question")
        if(questions.isEmpty()) questions = loadQuestions()
        Logger.info("Next q is ${questions.get(questionCount).question}")
        return questions.get(questionCount++)
    }

    fun answer(questionNumber: Int, answer: String): Boolean {
        return questions[questionNumber - 1].checkAnswer(answer)
    }

    abstract fun loadQuestions(): List<Message.Question>
}

@Singleton
class OpenTriviaQuestionAsker @Inject constructor(val ws: WSClient): QuestionAsker() {

    override fun loadQuestions(): List<Message.Question> {
        Logger.info("Fetching questions from OpenTrivia")
        val request = ws.url("http://opentdb.com/api.php").setQueryParameter("amount", "50")
        val responsePromise: CompletionStage<OpenTriviaResponse> = request.get().thenApply<JsonNode>(WSResponse::asJson).thenApply { jsonNode ->
            Json.fromJson(jsonNode, OpenTriviaResponse::class.java)
        }

        val response = responsePromise.toCompletableFuture().get()
        return response.results.mapIndexed { index, otquestion ->
            Message.BuzzerQuestion(index, otquestion.question, otquestion.correct_answer)
        }
    }
}

@Singleton
class FixedQuestionAsker: QuestionAsker() {
    override fun loadQuestions(): List<Message.Question> {
       return listOf(Message.SimpleQuestion(0, "what's the first number?", "one"),
               Message.SimpleQuestion(0, "what's the second number?", "two"),
               Message.SimpleQuestion(0, "what's the third number?", "three"),
               Message.SimpleQuestion(0, "what's the fourth number?", "four"),
               Message.SimpleQuestion(0, "what's the fifth number?", "five"))
    }
}

data class OpenTriviaResponse(@JsonProperty("response_code") val response_code: Int, @JsonProperty("results") val results: List<OpenTriviaQuestion>)
data class OpenTriviaQuestion(@JsonProperty("category") val category: String, @JsonProperty("type") val type: String, @JsonProperty("difficulty") val difficulty: String, @JsonProperty("question") val question: String, @JsonProperty("correct_answer") val correct_answer: String)