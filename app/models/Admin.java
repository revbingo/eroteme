package models;

import play.libs.Json;

public class Admin {

	private JsonWebSocket out;
	
	public Admin(JsonWebSocket out) {
		this.out = out;
	}
	
	public void notify(Object obj) {
		out.write(Json.toJson(obj));
	}
}
