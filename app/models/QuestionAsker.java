package models;

import java.util.ArrayList;
import java.util.List;

import models.questions.BuzzerQuestion;
import models.questions.Question;
import play.Logger;

public class QuestionAsker {

	private List<Question> questions = new ArrayList<Question>();
	private int questionCount = 0;
	
	public Question nextQuestion() {
		Logger.debug("Next question!");
		questionCount++;
		Question question = new BuzzerQuestion(questionCount, "What time is it?");
		questions.add(question);
		return question;
	}
	
	public boolean answer(int questionNumber, String answer) {
		return questions.get(questionNumber - 1).checkAnswer(answer);
	}
}
