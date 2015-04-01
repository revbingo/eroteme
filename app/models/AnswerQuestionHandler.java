package models;

import models.Domain.QuestionAnswerResponse;
import play.Logger;
import play.libs.F.Option;

import com.fasterxml.jackson.databind.JsonNode;

public class AnswerQuestionHandler implements Handler {

	private QuestionAsker asker;
	private QuizManager manager;
	
	public AnswerQuestionHandler(QuizManager manager, QuestionAsker asker) {
		this.asker = asker;
		this.manager = manager;
	}
	
	@Override
	public Option<Object> handle(String teamName, JsonNode message) {
		boolean correct = false;
		try {
			correct = asker.answer(message.get("questionNumber").asInt(), message.get("answer").asText());
			Logger.debug(teamName + " got the answer " + correct);
			manager.getTeamRoster().get(teamName).score();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return Option.Some(new QuestionAnswerResponse(correct));
	}
}