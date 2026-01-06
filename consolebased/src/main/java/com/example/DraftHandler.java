package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import com.example.common.Logger;
import com.example.common.VaribleOddsPicker;
import com.example.common.PlayerModels.*;
import com.example.console.PlayerModel;
import com.example.console.TeamModel;

public class DraftHandler {
	private ArrayList<PlayerModel> playersLeft;
	private ArrayList<TeamModel> teams;
	private VaribleOddsPicker randomNumGen;
	private String username;
	private Scanner scanner;
	private ArrayList<DraftPick> draftPicks; // Track all draft picks
	private int currentRound;
	private int currentPick;

	public DraftHandler(ArrayList<PlayerModel> startingPlayers, int numTeams, String userTeamName, String desiredDraftPickString, Scanner scanner) {
		this.playersLeft = new ArrayList<PlayerModel>(startingPlayers);
		this.randomNumGen = new VaribleOddsPicker();
		this.teams = new ArrayList<TeamModel>();
		this.scanner = scanner;
		this.draftPicks = new ArrayList<DraftPick>();
		this.currentRound = 1;
		this.currentPick = 1;
		int desiredDraftPickInt;

		this.username = userTeamName; // For now, use team name as username (should be passed in)
		Logger.logInfo("Initializing DraftHandler for " + numTeams + " teams");

		if(desiredDraftPickString.matches("R") || desiredDraftPickString.matches("r")) {
			desiredDraftPickInt = (int)Math.floor(Math.random() * numTeams) +1;
		} else desiredDraftPickInt = Integer.parseInt(desiredDraftPickString);

		Logger.logUserInteraction(this.username, "DRAFT_POSITION", "Selected position: " + desiredDraftPickInt);

		for(int currTeamNum = 1; currTeamNum <= numTeams; currTeamNum++) {
			if(currTeamNum == desiredDraftPickInt) {
				this.teams.add(new TeamModel(userTeamName,true,currTeamNum));
				Logger.logTeamUpdate(-1, this.username, userTeamName, "USER_TEAM_CREATED");
			} else {
				this.teams.add(new TeamModel("Test Team " + currTeamNum, false,currTeamNum));
				Logger.logTeamUpdate(-1, "SYSTEM", "Test Team " + currTeamNum, "CPU_TEAM_CREATED");
			}
		}
		Logger.logInfo("DraftHandler initialized with " + this.teams.size() + " teams");
	}

	public void startDraft() {
		Logger.logInfo("Starting draft simulation");
		while(this.playersLeft.size() >= this.teams.size() &&
			this.teams.get(0).getTeamSize() <= 15) { 
			for(TeamModel currTeam : this.teams) {
				this.nextDraftPick(currTeam);
				currentPick++;
			}
			Collections.reverse(this.teams);
			currentRound++;
			currentPick = 1;
		}
		Logger.logInfo("Draft simulation completed");
	}

	private PlayerModel makeComputerDraftCertainPooss(TeamModel currTeam, int nextPick) {
		if(currTeam.getTeamSize() == 8) {
			if (currTeam.getTeamTreeMap().get(QuarterBackPlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextForcedPosition(currTeam, QuarterBackPlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}
		else if (currTeam.getTeamSize() == 10) {
			if (currTeam.getTeamTreeMap().get(TightEndPlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextForcedPosition(currTeam, TightEndPlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}
		else if (currTeam.getTeamSize() == 13) {
			if (currTeam.getTeamTreeMap().get(KickerPlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextForcedPosition(currTeam, KickerPlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}
		else if (currTeam.getTeamSize() == 14) {
			if (currTeam.getTeamTreeMap().get(DefensePlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextForcedPosition(currTeam, DefensePlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}	
		return null;
	}

	private PlayerModel getNextForcedPosition(TeamModel currTeam, String positionToDraft, int nextPick) {
		ArrayList<PlayerModel> copyOfThisPlayerSLeft = new ArrayList<PlayerModel>(this.playersLeft);
		for (int playerToSkipToPick = 1; playerToSkipToPick < nextPick; playerToSkipToPick++) {
			for (PlayerModel nextPlayerPossible : copyOfThisPlayerSLeft){
				if (nextPlayerPossible.getPosition() == positionToDraft) {
					copyOfThisPlayerSLeft.remove(nextPlayerPossible);
					break;
				}
			}
		}
		for (PlayerModel nextPlayerPossible : copyOfThisPlayerSLeft){
			if (nextPlayerPossible.getPosition() == positionToDraft) {
				return nextPlayerPossible;
			}
		}
		return null;
	}

	private void nextDraftPick(TeamModel currTeam) {
		int nextPick;
		PlayerModel nextPlayer;
		if(currTeam.isUserTeam()) {
			this.printNextAvailablePlayers();
			System.out.println("Your pick.... \n Enter the number of the player in the list to draft");
			nextPick = this.scanner.nextInt();
			this.scanner.nextLine(); // Consume the newline
			System.out.println();
			nextPlayer = this.playersLeft.get(nextPick-1);
			Logger.logUserInteraction(this.username, "PLAYER_SELECTED", "Selected player: " + nextPlayer.getFullName());
		}
		else {
			nextPick = this.randomNumGen.newOdds(this.playersLeft.size());
			if (this.makeComputerDraftCertainPooss(currTeam, nextPick) != null) {
				nextPlayer = makeComputerDraftCertainPooss(currTeam, nextPick);
			}
			else {
				nextPlayer = this.playersLeft.get(nextPick-1);
			}
			Logger.logPlayerDrafted(-1, "CPU", nextPlayer.getFullName(), nextPlayer.getPosition(), 1, nextPick);
		}
		System.out.println(currTeam.getTeamName()+ " picked " + nextPlayer);
		Logger.logTeamUpdate(-1, currTeam.isUserTeam() ? this.username : "CPU", currTeam.getTeamName(), "PLAYER_ADDED");
		currTeam.addPlayer(nextPlayer.getPosition(), nextPlayer);

		// Track this draft pick
		draftPicks.add(new DraftPick(currentRound, currentPick, currTeam.getTeamName(), nextPlayer));

		this.playersLeft.remove(nextPlayer);
		this.playersLeft.sort(null);
	}

	public void printTeams() {
		Logger.logInfo("Printing final team results");
		for(TeamModel currTeam : this.teams) {
			System.out.println(currTeam.getEnhancedDisplay());
		}
	}

	public void printDraftLog() {
		System.out.println("\n" + "=".repeat(80));
		System.out.println("                              DRAFT LOG");
		System.out.println("=".repeat(80));
		System.out.printf("%-8s %-8s %-20s %-25s %-8s %-8s %-8s %-12s%n", 
			"ROUND", "PICK", "TEAM", "PLAYER", "POS", "ADP", "DRAFTED", "PRED SCORE");
		System.out.println("-".repeat(80));
		
		for (DraftPick pick : draftPicks) {
			System.out.printf("%-8d %-8d %-20s %-25s %-8s %-8.1f %-8d %-12.1f%n",
				pick.round, pick.pick, pick.teamName, pick.player.getFullName(), 
				pick.player.getPosition(), pick.player.getAvgADP(), pick.draftedAt, pick.player.getPredictedScore());
		}
		System.out.println("=".repeat(80));
	}

	public ArrayList<TeamModel> returnTeams() {
		return this.teams;
	}

	public ArrayList<DraftPick> getDraftPicks() {
		return this.draftPicks;
	}

	private void printNextAvailablePlayers() {
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
		Logger.logUserInteraction(this.username, "SHOW_AVAILABLE_PLAYERS", "Displayed " + maxSizeOfList + " available players");
	}

	// Inner class to track draft picks
	private static class DraftPick {
		int round;
		int pick;
		String teamName;
		PlayerModel player;
		int draftedAt;

		DraftPick(int round, int pick, String teamName, PlayerModel player) {
			this.round = round;
			this.pick = pick;
			this.teamName = teamName;
			this.player = player;
			this.draftedAt = (round - 1) * 4 + pick; // Assuming 4 teams for now
		}
	}
}
