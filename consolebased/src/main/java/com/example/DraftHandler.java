package com.example;

import java.io.InputStreamReader;
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

	private void nextDraftPick(TeamModel currTeam) {
		int nextPick;
		if(currTeam.isUserTeam()) {
			this.printNextAvilPlayers();
			System.out.println("Your pick.... \nEnter the number of the player in the list to draft");
			Scanner inputReader = new Scanner(System.in);
			nextPick = inputReader.nextInt();
			System.out.println();
		}
		else {
			nextPick = this.randomNumGen.newOdds(this.playersLeft.size());
		}	
		System.out.println(currTeam.getTeamName()+ " picked " + this.playersLeft.get(nextPick-1));
		currTeam.addPlayer(this.playersLeft.get(nextPick-1).getPosition(), this.playersLeft.remove(nextPick-1));
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
