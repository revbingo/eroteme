package models;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.WebSocket;

public class JsonWebSocket {

	private WebSocket.Out<JsonNode> wrapped;
	
	public JsonWebSocket(WebSocket.Out<JsonNode> out) {
		this.wrapped = out;
	}

	public WebSocket.Out<JsonNode> get() {
		return this.wrapped;
	}
	
	public void write(JsonNode json) {
		wrapped.write(json);
	}
}

