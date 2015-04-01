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
	private Admin admin = new Admin(null);

	private ALogger requestLogger = Logger.of("requestLogger");

	private static QuestionAsker questionAsker = new QuestionAsker();
	
	public QuizMaster() {
		handlers = new HashMap<>();
		handlers.put("nextQuestion", new NextQuestionHandler(this, questionAsker));
		handlers.put("answer", new AnswerQuestionHandler(this, questionAsker));
	}

	public void join(String teamName, JsonWebSocket out) {
		requestLogger.info("Join:" + teamName);
		Team theTeam = new Team(teamName, out);
		teamRoster.put(teamName, theTeam);
		theTeam.notify(Option.Some(new Domain.RegistrationResponse()));
		admin.notify(new Domain.TeamListResponse(teamRoster.values()));
	}
	
	public void registerAdmin(JsonWebSocket outSocket) {
		requestLogger.info("Admin join");
		admin.destroy();
		admin = new Admin(outSocket);
	}
	
	public void deregisterAdmin() {
		requestLogger.info("Admin leave");
		admin = new Admin(null);
	}
	
	public void leave(String teamName) {
		requestLogger.info("Leave:" + teamName);
		teamRoster.remove(teamName);
		admin.notify(new Domain.TeamListResponse(teamRoster.values()));
	}
	
	public void messageReceived(String teamName, JsonNode message) throws Exception {
		JsonNode jsonMessage = (JsonNode) message;
		requestLogger.info("Json:" + Json.stringify(jsonMessage));
		
		String type = jsonMessage.get("type").asText();
		Handler handler = handlers.getOrDefault(type, new NullHandler());

		handler.handle(teamName, jsonMessage);
	}

	public void notifyTeam(String teamName, Option<Object> response) {
		Team team = teamRoster.getOrDefault(teamName, Team.nil());
		if(response.isDefined()) {
			team.notify(response);
		}
	}
	
	public void score(String teamName) {
		Team team = teamRoster.get(teamName);
		if(team != null) {
			team.score();
			admin.notify(new Domain.TeamListResponse(teamRoster.values()));
		}
	}
	
	public void notifyTeams(Option<Object> obj) {
		getTeamRoster().forEach((name, team) -> {
			team.notify(obj);
		});
	}
	
	public TeamRoster getTeamRoster() {
		return teamRoster;
	}
}
