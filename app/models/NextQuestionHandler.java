package models;

import models.questions.Question;
import play.libs.Json;
import play.libs.F.Option;

import com.fasterxml.jackson.databind.JsonNode;

public class NextQuestionHandler implements Handler {

	private QuestionAsker asker;
	private QuizManager manager;
	
	public NextQuestionHandler(QuizManager manager, QuestionAsker asker) {
		this.asker = asker;
		this.manager = manager;
	}
	
	@Override
	public Option<Object> handle(String teamName, JsonNode message) {
		Question question = asker.nextQuestion(manager.getTeamRoster());
		manager.getTeamRoster().forEach((name, team) -> {
			team.getOut().write(Json.toJson(question));
		});
		return Option.Some(question);
	}

}