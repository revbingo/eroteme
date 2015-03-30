package controllers;

import static play.libs.Json.toJson;
import models.JsonWebSocket;
import models.QuizManager;
import play.libs.F.Option;
import play.mvc.Controller;
import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class WSControl extends Controller {
	
	private final static QuizManager quizManager = new QuizManager();
	
	public static WebSocket<JsonNode> bind(String teamName) {
		return WebSocket.whenReady((in,out) -> {
			JsonWebSocket outSocket = new JsonWebSocket(out);
			
			in.onClose(() -> {
				quizManager.remove(teamName);
			});
			
			in.onMessage((json) -> {
				Option<Object> response = quizManager.messageReceived(teamName, json);
				writeOption(outSocket, response);
			});
			
			Option<Object> response = quizManager.join(teamName, outSocket);
			writeOption(outSocket, response);
		});
	}
	
	private static void writeOption(JsonWebSocket out, Option<Object> option) {
		if(!option.isEmpty()) {
			out.write(toJson(option.get()));
		}
	}
}