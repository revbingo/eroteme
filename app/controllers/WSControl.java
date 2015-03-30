package controllers;

import static akka.pattern.Patterns.ask;
import static play.libs.Json.toJson;


import java.util.concurrent.TimeUnit;


import models.JsonWebSocket;
import models.QuizManager;
import models.QuizManager.JoinRequest;
import play.libs.Akka;
import play.libs.F.Option;
import play.mvc.Controller;
import play.mvc.WebSocket;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;


import com.fasterxml.jackson.databind.JsonNode;

@SuppressWarnings("unchecked")
public class WSControl extends Controller {
	
	private final static ActorRef quizActor = Akka.system().actorOf(Props.create(QuizManager.class));
	
	private final static Duration TIMEOUT = Duration.create(1, TimeUnit.SECONDS);
	
	public static WebSocket<JsonNode> bind(String teamName) {
		return WebSocket.whenReady((in,out) -> {
			JsonWebSocket outSocket = new JsonWebSocket(out);
			in.onMessage((json) -> {
				Option<Object> response = (Option<Object>) Await.result(ask(quizActor, json, TIMEOUT.toMillis()), TIMEOUT);
				if(!response.isEmpty()) {
					writeOption(outSocket, response);
				}
			});
			
			Option<Object> response =  registerWithQuizManager(teamName, outSocket);
			writeOption(outSocket, response);
		});
	}
	
	private static Option<Object> registerWithQuizManager(String teamName, JsonWebSocket out) throws Exception {
		return (Option<Object>) Await.result(ask(quizActor, new JoinRequest(teamName, out), TIMEOUT.toMillis()), TIMEOUT);
	}
	
	private static void writeOption(JsonWebSocket out, Option<Object> option) {
		out.write(toJson(option.get()));
	}
}