package models;

import java.time.LocalDateTime;

import models.QuizManager.NextQuestionRequest;
import models.questions.SimpleQuestion;
import play.Logger;
import play.libs.Json;

public class QuestionAsker {

	public void onReceive(Object nextQuestionRequest) {
		NextQuestionRequest request = (NextQuestionRequest) nextQuestionRequest;
		Logger.debug("Next question!");
		request.teams.forEach((name, out) -> {
			out.write(Json.toJson(new SimpleQuestion("What time is it? " + LocalDateTime.now().toString(), "now")));
		});
	}

}
