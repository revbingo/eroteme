package models;

import play.libs.F.Option;
import play.libs.Json;

public class Team {

	private JsonWebSocket out;

	private String name;
	private int score = 0;
	
	public Team(String name, JsonWebSocket out) {
		this.name = name;
		this.out = out;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}
	
	public void score() {
		this.score++;
	}	
	
	public void notify(Option<Object> obj) {
		if(out != null && !obj.isEmpty()) {
			out.write(Json.toJson(obj));
		}
	}

	public static Team nil() {
		return new Team(null, null);
	}
}
