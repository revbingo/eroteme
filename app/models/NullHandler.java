package models;

import models.Domain.ErrorResponse;
import play.libs.F.Option;

import com.fasterxml.jackson.databind.JsonNode;

public class NullHandler implements Handler {

	@Override
	public Option<Object> handle(String teamName, JsonNode message) {
		return Option.Some(new ErrorResponse("Message type " + message.get("type").asText() + " not recognised"));
	}
}