package models;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public class ScoreHandler implements Handler {

	private QuizMaster quizMaster;

	public ScoreHandler(QuizMaster quizMaster) {
		this.quizMaster = quizMaster;
	}
	
	@Override
	public void handle(Team team, JsonNode message) {
		Team teamThatScored = quizMaster.getTeamRoster().getOrDefault(message.get("team").asText(), Team.nil());
		int delta = message.get("delta").asInt();
		teamThatScored.scored(delta);
		quizMaster.notifyTeam(teamThatScored, Optional.of(new Domain.Scored(teamThatScored.getScore())));
		quizMaster.notifyAdmin();
	}

}
