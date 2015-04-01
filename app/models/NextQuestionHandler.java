package models;

import models.questions.Question;
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
	public void handle(Team team, JsonNode message) {
		Question question = asker.nextQuestion();
		quizMaster.notifyTeams(Option.Some(question));
	}
}