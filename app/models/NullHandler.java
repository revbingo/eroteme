package models;

import play.Logger;

import com.fasterxml.jackson.databind.JsonNode;

public class NullHandler implements Handler {

	@Override
	public void handle(String teamName, JsonNode message) {
		Logger.warn("Unexpected message type:" + message.toString());
	}
}