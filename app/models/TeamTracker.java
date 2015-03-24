package models;

import java.util.HashMap;
import java.util.Set;

public class TeamTracker {
	
	private HashMap<String, Integer> teams = new HashMap<String, Integer>();
	private static TeamTracker _instance;
	
	private TeamTracker() {}
	
	public static TeamTracker getInstance() {
		if(_instance == null) {
			_instance = new TeamTracker();
		}
		return _instance;
	}
	
	public void addTeam(String teamName) {
		teams.put(teamName, 0);
	}
	
	public Set<String> getTeamList() {
		return teams.keySet();
	}
	
	
}
