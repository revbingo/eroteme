package controllers;

import models.MyWebSocketActor;
import play.mvc.Controller;
import play.mvc.WebSocket;

public class WSControl extends Controller {
	
	public static WebSocket<String> bind() {
		return WebSocket.withActor(MyWebSocketActor::props);
	}

}
