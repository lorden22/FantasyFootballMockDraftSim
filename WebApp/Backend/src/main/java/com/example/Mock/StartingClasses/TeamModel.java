package com.example.Mock.StartingClasses;
import java.util.ArrayList;
import java.util.TreeMap;

public class TeamModel implements Comparable<TeamModel>{
	private String teamName;
	private boolean userTeam = false;
	private TreeMap<String,ArrayList<PlayerModel>> thisTeamPlayers;
	private int teamNumber;
	
	
	
	public TeamModel(String teamName, Boolean userTeam, int teamNumber){
		this.teamName = teamName;
		this.thisTeamPlayers = new TreeMap<String,ArrayList<PlayerModel>>();
		this.userTeam = userTeam;
		this.teamNumber = teamNumber;
		
		ArrayList<PlayerModel> quarterBackList = new ArrayList<PlayerModel>();
		ArrayList<PlayerModel> runningBackList = new ArrayList<PlayerModel>();
		ArrayList<PlayerModel> tightEndList = new ArrayList<PlayerModel>();
		ArrayList<PlayerModel> wideReceiverList = new ArrayList<PlayerModel>();
		ArrayList<PlayerModel> kickerList = new ArrayList<PlayerModel>();
		ArrayList<PlayerModel> defenseList = new ArrayList<PlayerModel>();
		
		this.thisTeamPlayers.put(QuarterBackPlayerModel.POSITIONSHORTHANDLE, quarterBackList);
		this.thisTeamPlayers.put(RunningBackPlayerModel.POSITIONSHORTHANDLE, runningBackList);
		this.thisTeamPlayers.put(TightEndPlayerModel.POSITIONSHORTHANDLE, tightEndList);
		this.thisTeamPlayers.put(WideReceiverPlayerModel.POSITIONSHORTHANDLE, wideReceiverList);
		this.thisTeamPlayers.put(KickerPlayerModel.POSITIONSHORTHANDLE,kickerList);
		this.thisTeamPlayers.put(DefensePlayerModel.POSITIONSHORTHANDLE,defenseList);
	}

	public void addPlayer(String playerClassString, PlayerModel playerToAdd) {
		this.thisTeamPlayers.get(playerClassString).add(playerToAdd);
		this.thisTeamPlayers.get(playerClassString).sort(null);
	}

	public String getTeamName() {
		return this.teamName;
	}

	public int getTeamSize() {
		int teamSize = 0;
		for(String currPostion : this.thisTeamPlayers.keySet()) {
			teamSize += this.thisTeamPlayers.get(currPostion).size();
		}
		return teamSize;
	}

	private int getTeamNumber() {
		return this.teamNumber;
	}

	public boolean isUserTeam() {
		return this.userTeam;
	}

	public int compareTo(TeamModel otherTeam) {
		if (this.getTeamNumber() > otherTeam.getTeamNumber()) {
			return 1;
		}
		else if (this.getTeamNumber() < otherTeam.getTeamNumber()) {
			return -1;
		}
		else return 0;
	}

	private PlayerModel getStartersForPosition(ArrayList<PlayerModel> playersAtThisPosition) {
		if(playersAtThisPosition.isEmpty()) {
			return null;
		}
		else {
			PlayerModel starter = playersAtThisPosition.get(0);
			for(PlayerModel nextPlayer : playersAtThisPosition) {
				if(starter.getPredictedScore() / 17.0 < nextPlayer.getPredictedScore() /17) {
					starter = nextPlayer;
				}
			}
			return starter;
		}
	}

	private String getSpecialPositionStarters(TreeMap<String,ArrayList<PlayerModel>> copyOfThisTeamPlayers) {
		String kickerStarterString = KickerPlayerModel.POSITIONSHORTHANDLE + " - None";
		String defenseStarterString = DefensePlayerModel.POSITIONSHORTHANDLE + " - None";
		for(String currPosition : copyOfThisTeamPlayers.keySet()) {
			if( (currPosition.equals(KickerPlayerModel.POSITIONSHORTHANDLE) || 
				currPosition.equals(DefensePlayerModel.POSITIONSHORTHANDLE)) &&
				!copyOfThisTeamPlayers.get(currPosition).isEmpty()) {
					PlayerModel currSpecialStarter = this.getStartersForPosition(copyOfThisTeamPlayers.get(currPosition));
					if(currPosition.equals(DefensePlayerModel.POSITIONSHORTHANDLE)) {
						defenseStarterString = currPosition + " - " + currSpecialStarter;
					}
					else {
						kickerStarterString = currPosition + " - " + currSpecialStarter;
					}
					copyOfThisTeamPlayers.get(currPosition).remove(currSpecialStarter);
				}
			} 
		return kickerStarterString + "\n" + defenseStarterString;
	}

	public TreeMap<String,ArrayList<PlayerModel>> getTeamTreeMap() {
		return this.thisTeamPlayers;
	}

	public String toString() {
		TreeMap<String,ArrayList<PlayerModel>> copyOfThisTeamPlayers = new TreeMap<String,ArrayList<PlayerModel>>();

		for(String currPosition : thisTeamPlayers.keySet()) {
			copyOfThisTeamPlayers.put(currPosition, new ArrayList<PlayerModel>(thisTeamPlayers.get(currPosition)));
		}
		
		PlayerModel flexPlayer = null;	
		
		String returnString = "\n       " + this.teamName + "      " + "\n---------------------------------------\n";
		
		for(String currPosition : copyOfThisTeamPlayers.keySet()) {

			if(currPosition.equals(KickerPlayerModel.POSITIONSHORTHANDLE) ||
			currPosition.equals(DefensePlayerModel.POSITIONSHORTHANDLE)) {
				continue;
			}

			int amountOfStarters = 1;
			
			ArrayList<PlayerModel> playersAtThisPosition = copyOfThisTeamPlayers.get(currPosition);
			
			if(currPosition.equals(RunningBackPlayerModel.POSITIONSHORTHANDLE) ||
			currPosition.equals(WideReceiverPlayerModel.POSITIONSHORTHANDLE)) {
				amountOfStarters = 2;
			}

			for (int currStarters = 1; currStarters <= amountOfStarters; currStarters++) {

				PlayerModel currStarer = this.getStartersForPosition(playersAtThisPosition);
				if(currStarer != null) { 
					returnString += currPosition + " - " + currStarer + "\n";
					playersAtThisPosition.remove(currStarer);
				}
				else returnString += currPosition + " - None\n";
			}

			if(currPosition.equals(RunningBackPlayerModel.POSITIONSHORTHANDLE) ||
			currPosition.equals(WideReceiverPlayerModel.POSITIONSHORTHANDLE) ||
			currPosition.equals(TightEndPlayerModel.POSITIONSHORTHANDLE)) {
					
				
				PlayerModel firstPlayerOnBench = this.getStartersForPosition(playersAtThisPosition);
				if(firstPlayerOnBench != null) {
					if(flexPlayer == null) {
						flexPlayer = firstPlayerOnBench;
					}
					else if(flexPlayer.getPredictedScore() / 16 < firstPlayerOnBench.getPredictedScore() /16) {
						playersAtThisPosition.remove(firstPlayerOnBench);
						copyOfThisTeamPlayers.get(flexPlayer.getPosition()).add(flexPlayer);
						flexPlayer = firstPlayerOnBench;
					}
				} 
			}				
		}
		if (flexPlayer == null) {
			returnString += "Flex - None \n";
		}
		else {	
			returnString += "Flex - " + flexPlayer + "\n";
			copyOfThisTeamPlayers.get(flexPlayer.getPosition()).remove(flexPlayer);
		}

		returnString += this.getSpecialPositionStarters(copyOfThisTeamPlayers) + "\n\n";

		for(String currPosition : copyOfThisTeamPlayers.keySet()) {
			for(PlayerModel nextPositionBenchPlayer : copyOfThisTeamPlayers.get(currPosition)) {
				returnString += "Bench/" + currPosition + " - " + nextPositionBenchPlayer + "\n";
			}
		}
		return returnString;
	}
	
}
