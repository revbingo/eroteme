package models.questions;

import play.Logger;

public class SimpleQuestion extends Question {

	private String answer;
	private AnswerType answerType = AnswerType.SIMPLE;
	
	public SimpleQuestion(int questionNumber, String question, String answer) {
		super(questionNumber, question);
		this.answer = answer;
	}

	@Override
	public boolean checkAnswer(String answer) {
		Logger.debug("checking answer " + answer + " against " + this.answer);
		return answer.equalsIgnoreCase(this.answer);
	}

	public AnswerType getAnswerType() {
		return answerType;
	}
}
