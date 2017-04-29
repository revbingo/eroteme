package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.JsonWebSocket;
import models.QuizMaster;
import play.mvc.Controller;
import play.mvc.WebSocket;

import javax.inject.Inject;

public class WSControl extends Controller {

	@Inject
	private QuizMaster quizMaster;
	
	public WebSocket<JsonNode> bind(String teamName) {
		return WebSocket.whenReady((in,out) -> {
			JsonWebSocket outSocket = new JsonWebSocket(out);
			
			quizMaster.join(teamName, outSocket);
			
			in.onMessage((json) -> {
				quizMaster.messageReceived(teamName, json);
			});
			
		});
	}
	
	public WebSocket<JsonNode> bindAdmin() {
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