package models;

import play.libs.Json;

public class Admin {

	private JsonWebSocket out;
	
	public Admin(JsonWebSocket out) {
		this.out = out;
	}
	
	public void notify(Object obj) {
		if(out != null) {
			out.write(Json.toJson(obj));
		}
	}
	
	public void destroy() {
		if(out != null) {
			out.close();
		}
	}
}
