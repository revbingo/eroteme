package models;

import java.time.LocalDateTime;
import java.util.Map;

import play.Logger;
import play.libs.F.Option;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.WebSocket;
import models.QuizManager.NextQuestionRequest;
import models.questions.*;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.JsonNode;

public class QuestionAsker extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		NextQuestionRequest request = (NextQuestionRequest) arg0;
		Logger.debug("Next question!");
		request.teams.forEach((name, out) -> {
			out.write(Json.toJson(new SimpleQuestion("What time is it?" + LocalDateTime.now().toString(),"now")));
		});
	}

}
