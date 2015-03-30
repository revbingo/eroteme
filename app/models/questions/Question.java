package models.questions;

public abstract class Question {
	public enum AnswerType {
		SIMPLE
	}
	
	private String question;
	
	public Question(String question) {
		this.question = question;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public abstract boolean checkAnswer(String answer);
}
