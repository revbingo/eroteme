package models;

import play.libs.Json;

public class Team {

	private JsonWebSocket out;

	private String name;
	private int score = 0;
	
	public Team(String name, JsonWebSocket out) {
		this.name = name;
		this.out = out;
	}

	public JsonWebSocket getOut() {
		return out;
	}

	public void setOut(JsonWebSocket out) {
		this.out = out;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}
	
	public void score() {
		this.score++;
	}	
	
	public void notify(Object obj) {
		out.write(Json.toJson(obj));
	}
}
