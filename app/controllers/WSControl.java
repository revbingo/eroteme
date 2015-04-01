package controllers;

import models.JsonWebSocket;
import models.QuizMaster;
import play.mvc.Controller;
import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class WSControl extends Controller {
	
	private final static QuizMaster quizMaster = new QuizMaster();
	
	public static WebSocket<JsonNode> bind(String teamName) {
		return WebSocket.whenReady((in,out) -> {
			JsonWebSocket outSocket = new JsonWebSocket(out);
			
			quizMaster.join(teamName, outSocket);
			
			in.onClose(() -> {
				quizMaster.leave(teamName);
			});
			
			in.onMessage((json) -> {
				quizMaster.messageReceived(teamName, json);
			});
			
		});
	}
}