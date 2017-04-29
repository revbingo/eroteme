package models;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

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
		quizMaster.notifyTeam(team, Optional.of(ack));
		quizMaster.notifyAdmin();
	}
}
