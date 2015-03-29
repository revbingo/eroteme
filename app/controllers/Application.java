package controllers;

import java.util.Base64;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.quiz;

public class Application extends Controller {

	private final static String TEAM_COOKIE = "team";
	
    public static Result index() {
    	response().discardCookie(TEAM_COOKIE);
        return ok(index.render());
    }
    
    public static Result signup() {
    	String teamName = request().body().asFormUrlEncoded().get("teamName")[0];
    	String hashedName = new String(Base64.getEncoder().encode(teamName.getBytes()));
    	response().setCookie(TEAM_COOKIE, hashedName);
    	return redirect("/quiz");
    }
    
    public static Result quiz() {
    	if(request().cookie(TEAM_COOKIE) == null) {
    		return redirect("/");
    	}
    	String teamName = new String(Base64.getDecoder().decode(request().cookie(TEAM_COOKIE).value().getBytes()));
    	return ok(quiz.render(teamName));
    }
}
