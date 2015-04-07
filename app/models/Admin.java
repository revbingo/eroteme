package models;

import play.libs.F.Option;
import play.libs.Json;

public class Admin {

	private JsonWebSocket out;
	
	public Admin(JsonWebSocket out) {
		this.out = out;
	}
	
	public void notify(Option<Object> obj) {
		if(out != null && obj.isDefined()) {
			out.write(Json.toJson(obj.get()));
		}
	}
	
	public void destroy() {
		if(out != null) {
			out.close();
		}
	}
}
