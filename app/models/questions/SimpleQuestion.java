package models.questions;

import play.Logger;

public class SimpleQuestion extends Question {

	private String answer;
	
	public SimpleQuestion(int questionNumber, String question, String answer) {
		super(AnswerType.SIMPLE, questionNumber, question);
		this.answer = answer;
	}

	@Override
	public boolean checkAnswer(String answer) {
		Logger.debug("checking answer " + answer + " against " + this.answer);
		return answer.equalsIgnoreCase(this.answer);
	}

}
