package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.quiz;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }
    
    public static Result signup() {
    	String teamName = request().body().asFormUrlEncoded().get("teamName")[0];
    	session().put("teamName", teamName);
    	return redirect("/quiz");
    }
    
    public static Result quiz() {
    	return ok(quiz.render(session().get("teamName")));
    }
}
