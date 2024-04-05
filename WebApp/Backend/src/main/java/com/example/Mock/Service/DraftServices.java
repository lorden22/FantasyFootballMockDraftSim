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

    // checks from main page
    public boolean checkForCurrentDraft(JdbcTemplate jdbcTemplate, String username) {
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM drafts WHERE username='" + username + "' and complete_status=0",Integer.class);
        return count >= 1;
    }

    public boolean checkForPastDrafts(JdbcTemplate jdbcTemplate, String username) {
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM drafts WHERE username='" + username + "' and complete_status=0",Integer.class);
        return count >= 1;
    }

    public boolean deleteThisDraft(JdbcTemplate jdbcTemplate, String username) {
        int draftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        jdbcTemplate.update("DELETE FROM drafts WHERE draft_id = " + draftID);
        jdbcTemplate.update("DELETE FROM teams WHERE draft_id = " + draftID);
        return this.checkForCurrentDraft(jdbcTemplate, username);
    }

    // check for draft over
    public boolean isDraftOver(JdbcTemplate jdbcTemplate){
        return this.draftDataObject.isDraftOver();
    }

    // meta data for draft
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

    public int getUserStartingDraftSpot(JdbcTemplate jdbcTemplate, String username) {
        String teamName = this.getUserTeamName(jdbcTemplate, username);
        int draftId = this.getUserMostRecentDraftID(jdbcTemplate, username);
        return jdbcTemplate.queryForObject("SELECT draft_spot FROM teams WHERE team_name = '" + teamName + "' AND draft_id = " + draftId + " AND user_team = 1", Integer.class);
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

    public List<PlayerModel> getPlayersLeft(JdbcTemplate jdbcTemplate, String username) {
         return null;
    }

    public List<PlayerModel> getDraftedPlayers(JdbcTemplate jdbcTemplate, String username) {
        return null;
    }
    
    // controls for draft itself
    public List<PlayerModel> startDraft(JdbcTemplate jdbcTemplate, String username, String teamName, int draftSize, int desiredDraftPosition, DraftDataObject draftDataObject) {
        jdbcTemplate.update("INSERT INTO drafts (username, num_teams, curr_round, curr_pick) VALUES (?,?,?,?)", username, draftSize, 1, 1);
        int currDraftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        jdbcTemplate.update("INSERT INTO teams (team_name, draft_spot, draft_id, user_team) VALUES (?,?,?,?)", teamName, desiredDraftPosition, currDraftID, 1);
        this.createCPUTeamsForThisDraft(jdbcTemplate, draftSize, desiredDraftPosition, currDraftID);
        System.out.println("Creating Draft players from DB");
        //System.out.println(this.createPlayerModelFromDB(jdbcTemplate, jdbcTemplate.queryForList("SELECT * FROM players")));
        //return this.createPlayerModelFromDB(jdbcTemplate, jdbcTemplate.queryForList("SELECT * FROM players"));
        return null;
    }

    public void createCPUTeamsForThisDraft(JdbcTemplate jdbcTemplate, int draftSize, int desiredDraftPosition, int currDraftID) {
        for(int i = 1; i <= draftSize; i++) {
            if(i != desiredDraftPosition) {
                jdbcTemplate.update("INSERT INTO teams (team_name, draft_spot, draft_id, user_team) VALUES (?,?,?,?)", "CPU Team " + i, i, currDraftID, 0);
            }
        }
    }

    public List<PlayerModel> userDraftPick(JdbcTemplate jdbcTemplate, String username, int pick){
        List<PlayerModel> players = this.draftDataObject.userDraftPick(pick);
        if(this.draftDataObject.isDraftOver()) {
            //saveDraftHistory();
        }
        return players;
    }

    /*
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

    public boolean makePick(JdbcTemplate jdbcTemplate, int draftID, int pick, int playerRankToPick) {
        String teamArray = jdbcTemplate.queryForObject("SELECT COALESCE(team_array, '') AS team_array FROM teams WHERE draft_spot= " + draftSpot + "and draft_id="+draftID , String.class);
        teamArray += playerRankToPick + ",";
        jdbcTemplate.update("UPDATE teams SET team_array = ? WHERE draft_spot = ? and draft_id = ?", teamArray, pick, draftID);
        return true;
    }
    

    public List<PlayerModel> simTo(JdbcTemplate jdbcTemplate, String username) {
        List<PlayerModel> players = this.draftDataObject.simComputerPicks();
        if(this.draftDataObject.isDraftOver()) {
            //saveDraftHistory();
        }
        return players; 
    }

    // Parsing Team Data from DB to create TeamTreeMap
    private List<Integer> createTeamIntArrayFromDB(JdbcTemplate jdbcTemplate, int draftID, int draftSpot) {
        List<Integer> teamArrayInt = new ArrayList<Integer>();
        String teamArray = jdbcTemplate.queryForObject("SELECT COALESCE(team_array, '') AS team_array FROM teams WHERE draft_spot= " + draftSpot + "and draft_id="+draftID , String.class);
        if (teamArray.length() > 0) {
            String[] teamArraySplit = teamArray.split(",");
            for(String player : teamArraySplit) {
                teamArrayInt.add(Integer.parseInt(player));
            }
        }
        return teamArrayInt;
    }

    private List<PlayerModel> createPlayerModelListFromIntList(JdbcTemplate jdbcTemplate, List<Integer> teamArrayInt, int draftSize) {
        List<PlayerModel> playerModelList = new ArrayList<PlayerModel>();
        for(int playerInt : teamArrayInt) {
            playerModelList.add(this.createPlayerModel(jdbcTemplate, playerInt, draftSize));
        }
        return playerModelList;
    }

    private PlayerModel createPlayerModel(JdbcTemplate jdbcTemplate, int pRank, int draftSize) {
        Map<String,Object> playerMap = jdbcTemplate.queryForList("SELECT * FROM players WHERE player_id = " + pRank).get(0);
        
        String name = (String)playerMap.get("namme");
        String position = (String)playerMap.get("position");
        int playerRank = (int)playerMap.get("player_rank");
        Double predictedScore = (Double)playerMap.get("predicted_score");
        String avgADP;

        if(playerRank <= draftSize) {
            avgADP = "1."+playerRank;         
        }
        else {
            int pick = playerRank % draftSize;
            int round = playerRank / draftSize;
            avgADP = round + "." + pick;
        }
        return new PlayerModel(name, position, (double)playerRank, (double)predictedScore, Double.parseDouble(avgADP));
    }

    private TreeMap<String,PlayerModel> createTeamTreeFromDB(JdbcTemplate jdbcTemplate, List<PlayerModel> teamPlayerList) {
        TreeMap<String,PlayerModel> teamTree = new TreeMap<String,PlayerModel>();
        for(PlayerModel player : teamPlayerList) {
            teamTree.put(player.getPosition(), player);
        }
        return teamTree;
    }

    // draft history stuff - needed to be fixed
    public HashMap<String,String> returnDraftMetaData(JdbcTemplate jdbcTemplate, String username, int nextDraftID) {
         return this.allPastsDraftsDataObject.get(nextDraftID-1).getDraftMetaData();
    }

    public List<PlayerModel> returnDraftedPlayers(int nextDraftID) {
        return this.allPastsDraftsDataObject.get(nextDraftID-1).getDraftedPlayers();
    }

    public ArrayList<HashMap<String,String>> getDraftHistoryMetaData(JdbcTemplate jdbcTemplate, String username) {
        System.out.println(this.allPastsDraftsDataObject.size());
        ArrayList<HashMap<String,String>> allMetaData = new ArrayList<HashMap<String,String>>();
        for(int currDraftIndex : this.allPastsDraftsDataObject.keySet()) {
            System.out.println((currDraftIndex));
            System.out.println(this.allPastsDraftsDataObject.get(currDraftIndex).getDraftMetaData());
            allMetaData.add(this.allPastsDraftsDataObject.get(currDraftIndex).getDraftMetaData());
        }
        return allMetaData;
    }

    public List<PlayerModel> getDraftHistoryDraftedPlayerLog(JdbcTemplate jdbcTemplate, String username, int draftID) {
        return this.allPastsDraftsDataObject.get(draftID-1).getDraftedPlayers();
    }

    public List<TreeMap<String,ArrayList<PlayerModel>>> getDraftHistoryAllTeamsMap(JdbcTemplate jdbcTemplate, String username, int draftID) {
        return this.allPastsDraftsDataObject.get(draftID-1).getDraftHistoryAllTeamsMap();
    }

    public List<TeamModel> getDraftHistoryTeamList(JdbcTemplate jdbcTemplate, String username, int draftID) {
        return this.allPastsDraftsDataObject.get(draftID-1).getTeams();
    }
}
