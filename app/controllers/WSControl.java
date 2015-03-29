package controllers;

import static akka.pattern.Patterns.ask;

import java.util.concurrent.TimeUnit;

import models.QuizManager;
import models.QuizManager.JoinRequest;
import play.libs.Akka;
import play.libs.F.Option;
import play.libs.Json;
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
			in.onMessage((json) -> {
				Option<Object> response = (Option<Object>) Await.result(ask(quizActor, json, TIMEOUT.toMillis()), TIMEOUT);
				if(!response.isEmpty()) {
					out.write(Json.toJson(response.get()));
				}
			});
			
			out.write(Json.toJson(registerWithQuizManager(teamName, out)));
		});
	}
	
	private static Option<Object> registerWithQuizManager(String teamName, WebSocket.Out<JsonNode> out) throws Exception {
		return (Option<Object>) Await.result(ask(quizActor, new JoinRequest(teamName, out), TIMEOUT.toMillis()), TIMEOUT);
	}
}