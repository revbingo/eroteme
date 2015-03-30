package controllers;

import static play.libs.Json.toJson;
import models.JsonWebSocket;
import models.QuizManager;
import models.QuizManager.JoinRequest;
import play.libs.F.Option;
import play.mvc.Controller;
import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class WSControl extends Controller {
	
	private final static QuizManager quizManager = new QuizManager();
	
	public static WebSocket<JsonNode> bind(String teamName) {
		return WebSocket.whenReady((in,out) -> {
			JsonWebSocket outSocket = new JsonWebSocket(out);
			in.onMessage((json) -> {
				Option<Object> response = quizManager.messageReceived(json);
				writeOption(outSocket, response);
			});
			
			Option<Object> response =  registerWithQuizManager(teamName, outSocket);
			writeOption(outSocket, response);
		});
	}
	
	private static Option<Object> registerWithQuizManager(String teamName, JsonWebSocket out) throws Exception {
		return quizManager.join(new JoinRequest(teamName, out));
	}
	
	private static void writeOption(JsonWebSocket out, Option<Object> option) {
		if(!option.isEmpty()) {
			out.write(toJson(option.get()));
		}
	}
}