package models;

import java.util.HashMap;
import java.util.Map;

import play.Logger;
import play.Logger.ALogger;
import play.libs.F.Option;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

public class QuizMaster {

	private final Map<String, Handler> handlers;
	private final TeamRoster teamRoster = new TeamRoster();
	private Admin admin;

	private ALogger requestLogger = Logger.of("requestLogger");

	private static QuestionAsker questionAsker = new QuestionAsker();
	
	public QuizMaster() {
		handlers = new HashMap<>();
		handlers.put("nextQuestion", new NextQuestionHandler(this, questionAsker));
		handlers.put("answer", new AnswerQuestionHandler(this, questionAsker));
	}

	public Option<Object> join(String teamName, JsonWebSocket out) {
		Option<Object> response = Option.None();
		if(!teamName.isEmpty()) {
			requestLogger.info("Join:" + teamName);
			Team theTeam = new Team(teamName, out);
			teamRoster.put(teamName, theTeam);
			
			response = Option.Some(new Domain.RegistrationResponse());
		} else {
			Logger.debug("admin joined");
			requestLogger.info("Admin");
			admin = new Admin(out);
		}
		
		if(admin != null) {
			admin.notify(new Domain.TeamListResponse(teamRoster.keySet()));
		}
		
		return response;
	}
	
	public void leave(String teamName) {
		if(!teamName.isEmpty()) {
			teamRoster.remove(teamName);
			if(admin != null) {
				admin.notify(new Domain.TeamListResponse(teamRoster.keySet()));
			}
		} else {
			admin = null;
		}
	}
	
	public Option<Object> messageReceived(String teamName, JsonNode message) throws Exception {
		JsonNode jsonMessage = (JsonNode) message;
		requestLogger.info("Json:" + Json.stringify(jsonMessage));
		Handler handler = getHandlerForMessage(jsonMessage).getOrElse(new NullHandler());
		Option<Object> response = handler.handle(teamName, jsonMessage);
		
		return response;
	}

	public void score(String teamName) {
		Team team = teamRoster.get(teamName);
		if(team != null) team.score();

	}
	
	public void notifyTeams(Object obj) {
		getTeamRoster().forEach((name, team) -> {
			team.getOut().write(Json.toJson(obj));
		});
	}
	
	private Option<Handler> getHandlerForMessage(JsonNode jsonMessage) {
		String type = jsonMessage.get("type").asText();
		
		Handler handler = handlers.get(type);
		return Option.Some(handler);
	}

	public TeamRoster getTeamRoster() {
		return teamRoster;
	}
	
}
