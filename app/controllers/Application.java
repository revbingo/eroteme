package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.quiz;

import java.util.Base64;

public class Application extends Controller {

	private final static String TEAM_COOKIE = "team";
	
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
    	return redirect("/quiz");
    }
    
    public Result quiz() {
    	if(request().cookie(TEAM_COOKIE) == null) {
    		return redirect("/");
    	}
    	String teamName = new String(Base64.getDecoder().decode(request().cookie(TEAM_COOKIE).value().getBytes()));
    	return ok(quiz.render(teamName));
    }
    
    public Result logout() {
    	response().discardCookie(TEAM_COOKIE);
    	return redirect("/");
    }
}
