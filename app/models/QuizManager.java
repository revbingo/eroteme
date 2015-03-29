package models;

import java.util.HashMap;
import java.util.Map;

import play.Logger;
import play.libs.F.Option;
import play.mvc.WebSocket;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.JsonNode;

public class QuizManager extends UntypedActor {

	private final Map<String, Handler> handlers;
	public final Map<String, WebSocket.Out<JsonNode>> teams = new HashMap<>();
	
	public QuizManager() {
		handlers = new HashMap<>();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		Logger.debug("onReceive " + message.getClass().getCanonicalName());
		
		if(message instanceof JoinRequest) {
			JoinRequest join = (JoinRequest) message;
			teams.put(join.teamName, join.out);
			sender().tell(Option.Some(new RegistrationResponse()), self());
		} else if(message instanceof JsonNode) {
			JsonNode jsonMessage = (JsonNode) message;
			Handler handler = getHandlerForMessage(jsonMessage);
			Object response = handler.handle(jsonMessage);
			
			sender().tell(response, self());
		} else {
			sender().tell(Option.None(), self());
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
		public Option<Object> handle(JsonNode message) {
			return Option.Some(new ErrorResponse("Message type " + message.get("type").asText() + " not recognised"));
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
	
	public static class JoinRequest {
		public String teamName;
		public WebSocket.Out<JsonNode> out;
		
		public JoinRequest(String teamName, WebSocket.Out<JsonNode> out) {
			this.teamName = teamName;
			this.out = out;
		}
	}

}
