package models;

import java.util.ArrayList;
import java.util.List;

import models.questions.Question;
import models.questions.SimpleQuestion;
import play.Logger;

public class QuestionAsker {

	private List<Question> questions = new ArrayList<Question>();
	private int questionCount = 0;
	
	public Question nextQuestion() {
		Logger.debug("Next question!");
		questionCount++;
		Question question = new SimpleQuestion(questionCount, "What time is it?", "now");
		questions.add(question);
		return question;
	}
	
	public boolean answer(int questionNumber, String answer) {
		return questions.get(questionNumber - 1).checkAnswer(answer);
	}
}
