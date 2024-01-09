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


@Service
@Scope(value="prototype")
public class DraftServices {

    private TreeMap<Integer,DraftDataObject> allPastsDraftsDataObject;
    private DraftDataObject draftDataObject;
    private int nextDraftID = 1;

    public DraftServices() {
        this.allPastsDraftsDataObject = new TreeMap<Integer,DraftDataObject>();
    }

    public String getUserTeamName(JdbcTemplate jdbcTemplate, String username) {
        return jdbcTemplate.queryForObject("SELECT team_name FROM teams WHERE username = '" + username + "' AND draft_id = (SELECT MAX(draft_id) FROM drafts)", String.class);
    }

    public int getDraftSize(JdbcTemplate jdbcTemplate, String username) {
        return jdbcTemplate.queryForObject("SELECT num_teams FROM drafts WHERE username = '" + username + "' AND draft_id = (SELECT MAX(draft_id) FROM drafts)", Integer.class);
    }


    public int getUserStartingDraftSpot(JdbcTemplate jdbcTemplate, String teamname) {
        String teamName = this.getUserTeamName(jdbcTemplate, teamname);
        return jdbcTemplate.queryForObject("SELECT draft_spot FROM teams WHERE team_name = '" + teamName + "' AND draft_id = (SELECT MAX(draft_id) FROM drafts) AND user_team = 1", Integer.class);
    }

    public List<PlayerModel> getPlayersLeft() {
        return this.draftDataObject.getPlayersLeft();
    }

    public List<PlayerModel> getDraftedPlayers() {
        return this.draftDataObject.getDraftedPlayers();
    }

    public int getCurrRound(JdbcTemplate jdbcTemplate, String username) {
        System.out.println("Current round - " + jdbcTemplate.queryForObject("SELECT curr_round FROM drafts WHERE username = '" + username + "' AND draft_id = (SELECT MAX(draft_id) FROM drafts)", Integer.class));
        return this.draftDataObject.getCurrRound();
    }

    public int getCurrPick(JdbcTemplate jdbcTemplate, String username) {
        System.out.println("Current pick - " + jdbcTemplate.queryForObject("SELECT curr_pick FROM drafts WHERE username = '" + username + "' AND draft_id = (SELECT MAX(draft_id) FROM drafts)", Integer.class));
        return this.draftDataObject.getCurrPick();
    }

    public int getNextUserPick(JdbcTemplate jdbcTemplate, String username){
        int draftSize = this.getDraftSize(jdbcTemplate, username);
        int userPickInOddRound = this.getUserStartingDraftSpot(jdbcTemplate, username);
        int userPickInEvenRound = Math.abs(userPickInOddRound - draftSize) + 1;
        int currRound = this.getCurrRound(jdbcTemplate, username);
        int currPick = this.getCurrPick(jdbcTemplate, username);
        if(currRound % 2 == 0 && userPickInEvenRound >= currPick) {
            System.out.println("Next User Pick - " + userPickInEvenRound);
            //return userPickInEvenRound;
        }
        else {
            System.out.println("Next User Pick - " + userPickInOddRound);
            //return userPickInOddRoundd
        }
        return this.draftDataObject.getNextUserPick();
    }

    public int getNextUserPickRound(JdbcTemplate jdbcTemplate, String username) {
        int draftSize = this.getDraftSize(jdbcTemplate, username);
        int userPickInOddRound = this.getUserStartingDraftSpot(jdbcTemplate, username);
        int userPickInEvenRound = Math.abs(userPickInOddRound - draftSize) + 1;
        int currRound = this.getCurrRound(jdbcTemplate, username);
        int currPick = this.getCurrPick(jdbcTemplate, username);
        if(currRound % 2 == 0 && userPickInEvenRound >= currPick) {
            System.out.println("Next User Pick Round - " + currRound);
            //return currRound;
        }
        else {
            System.out.println("Next User Pick Round - " + (currRound + 1));
            //return currRound + 1;
        }
        return this.draftDataObject.getNextUserPickRound();
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

    public List<PlayerModel> simTo(){
        List<PlayerModel> players = this.draftDataObject.simComputerPicks();
        if(this.draftDataObject.isDraftOver()) {
            saveDraftHistory();
        }
        return players; 
    }

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
