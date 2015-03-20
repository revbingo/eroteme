package controllers;

import models.WSHandlerActor;
import play.mvc.Controller;
import play.mvc.WebSocket;

public class WSControl extends Controller {
	
	public static WebSocket<String> bind() {
		return WebSocket.withActor(WSHandlerActor::props);
	}
}
