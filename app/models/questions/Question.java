package models.questions;

public abstract class Question {
	public enum AnswerType {
		SIMPLE, BUZZER
	}
	
	private String question;
	private int questionNumber;
	private AnswerType answerType;
	
	public Question(AnswerType type, int questionNumber, String question) {
		this.answerType = type;
		this.question = question;
		this.questionNumber = questionNumber;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public int getQuestionNumber() {
		return questionNumber;
	}
	
	public AnswerType getAnswerType() {
		return answerType;
	}
	 
	public abstract boolean checkAnswer(String answer);
}
