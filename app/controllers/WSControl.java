package controllers;

import static akka.pattern.Patterns.ask;

import java.util.concurrent.TimeUnit;

import models.QuizManager;
import models.QuizManager.Join;
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

	public static WebSocket<JsonNode> bind(String teamName) {
		return WebSocket.whenReady((in,out) -> {
			join(teamName, in, out);
		});
	}
	
	public static void join(String teamName, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) throws Exception {
		Await.result(ask(quizActor, new Join(teamName, out), 1000), Duration.create(1, TimeUnit.SECONDS));
		
		in.onMessage((json) -> {
				quizActor.tell(json, null);
		});
		
		out.write(Json.toJson("Ready!"));
	}
}