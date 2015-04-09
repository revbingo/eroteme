package models;

import play.libs.F.Option;
import play.libs.Json;

public class Team {

	private JsonWebSocket out;

	private final static int NOT_BUZZED = 1000;
	private String name;
	private int score = 0;
	private int buzzOrder = NOT_BUZZED;
	
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
	
	public int getBuzzOrder() {
		return this.buzzOrder;
	}
	
	public void scored(int delta) {
		this.score += delta;
	}	
	
	public void buzzed(int buzzOrder) {
		this.buzzOrder = buzzOrder;
	}
	
	public void resetBuzzer() {
		this.buzzOrder = NOT_BUZZED;
	}
	
	public boolean haveBuzzed() {
		return this.buzzOrder > 0;
	}
	
	public void notify(Option<Object> obj) {
		if(out != null && !obj.isEmpty()) {
			out.write(Json.toJson(obj.get()));
		}
	}

	public static Team nil() {
		return new Team(null, null);
	}
	
}
