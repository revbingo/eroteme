package models;

import com.fasterxml.jackson.databind.JsonNode;
import models.Domain.QuestionAnswerResponse;

import java.util.Optional;

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
				team.scored(1);
			}
			quizMaster.notifyTeam(team, Optional.of(new QuestionAnswerResponse(correct, team.getScore())));
			quizMaster.notifyAdmin();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}