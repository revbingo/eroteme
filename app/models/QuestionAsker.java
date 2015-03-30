package models;

import java.time.LocalDateTime;

import models.questions.SimpleQuestion;
import play.Logger;
import play.libs.Json;

public class QuestionAsker {

	public void nextQuestion(TeamRoster teams) {
		Logger.debug("Next question!");
		teams.forEach((name, out) -> {
			out.write(Json.toJson(new SimpleQuestion("What time is it? " + LocalDateTime.now().toString(), "now")));
		});
	}

}
