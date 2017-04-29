package models;

import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QuizMaster {

	private final Map<String, Handler> handlers;
	private final TeamRoster teamRoster = new TeamRoster();
	private Admin admin = new Admin(null);

	private ALogger requestLogger = Logger.of("requestLogger");

	private static QuestionAsker questionAsker = new QuestionAsker();
	private static BuzzerManager buzzerManager = new BuzzerManager();
	
	public QuizMaster() {
		handlers = new HashMap<>();
		handlers.put("nextQuestion", new NextQuestionHandler(this, questionAsker, buzzerManager));
		handlers.put("answer", new AnswerQuestionHandler(this, questionAsker));
		handlers.put("buzz", new BuzzerHandler(this, buzzerManager));
		handlers.put("score", new ScoreHandler(this));
	}

	public void join(String teamName, JsonWebSocket out) {
		requestLogger.info("Join:" + teamName);
		
		Team theTeam = teamRoster.get(teamName);
		if(theTeam == null) {
			theTeam = new Team(teamName, out);
		} else {
			theTeam.setOut(out);
		}
		
		teamRoster.put(teamName, theTeam);
		theTeam.notify(Optional.of(new Domain.RegistrationResponse(theTeam)));
		notifyAdmin();
	}
	
	public void registerAdmin(JsonWebSocket outSocket) {
		requestLogger.info("Admin join");
		admin.destroy();
		admin = new Admin(outSocket);
		notifyAdmin();
	}
	
	public void deregisterAdmin() {
		requestLogger.info("Admin leave");
		admin = new Admin(null);
	}
	
	public void leave(String teamName) {
		requestLogger.info("Leave:" + teamName);
		teamRoster.remove(teamName);
		notifyAdmin();
	}
	
	public void messageReceived(String teamName, JsonNode message) throws Exception {
		JsonNode jsonMessage = (JsonNode) message;
		requestLogger.info("Json:" + Json.stringify(jsonMessage));
		
		String type = jsonMessage.get("type").asText();
		Handler handler = handlers.getOrDefault(type, new NullHandler());
		Team team = teamRoster.getOrDefault(teamName, Team.nil());

		handler.handle(team, jsonMessage);
	}

	public void notifyTeam(Team team, Optional<Object> response) {
		if(response.isPresent()) {
			team.notify(response);
		}
	}
	
	public void notifyTeams(Optional<Object> obj) {
		getTeamRoster().forEach((name, team) -> {
			team.notify(obj);
		});
	}
	
	public void notifyAdmin() {
		admin.notify(Optional.of(new Domain.TeamListResponse(teamRoster.values())));
	}
	
	public void notifyAdmin(Optional<Object> obj) {
		admin.notify(obj);
	}
	
	public TeamRoster getTeamRoster() {
		return teamRoster;
	}
}
