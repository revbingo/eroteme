package controllers;

import static akka.pattern.Patterns.ask;

import java.util.concurrent.TimeUnit;

import models.QuizManager;
import models.QuizManager.Join;
import models.QuizManager.RegistrationResponse;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.WebSocket;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;

import com.fasterxml.jackson.databind.JsonNode;

public class WSControl extends Controller {
	
	private final static ActorRef quizActor = Akka.system().actorOf(Props.create(QuizManager.class));
	private final static Duration TIMEOUT = Duration.create(1, TimeUnit.SECONDS);
	
	public static WebSocket<JsonNode> bind(String teamName) {
		return WebSocket.whenReady((in,out) -> {
			in.onMessage((json) -> {
					Object response = Await.result(ask(quizActor, json, TIMEOUT.toMillis()), TIMEOUT);
					out.write(Json.toJson(response));
			});
			
			out.write(Json.toJson(registerWithQuizManager(teamName, out)));
		});
	}
	
	private static RegistrationResponse registerWithQuizManager(String teamName, WebSocket.Out<JsonNode> out) throws Exception {
		return (RegistrationResponse) Await.result(ask(quizActor, new Join(teamName, out), TIMEOUT.toMillis()), TIMEOUT);
	}
}