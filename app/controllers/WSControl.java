package controllers;

import static play.libs.Json.toJson;
import models.JsonWebSocket;
import models.QuizMaster;
import play.libs.F.Option;
import play.mvc.Controller;
import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class WSControl extends Controller {
	
	private final static QuizMaster quizMaster = new QuizMaster();
	
	public static WebSocket<JsonNode> bind(String teamName) {
		return WebSocket.whenReady((in,out) -> {
			JsonWebSocket outSocket = new JsonWebSocket(out);
			
			in.onClose(() -> {
				quizMaster.leave(teamName);
			});
			
			in.onMessage((json) -> {
				quizMaster.messageReceived(teamName, json);
			});
			
			Option<Object> response = quizMaster.join(teamName, outSocket);
			writeOption(outSocket, response);
		});
	}
	
	private static void writeOption(JsonWebSocket out, Option<Object> option) {
		if(!option.isEmpty()) {
			out.write(toJson(option.get()));
		}
	}
}