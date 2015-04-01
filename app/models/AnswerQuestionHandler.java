package models;

import models.Domain.QuestionAnswerResponse;
import play.Logger;
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
	public Option<Object> handle(String teamName, JsonNode message) {
		boolean correct = false;
		try {
			correct = asker.answer(message.get("questionNumber").asInt(), message.get("answer").asText());
			Logger.debug(teamName + " got the answer " + correct);
			quizMaster.getTeamRoster().get(teamName).score();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return Option.Some(new QuestionAnswerResponse(correct));
	}
}