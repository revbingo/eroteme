package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }
    
    public static Result signup() {
    	String teamName = request().body().asFormUrlEncoded().get("teamName")[0];
    	return ok(signup.render(teamName));
    }
}
