package com.example.Mock.StartingClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class DraftHandler {
	private ArrayList<PlayerModel> playersLeft;
	private ArrayList<TeamModel> teams;
	private VaribleOddsPicker randomNumGen;
	
	public DraftHandler(ArrayList<PlayerModel> startingPlayers, int numTeams, String userTeamName, String desiredDraftPickString) {
		this.playersLeft = new ArrayList<PlayerModel>(startingPlayers);
		this.randomNumGen = new VaribleOddsPicker();
		this.teams = new ArrayList<TeamModel>();
		int desiredDraftPickInt;

		if(desiredDraftPickString.matches("R") ||
		desiredDraftPickString.matches("r")) {
			desiredDraftPickInt = (int)Math.floor(Math.random() * numTeams) +1;
		}
		else desiredDraftPickInt = Integer.parseInt(desiredDraftPickString);

		for(int currTeamNum = 1; currTeamNum <= numTeams; currTeamNum++) {
			if(currTeamNum == desiredDraftPickInt) {
				this.teams.add(new TeamModel(userTeamName,true,currTeamNum));
			}
			else this.teams.add(new TeamModel("Test Team " + currTeamNum, false,currTeamNum));
		}
	}

	public void startDraft() {
		while(this.playersLeft.size() >= this.teams.size() &&
			this.teams.get(0).getTeamSize() <= 15) { 
			for(TeamModel currTeam : this.teams) {
				this.nextDraftPick(currTeam);
			}
			Collections.reverse(this.teams);
		}

	}



	private PlayerModel makeComputerDraftCertainPooss(TeamModel currTeam, int nextPick) {
		if(currTeam.getTeamSize() == 8) {
			if (currTeam.getTeamTreeMap().get(QuarterBackPlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextFocredPosstion(currTeam, QuarterBackPlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}
		else if (currTeam.getTeamSize() == 10) {
			if (currTeam.getTeamTreeMap().get(TightEndPlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextFocredPosstion(currTeam, TightEndPlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}
		else if (currTeam.getTeamSize() == 13) {
			if (currTeam.getTeamTreeMap().get(KickerPlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextFocredPosstion(currTeam, KickerPlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}
		else if (currTeam.getTeamSize() == 14) {
			if (currTeam.getTeamTreeMap().get(DefensePlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextFocredPosstion(currTeam, DefensePlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}	
		return null;
	}

	private PlayerModel getNextFocredPosstion(TeamModel currTeam, String possitionToDraft, int nextPick) {
		ArrayList<PlayerModel> copyOfThisPlayerSLeft = new ArrayList<PlayerModel>(this.playersLeft);
		for (int playerToSkipToPick = 1; playerToSkipToPick < nextPick; playerToSkipToPick++) {
			for (PlayerModel nextPlayerPossible : copyOfThisPlayerSLeft){
				if (nextPlayerPossible.getPosition() == possitionToDraft) {
					copyOfThisPlayerSLeft.remove(nextPlayerPossible);
					break;
				}
			}
		}
		for (PlayerModel nextPlayerPossible : copyOfThisPlayerSLeft){
			if (nextPlayerPossible.getPosition() == possitionToDraft) {
				return nextPlayerPossible;
			}
		}
		return null;
	}

	private void nextDraftPick(TeamModel currTeam) {
		int nextPick;
		PlayerModel nextPlayer;
		if(currTeam.isUserTeam()) {
			this.printNextAvilPlayers();
			System.out.println("Your pick.... \n Enter the number of the player in the list to draft");
			Scanner inputReader = new Scanner(System.in);
			nextPick = inputReader.nextInt();
			System.out.println();
			nextPlayer = this.playersLeft.get(nextPick-1);
		}
		else {
			nextPick = this.randomNumGen.newOdds(this.playersLeft.size());
			if (this.makeComputerDraftCertainPooss(currTeam, nextPick) != null) {
				nextPlayer = makeComputerDraftCertainPooss(currTeam, nextPick);
			}
			else {
				nextPlayer = this.playersLeft.get(nextPick-1);
			}
		}
		System.out.println(currTeam.getTeamName()+ " picked " + nextPlayer);
		currTeam.addPlayer(nextPlayer.getPosition(), nextPlayer);

		this.playersLeft.remove(nextPlayer);
		this.playersLeft.sort(null);
	}

	public void printTeams() {
		for(TeamModel currTeam : this.teams) {
			System.out.println(currTeam);
		}
	}

	public ArrayList<TeamModel> returnTeams() {
		return this.teams;
	}
	
	public ArrayList<PlayerModel> returnLeftPlayers() {
		return this.playersLeft;
	}

	private void printNextAvilPlayers() {
		System.out.println();
		System.out.println("Next Available Players Eligible to Draft");
		int maxSizeOfList = 6;
		if(maxSizeOfList > this.playersLeft.size()) {
			maxSizeOfList = this.playersLeft.size();
		}
		for(PlayerModel nextAvilPlayer : this.playersLeft.subList(0, maxSizeOfList)) {
			System.out.println(this.playersLeft.indexOf(nextAvilPlayer)+1 + " - " + nextAvilPlayer);
		}
		System.out.println();
	}





}
