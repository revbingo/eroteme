package models;

import models.questions.Question;
import play.libs.Json;
import play.libs.F.Option;

import com.fasterxml.jackson.databind.JsonNode;

public class NextQuestionHandler implements Handler {

	private QuestionAsker asker;
	private QuizMaster quizMaster;
	
	public NextQuestionHandler(QuizMaster quizMaster, QuestionAsker asker) {
		this.asker = asker;
		this.quizMaster = quizMaster;
	}
	
	@Override
	public Option<Object> handle(String teamName, JsonNode message) {
		Question question = asker.nextQuestion(quizMaster.getTeamRoster());
		quizMaster.getTeamRoster().forEach((name, team) -> {
			team.getOut().write(Json.toJson(question));
		});
		return Option.Some(question);
	}

}