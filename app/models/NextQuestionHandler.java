package models;

import models.questions.Question;
import play.libs.F.Option;

import com.fasterxml.jackson.databind.JsonNode;

public class NextQuestionHandler implements Handler {

	private QuestionAsker asker;
	private QuizMaster quizMaster;
	private BuzzerManager buzzerManager; 
	
	public NextQuestionHandler(QuizMaster quizMaster, QuestionAsker asker, BuzzerManager buzzerManager) {
		this.asker = asker;
		this.quizMaster = quizMaster;
		this.buzzerManager = buzzerManager;
	}
	
	@Override
	public void handle(Team team, JsonNode message) {
		Question question = asker.nextQuestion();
		buzzerManager.reset();
		quizMaster.notifyTeams(Option.Some(question));
	}
}