package models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Akka;
import play.libs.F.Option;
import play.libs.Json;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.JsonNode;

public class QuizManager {

	private final Map<String, Handler> handlers;
	private final TeamRoster teamRoster = new TeamRoster();
	private JsonWebSocket admin;

	private ALogger requestLogger = Logger.of("requestLogger");

	private static ActorRef questionAsker = Akka.system().actorOf(Props.create(QuestionAsker.class));
	
	public QuizManager() {
		handlers = new HashMap<>();
		handlers.put("nextQuestion", new NextQuestionHandler(questionAsker));
//		handlers.out("answer", new AnswerHandler(questionAsker));
	}

	public Option<Object> onReceive(Object message) throws Exception {
		if(message instanceof JoinRequest) {
			JoinRequest join = (JoinRequest) message;
			if(!join.teamName.isEmpty()) {
				requestLogger.info("Join:" + join.teamName);
				teamRoster.put(join.teamName, join.out);
				
				if(admin != null) {
					admin.get().write(Json.toJson(new TeamListResponse(teamRoster.keySet())));
				}
				return Option.Some(new RegistrationResponse());
			} else {
				requestLogger.info("Admin");
				admin = join.out;
				return Option.Some(new TeamListResponse(teamRoster.keySet()));
			}
		} else if(message instanceof JsonNode) {
			JsonNode jsonMessage = (JsonNode) message;
			requestLogger.info("Json:" + Json.stringify(jsonMessage));
			Handler handler = getHandlerForMessage(jsonMessage);
			Option<Object> response = handler.handle(jsonMessage);
			
			return response;
		} else {
			requestLogger.error("Invalid: " + message.getClass().getCanonicalName());
			return Option.None();
		}
	}

	private Handler getHandlerForMessage(JsonNode jsonMessage) {
		String type = jsonMessage.get("type").asText();
		
		Handler handler = handlers.get(type);
		if(handler == null) handler = new NullHandler();
		return handler;
	}
	
	public class NullHandler implements Handler {

		@Override
		public Option<Object> handle(JsonNode message) {
			return Option.Some(new ErrorResponse("Message type " + message.get("type").asText() + " not recognised"));
		}

	}
	
	public class NextQuestionHandler implements Handler {

		private QuestionAsker asker;
		
		public NextQuestionHandler(QuestionAsker asker) {
			this.asker = asker;
		}
		
		@Override
		public Option<Object> handle(JsonNode message) {
			asker.onReceive(new NextQuestionRequest(teamRoster));
			return Option.None();
		}

	}
	
	public class RegistrationResponse {
		public String type = "registrationResponse";
		public int statusCode = 200;
	}
	
	public class ErrorResponse {
		
		public String type = "error";
		public String message;
		
		public ErrorResponse(String message) {
			this.message = message;
		}
	}
	
	public static class JoinRequest {
		public String teamName;
		public JsonWebSocket out;
		
		public JoinRequest(String teamName, JsonWebSocket out) {
			this.teamName = teamName;
			this.out = out;
		}
	}
	
	public class TeamListResponse {
		public String type = "teamList";
		public Set<String> teams;
		
		public TeamListResponse(Set<String> teams) {
			this.teams = teams;
		}
	}
	
	public class NextQuestionRequest {
		public TeamRoster teams;
		
		public NextQuestionRequest(TeamRoster teams) {
			this.teams = teams;
		}
	}
}
