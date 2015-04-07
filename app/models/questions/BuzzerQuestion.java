package models.questions;

public class BuzzerQuestion extends Question {

	public BuzzerQuestion(int questionNumber, String question) {
		super(AnswerType.BUZZER, questionNumber, question);
	}

	@Override
	public boolean checkAnswer(String answer) {
		return false;
	}
}
