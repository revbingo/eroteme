package controllers;

import models.QuizMaster;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.index;
import views.html.quiz;

import javax.inject.Inject;
import java.util.Base64;

public class Application extends Controller {

	private final static String TEAM_COOKIE = "team";

	@Inject QuizMaster quizMaster;

    public Result index() {
    	if(request().cookie(TEAM_COOKIE) != null) {
    		return redirect("/quiz");
    	}
        return ok(index.render());
    }
    
    public Result signup() {
    	String teamName = request().body().asFormUrlEncoded().get("teamName")[0];
    	String hashedName = new String(Base64.getEncoder().encode(teamName.getBytes()));
    	response().setCookie(TEAM_COOKIE, hashedName);
    	quizMaster.join(teamName);
    	return redirect("/quiz");
    }
    
    public Result quiz() {
    	if(request().cookie(TEAM_COOKIE) == null) {
    		return redirect("/");
    	}
		String teamName = getTeamNameFromCookie(request().cookie(TEAM_COOKIE));
		return ok(quiz.render(teamName));
    }

	public Result logout() {
		quizMaster.leave(getTeamNameFromCookie(request().cookie(TEAM_COOKIE)));
    	response().discardCookie(TEAM_COOKIE);
    	return redirect("/");
    }

	private String getTeamNameFromCookie(Http.Cookie cookie) {
		return new String(Base64.getDecoder().decode(cookie.value().getBytes()));
	}

}
