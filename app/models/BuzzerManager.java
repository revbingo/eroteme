package models;

import java.util.ArrayList;
import java.util.List;

public class BuzzerManager {

	private List<Team> respondedTeams = new ArrayList<Team>();
	
	public void reset() {
		this.respondedTeams = new ArrayList<Team>();
	}
	
	public List<Team> getRespondedTeams() {
		return respondedTeams;
	}
	
	public synchronized int respond(Team team) {
		respondedTeams.add(team);
		return respondedTeams.size();
	}
}
