package com.example.Mock.StartingClasses;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import javax.print.DocFlavor.STRING;

public class DraftHandler {
	private ArrayList<PlayerModel> playersLeft;
	private ArrayList<PlayerModel> draftLog;
	private ArrayList<TeamModel> teams;
	private VaribleOddsPicker randomNumGen;
	private int currRoundPick;
	private int currRound;
	private int nextUserPick;
	private int nextUserPickRound;
	private boolean isDraftOver;
	private String userTeamName;
	private int startingDraftPick;
	private String draftDate;
	private String draftTime;

	
	public DraftHandler(ArrayList<PlayerModel> startingPlayers, int numTeams, String userTeamName, String desiredDraftPickString) {
		this.playersLeft = startingPlayers;
		this.draftLog = new ArrayList<PlayerModel>();
		this.isDraftOver = false;
		this.randomNumGen = new VaribleOddsPicker();
		this.teams = new ArrayList<TeamModel>();
		this.currRoundPick = 1;
		this.currRound = 1;
		this.nextUserPickRound = 1;
		this.userTeamName = userTeamName;
		this.startingDraftPick = Integer.parseInt(desiredDraftPickString);
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		DateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
		this.draftDate = dateFormat.format(cal.getTime());
		this.draftTime = timeFormat.format(cal.getTime());
		
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
		while (!(this.currRoundPick== this.nextUserPick && this.currRound == this.nextUserPickRound) && this.currRound <= 15) {
			TeamModel currTeam = this.teams.get(this.currRoundPick-1);
			PlayerModel playerPicked = this.nextDraftPick(currTeam,this.currRoundPick);
			if(this.currRoundPick + "".length() == 1 && this.teams.size() > 9) {
				playerPicked.setSpotDrafted(this.currRound+".0"+this.currRoundPick);
			}
			else playerPicked.setSpotDrafted(this.currRound+"."+this.currRoundPick);
			playerPicked.setTeamDraftedBy(currTeam.getTeamName());
			currTeam.addPlayer(playerPicked.getPosition(),playerPicked);
			computerDraftLog.add(playerPicked);				
			this.checkForChangesInDraftEnv();
		}
		if (checkForEndOfDraft()) {
			computerDraftLog.add(new PlayerModel(null, null, null,0,0,0));
			this.isDraftOver = true;
		}
		this.draftLog.addAll(computerDraftLog);
		return computerDraftLog;
	}

	public ArrayList<PlayerModel> userDraftPick(int pick) {
		ArrayList<PlayerModel> userDraftLog = new ArrayList<PlayerModel>();
		for (TeamModel currTeam: this.teams) {
			if (currTeam.isUserTeam()) {
				PlayerModel playerPicked = this.nextDraftPick(currTeam, pick);
				if(this.currRoundPick + "".length() == 1 && this.teams.size() > 9) {
					playerPicked.setSpotDrafted(this.currRound+".0"+this.currRoundPick);
				}
				else playerPicked.setSpotDrafted(this.currRound+"."+this.currRoundPick);
				playerPicked.setTeamDraftedBy(currTeam.getTeamName());
				userDraftLog.add(playerPicked);
				currTeam.addPlayer(playerPicked.getPosition(),playerPicked);
				this.findUserNextDraftPick(this.teams);
				this.checkForChangesInDraftEnv();
				break;
			}
		}
		if (checkForEndOfDraft()) {
			userDraftLog.add(new PlayerModel(null, null, null,0,0,0));
			this.isDraftOver = true;
		}
		this.draftLog.addAll(userDraftLog);
		return userDraftLog;
	}

	public ArrayList<TeamModel> getTeams() {
		return this.teams;
	}

	public ArrayList<PlayerModel> retunPlayesLeft() {
		return this.playersLeft;
	}

	public ArrayList<PlayerModel> returnDraftLog() {
		return this.draftLog;
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

	public int getNextUserPickRound() {
		return this.nextUserPickRound;
	}

	public boolean isDraftOver() {
		return this.isDraftOver;
	}

	public String getUserTeamName() {
		return this.userTeamName;
	}

	public int startingDraftPick() {
		return this.startingDraftPick;
	}

	public int getDraftSize() {
		return this.teams.size();
	}

	public String getDate() {
		return this.draftDate;
	}

	public String getTime() {
		return this.draftTime;
	}

	public List<TreeMap<String,ArrayList<PlayerModel>>> getAllTeamsMap() {
		List<TreeMap<String,ArrayList<PlayerModel>>> allTeamsMap = new ArrayList<TreeMap<String,ArrayList<PlayerModel>>>();
		for(TeamModel currTeam : this.teams) {
			allTeamsMap.add(currTeam.getTeamTreeMap());
		}
		return allTeamsMap;
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
		else if (currTeam.getTeamSize() == 12) {
			if (currTeam.getTeamTreeMap().get(KickerPlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextFocredPosstion(currTeam, KickerPlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}
		else if (currTeam.getTeamSize() == 13) {
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

	private void findUserNextDraftPick(ArrayList<TeamModel> teamsToCopy) {
		ArrayList<TeamModel> copyOfTeams = new ArrayList<TeamModel>();
		copyOfTeams.addAll(teamsToCopy);
		Collections.reverse(copyOfTeams);
	
		for(TeamModel currTeam : copyOfTeams) {
			if (currTeam.isUserTeam()) {
				this.nextUserPick = copyOfTeams.indexOf(currTeam)+1;
				break;
			}
		}
		this.nextUserPickRound++;
	}


	private void checkForChangesInDraftEnv() {
		if(this.currRoundPick+1 > this.teams.size()) {
			Collections.reverse(this.teams);
			this.currRoundPick = 1;
			this.currRound++;
		}
		else {
			this.currRoundPick++;
		}
	}

	private boolean checkForEndOfDraft() {
		if (this.currRound > 15) {
			for(TeamModel currTeam : this.teams) {
				System.out.println(currTeam.getTeamTreeMap());
			}
			if(this.teams.get(0).getTeamName() != "Test Team 1") {
				Collections.reverse(this.teams);
			}
			return true;
		}
		return false;
	}
}
