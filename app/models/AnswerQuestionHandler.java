package models;

import models.Domain.QuestionAnswerResponse;
import play.libs.F.Option;

import com.fasterxml.jackson.databind.JsonNode;

public class AnswerQuestionHandler implements Handler {

	private QuestionAsker asker;
	private QuizMaster quizMaster;
	
	public AnswerQuestionHandler(QuizMaster quizMaster, QuestionAsker asker) {
		this.asker = asker;
		this.quizMaster = quizMaster;
	}
	
	@Override
	public void handle(Team team, JsonNode message) {
		boolean correct = false;
		try {
			correct = asker.answer(message.get("questionNumber").asInt(), message.get("answer").asText());
			if(correct) {
				team.score();
			}
			quizMaster.notifyTeam(team, Option.Some(new QuestionAnswerResponse(correct)));
			quizMaster.notifyAdmin();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}