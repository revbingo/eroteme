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

	@Inject
	QuizMaster quizMaster;

	@Inject
	WSClient wsClient;

	public Result index() {
		return ok(index.render());
	}

	public Result configure() {
		return ok(configure.render());
	}

	public Result create() {
		CreateQuizForm quizConfig = factory.form(CreateQuizForm.class).bindFromRequest().get();
		quizMaster.setFirstAnswerScores(quizConfig.getSingleAnswer());
		quizMaster.setQuestionCount(quizConfig.getQuestionCount());
		QuestionAsker source = null;
		if(quizConfig.getQuestionSource().equals("byo")) {
			source = new FreeQuestionAsker();
		} else if(quizConfig.getQuestionSource().equals("opentrivia")) {
			source = new OpenTriviaQuestionAsker(wsClient);
		} else {
			source = new FixedQuestionAsker();
		}
		quizMaster.setQuestionSource(source);
		return redirect("/admin");
	}
}
