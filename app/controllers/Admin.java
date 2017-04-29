package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import views.html.admin.*;

public class Admin extends Controller {
	
	public Result index() {
		return ok(index.render());
	}
}
