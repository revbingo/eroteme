package models;

import play.libs.F.Option;

import com.fasterxml.jackson.databind.JsonNode;

public class BuzzerHandler implements Handler {

	private QuizMaster quizMaster;
	private BuzzerManager buzzerManager;
	
	public BuzzerHandler(QuizMaster quizMaster, BuzzerManager buzzerManager) {
		this.quizMaster = quizMaster;
		this.buzzerManager = buzzerManager;
	}

	@Override
	public void handle(Team team, JsonNode message) {
		int responseOrder = this.buzzerManager.respond(team);
		team.buzzed(responseOrder);
		Domain.BuzzAck ack = new Domain.BuzzAck(team.getName(), responseOrder);
		quizMaster.notifyTeam(team, Option.Some(ack));
		quizMaster.notifyAdmin();
	}
}
