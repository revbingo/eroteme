package models;

import java.time.LocalDateTime;
import java.util.Map;

import play.Logger;
import play.libs.F.Option;
import play.libs.Json;
import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class NextQuestionHandler implements Handler {

	private Map<String, WebSocket.Out<JsonNode>> teams;
	
	public NextQuestionHandler(Map<String, WebSocket.Out<JsonNode>> teams) {
		this.teams = teams;
	}
	
	@Override
	public Option<Object> handle(JsonNode message) {
		Logger.debug("Next question!");
		teams.forEach((name, out) -> {
			out.write(Json.toJson(new Question("did you see that?" + LocalDateTime.now().toString())));
		});
		return Option.None();
	}

	public static class Question {
		public String question;
		
		public Question(String question) {
			this.question = question;
		}
	}
}
