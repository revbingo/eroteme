package models.questions;

public abstract class Question {
	public enum AnswerType {
		SIMPLE
	}
	
	private String question;
	private int questionNumber;
	
	public Question(int questionNumber, String question) {
		this.question = question;
		this.questionNumber = questionNumber;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public int getQuestionNumber() {
		return questionNumber;
	}
	
	public abstract boolean checkAnswer(String answer);
}
