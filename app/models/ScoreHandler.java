package models;

import play.libs.F.Option;

import com.fasterxml.jackson.databind.JsonNode;

public class ScoreHandler implements Handler {

	private QuizMaster quizMaster;

	public ScoreHandler(QuizMaster quizMaster) {
		this.quizMaster = quizMaster;
	}
	
	@Override
	public void handle(Team team, JsonNode message) {
		System.out.println("Score handler!");
		Team teamThatScored = quizMaster.getTeamRoster().getOrDefault(message.get("team").asText(), Team.nil());
		teamThatScored.scored();
		quizMaster.notifyTeam(teamThatScored, Option.Some(new Domain.Scored(teamThatScored.getScore())));
		quizMaster.notifyAdmin();
	}

}
