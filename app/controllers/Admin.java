package controllers;

import controllers.forms.CreateQuizForm;
import models.QuizMaster;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.configure;
import views.html.admin.index;

import javax.inject.Inject;

public class Admin extends Controller {

	@Inject FormFactory factory;

	@Inject QuizMaster quizMaster;

	public Result index() {
		if(quizMaster.getQuizState() == QuizMaster.QuizState.NOT_STARTED) {
			return redirect("/newQuiz");
		}
		return ok(index.render());
	}

	public Result configure() {
		return ok(configure.render());
	}

	public Result create() {
		CreateQuizForm quizConfig = factory.form(CreateQuizForm.class).bindFromRequest().get();
		quizMaster.startQuiz(quizConfig);
		return redirect("/admin");
	}
}
