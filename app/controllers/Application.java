package controllers;

import models.QuizMaster;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.quiz;

import javax.inject.Inject;

public class Application extends Controller {

	private final static String TEAM_COOKIE = "team";

	@Inject private QuizMaster quizMaster;

    public Result index() {
		String teamName = session().get(TEAM_COOKIE);
		if(teamName != null) return redirect("/quiz");
        return ok(index.render());
    }
    
    public Result signup() {
    	String teamName = request().body().asFormUrlEncoded().get("teamName")[0];
		session().put(TEAM_COOKIE, teamName);
    	quizMaster.join(teamName);
    	return redirect("/quiz");
    }
    
    public Result quiz() {
		String teamName = session().get(TEAM_COOKIE);
		if(teamName == null) return redirect("/");
		return ok(quiz.render(teamName));
    }

	public Result logout() {
		String teamName = session().get(TEAM_COOKIE);
		if(teamName != null) {
			quizMaster.leave(teamName);
			session().remove(TEAM_COOKIE);
		}
    	return redirect("/");
    }

}
