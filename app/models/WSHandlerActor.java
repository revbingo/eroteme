package models;

import static play.libs.Json.*;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.libs.Json;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class WSHandlerActor extends UntypedActor {

	private final ActorRef out;
	private final Map<String, Handler> handlers;
	
	public WSHandlerActor(ActorRef out) {
		this.out = out;
		handlers = new HashMap<String, Handler>();
		handlers.put("REGISTER", new RegistrationHandler());
	}

	public static Props props(ActorRef out) {
		return Props.create(WSHandlerActor.class, out);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (!(message instanceof String)) return;
		
		JsonNode jsonMessage = Json.parse((String) message);
		
		Handler handler = getHandlerForMessage(jsonMessage);
		Object response = handler.handle(out, jsonMessage);
		out.tell(stringify(toJson(response)), self());
	}

	private Handler getHandlerForMessage(JsonNode jsonMessage) {
		String type = jsonMessage.get("type").asText();
		
		Handler handler = handlers.get(type);
		if(handler == null) handler = new NullHandler();
		return handler;
	}
	
	public static class RegistrationHandler implements Handler {
		
		@Override
		public Object handle(ActorRef out, JsonNode message) {
			return new RegistrationResponse();
		}
	}
	
	public static class NullHandler implements Handler {

		@Override
		public Object handle(ActorRef out, JsonNode message) {
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
