package models.questions;

public class SimpleQuestion extends Question {

	private String answer;
	
	public SimpleQuestion(String question, String answer) {
		super(question);
		this.answer = answer;
	}

	@Override
	public boolean checkAnswer(String answer) {
		return answer.equalsIgnoreCase(this.answer);
	}

}
