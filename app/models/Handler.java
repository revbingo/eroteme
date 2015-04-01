package models;

import com.fasterxml.jackson.databind.JsonNode;

public interface Handler {

	public void handle(String teamName, JsonNode message);
}
