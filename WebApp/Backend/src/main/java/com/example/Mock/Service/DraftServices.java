package com.example.Mock.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.Mock.VaribleOddsPicker;
import com.example.Mock.DAO.PlayerDataObject;
import com.example.Mock.DAO.TeamDataObject;
import com.example.Mock.PlayerModels.DefensePlayerModel;
import com.example.Mock.PlayerModels.KickerPlayerModel;
import com.example.Mock.PlayerModels.QuarterBackPlayerModel;
import com.example.Mock.PlayerModels.TightEndPlayerModel;

@Service
@Scope(value="prototype")
public class DraftServices {

    private int nextDraftID;
    private VaribleOddsPicker randomNumGen = new VaribleOddsPicker();

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DraftServices(int currDraftID, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.nextDraftID = currDraftID;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    // checks from main page
    public boolean checkForCurrentDraft(String username) {
        String sql = "SELECT COUNT(*) FROM drafts WHERE username=:username AND complete_status=0";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count >= 1;
    }

    public boolean checkForPastDrafts(String username) {
        String sql = "SELECT COUNT(*) FROM drafts WHERE username=:username AND complete_status=1";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count >= 1;
    }

    public boolean userMarkCurrentDraftComplete(String username) {
        int draftID = this.getUserMostRecentDraftID(username);
        String sql = "UPDATE drafts SET complete_status = 1 WHERE username=:username AND draft_id=:draftID";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        params.addValue("draftID", draftID);
        namedParameterJdbcTemplate.update(sql, params);
        return this.checkForCurrentDraft(username);
    }

    public boolean deleteThisDraft(String username) {
        int draftID = this.getUserMostRecentDraftID(username);
        String sql1 = "DELETE FROM teams WHERE draft_id=:draftID";
        String sql2 = "DELETE FROM drafts WHERE draft_id=:draftID";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("draftID", draftID);
        namedParameterJdbcTemplate.update(sql1, params);
        namedParameterJdbcTemplate.update(sql2, params);
        return this.checkForCurrentDraft(username);
    }

    // meta data for draft
    public int getUserMostRecentDraftID(String username) {
        String sql = "SELECT MAX(draft_id) FROM drafts WHERE username=:username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public String getUserTeamName(int draftID) {
        String sql = "SELECT team_name FROM teams WHERE user_team = 1 AND draft_id = :draftID";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("draftID", draftID);
        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }

    public int getDraftSize(int draftID) {
        String sql = "SELECT num_teams FROM drafts WHERE draft_id = :draftID";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("draftID", draftID);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public int getCurrRound(int draftID) {
        String sql = "SELECT curr_round FROM drafts WHERE draft_id = :draftID";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("draftID", draftID);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public int getCurrPick(int draftID) {
        String sql = "SELECT curr_pick FROM drafts WHERE draft_id = :draftID";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("draftID", draftID);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public int getUserStartingDraftSpot(String teamName, int draftID) {
        String sql = "SELECT draft_spot FROM teams WHERE team_name = :teamName AND draft_id = :draftID AND user_team = 1";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("teamName", teamName);
        params.addValue("draftID", draftID);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public int getNextUserPick(int draftSize, int userPickInOddRound, int userPickInEvenRound, int currRound, int currPick){        
        boolean isEvenRound = currRound % 2 == 0;
        int nextPick = isEvenRound ? userPickInEvenRound : userPickInOddRound;        
        if ((isEvenRound && currPick > userPickInEvenRound) || (!isEvenRound && currPick > userPickInOddRound)) {
            nextPick = isEvenRound ? userPickInOddRound : userPickInEvenRound; // Flip the pick for the next round
        }
        return nextPick;
    }

    public int getNextUserPickRound(int draftSize, int userPickInOddRound, int userPickInEvenRound, int currRound, int currPick) {
        boolean isEvenRound = currRound % 2 == 0;
        int nextRound = currRound;        
        if ((isEvenRound && currPick > userPickInEvenRound) || (!isEvenRound && currPick > userPickInOddRound)) {
            nextRound += 1;
        }
        return nextRound;
    }
    
    public List<PlayerDataObject> getAllPlayers(int draftSize) {
        String sql = "SELECT COUNT(*) FROM players";
        int count = namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        List<Integer> allIntList = new ArrayList<>();
        for(int i = 1; i <= count; i++) {
            allIntList.add(i);
        }
        return this.createPlayerModelListFromIntList(allIntList, draftSize);
    }

    public List<PlayerDataObject> getPlayersLeft(int draftID, int draftSize) {
        ArrayList<PlayerDataObject> allPlayers = new ArrayList<>(this.getAllPlayers(draftSize));
        for(int i = 1; i <= draftSize; i++) {
            List<Integer> teamArrayInt = this.createTeamIntArrayFromDB(draftID, i);
            List<PlayerDataObject> teamPlayerList = this.createPlayerModelListFromIntList(teamArrayInt, draftSize);
            allPlayers.removeAll(teamPlayerList);
        }
        return allPlayers;
    }

    public List<PlayerDataObject> getDraftedPlayers(int draftID, int draftSize) {
        ArrayList<PlayerDataObject> allPlayers = new ArrayList<>();
        for(int i = 1; i <= draftSize; i++) {
            List<Integer> teamArrayInt = this.createTeamIntArrayFromDB(draftID, i);
            List<PlayerDataObject> teamPlayerList = this.createPlayerModelListFromIntList(teamArrayInt, draftSize);
            allPlayers.addAll(teamPlayerList);
        }
        return allPlayers;
    }

    public boolean isDraftOver(int currRound, int currPick) {
        return currRound == 16 && currPick == 1;
    }
    
    // controls for draft itself
    public List<PlayerDataObject> startDraft(String username, String teamName, int draftSize, int desiredDraftPosition) {
        String sql1 = "INSERT INTO drafts (username, num_teams, curr_round, curr_pick) VALUES (:username, :numTeams, :currRound, :currPick)";
        MapSqlParameterSource params1 = new MapSqlParameterSource();
        params1.addValue("username", username);
        params1.addValue("numTeams", draftSize);
        params1.addValue("currRound", 1);
        params1.addValue("currPick", 1);
        namedParameterJdbcTemplate.update(sql1, params1);

        int currDraftID = this.getUserMostRecentDraftID(username);

        String sql2 = "INSERT INTO teams (team_name, draft_spot, draft_id, user_team) VALUES (:teamName, :draftSpot, :draftID, :userTeam)";
        MapSqlParameterSource params2 = new MapSqlParameterSource();
        params2.addValue("teamName", teamName);
        params2.addValue("draftSpot", desiredDraftPosition);
        params2.addValue("draftID", currDraftID);
        params2.addValue("userTeam", 1);
        namedParameterJdbcTemplate.update(sql2, params2);

        this.createCPUTeamsForThisDraft(draftSize, desiredDraftPosition, currDraftID);
        System.out.println("Creating Draft players from DB");
        ArrayList<PlayerDataObject> allPlayers = new ArrayList<>(this.getAllPlayers(draftSize));
        System.out.println(allPlayers);
        return allPlayers;
    }

    public void createCPUTeamsForThisDraft(int draftSize, int desiredDraftPosition, int currDraftID) {
        for(int i = 1; i <= draftSize; i++) {
            if(i != desiredDraftPosition) {
                String sql = "INSERT INTO teams (team_name, draft_spot, draft_id, user_team) VALUES (:teamName, :draftSpot, :draftID, :userTeam)";
                MapSqlParameterSource params = new MapSqlParameterSource();
                params.addValue("teamName", "CPU Team " + i);
                params.addValue("draftSpot", i);
                params.addValue("draftID", currDraftID);
                params.addValue("userTeam", 0);
                namedParameterJdbcTemplate.update(sql, params);
            }
        }
    }

    private boolean moveToNextPick(int draftID, String username){
        int currPick = this.getCurrPick(draftID);
        int currRound = this.getCurrRound(draftID);
        int draftSize = this.getDraftSize(draftID);
        String sql;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("draftID", draftID);
        if(currPick == draftSize) {
            sql = "UPDATE drafts SET curr_round = curr_round + 1, curr_pick = 1 WHERE draft_id=:draftID";
        } else {
            sql = "UPDATE drafts SET curr_pick = curr_pick + 1 WHERE draft_id=:draftID";
        }
        namedParameterJdbcTemplate.update(sql, params);
        return true;
    }

    private PlayerDataObject updatePlayerPickedMetaData(PlayerDataObject playerPicked, int currRound, int currPick, String teamName) {
        String currPickStr = String.valueOf(currPick);
        if(currPickStr.length() == 1) currPickStr = "0" + currPick;
        playerPicked.setSpotDrafted(currRound + "." + currPickStr);
        playerPicked.setTeamDraftedBy(teamName);
        return playerPicked;
    }

    public List<PlayerDataObject> userDraftPick(String username, int pick){
        int draftID = this.getUserMostRecentDraftID(username);
        int draftSize = this.getDraftSize(draftID);
        List<PlayerDataObject> playersLeft = this.getPlayersLeft(draftID, draftSize);
        PlayerDataObject playerToDraft = playersLeft.get(pick-1);
        int currRound = this.getCurrRound(draftID);
        int currPick = this.getCurrPick(draftID);
        int playerRank = (int)playerToDraft.getRank();
        String teamName = this.getTeamNameFromDraftSpot(draftID, draftID);
        this.updatePlayerPickedMetaData(playerToDraft, currRound, currPick, teamName);
        this.makePick(draftID, currPick, playerRank);
        this.moveToNextPick(draftID, username);
        return new ArrayList<>(Collections.singletonList(playerToDraft));
    }

    private int checkForForcePickNeeded(int currRoundPick, int currPick, int draftID, int draftSize, List<PlayerDataObject> playersLeft) {
        List<Integer> currTeamIntArray = this.createTeamIntArrayFromDB(draftID, currPick);
        List<PlayerDataObject> currTeamPlayerList = this.createPlayerModelListFromIntList(currTeamIntArray, draftSize);
        TreeMap<String, List<PlayerDataObject>> currTeamTreeMap = this.createTeamTreeFromDB(currTeamPlayerList);
        System.out.println(currTeamTreeMap);
        if(currRoundPick == 8) {
            if (!currTeamTreeMap.containsKey(QuarterBackPlayerModel.POSITIONSHORTHANDLE)){
                return getNextForcedPosition(playersLeft, QuarterBackPlayerModel.POSITIONSHORTHANDLE);
            }
        } else if (currRoundPick == 10) {
            if (!currTeamTreeMap.containsKey(TightEndPlayerModel.POSITIONSHORTHANDLE)) {
                return getNextForcedPosition(playersLeft, TightEndPlayerModel.POSITIONSHORTHANDLE);
            }
        } else if (currRoundPick == 12) {
            if (!currTeamTreeMap.containsKey(KickerPlayerModel.POSITIONSHORTHANDLE)) {
                return getNextForcedPosition(playersLeft, KickerPlayerModel.POSITIONSHORTHANDLE);
            }
        } else if (currRoundPick == 13) {
            if (!currTeamTreeMap.containsKey(DefensePlayerModel.POSITIONSHORTHANDLE)){
                return getNextForcedPosition(playersLeft, DefensePlayerModel.POSITIONSHORTHANDLE);
            }
        }   
        return -1;
    }
    
    private int getNextForcedPosition(List<PlayerDataObject> playersLeft, String position) {
        for(int i = 0; i < playersLeft.size(); i++) {
            if(playersLeft.get(i).getPosition().equals(position)) {
                PlayerDataObject playerToDraft = playersLeft.get(i);
                return (int)playerToDraft.getRank();
            }
        }
        return -1;
    }

    public boolean makePick(int draftID, int draftSpot, int playerRankToPick) {
        int draftSpotConverted = draftSpot;
        if (checkForEvenOrOddRound(draftID)) {
            draftSpotConverted = flipDraftPick(draftSpot, this.getDraftSize(draftID));
        }
        String sql1 = "SELECT COALESCE(team_array, '') AS team_array FROM teams WHERE draft_spot=:draftSpotConverted AND draft_id=:draftID";
        MapSqlParameterSource params1 = new MapSqlParameterSource();
        params1.addValue("draftSpotConverted", draftSpotConverted);
        params1.addValue("draftID", draftID);
        String teamArray = namedParameterJdbcTemplate.queryForObject(sql1, params1, String.class);
        teamArray += playerRankToPick + ",";
        String sql2 = "UPDATE teams SET team_array = :teamArray WHERE draft_spot = :draftSpotConverted AND draft_id = :draftID";
        MapSqlParameterSource params2 = new MapSqlParameterSource();
        params2.addValue("teamArray", teamArray);
        params2.addValue("draftSpotConverted", draftSpotConverted);
        params2.addValue("draftID", draftID);
        namedParameterJdbcTemplate.update(sql2, params2);
        return true;
    }
    
    public List<PlayerDataObject> simTo(String username) {
        int draftID = this.getUserMostRecentDraftID(username);
        int draftSize = this.getDraftSize(draftID);
        List<PlayerDataObject> playersDraftDuringSim = new ArrayList<>();
        int nextUserPickRound = this.getNextUserPickRound(draftSize, this.getUserStartingDraftSpot(this.getUserTeamName(draftID), draftID), Math.abs(this.getUserStartingDraftSpot(this.getUserTeamName(draftID), draftID) - draftSize) + 1, this.getCurrRound(draftID), this.getCurrPick(draftID));
        int nextUserPick = this.getNextUserPick(draftSize, this.getUserStartingDraftSpot(this.getUserTeamName(draftID), draftID), Math.abs(this.getUserStartingDraftSpot(this.getUserTeamName(draftID), draftID) - draftSize) + 1, this.getCurrRound(draftID), this.getCurrPick(draftID));
        while (!this.isDraftOver(this.getCurrRound(draftID), this.getCurrPick(draftID)) &&
                !(nextUserPickRound == this.getCurrRound(draftID) 
                 && nextUserPick == this.getCurrPick(draftID))){
            int currRound = this.getCurrRound(draftID);
            System.out.println("Curr Round: " + currRound);
            int currPick = this.getCurrPick(draftID);
            System.out.println("Curr Pick: " + currPick);
            List<PlayerDataObject> playersLeft = this.getPlayersLeft(draftID, draftSize);
            int pick = this.checkForForcePickNeeded(currRound, currPick, draftID, draftSize, playersLeft);
            System.out.println("Pick: " + pick);
            if(pick == -1) {
                VaribleOddsPicker randomNumGen = new VaribleOddsPicker();
                pick = randomNumGen.newOdds(6);
                pick = (int)playersLeft.get(pick-1).getRank();
            }
            makePick(draftID, currPick, pick);
            moveToNextPick(draftID, username);
            String teamName = this.getTeamNameFromDraftSpot(draftID, currPick);
            PlayerDataObject playerDrafted = this.createPlayerModel(pick, draftSize);
            playerDrafted = this.updatePlayerPickedMetaData(playerDrafted, currRound, currPick, teamName);
            playersDraftDuringSim.add(playerDrafted);
            System.out.println(playersDraftDuringSim.get(playersDraftDuringSim.size()-1).getFullName() + " was drafted in round " + currRound + " pick " + currPick);
            System.out.println("'\n\n\'");
        }
        return playersDraftDuringSim;
    } 

    // Parsing Team Data from DB to create TeamTreeMap
    public List<Integer> createTeamIntArrayFromDB(int draftID, int draftSpot) {
        String sql = "SELECT COALESCE(team_array, '') AS team_array FROM teams WHERE draft_spot=:draftSpot AND draft_id=:draftID";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("draftSpot", draftSpot);
        params.addValue("draftID", draftID);
        String teamArray = namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        List<Integer> teamArrayInt = new ArrayList<>();
        if (teamArray.length() > 0) {
            String[] teamArraySplit = teamArray.split(",");
            for(String player : teamArraySplit) {
                teamArrayInt.add(Integer.parseInt(player));
            }
        }
        return teamArrayInt;
    }

    private List<PlayerDataObject> createPlayerModelListFromIntList(List<Integer> teamArrayInt, int draftSize) {
        List<PlayerDataObject> playerModelList = new ArrayList<>();
        for(int playerInt : teamArrayInt) {
            PlayerDataObject player = this.createPlayerModel(playerInt, draftSize);
            playerModelList.add(player);
        }
        return playerModelList;
    }

    private PlayerDataObject createPlayerModel(int pRank, int draftSize) {
        String sql = "SELECT * FROM players WHERE player_rank = :pRank";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("pRank", pRank);
        Map<String, Object> playerMap = namedParameterJdbcTemplate.queryForMap(sql, params);
        String name = (String)playerMap.get("name");
        String position = (String)playerMap.get("position");
        int playerRank = (int)playerMap.get("player_rank");
        Object objectPredictedscored = playerMap.get("predicted_score");
        Double predictedScore = ((BigDecimal) objectPredictedscored).doubleValue();
        String avgADP;
        int round;
        if(playerRank <= draftSize) round = 1;
        else round = (playerRank / draftSize) + 1;
        int pick = playerRank % draftSize;
        if (pick == 0) {
            pick = 1;
        }
        String pickStr = Integer.toString(pick);    
        if (Integer.toString(pick).length() == 1) {
            pickStr = "0" + pick;
        }
        avgADP = round + "." + pickStr;
        return new PlayerDataObject(name, position, (double)playerRank, (double)predictedScore, Double.parseDouble(avgADP));
    }

    private TreeMap<String,List<PlayerDataObject>> createTeamTreeFromDB(List<PlayerDataObject> teamPlayerList) {
        TreeMap<String,List<PlayerDataObject>> teamTree = new TreeMap<>();
        for(PlayerDataObject player : teamPlayerList) {
            if (teamTree.containsKey(player.getPosition())) {
                teamTree.get(player.getPosition()).add(player);
            } else {
                teamTree.put(player.getPosition(), new ArrayList<>(Collections.singletonList(player)));
            }
        }
        return teamTree;
    }

    public ArrayList<HashMap<String,String>> getDraftHistoryMetaData(String username) {
        ArrayList<HashMap<String,String>> draftHistoryMetaData = new ArrayList<>();
        String sql = "SELECT draft_id FROM drafts WHERE username=:username AND complete_status=1";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        List<Integer> draftIDSFinished = namedParameterJdbcTemplate.queryForList(sql, params, Integer.class);
        for(int currDraftID: draftIDSFinished) {
            Map<String, Object> draftMetaDataFromDB = namedParameterJdbcTemplate.queryForMap("SELECT * FROM drafts WHERE username = :username AND complete_status=1 AND draft_id = :draftID", 
                new MapSqlParameterSource("username", username).addValue("draftID", currDraftID));
            Map<String, Object> userTeamMetaDataFromDB = namedParameterJdbcTemplate.queryForMap("SELECT * FROM teams WHERE user_team=1 AND draft_id = :draftID", 
                new MapSqlParameterSource("draftID", currDraftID));
            
            HashMap<String,String> draftMetaDataToReturn = new HashMap<>();
            draftMetaDataToReturn.put("draft_id", String.valueOf(currDraftID));
            draftMetaDataToReturn.put("team_name", (String)userTeamMetaDataFromDB.get("team_name"));
            draftMetaDataToReturn.put("draft_spot", String.valueOf(userTeamMetaDataFromDB.get("draft_spot")));
            draftMetaDataToReturn.put("num_teams", String.valueOf(draftMetaDataFromDB.get("num_teams")));
            LocalDateTime dateTime = (LocalDateTime)draftMetaDataFromDB.get("date");
            draftMetaDataToReturn.put("date", dateTime.toLocalDate().toString());
            draftMetaDataToReturn.put("time", dateTime.toLocalTime().toString());
            draftHistoryMetaData.add(draftMetaDataToReturn);
        }
        System.out.println(draftHistoryMetaData);
        return draftHistoryMetaData;
    }

    public List<PlayerDataObject> getDraftHistoryDraftedPlayerLog(String username, int draftID) {
        ArrayList<PlayerDataObject> allPlayers = new ArrayList<>();
        int draftSize = this.getDraftSize(draftID);
        for (int i = 1; i <= draftSize; i++) {
            List<Integer> teamArrayInt = this.createTeamIntArrayFromDB(draftID, i);
            List<PlayerDataObject> teamPlayerList = this.createPlayerModelListFromIntList(teamArrayInt, draftSize);
            String teamName = namedParameterJdbcTemplate.queryForObject("SELECT team_name FROM teams WHERE draft_id = :draftID AND draft_spot = :draftSpot", 
                new MapSqlParameterSource("draftID", draftID).addValue("draftSpot", i), String.class);
            String teamDraftSpot = namedParameterJdbcTemplate.queryForObject("SELECT draft_spot FROM teams WHERE draft_id = :draftID AND draft_spot = :draftSpot", 
                new MapSqlParameterSource("draftID", draftID).addValue("draftSpot", i), String.class);
            int round = 1;
            int pickOdd = Integer.parseInt(teamDraftSpot);
            int pickEven = Math.abs(Integer.parseInt(teamDraftSpot) - draftSize) + 1;
            int pick = pickOdd;
            for(PlayerDataObject player : teamPlayerList) {
                player.setSpotDrafted(round+"."+pick);
                player.setTeamDraftedBy(teamName);
                round++;
                pick = round % 2 == 0 ? pickEven : pickOdd;
            }
            allPlayers.addAll(teamPlayerList);
            System.out.println(teamPlayerList);
        }
        Collections.sort(allPlayers, PlayerDataObject.spotDraftedComparator);
        return allPlayers;
    }

    public TreeMap<String,ArrayList<PlayerDataObject>> getDraftHistoryAllTeamsMap(String username, int draftID, int teamIndex) {
        TreeMap<String,ArrayList<PlayerDataObject>> teamMap = new TreeMap<>();
        List<Integer> teamArrayList = this.createTeamIntArrayFromDB(draftID, teamIndex);
        System.out.println(teamArrayList);
        int draftSize = namedParameterJdbcTemplate.queryForObject("SELECT num_teams FROM drafts WHERE username = :username AND draft_id = :draftID", 
            new MapSqlParameterSource("username", username).addValue("draftID", draftID), Integer.class);
        List<PlayerDataObject> teamPlayerList = this.createPlayerModelListFromIntList(teamArrayList, draftSize);
        System.out.println(teamPlayerList);
        String teamName = namedParameterJdbcTemplate.queryForObject("SELECT team_name FROM teams WHERE draft_id = :draftID AND draft_spot = :draftSpot", 
            new MapSqlParameterSource("draftID", draftID).addValue("draftSpot", teamIndex), String.class);
        int round = 1;
        int pickOdd = teamIndex;
        int pickEven = Math.abs(teamIndex - draftSize) + 1;
        int pick = pickOdd;
        for (PlayerDataObject player : teamPlayerList) {
            player.setSpotDrafted(round + "." + pick);
            player.setTeamDraftedBy(teamName);
            round++;
            pick = round % 2 == 0 ? pickEven : pickOdd;
            if(teamMap.containsKey(player.getPosition())) {
                teamMap.get(player.getPosition()).add(player);
            } else {
                teamMap.put(player.getPosition(), new ArrayList<>(Collections.singletonList(player)));
            }
        }
        System.out.println(teamMap);
        return teamMap;
    }

    public List<TeamDataObject> getDraftHistoryTeamList(String username, int draftID) {
        ArrayList<TeamDataObject> teamList = new ArrayList<>();
        int draftSize = namedParameterJdbcTemplate.queryForObject("SELECT num_teams FROM drafts WHERE username = :username AND draft_id = :draftID", 
            new MapSqlParameterSource("username", username).addValue("draftID", draftID), Integer.class);
        for (int i = 1; i <= draftSize; i++) {
            Map<String,Object> teamData = namedParameterJdbcTemplate.queryForMap("SELECT * FROM teams WHERE draft_id = :draftID AND draft_spot = :draftSpot", 
                new MapSqlParameterSource("draftID", draftID).addValue("draftSpot", i));
            String teamName = (String)teamData.get("team_name");
            int draftSpot = i;
            boolean userTeam = (int)teamData.get("user_team") == 1;
            TeamDataObject team = new TeamDataObject(teamName, userTeam, draftSpot);
            teamList.add(team);
         }
        return teamList;
    }

    private boolean checkForEvenOrOddRound(int draft_id) {
        String sql = "SELECT curr_round FROM drafts WHERE draft_id = :draftID";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("draftID", draft_id);
        int currRound = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return currRound % 2 == 0;
    }

    private int flipDraftPick(int pickToFlip, int draftSize) {
        if (pickToFlip == draftSize) return 1;
        else return Math.abs(pickToFlip - draftSize)+1;
    }

    private String getTeamNameFromDraftSpot(int draftID, int draftSpot) {
        String sql = "SELECT team_name FROM teams WHERE draft_id = :draftID AND draft_spot = :draftSpot";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("draftID", draftID);
        params.addValue("draftSpot", draftSpot);
        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }
}
