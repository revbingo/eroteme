package controllers;

import models.*;
import play.data.FormFactory;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.configure;
import views.html.admin.index;

import javax.inject.Inject;

public class Admin extends Controller {

	@Inject FormFactory factory;

	@Inject QuizMaster quizMaster;

	@Inject WSClient wsClient;

	public Result index() {
		return ok(index.render());
	}

	public Result configure() {
		return ok(configure.render());
	}

	public Result create() {
		CreateQuizForm quizConfig = factory.form(CreateQuizForm.class).bindFromRequest().get();
		quizMaster.setFirstAnswerScores(quizConfig.getSingleAnswer());
		quizMaster.setQuestionCount(quizConfig.getQuestionCount().intValue());
		quizMaster.setCurrentQuestionNumber(0);

		QuestionSource source;
		switch(quizConfig.getQuestionSource()) {
			case "byo":
				source = new FreeQuestionSource();
				break;
			case "opentrivia":
				source = new OpenTriviaQuestionSource(wsClient);
				break;
			default:
				source = new FixedQuestionSource();
 		}
		quizMaster.setQuestionSource(source);

		Event.AnswerType answerType = null;
		switch(quizConfig.getQuestionType()) {
			case "text":
				answerType = Event.AnswerType.TEXT;
				break;
			case "voice":
				answerType = Event.AnswerType.VOICE;
				break;
		}
		quizMaster.setAnswerType(answerType);
		return redirect("/admin");
	}
}
