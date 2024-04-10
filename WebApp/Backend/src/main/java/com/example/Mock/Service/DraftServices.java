package com.example.Mock.Service;

import java.lang.reflect.Array;
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
import com.example.Mock.StartingClasses.DefensePlayerModel;
import com.example.Mock.StartingClasses.KickerPlayerModel;
import com.example.Mock.StartingClasses.PlayerModel;
import com.example.Mock.StartingClasses.QuarterBackPlayerModel;
import com.example.Mock.StartingClasses.TeamModel;
import com.example.Mock.StartingClasses.TightEndPlayerModel;
import com.example.Mock.StartingClasses.VaribleOddsPicker;


@Service
@Scope(value="prototype")
public class DraftServices {

    private TreeMap<Integer,DraftDataObject> allPastsDraftsDataObject;
    private DraftDataObject draftDataObject;
    private int nextDraftID;
    private VaribleOddsPicker randomNumGen = new VaribleOddsPicker();


    public DraftServices(int currDraftID) {
        this.allPastsDraftsDataObject = new TreeMap<Integer,DraftDataObject>();
        this.nextDraftID = currDraftID;
    }

    // checks from main page
    public boolean checkForCurrentDraft(JdbcTemplate jdbcTemplate, String username) {
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM drafts WHERE username='" + username + "' and complete_status=0",Integer.class);
        return count >= 1;
    }

    public boolean checkForPastDrafts(JdbcTemplate jdbcTemplate, String username) {
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM drafts WHERE username='" + username + "' and complete_status=1",Integer.class);
        return count >= 1;
    }

    public boolean userMarkCurrentDraftComplete(JdbcTemplate jdbcTemplate, String username) {
        int draftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        jdbcTemplate.update("UPDATE drafts SET complete_status = 1 WHERE username='" + username + "' AND draft_id=" + draftID);
        return this.checkForCurrentDraft(jdbcTemplate, username);
    }

    public boolean deleteThisDraft(JdbcTemplate jdbcTemplate, String username) {
        int draftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        jdbcTemplate.update("DELETE FROM teams WHERE draft_id = " + draftID);
        jdbcTemplate.update("DELETE FROM drafts WHERE draft_id = " + draftID);
        return this.checkForCurrentDraft(jdbcTemplate, username);
    }

    // meta data for draft
    public int getUserMostRecentDraftID(JdbcTemplate jdbcTemplate, String username) {
        return jdbcTemplate.queryForObject("SELECT MAX(draft_id) FROM drafts WHERE username='" + username + "'", Integer.class);
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
        int userPickInOddRound= this.getUserStartingDraftSpot(jdbcTemplate, username);
        int userPickInEvenRound = Math.abs(userPickInOddRound - draftSize) + 1;
        int currRound = this.getCurrRound(jdbcTemplate, username);
        int currPick = this.getCurrPick(jdbcTemplate, username);
        boolean isEvenRound = currRound % 2 == 0;
        int nextPick = isEvenRound ? userPickInEvenRound : userPickInOddRound;        
        if ((isEvenRound && currPick > userPickInEvenRound) || (!isEvenRound && currPick > userPickInOddRound)) {
            nextPick = isEvenRound ? userPickInOddRound : userPickInEvenRound; // Flip the pick for the next round
        }
        return nextPick;
    }

    public int getNextUserPickRound(JdbcTemplate jdbcTemplate, String username) {
        int draftSize = this.getDraftSize(jdbcTemplate, username);
        int userPickInOddRound = this.getUserStartingDraftSpot(jdbcTemplate, username);
        int userPickInEvenRound = Math.abs(userPickInOddRound - draftSize) + 1;
        int currRound = this.getCurrRound(jdbcTemplate, username);
        int currPick = this.getCurrPick(jdbcTemplate, username);
        boolean isEvenRound = currRound % 2 == 0;
        int nextRound = currRound;        
        if ((isEvenRound && currPick > userPickInEvenRound) || (!isEvenRound && currPick > userPickInOddRound)) {
            nextRound += 1;
        }
        return nextRound;
    }
    
    public List<PlayerModel> getAllPlayers(JdbcTemplate jdbcTemplate, String username) {
        int draftSize = this.getDraftSize(jdbcTemplate, username);
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM players", Integer.class);
        List<Integer> allIntList = new ArrayList<Integer>();
        for(int i = 1; i <= count; i++) {
            allIntList.add(i);
        }
        List<PlayerModel> allPlayers = this.createPlayerModelListFromIntList(jdbcTemplate, allIntList, draftSize);
        return allPlayers;
    }

    public List<PlayerModel> getPlayersLeft(JdbcTemplate jdbcTemplate, String username) {
        ArrayList<PlayerModel> allPlayers = new ArrayList<PlayerModel>(this.getAllPlayers(jdbcTemplate, username));
        int draftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        int draftSize = this.getDraftSize(jdbcTemplate, username);
        for(int i = 1; i <= draftSize; i++) {
            List<Integer> teamArrayInt = this.createTeamIntArrayFromDB(jdbcTemplate, draftID, i);
            List<PlayerModel> teamPlayerList = this.createPlayerModelListFromIntList(jdbcTemplate, teamArrayInt, draftSize);
            allPlayers.removeAll(teamPlayerList);
        }
        return allPlayers;
    }


    public List<PlayerModel> getDraftedPlayers(JdbcTemplate jdbcTemplate, String username) {
        ArrayList<PlayerModel> allPlayers = new ArrayList<PlayerModel>();
        int draftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        int draftSize = this.getDraftSize(jdbcTemplate, username);
        for(int i = 1; i <= draftSize; i++) {
            List<Integer> teamArrayInt = this.createTeamIntArrayFromDB(jdbcTemplate, draftID, i);
            List<PlayerModel> teamPlayerList = this.createPlayerModelListFromIntList(jdbcTemplate, teamArrayInt, draftSize);
            allPlayers.addAll(teamPlayerList);
        }
        return allPlayers;
    }

    public boolean isDraftOver (JdbcTemplate jdbcTemplate, String username) {
        int draftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        int draftSize = this.getDraftSize(jdbcTemplate, username);
        int currRound = this.getCurrRound(jdbcTemplate, username);
        int currPick = this.getCurrPick(jdbcTemplate, username);
        if(currRound == 16 && currPick == 1) {
            return true;
        }
        return false;
    }
    
    // controls for draft itself
    public List<PlayerModel> startDraft(JdbcTemplate jdbcTemplate, String username, String teamName, int draftSize, int desiredDraftPosition, DraftDataObject draftDataObject) {
        jdbcTemplate.update("INSERT INTO drafts (username, num_teams, curr_round, curr_pick) VALUES (?,?,?,?)", username, draftSize, 1, 1);
        int currDraftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        jdbcTemplate.update("INSERT INTO teams (team_name, draft_spot, draft_id, user_team) VALUES (?,?,?,?)", teamName, desiredDraftPosition, currDraftID, 1);
        this.createCPUTeamsForThisDraft(jdbcTemplate, draftSize, desiredDraftPosition, currDraftID);
        System.out.println("Creating Draft players from DB");
        ArrayList<PlayerModel> allPlayers = new ArrayList<PlayerModel>(this.getAllPlayers(jdbcTemplate, username));
        System.out.println(allPlayers);
        return allPlayers;
    }

    public void createCPUTeamsForThisDraft(JdbcTemplate jdbcTemplate, int draftSize, int desiredDraftPosition, int currDraftID) {
        for(int i = 1; i <= draftSize; i++) {
            if(i != desiredDraftPosition) {
                jdbcTemplate.update("INSERT INTO teams (team_name, draft_spot, draft_id, user_team) VALUES (?,?,?,?)", "CPU Team " + i, i, currDraftID, 0);
            }
        }
    }

    private boolean moveToNextPick(JdbcTemplate jdbcTemplate, String username){
        int currPick = this.getCurrPick(jdbcTemplate, username);
        int currRound = this.getCurrRound(jdbcTemplate, username);
        int draftSize = this.getDraftSize(jdbcTemplate, username);
        int draftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        if(currPick == draftSize) {
            jdbcTemplate.update("UPDATE drafts SET curr_round = curr_round + 1, curr_pick = 1 WHERE username='" + username + "' AND draft_id=" + draftID);
            return true;
        }
        else {
            jdbcTemplate.update("UPDATE drafts SET curr_pick = curr_pick + 1 WHERE username='" + username + "' AND draft_id=" + draftID);
            return true;
        }
    }

    public List<PlayerModel> userDraftPick(JdbcTemplate jdbcTemplate, String username, int pick){
        List<PlayerModel> playersLeft = this.getPlayersLeft(jdbcTemplate, username);
        PlayerModel playerToDraft = playersLeft.get(pick-1);
        int draftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        int currPick = this.getCurrPick(jdbcTemplate, username);
        int playerRank = (int)playerToDraft.getRank();
        this.makePick(jdbcTemplate, draftID, currPick, playerRank);
        this.moveToNextPick(jdbcTemplate, username);
        return new ArrayList<PlayerModel>(Arrays.asList(playerToDraft));
    }


    private int checkForForcePickNeeded(JdbcTemplate jdbcTemplate, int currRoundPick, int currPick, int draftID, String username) {
        int draftSize = this.getDraftSize(jdbcTemplate, username);
        List<Integer> currTeamIntArray = this.createTeamIntArrayFromDB(jdbcTemplate, draftID, currPick);
        List<PlayerModel> currTeamPlayerList = this.createPlayerModelListFromIntList(jdbcTemplate, currTeamIntArray, draftSize);
        TreeMap<String,List<PlayerModel>> currTeamTreeMap = this.createTeamTreeFromDB(jdbcTemplate, currTeamPlayerList);
        List<PlayerModel> playersLeft = this.getPlayersLeft(jdbcTemplate, username);
        if(currRoundPick == 8) {
			if (!currTeamTreeMap.containsKey(QuarterBackPlayerModel.POSITIONSHORTHANDLE)){
				return getNextForcedPosition(playersLeft, QuarterBackPlayerModel.POSITIONSHORTHANDLE);
			}
		}
		else if (currRoundPick == 10) {
			if (!currTeamTreeMap.containsKey(TightEndPlayerModel.POSITIONSHORTHANDLE)) {
				return getNextForcedPosition(playersLeft,TightEndPlayerModel.POSITIONSHORTHANDLE);
			}
		}
		else if (currRoundPick == 12) {
			if (!currTeamTreeMap.containsKey(KickerPlayerModel.POSITIONSHORTHANDLE)) {
				return getNextForcedPosition(playersLeft,KickerPlayerModel.POSITIONSHORTHANDLE);
			}
		}
		else if (currRoundPick == 13) {
			if (!currTeamTreeMap.containsKey(DefensePlayerModel.POSITIONSHORTHANDLE)){
				return getNextForcedPosition(playersLeft,DefensePlayerModel.POSITIONSHORTHANDLE);
			}
		}	
		return -1;
    }
    

    private int getNextForcedPosition(List<PlayerModel> playersLeft, String position) {
        for(int i = 0; i < playersLeft.size(); i++) {
            if(playersLeft.get(i).getPosition().equals(position)) {
                PlayerModel playerToDraft = playersLeft.get(i);
                return (int)playerToDraft.getRank();
            }
        }
        return -1;
    }

    public boolean makePick(JdbcTemplate jdbcTemplate, int draftID, int draftSpot, int playerRankToPick) {
        String teamArray = jdbcTemplate.queryForObject("SELECT COALESCE(team_array, '') AS team_array FROM teams WHERE draft_spot=" + draftSpot + " AND draft_id="+draftID , String.class);
        teamArray += playerRankToPick + ",";
        jdbcTemplate.update("UPDATE teams SET team_array = ? WHERE draft_spot = ? and draft_id = ?", teamArray, draftSpot, draftID);
        return true;
    }
    
    public List<PlayerModel> simTo(JdbcTemplate jdbcTemplate, String username) {
        List<PlayerModel> playersDraftDuringSim = new ArrayList<PlayerModel>();
        int draftID = this.getUserMostRecentDraftID(jdbcTemplate, username);
        int draftSize = this.getDraftSize(jdbcTemplate, username);
        int nextUserPickRound = this.getNextUserPickRound(jdbcTemplate, username);
        
        int nextUserPick = this.getNextUserPick(jdbcTemplate, username);
        while (!this.isDraftOver(jdbcTemplate, username) &&
                !(nextUserPickRound == this.getCurrRound(jdbcTemplate, username) 
                 && nextUserPick == this.getCurrPick(jdbcTemplate, username))){
            int currRound = this.getCurrRound(jdbcTemplate, username);
            int currPick = this.getCurrPick(jdbcTemplate, username);
            int pick = this.checkForForcePickNeeded(jdbcTemplate, currRound, currPick, draftID, username);
            if(pick == -1) {
                VaribleOddsPicker randomNumGen = new VaribleOddsPicker();
                pick = randomNumGen.newOdds(6);
                List<PlayerModel> playersLeft = this.getPlayersLeft(jdbcTemplate, username);
                pick = (int)playersLeft.get(pick-1).getRank();
            }
            makePick(jdbcTemplate, draftID, currPick, pick);
            moveToNextPick(jdbcTemplate, username);
            playersDraftDuringSim.add(this.createPlayerModel(jdbcTemplate, pick, draftSize));
        }
        return playersDraftDuringSim;
    } 

    // Parsing Team Data from DB to create TeamTreeMap
    public List<Integer> createTeamIntArrayFromDB(JdbcTemplate jdbcTemplate, int draftID, int draftSpot) {
        List<Integer> teamArrayInt = new ArrayList<Integer>();
        String teamArray = jdbcTemplate.queryForObject("SELECT COALESCE(team_array, '') AS team_array FROM teams WHERE draft_spot=" + draftSpot + " AND draft_id="+draftID, String.class);
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
        
        Map<String,Object> playerMap = jdbcTemplate.queryForList("SELECT * FROM players WHERE player_rank = " + pRank).get(0);
        String name = (String)playerMap.get("name");
        String position = (String)playerMap.get("position");
        int playerRank = (int)playerMap.get("player_rank");
        Object objectPredictedscored = playerMap.get("predicted_score");
        Double predictedScore = ((BigDecimal) objectPredictedscored).doubleValue();
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

    private TreeMap<String,List<PlayerModel>> createTeamTreeFromDB(JdbcTemplate jdbcTemplate, List<PlayerModel> teamPlayerList) {
        TreeMap<String,List<PlayerModel>> teamTree = new TreeMap<String,List<PlayerModel>>();
        for(PlayerModel player : teamPlayerList) {
            if (teamTree.containsKey(player.getPosition())) {
                teamTree.get(player.getPosition()).add(player);
            }
            else {
                teamTree.put(player.getPosition(), new ArrayList<PlayerModel>(Arrays.asList(player)));
            }
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
        ArrayList<HashMap<String,String>> allMetaData = new ArrayList<HashMap<String,String>>();
        for(int currDraftIndex : this.allPastsDraftsDataObject.keySet()) {
            
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
