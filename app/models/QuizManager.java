package models;

import java.util.HashMap;
import java.util.Map;

import play.Logger;
import play.Logger.ALogger;
import play.libs.F.Option;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

public class QuizManager {

	private final Map<String, Handler> handlers;
	private final TeamRoster teamRoster = new TeamRoster();
	private JsonWebSocket admin;

	private ALogger requestLogger = Logger.of("requestLogger");

	private static QuestionAsker questionAsker = new QuestionAsker();
	
	public QuizManager() {
		handlers = new HashMap<>();
		handlers.put("nextQuestion", new NextQuestionHandler(this, questionAsker));
		handlers.put("answer", new AnswerQuestionHandler(this, questionAsker));
	}

	public Option<Object> join(String teamName, JsonWebSocket out) {
		if(!teamName.isEmpty()) {
			requestLogger.info("Join:" + teamName);
			Team theTeam = new Team(teamName, out);
			teamRoster.put(teamName, theTeam);
			
			if(admin != null) {
				admin.get().write(Json.toJson(new Domain.TeamListResponse(teamRoster.keySet())));
			}
			return Option.Some(new Domain.RegistrationResponse());
		} else {
			Logger.debug("admin joined");
			requestLogger.info("Admin");
			admin = out;
			return Option.Some(new Domain.TeamListResponse(teamRoster.keySet()));
		}
	}
	
	public Option<Object> messageReceived(String teamName, JsonNode message) throws Exception {
		JsonNode jsonMessage = (JsonNode) message;
		requestLogger.info("Json:" + Json.stringify(jsonMessage));
		Handler handler = getHandlerForMessage(jsonMessage).getOrElse(new NullHandler());
		Option<Object> response = handler.handle(teamName, jsonMessage);
		
		return response;
	}

	private Option<Handler> getHandlerForMessage(JsonNode jsonMessage) {
		String type = jsonMessage.get("type").asText();
		
		Handler handler = handlers.get(type);
		return Option.Some(handler);
	}

	public TeamRoster getTeamRoster() {
		return teamRoster;
	}
	
	public void remove(String teamName) {
		if(!teamName.isEmpty()) {
			teamRoster.remove(teamName);
			if(admin != null) {
				admin.write(Json.toJson(new Domain.TeamListResponse(teamRoster.keySet())));
			}
		} else {
			admin = null;
		}
	}
}
