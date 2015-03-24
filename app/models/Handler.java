package models;

import akka.actor.ActorRef;

public interface Handler {

	public void handle(ActorRef out);
}
