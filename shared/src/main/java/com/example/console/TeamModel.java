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
	
	public String getEnhancedDisplay() {
		StringBuilder display = new StringBuilder();
		display.append("\n").append("=".repeat(60)).append("\n");
		display.append("TEAM: ").append(this.teamName).append("\n");
		display.append("=".repeat(60)).append("\n");
		
		// Starters section
		display.append("\n🏈 STARTERS:\n");
		display.append("-".repeat(40)).append("\n");
		
		// QB Starter
		ArrayList<PlayerDataObject> qbs = this.thisTeamPlayers.get(QuarterBackPlayerModel.POSITIONSHORTHANDLE);
		if (!qbs.isEmpty()) {
			PlayerDataObject qbStarter = getStartersForPosition(qbs);
			display.append(String.format("QB    %-20s (ADP: %-6.1f, Pick: %-2d, Score: %-6.1f)\n", 
				qbStarter.getFullName(), qbStarter.getAvgADP(), getDraftPickNumber(qbStarter), qbStarter.getPredictedScore()));
		} else {
			display.append("QB    None\n");
		}
		
		// RB Starters (2)
		ArrayList<PlayerDataObject> rbs = this.thisTeamPlayers.get(RunningBackPlayerModel.POSITIONSHORTHANDLE);
		if (!rbs.isEmpty()) {
			ArrayList<PlayerDataObject> rbCopy = new ArrayList<>(rbs);
			PlayerDataObject rb1 = getStartersForPosition(rbCopy);
			display.append(String.format("RB1   %-20s (ADP: %-6.1f, Pick: %-2d, Score: %-6.1f)\n", 
				rb1.getFullName(), rb1.getAvgADP(), getDraftPickNumber(rb1), rb1.getPredictedScore()));
			rbCopy.remove(rb1);
			
			if (!rbCopy.isEmpty()) {
				PlayerDataObject rb2 = getStartersForPosition(rbCopy);
				display.append(String.format("RB2   %-20s (ADP: %-6.1f, Pick: %-2d, Score: %-6.1f)\n", 
					rb2.getFullName(), rb2.getAvgADP(), getDraftPickNumber(rb2), rb2.getPredictedScore()));
			}
		} else {
			display.append("RB1   None\n");
			display.append("RB2   None\n");
		}
		
		// WR Starters (2)
		ArrayList<PlayerDataObject> wrs = this.thisTeamPlayers.get(WideReceiverPlayerModel.POSITIONSHORTHANDLE);
		if (!wrs.isEmpty()) {
			ArrayList<PlayerDataObject> wrCopy = new ArrayList<>(wrs);
			PlayerDataObject wr1 = getStartersForPosition(wrCopy);
			display.append(String.format("WR1   %-20s (ADP: %-6.1f, Pick: %-2d, Score: %-6.1f)\n", 
				wr1.getFullName(), wr1.getAvgADP(), getDraftPickNumber(wr1), wr1.getPredictedScore()));
			wrCopy.remove(wr1);
			
			if (!wrCopy.isEmpty()) {
				PlayerDataObject wr2 = getStartersForPosition(wrCopy);
				display.append(String.format("WR2   %-20s (ADP: %-6.1f, Pick: %-2d, Score: %-6.1f)\n", 
					wr2.getFullName(), wr2.getAvgADP(), getDraftPickNumber(wr2), wr2.getPredictedScore()));
			}
		} else {
			display.append("WR1   None\n");
			display.append("WR2   None\n");
		}
		
		// TE Starter
		ArrayList<PlayerDataObject> tes = this.thisTeamPlayers.get(TightEndPlayerModel.POSITIONSHORTHANDLE);
		if (!tes.isEmpty()) {
			PlayerDataObject teStarter = getStartersForPosition(tes);
			display.append(String.format("TE    %-20s (ADP: %-6.1f, Pick: %-2d, Score: %-6.1f)\n", 
				teStarter.getFullName(), teStarter.getAvgADP(), getDraftPickNumber(teStarter), teStarter.getPredictedScore()));
		} else {
			display.append("TE    None\n");
		}
		
		// FLEX (best remaining RB/WR/TE)
		PlayerDataObject flexPlayer = getFlexPlayer();
		if (flexPlayer != null) {
			display.append(String.format("FLEX  %-20s (ADP: %-6.1f, Pick: %-2d, Score: %-6.1f)\n", 
				flexPlayer.getFullName(), flexPlayer.getAvgADP(), getDraftPickNumber(flexPlayer), flexPlayer.getPredictedScore()));
		} else {
			display.append("FLEX  None\n");
		}
		
		// K Starter
		ArrayList<PlayerDataObject> ks = this.thisTeamPlayers.get(KickerPlayerModel.POSITIONSHORTHANDLE);
		if (!ks.isEmpty()) {
			PlayerDataObject kStarter = getStartersForPosition(ks);
			display.append(String.format("K     %-20s (ADP: %-6.1f, Pick: %-2d, Score: %-6.1f)\n", 
				kStarter.getFullName(), kStarter.getAvgADP(), getDraftPickNumber(kStarter), kStarter.getPredictedScore()));
		} else {
			display.append("K     None\n");
		}
		
		// DST Starter
		ArrayList<PlayerDataObject> dsts = this.thisTeamPlayers.get(DefensePlayerModel.POSITIONSHORTHANDLE);
		if (!dsts.isEmpty()) {
			PlayerDataObject dstStarter = getStartersForPosition(dsts);
			display.append(String.format("DST   %-20s (ADP: %-6.1f, Pick: %-2d, Score: %-6.1f)\n", 
				dstStarter.getFullName(), dstStarter.getAvgADP(), getDraftPickNumber(dstStarter), dstStarter.getPredictedScore()));
		} else {
			display.append("DST   None\n");
		}
		
		// Bench section
		display.append("\n🪑 BENCH:\n");
		display.append("-".repeat(40)).append("\n");
		
		// Get all bench players (non-starters)
		ArrayList<PlayerDataObject> benchPlayers = getBenchPlayers();
		if (!benchPlayers.isEmpty()) {
			for (PlayerDataObject benchPlayer : benchPlayers) {
				display.append(String.format("BENCH %-20s (ADP: %-6.1f, Pick: %-2d, Score: %-6.1f)\n", 
					benchPlayer.getFullName(), benchPlayer.getAvgADP(), getDraftPickNumber(benchPlayer), benchPlayer.getPredictedScore()));
			}
		} else {
			display.append("No bench players\n");
		}
		
		display.append("=".repeat(60)).append("\n");
		return display.toString();
	}
	
	private PlayerDataObject getFlexPlayer() {
		ArrayList<PlayerDataObject> flexCandidates = new ArrayList<>();
		
		// Get best remaining RB
		ArrayList<PlayerDataObject> rbs = this.thisTeamPlayers.get(RunningBackPlayerModel.POSITIONSHORTHANDLE);
		if (rbs.size() > 2) {
			ArrayList<PlayerDataObject> rbCopy = new ArrayList<>(rbs);
			rbCopy.sort((p1, p2) -> Double.compare(p2.getPredictedScore(), p1.getPredictedScore()));
			flexCandidates.add(rbCopy.get(2)); // 3rd best RB
		}
		
		// Get best remaining WR
		ArrayList<PlayerDataObject> wrs = this.thisTeamPlayers.get(WideReceiverPlayerModel.POSITIONSHORTHANDLE);
		if (wrs.size() > 2) {
			ArrayList<PlayerDataObject> wrCopy = new ArrayList<>(wrs);
			wrCopy.sort((p1, p2) -> Double.compare(p2.getPredictedScore(), p1.getPredictedScore()));
			flexCandidates.add(wrCopy.get(2)); // 3rd best WR
		}
		
		// Get best remaining TE
		ArrayList<PlayerDataObject> tes = this.thisTeamPlayers.get(TightEndPlayerModel.POSITIONSHORTHANDLE);
		if (tes.size() > 1) {
			ArrayList<PlayerDataObject> teCopy = new ArrayList<>(tes);
			teCopy.sort((p1, p2) -> Double.compare(p2.getPredictedScore(), p1.getPredictedScore()));
			flexCandidates.add(teCopy.get(1)); // 2nd best TE
		}
		
		// Return the best flex candidate
		return flexCandidates.stream()
			.max((p1, p2) -> Double.compare(p1.getPredictedScore(), p2.getPredictedScore()))
			.orElse(null);
	}
	
	private ArrayList<PlayerDataObject> getBenchPlayers() {
		ArrayList<PlayerDataObject> benchPlayers = new ArrayList<>();
		
		// Add all players except starters
		for (String position : this.thisTeamPlayers.keySet()) {
			ArrayList<PlayerDataObject> players = this.thisTeamPlayers.get(position);
			if (players.size() > getStarterCount(position)) {
				// Sort by predicted score and add bench players
				ArrayList<PlayerDataObject> sortedPlayers = new ArrayList<>(players);
				sortedPlayers.sort((p1, p2) -> Double.compare(p2.getPredictedScore(), p1.getPredictedScore()));
				
				for (int i = getStarterCount(position); i < sortedPlayers.size(); i++) {
					benchPlayers.add(sortedPlayers.get(i));
				}
			}
		}
		
		// Sort bench players by predicted score
		benchPlayers.sort((p1, p2) -> Double.compare(p2.getPredictedScore(), p1.getPredictedScore()));
		return benchPlayers;
	}
	
	private int getStarterCount(String position) {
		switch (position) {
			case "QB": return 1;
			case "RB": return 2;
			case "WR": return 2;
			case "TE": return 1;
			case "K": return 1;
			case "DST": return 1;
			default: return 0;
		}
	}
	
	private int getDraftPickNumber(PlayerDataObject player) {
		// This is a placeholder - in a real implementation, you'd track this
		// For now, return a calculated position based on ADP
		return (int) Math.round(player.getAvgADP());
	}
} 