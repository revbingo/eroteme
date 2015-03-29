package models;

import com.fasterxml.jackson.databind.JsonNode;

public interface Handler {

	public Object handle(JsonNode message);
}
