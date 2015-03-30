package models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.questions.Question;
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
		handlers.put("nextQuestion", new NextQuestionHandler(questionAsker));
		handlers.put("answer", new AnswerQuestionHandler(questionAsker));
	}

	public Option<Object> join(String teamName, JsonWebSocket out) {
		if(!teamName.isEmpty()) {
			requestLogger.info("Join:" + teamName);
			teamRoster.put(teamName, out);
			
			if(admin != null) {
				admin.get().write(Json.toJson(new TeamListResponse(teamRoster.keySet())));
			}
			return Option.Some(new RegistrationResponse());
		} else {
			Logger.debug("admin joined");
			requestLogger.info("Admin");
			admin = out;
			return Option.Some(new TeamListResponse(teamRoster.keySet()));
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

	public class NullHandler implements Handler {

		@Override
		public Option<Object> handle(String teamName, JsonNode message) {
			return Option.Some(new ErrorResponse("Message type " + message.get("type").asText() + " not recognised"));
		}
	}
	
	public class NextQuestionHandler implements Handler {

		private QuestionAsker asker;
		
		public NextQuestionHandler(QuestionAsker asker) {
			this.asker = asker;
		}
		
		@Override
		public Option<Object> handle(String teamName, JsonNode message) {
			Logger.debug("asking the asker");
			Question question = asker.nextQuestion(teamRoster);
			teamRoster.forEach((name, out) -> {
				Logger.debug("askign " + name);
				out.write(Json.toJson(question));
			});
			Logger.debug("Asked a question");
			return Option.Some(question);
		}

	}
	
	public class AnswerQuestionHandler implements Handler {

		private QuestionAsker asker;
		
		public AnswerQuestionHandler(QuestionAsker asker) {
			this.asker = asker;
		}
		
		@Override
		public Option<Object> handle(String teamName, JsonNode message) {
			boolean correct = false;
			try {
				correct = asker.answer(message.get("questionNumber").asInt(), message.get("answer").asText());
				Logger.debug(teamName + " got the answer " + correct);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			return Option.Some(new QuestionAnswerResponse(correct));
		}
	}
	
	public class QuestionAnswerResponse {
		public boolean correct;
		public String type = "answerResponse";
		
		public QuestionAnswerResponse(boolean correct) {
			this.correct = correct;
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
	
	public class TeamListResponse {
		public String type = "teamList";
		public Set<String> teams;
		
		public TeamListResponse(Set<String> teams) {
			this.teams = teams;
		}
	}
	
	public class QuestionAdminResponse {
		public String type = "currentQuestion";
		public Question question;
		
		public QuestionAdminResponse(Question question) {
			this.question = question;
		}
	}

	public void remove(String teamName) {
		if(!teamName.isEmpty()) {
			teamRoster.remove(teamName);
			if(admin != null) {
				admin.write(Json.toJson(new TeamListResponse(teamRoster.keySet())));
			}
		} else {
			admin = null;
		}
	}
}
