package models;

import play.libs.Json;

import java.util.Optional;

public class Admin {

	private JsonWebSocket out;
	
	public Admin(JsonWebSocket out) {
		this.out = out;
	}
	
	public void notify(Optional<Object> obj) {
		if(out != null && obj.isPresent()) {
			out.write(Json.toJson(obj.get()));
		}
	}
	
	public void destroy() {
		if(out != null) {
			out.close();
		}
	}
}
