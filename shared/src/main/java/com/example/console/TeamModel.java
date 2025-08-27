package com.example.console;

import java.util.ArrayList;
import java.util.TreeMap;
import com.example.common.PlayerDataObject;
import com.example.common.Logger;
import com.example.common.PlayerModels.*;

public class TeamModel implements Comparable<TeamModel>{
	private String teamName;
	private boolean userTeam = false;
	private TreeMap<String,ArrayList<PlayerDataObject>> thisTeamPlayers;
	private int teamNumber;
	
	public TeamModel(String teamName, Boolean userTeam, int teamNumber){
		this.teamName = teamName;
		this.thisTeamPlayers = new TreeMap<>();
		this.userTeam = userTeam;
		this.teamNumber = teamNumber;
		Logger.logInfo("Created TeamModel: " + teamName + " (User team: " + userTeam + ")");
		
		ArrayList<PlayerDataObject> quarterBackList = new ArrayList<PlayerDataObject>();
		ArrayList<PlayerDataObject> runningBackList = new ArrayList<PlayerDataObject>();
		ArrayList<PlayerDataObject> tightEndList = new ArrayList<PlayerDataObject>();
		ArrayList<PlayerDataObject> wideReceiverList = new ArrayList<PlayerDataObject>();
		ArrayList<PlayerDataObject> kickerList = new ArrayList<PlayerDataObject>();
		ArrayList<PlayerDataObject> defenseList = new ArrayList<PlayerDataObject>();
		
		this.thisTeamPlayers.put(QuarterBackPlayerModel.POSITIONSHORTHANDLE, quarterBackList);
		this.thisTeamPlayers.put(RunningBackPlayerModel.POSITIONSHORTHANDLE, runningBackList);
		this.thisTeamPlayers.put(TightEndPlayerModel.POSITIONSHORTHANDLE, tightEndList);
		this.thisTeamPlayers.put(WideReceiverPlayerModel.POSITIONSHORTHANDLE, wideReceiverList);
		this.thisTeamPlayers.put(KickerPlayerModel.POSITIONSHORTHANDLE,kickerList);
		this.thisTeamPlayers.put(DefensePlayerModel.POSITIONSHORTHANDLE,defenseList);
	}
	
	public void addPlayer(String playerClassString, PlayerModel playerToAdd) {
		this.thisTeamPlayers.get(playerClassString).add(playerToAdd.getPlayerDataObject());
		Logger.logInfo("Added player " + playerToAdd.getFullName() + " to team " + this.teamName);
	}
	
	public String getTeamName() { return this.teamName; }
	public int getTeamSize() { 
		int teamSize = 0;
		for(String currPosition : this.thisTeamPlayers.keySet()) {
			teamSize += this.thisTeamPlayers.get(currPosition).size();
		}
		return teamSize;
	}
	public boolean isUserTeam() { return this.userTeam; }
	public TreeMap<String,ArrayList<PlayerDataObject>> getTeamTreeMap() { return this.thisTeamPlayers; }
	
	private PlayerDataObject getStartersForPosition(ArrayList<PlayerDataObject> playersAtThisPosition) { 
		return playersAtThisPosition.stream().max((p1, p2) -> Double.compare(p1.getPredictedScore(), p2.getPredictedScore())).orElse(null); 
	}
	
	private String getSpecialPositionStarters(TreeMap<String,ArrayList<PlayerDataObject>> copyOfThisTeamPlayers) {
		String specialPositionStarterString = "";
		
		for(String currPosition : copyOfThisTeamPlayers.keySet()) {
			if( (currPosition.equals(KickerPlayerModel.POSITIONSHORTHANDLE) || 
				currPosition.equals(DefensePlayerModel.POSITIONSHORTHANDLE)) &&
					!copyOfThisTeamPlayers.get(currPosition).isEmpty()) {
				
				if(currPosition.equals(DefensePlayerModel.POSITIONSHORTHANDLE)) {
					PlayerDataObject currSpecialStarter = this.getStartersForPosition(copyOfThisTeamPlayers.get(currPosition));
					specialPositionStarterString += currSpecialStarter.getFullName() + " - " + currSpecialStarter.getPredictedScore() + "\n";
				} else {
					PlayerDataObject currSpecialStarter = this.getStartersForPosition(copyOfThisTeamPlayers.get(currPosition));
					specialPositionStarterString += currSpecialStarter.getFullName() + " - " + currSpecialStarter.getPredictedScore() + "\n";
				}
			}
		}
		return specialPositionStarterString;
	}
	
	@Override
	public String toString() {
		TreeMap<String,ArrayList<PlayerDataObject>> copyOfThisTeamPlayers = new TreeMap<>();
		for(String key : this.thisTeamPlayers.keySet()) {
			copyOfThisTeamPlayers.put(key, new ArrayList<>(this.thisTeamPlayers.get(key)));
		}
		PlayerDataObject flexPlayer = null;
		
		String teamString = "\n" + this.teamName + "\n";
		
		for(String currPosition : copyOfThisTeamPlayers.keySet()) {
			ArrayList<PlayerDataObject> playersAtThisPosition = copyOfThisTeamPlayers.get(currPosition);
			if(!playersAtThisPosition.isEmpty()) {
				PlayerDataObject currStarer = this.getStartersForPosition(playersAtThisPosition);
				teamString += currStarer.getFullName() + " - " + currStarer.getPredictedScore() + "\n";
				playersAtThisPosition.remove(currStarer);
				
				if(!playersAtThisPosition.isEmpty()) {
					PlayerDataObject firstPlayerOnBench = this.getStartersForPosition(playersAtThisPosition);
					if(flexPlayer == null || firstPlayerOnBench.getPredictedScore() > flexPlayer.getPredictedScore()) {
						if(flexPlayer != null) {
							if(currPosition.equals(RunningBackPlayerModel.POSITIONSHORTHANDLE) ||
							currPosition.equals(WideReceiverPlayerModel.POSITIONSHORTHANDLE) ||
							currPosition.equals(TightEndPlayerModel.POSITIONSHORTHANDLE)) {
								flexPlayer = firstPlayerOnBench;
							}
						} else {
							if(currPosition.equals(RunningBackPlayerModel.POSITIONSHORTHANDLE) ||
							currPosition.equals(WideReceiverPlayerModel.POSITIONSHORTHANDLE)) {
								flexPlayer = firstPlayerOnBench;
							}
						}
					}
				}
			}
		}
		
		String kickerStarterString = KickerPlayerModel.POSITIONSHORTHANDLE + " - None";
		String defenseStarterString = DefensePlayerModel.POSITIONSHORTHANDLE + " - None";
		
		for(String currPosition : copyOfThisTeamPlayers.keySet()) {
			if( (currPosition.equals(KickerPlayerModel.POSITIONSHORTHANDLE) || 
				currPosition.equals(DefensePlayerModel.POSITIONSHORTHANDLE)) &&
					!copyOfThisTeamPlayers.get(currPosition).isEmpty()) {
				
				if(currPosition.equals(DefensePlayerModel.POSITIONSHORTHANDLE)) {
					defenseStarterString = currPosition + " - " + this.getStartersForPosition(copyOfThisTeamPlayers.get(currPosition)).getFullName();
				} else {
					kickerStarterString = currPosition + " - " + this.getStartersForPosition(copyOfThisTeamPlayers.get(currPosition)).getFullName();
				}
			}
		}
		
		if(flexPlayer != null) {
			teamString += "FLEX - " + flexPlayer.getFullName() + " - " + flexPlayer.getPredictedScore() + "\n";
		} else {
			teamString += "FLEX - None\n";
		}
		
		teamString += kickerStarterString + "\n";
		teamString += defenseStarterString + "\n";
		
		return teamString;
	}
	
	@Override
	public int compareTo(TeamModel otherTeam) {
		return this.teamName.compareTo(otherTeam.teamName);
	}
} 