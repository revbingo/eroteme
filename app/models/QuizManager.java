package models;

import static play.libs.Json.*;
import static akka.pattern.Patterns.ask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.Json;
import play.mvc.WebSocket;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class QuizManager extends UntypedActor {

	
	private final Map<String, Handler> handlers;
	
	private Map<String, WebSocket.Out<JsonNode>> teams = new HashMap<>();
	
	public QuizManager() {
		handlers = new HashMap<>();
	}

	public static class Join {
		public String teamName;
		public WebSocket.Out<JsonNode> out;
		
		public Join(String teamName, WebSocket.Out<JsonNode> out) {
			this.teamName = teamName;
			this.out = out;
		}
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		Logger.debug("onReceive " + message.getClass().getCanonicalName());
		if(message instanceof Join) {
			Join join = (Join) message;
			teams.put(join.teamName, join.out);
			sender().tell("OK",self());
		} else if(message instanceof JsonNode) {
			JsonNode jsonMessage = (JsonNode) message;
			Handler handler = getHandlerForMessage(jsonMessage);
			Object response = handler.handle(jsonMessage);
			WebSocket.Out<JsonNode> out = teams.get(jsonMessage.get("teamName").asText());
			out.write(toJson(response));
		} else {
			return;
		}
	}

	private Handler getHandlerForMessage(JsonNode jsonMessage) {
		String type = jsonMessage.get("type").asText();
		
		Handler handler = handlers.get(type);
		if(handler == null) handler = new NullHandler();
		return handler;
	}
	
	public static class NullHandler implements Handler {

		@Override
		public Object handle(JsonNode message) {
			Logger.debug(Json.stringify(message));
			return new ErrorResponse("Message type " + message.get("type").asText() + " not recognised");
		}
		
	}
	
	public static class RegistrationResponse {
		public String type = "registrationResponse";
		public int statusCode = 200;
	}
	
	public static class ErrorResponse {
		
		public String type = "error";
		public String message;
		
		public ErrorResponse(String message) {
			this.message = message;
		}
	}
}
