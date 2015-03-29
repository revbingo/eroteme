package models;

import play.libs.F.Option;

import com.fasterxml.jackson.databind.JsonNode;

public interface Handler {

	public Option<Object> handle(JsonNode message);
}
