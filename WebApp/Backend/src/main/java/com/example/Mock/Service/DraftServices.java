package com.example.Mock.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.ldap.HasControls;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties.Template;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.Mock.DAO.DraftDataObject;
import com.example.Mock.DAO.DraftedTeamsDataObject;
import com.example.Mock.StartingClasses.PlayerModel;
import com.example.Mock.StartingClasses.TeamModel;
import com.example.Mock.StartingClasses.VaribleOddsPicker;


@Service
@Scope(value="prototype")
public class DraftServices {

    private TreeMap<Integer,DraftDataObject> allPastsDraftsDataObject;
    private DraftDataObject draftDataObject;
    private int nextDraftID = 1;
    private VaribleOddsPicker randomNumGen = new VaribleOddsPicker();


    public DraftServices() {
        this.allPastsDraftsDataObject = new TreeMap<Integer,DraftDataObject>();
    }

    public int getUserMostRecentDraftID(JdbcTemplate jdbcTemplate, String username) {
        return jdbcTemplate.queryForObject("SELECT MAX(draft_id) FROM drafts WHERE username = '" + username + "'", Integer.class);
    }

    public String getUserTeamName(JdbcTemplate jdbcTemplate, String username) {
        int draftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        return jdbcTemplate.queryForObject("SELECT team_name FROM teams WHERE user_team = 1 and draft_id = " + draftID , String.class);
    }

    public int getDraftSize(JdbcTemplate jdbcTemplate, String username) {
        return jdbcTemplate.queryForObject("SELECT num_teams FROM drafts WHERE username = '" + username + "' AND draft_id = (SELECT MAX(draft_id) FROM drafts)", Integer.class);
    }


    public int getUserStartingDraftSpot(JdbcTemplate jdbcTemplate, String username) {
        String teamName = this.getUserTeamName(jdbcTemplate, username);
        int draftId = this.getUserMostRecentDraftID(jdbcTemplate, username);
        return jdbcTemplate.queryForObject("SELECT draft_spot FROM teams WHERE team_name = '" + teamName + "' AND draft_id = " + draftId + " AND user_team = 1", Integer.class);
    }

    public List<PlayerModel> getPlayersLeft() {
        return this.draftDataObject.getPlayersLeft();
    }

    public List<PlayerModel> getDraftedPlayers() {
        return this.draftDataObject.getDraftedPlayers();
    }

    public int getCurrRound(JdbcTemplate jdbcTemplate, String username) {
        int draftId = this.getUserMostRecentDraftID(jdbcTemplate, username);
        int currRound = jdbcTemplate.queryForObject("SELECT curr_round FROM drafts WHERE username = '" + username + "' AND draft_id = " + draftId, Integer.class);
        return currRound;
    }

    public int getCurrPick(JdbcTemplate jdbcTemplate, String username) {
        int draft_id = this.getUserMostRecentDraftID(jdbcTemplate, username);
        int currPick = jdbcTemplate.queryForObject("SELECT curr_pick FROM drafts WHERE username = '" + username + "' AND draft_id = " + draft_id, Integer.class);
        return currPick;
    }

    private List<Integer> getDraftedTeamStringArray(JdbcTemplate jdbcTemplate, int draftID, int draftSpot) {
        String teamArray = jdbcTemplate.queryForObject("SELECT team_array FROM teams WHERE draft_id = " + draftID + " AND draft_spot = " + draftSpot, String.class);
        String[] teamArraySplit = teamArray.split(",");
        List<Integer> teamArrayInt = new ArrayList<Integer>();
        for(String player : teamArraySplit) {
            teamArrayInt.add(Integer.parseInt(player));
        }
        return teamArrayInt;
    }

    private List<Integer> getAllDraftedPlayers(JdbcTemplate jdbcTemplate, String username) {
    int draftSize = this.getDraftSize(jdbcTemplate, username);
    List<Integer> allDraftedPlayers = new ArrayList<Integer>();
    for (int i = 1; i <= draftSize; i++) {
        List<Integer> teamArray = this.getDraftedTeamStringArray(jdbcTemplate, this.getUserMostRecentDraftID(jdbcTemplate, username), i);
        allDraftedPlayers.addAll(teamArray);
    }
    return allDraftedPlayers;
    }
    
    public int getNextUserPick(JdbcTemplate jdbcTemplate, String username){
        int draftSize = this.getDraftSize(jdbcTemplate, username);
        int userPickInOddRound = this.getUserStartingDraftSpot(jdbcTemplate, username);
        int userPickInEvenRound = Math.abs(userPickInOddRound - draftSize) + 1;
        int currRound = this.getCurrRound(jdbcTemplate, username);
        int currPick = this.getCurrPick(jdbcTemplate, username);
        if(currRound % 2 == 0 && userPickInEvenRound >= currPick) {
            System.out.println("Next User Pick - " + userPickInEvenRound);
            return userPickInEvenRound;
        }
        else {
            System.out.println("Next User Pick - " + userPickInOddRound);
            return userPickInOddRound;
        }
        //return this.draftDataObject.getNextUserPick();
    }

    public int getNextUserPickRound(JdbcTemplate jdbcTemplate, String username) {
        int draftSize = this.getDraftSize(jdbcTemplate, username);
        int userPickInOddRound = this.getUserStartingDraftSpot(jdbcTemplate, username);
        int userPickInEvenRound = Math.abs(userPickInOddRound - draftSize) + 1;
        int currRound = this.getCurrRound(jdbcTemplate, username);
        int currPick = this.getCurrPick(jdbcTemplate, username);
        if(currRound % 2 == 0 && userPickInEvenRound >= currPick) {
            System.out.println("Next User Pick Round - " + currRound);
            return currRound;
        }
        else {
            System.out.println("Next User Pick Round - " + (currRound + 1));
            return currRound + 1;
        }
    }

    public List<PlayerModel> startDraft(String username, String teamName, int draftSize, int desiredDraftPosition, DraftDataObject draftDataObject, JdbcTemplate jdbcTemplate) {
        this.draftDataObject = new DraftDataObject();
        jdbcTemplate.update("INSERT INTO drafts (username, num_teams, curr_round, curr_pick) VALUES (?,?,?,?)", username, draftSize, 1, 1);
        int currDraftID = jdbcTemplate.queryForObject("SELECT MAX(draft_id) FROM drafts", Integer.class);
        jdbcTemplate.update("INSERT INTO teams (team_name, draft_spot, draft_id, user_team) VALUES (?,?,?,?)", teamName, desiredDraftPosition, currDraftID, 1);
        this.createCPUTeamsForThisDraft(draftSize, desiredDraftPosition, currDraftID, username, teamName, jdbcTemplate);
        System.out.println(this.createPlayerModelFromDB(jdbcTemplate.queryForList("SELECT * FROM players")));
        this.draftDataObject.startDraft(teamName, draftSize, desiredDraftPosition,this.nextDraftID);
        return this.createPlayerModelFromDB(jdbcTemplate.queryForList("SELECT * FROM players"));
    }

    public List<PlayerModel> simTo(String username, JdbcTemplate jdbcTemplate) {
        List<PlayerModel> players = this.draftDataObject.simComputerPicks();
        if(this.draftDataObject.isDraftOver()) {
            saveDraftHistory();
        }
        return players; 
    }

    /*
    public ArrayList<Strings> simTo(String username, JdbcTemplate jdbcTemplate){
        ArrayList<Strings> computerDraftLog = new ArrayList<Strings>();
        int currRound = this.getCurrRound();
        int currRoundPick = this.getCurrPick();
        int nextUserPick = this.getNextUserPick();
        int nextUserPickRound = this.getNextUserPickRound();
        int draftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        while (!(currRoundPick == nextUserPick && currRound == nextUserPickRound) && currRound <= 15) {
            // TeamModel currTeam = this.teams.get(this.currRoundPick-1);
            // PlayerModel playerPicked = this.nextDraftPick(currTeam,this.currRoundPick);
            int nextPlayer = this.nextDraftPick(currPick,currRoundPick,draftID,-1,jdbcTemplate);
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

    private ArrayList<Integer> getPlayersNotDrafted (int draftID, jdbcTemplate jdbcTemplate) {
        ArrayList<Integer> playersNotDrafted = new ArrayList<Integer>();
        int draftSize = jdbcTemplate.queryForObject("Select count(players) FROM players", Integer.class);
        for(int i = 1; i <= draftSize; i++) {
            playersNotDrafted.add(i);
        }
        List<Map<String,Object>> playersDrafted = jdbcTemplate.queryForList("SELECT player_id FROM drafted_players WHERE draft_id = ?", draftID);
        for(Map<String,Object> player : playersDrafted) {
            playersNotDrafted.remove((Integer)player.get("player_id"));
        }
        System.out.println("Players Not Drafted: " + playersNotDrafted);
        return playersNotDrafted;
    }


    private int nextDraftPick(int currPick, int currRoundPick, int draftID, int nextPick, jdbcTemplate jdbcTemplate) {
		int nextPlayer;
		if(nextPick != -1) {
            jdbcTemplate.update("UPDATE teams SET team_array = CONCAT(COALESCE((SELECT team_array FROM teams WHERE draft_spot = ?), ''), ' ?,') where
                draft_spot = ?", currPick, nextPick, nextPick);
			//nextPlayer = this.playersLeft.get(nextPick-1);
            //int currPick = this.getCurrPick();
            //int draftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
		}
		else {
            boolean forcePickNeeded = this.checkForForcePick(currPick, currRoundPick, jdbcTemplate);
			nextPlayer = this.randomNumGen.newOdds(this.returnPlayersNotDrafted(draftID, jdbcTemplate));
			## HERE if (this.makeComputerDraftCertainPoss(currTeam, nextPick) != null) {
				nextPlayer = makeComputerDraftCertainPoss(currTeam, nextPick);
			}
			else {
				nextPlayer = this.playersLeft.get(nextPick-1);
			}
		}
		this.playersLeft.remove(nextPlayer);
		this.playersLeft.sort(null);
		return nextPlayer;
	}

    private int checkForForcePickNeeded(int currPick, int currRoundPick, int draftID, jdbcTemplate jdbcTemplate) {
        String teamArray = this.getDraftedTeamStringArray(jdbcTemplate, draftID, currPick);
        if(currRoundPick == 8) {
			if (currTeam.getTeamTreeMap().get(QuarterBackPlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextFocredPosstion(currTeam, QuarterBackPlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}
		else if (currRoundPick == 10) {
			if (currTeam.getTeamTreeMap().get(TightEndPlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextFocredPosstion(currTeam, TightEndPlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}
		else if (currRoundPick == 12) {
			if (currTeam.getTeamTreeMap().get(KickerPlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextFocredPosstion(currTeam, KickerPlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}
		else if (currRoundPick == 13) {
			if (currTeam.getTeamTreeMap().get(DefensePlayerModel.POSITIONSHORTHANDLE).isEmpty()) {
				return getNextFocredPosstion(currTeam, DefensePlayerModel.POSITIONSHORTHANDLE,nextPick);
			}
		}	
		return -1;

    }

    */

    public List<PlayerModel> userDraftPick(int pick){
        List<PlayerModel> players = this.draftDataObject.userDraftPick(pick);
        if(this.draftDataObject.isDraftOver()) {
            saveDraftHistory();
        }
        return players;
    }

    public boolean isDraftOver(){
        return this.draftDataObject.isDraftOver();
    }

    public boolean checkForPastDrafts() {
        return !(this.allPastsDraftsDataObject.isEmpty());
    }

    public boolean deleteThisDraft() {
        this.draftDataObject = null;
        return checkForDraft();    
    }

    public boolean checkForDraft() {
        return  this.draftDataObject != null;
    }

    public HashMap<String,String> returnDraftMetaData(int nextDraftID) {
         return this.allPastsDraftsDataObject.get(nextDraftID-1).getDraftMetaData();
    }

    public List<PlayerModel> returnDraftedPlayers(int nextDraftID) {
        return this.allPastsDraftsDataObject.get(nextDraftID-1).getDraftedPlayers();
    }

    public ArrayList<HashMap<String,String>> getDraftHistoryMetaData() {
        System.out.println(this.allPastsDraftsDataObject.size());
        ArrayList<HashMap<String,String>> allMetaData = new ArrayList<HashMap<String,String>>();
        for(int currDraftIndex : this.allPastsDraftsDataObject.keySet()) {
            System.out.println((currDraftIndex));
            System.out.println(this.allPastsDraftsDataObject.get(currDraftIndex).getDraftMetaData());
            allMetaData.add(this.allPastsDraftsDataObject.get(currDraftIndex).getDraftMetaData());
        }
        return allMetaData;
    }

    public List<PlayerModel> getDraftHistoryDraftedPlayerLog(int draftID) {
        return this.allPastsDraftsDataObject.get(draftID-1).getDraftedPlayers();
    }

    public List<TreeMap<String,ArrayList<PlayerModel>>> getDraftHistoryAllTeamsMap(int draftID) {
        return this.allPastsDraftsDataObject.get(draftID-1).getDraftHistoryAllTeamsMap();
    }

    public List<TeamModel> getDraftHistoryTeamList(int draftID) {
        return this.allPastsDraftsDataObject.get(draftID-1).getTeams();
    }

    private void saveDraftHistory() {
        this.allPastsDraftsDataObject.put(this.nextDraftID-1, this.draftDataObject);
        this.nextDraftID++;
        System.out.println("Draft History Saved - Draft ID: " + (this.nextDraftID));
    }

    private void createCPUTeamsForThisDraft(int draftSize, int desiredDraftPosition, int currDraftID, String username, String teamName, JdbcTemplate jdbcTemplate) {
        for(int i = 1; i <= draftSize; i++) {
            if(i != desiredDraftPosition) {
                jdbcTemplate.update("INSERT INTO teams (team_name, draft_spot, draft_id, user_team) VALUES (?,?,?,?)", "CPU Team " + i, i, currDraftID, 0);
            }
        }
    }

    private List<PlayerModel> createPlayerModelFromDB(List<Map<String,Object>> playersFromDB) {
        List<PlayerModel> players = new ArrayList<PlayerModel>();
        for(Map<String,Object> player : playersFromDB) {
            String fullName = (String)player.get("name");
            String[] name = fullName.split(" ");
            String firstName = name[0];
            String lastName = String.join(" ", Arrays.copyOfRange(name, 1, name.length));
            players.add(new PlayerModel(
                    firstName,
                    lastName,
                    (String) player.get("position"),
                    ((BigDecimal) player.get("predicted_score")).doubleValue(),
                    (Integer) player.get("player_rank")
                ));
        }
        return players;
    }
}
