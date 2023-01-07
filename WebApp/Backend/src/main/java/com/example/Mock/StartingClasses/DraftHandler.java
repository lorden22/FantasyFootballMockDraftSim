package com.example.Mock.StartingClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class DraftHandler {
	private ArrayList<PlayerModel> playersLeft;
	private ArrayList<TeamModel> teams;
	private VaribleOddsPicker randomNumGen;
	private int currRoundPick;
	private int currRound;
	private int nextUserPick;
	
	public DraftHandler(ArrayList<PlayerModel> startingPlayers, int numTeams, String userTeamName, String desiredDraftPickString) {
		this.playersLeft = startingPlayers;
		this.randomNumGen = new VaribleOddsPicker();
		this.teams = new ArrayList<TeamModel>();
		this.currRoundPick = 1;
		this.currRound = 1;


		if(desiredDraftPickString.matches("R") ||
		desiredDraftPickString.matches("r")) {
			this.nextUserPick = (int)Math.floor(Math.random() * numTeams) +1;
		}
		else this.nextUserPick = Integer.parseInt(desiredDraftPickString);

		for(int currTeamNum = 1; currTeamNum <= numTeams; currTeamNum++) {
			if(currTeamNum == this.nextUserPick) {
				this.teams.add(new TeamModel(userTeamName,true,currTeamNum));
			}
			else this.teams.add(new TeamModel("Test Team " + currTeamNum, false,currTeamNum));
		}
	}

	public ArrayList<PlayerModel> simTo(){
		ArrayList<PlayerModel> computerDraftLog = new ArrayList<PlayerModel>();
		while (this.currRoundPick != this.nextUserPick && this.currRound <= 15) {
			TeamModel currTeam = this.teams.get(this.currRoundPick-1);
			PlayerModel playerPicked = this.nextDraftPick(currTeam,this.currRoundPick);
			playerPicked.setSpotDrafted(this.currRound+"."+this.currRoundPick);
			playerPicked.setTeamDraftedBy(currTeam.getTeamName());
			computerDraftLog.add(playerPicked);
			this.checkForChangesInDraftEnv();
		}
		if (checkForEndOfDraft()) {
			computerDraftLog.add(new PlayerModel(null, null, null,0,0,0));
		}
		return computerDraftLog;
	}

	public ArrayList<PlayerModel> userDraftPick(int pick) {
		ArrayList<PlayerModel> userDraftLog = new ArrayList<PlayerModel>();
		for (TeamModel currTeam: this.teams) {
			if (currTeam.isUserTeam()) {
				PlayerModel playerPicked = this.nextDraftPick(currTeam, pick);
				playerPicked.setSpotDrafted(this.currRound+"."+this.currRoundPick);
				playerPicked.setTeamDraftedBy(currTeam.getTeamName());
				userDraftLog.add(playerPicked);
				this.checkForChangesInDraftEnv();
				break;
			}
		}
		if (checkForEndOfDraft()) {
			userDraftLog.add(new PlayerModel(null, null, null,0,0,0));
		}
		return userDraftLog;
	}

	public ArrayList<TeamModel> returnTeams() {
		return this.teams;
	}

	public ArrayList<PlayerModel> retunPlayesLeft() {
		return this.playersLeft;
	}

	public int getCurrRound() {
		return this.currRound;
	}

	public int getCurrPick() {
		return this.currRoundPick;
	}

	public int getNextUserPick() {
		return this.nextUserPick;
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

	private PlayerModel nextDraftPick(TeamModel currTeam, int nextPick) {
		PlayerModel nextPlayer;
		if(currTeam.isUserTeam()) {
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
		this.playersLeft.remove(nextPlayer);
		this.playersLeft.sort(null);
		return nextPlayer;
	}

	private int findNexUserDraftPick(){
		for(TeamModel currTeam : this.teams) {
			if (currTeam.isUserTeam()) {
				return this.teams.indexOf(currTeam)+1;
			}
		}
		return -1;
	}

	private void checkForChangesInDraftEnv() {
		if(this.currRoundPick+1 > this.teams.size()) {
			Collections.reverse(this.teams);
			this.currRoundPick = 1;
			this.currRound++;
			this.nextUserPick = findNexUserDraftPick();
		}
		else {
			this.currRoundPick++;
		}
		System.out.println(this.currRound+"."+this.currRoundPick);
	}

	private boolean checkForEndOfDraft() {
		if (this.currRound > 15) {
			return true;
		}
		return false;
	}
}
