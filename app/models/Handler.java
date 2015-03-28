package models;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;

public interface Handler {

	public Object handle(ActorRef out, JsonNode message);
}
