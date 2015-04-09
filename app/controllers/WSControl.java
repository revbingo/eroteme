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
			
			in.onMessage((json) -> {
				quizMaster.messageReceived(teamName, json);
			});
			
		});
	}
	
	public static WebSocket<JsonNode> bindAdmin() {
		return WebSocket.whenReady((in,out) -> {
			JsonWebSocket outSocket = new JsonWebSocket(out);
			
			quizMaster.registerAdmin(outSocket);
			
			in.onClose(() -> {
				quizMaster.deregisterAdmin();
			});
			
			in.onMessage((json) -> {
				quizMaster.messageReceived("admin", json);
			});
		});
	}
}