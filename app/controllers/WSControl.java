package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.JsonWebSocket;
import models.QuizMaster;
import play.Logger;
import play.mvc.Controller;
import play.mvc.LegacyWebSocket;
import play.mvc.WebSocket;

import javax.inject.Inject;

public class WSControl extends Controller {

	@Inject
	private QuizMaster quizMaster;

	@Inject
	private Logger logger;

	public LegacyWebSocket<JsonNode> bind(String teamName) {
		return WebSocket.whenReady((in,out) -> {
			JsonWebSocket outSocket = new JsonWebSocket(out);
			
			quizMaster.join(teamName, outSocket);
			
			in.onMessage((json) -> {
				try {
					quizMaster.messageReceived(teamName, json);
				} catch(Exception e) {
					logger.error(e.getMessage(), e);
				}
			});
			
		});
	}
	
	public LegacyWebSocket<JsonNode> bindAdmin() {
		return WebSocket.whenReady((in,out) -> {
			JsonWebSocket outSocket = new JsonWebSocket(out);
			
			quizMaster.registerAdmin(outSocket);
			
			in.onClose(() -> {
				quizMaster.deregisterAdmin();
			});
			
			in.onMessage((json) -> {
				try {
					quizMaster.messageReceived("admin", json);
				} catch(Exception e) {
					logger.error(e.getMessage(), e);
				}
			});
		});
	}
}