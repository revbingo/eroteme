package models;

import java.util.Collection;

import models.questions.Question;

public class Domain {
	public static class QuestionAnswerResponse {
		public boolean correct;
		public int score;
		public String type = "answerResponse";
		
		public QuestionAnswerResponse(boolean correct, int score) {
			this.correct = correct;
			this.score = score;
		}
	}
	
	public static class RegistrationResponse {
		public String type = "registrationResponse";
		public int statusCode = 200;
		public Team team;
		
		public RegistrationResponse(Team team) {
			this.team = team;
		}
		
	}
	
	public static class ErrorResponse {
		
		public String type = "error";
		public String message;
		
		public ErrorResponse(String message) {
			this.message = message;
		}
	}
	
	public static class TeamListResponse {
		public String type = "teamList";
		public Collection<Team> teams;
		
		public TeamListResponse(Collection<Team> teams) {
			this.teams = teams;
		}
	}
	
	public static class QuestionAdminResponse {
		public String type = "currentQuestion";
		public Question question;
		
		public QuestionAdminResponse(Question question) {
			this.question = question;
		}
	}
	
	public static class BuzzAck {
		public String type = "buzzAck";
		public int responseOrder;
		public String teamName;
		
		public BuzzAck(String teamName, int responseOrder) {
			this.teamName = teamName;
			this.responseOrder = responseOrder;
		}
	}
	
	public static class Scored {
		public String type = "scored";
		public int score;
		
		public Scored(int score) {
			this.score = score;
		}
	}
}