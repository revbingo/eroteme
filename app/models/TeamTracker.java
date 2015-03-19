package models;

import java.util.HashMap;
import java.util.Set;

public class TeamTracker {
	
	private HashMap<String, Integer> teams = new HashMap<String, Integer>();
	
	public void addTeam(String teamName) {
		teams.put(teamName, 0);
	}
	
	public Set<String> getTeamList() {
		return teams.keySet();
	}
}
