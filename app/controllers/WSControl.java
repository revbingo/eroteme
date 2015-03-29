package controllers;

import models.QuizManager;
import play.mvc.Controller;
import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class WSControl extends Controller {
	
	public static WebSocket<JsonNode> bind(String teamName) {
		return WebSocket.whenReady((in,out) -> {
			QuizManager.join(teamName, in, out);
		});
	}
}
