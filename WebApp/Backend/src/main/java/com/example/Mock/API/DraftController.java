package com.example.Mock.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import com.example.Mock.DAO.PlayerDataObject;
import com.example.Mock.DAO.TeamDataObject;
import com.example.Mock.Service.DraftServices;

@RequestMapping("api/teams")
@CrossOrigin
@RestController
public class DraftController {

    private DraftServices draftServices;
    
    @Autowired
    public DraftController(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        Integer count = namedParameterJdbcTemplate.queryForObject("SELECT MAX(draft_id) FROM drafts", new HashMap<>(), Integer.class);
        if(count==null) {
            count=1;
        }
        this.draftServices = new DraftServices(count);
        
        System.out.println("Database Connection: " + namedParameterJdbcTemplate.getJdbcTemplate().getDataSource().toString());
        System.out.println("Starting draft_id: " + count);
    }
    
    @PostMapping(path="/initaizeUserAccountSetup")
    public boolean initaizeUserAccountSetup(@RequestParam("username") String username) {
        return true;
    }

    @GetMapping(path="/getPlayersLeft/") 
    public List<PlayerDataObject> getPlayersLeft(@RequestParam("username") String username){
        int draftID = draftServices.getUserMostRecentDraftID(username);
        int draftSize = draftServices.getDraftSize(draftID);
        return draftServices.getPlayersLeft(draftID, draftSize);
    }

    @GetMapping(path="/getAllPlayersDrafted/")
    public List<PlayerDataObject> getAllPlayersDrafted(@RequestParam("username") String username) {
        int draftID = draftServices.getUserMostRecentDraftID(username);
        int draftSize = draftServices.getDraftSize(draftID);
        return draftServices.getDraftedPlayers(draftID, draftSize);
    }

    @PostMapping(path="/startDraft/")
    public List<PlayerDataObject> startDraft(@RequestParam("username") String username,
                                        @RequestParam("teamName") String teamName, 
                                        @RequestParam("draftSize") int draftSize, 
                                        @RequestParam("draftPosition") int draftPosition) {
        return draftServices.startDraft(username, teamName, draftSize, draftPosition);
    }

    @PostMapping(path="/simTo/")
    public List<PlayerDataObject> simtTo(@RequestParam("username") String username) {
        return draftServices.simTo(username);
    }

    @GetMapping(path="/getCurrRound/")
    public int getCurrRound(@RequestParam("username") String username) {
        int draftID = draftServices.getUserMostRecentDraftID(username);
        return draftServices.getCurrRound(draftID);
    }

    @GetMapping(path="/getCurrPick/")
    public int getCurrPick(@RequestParam("username") String username) {
        int draftID = draftServices.getUserMostRecentDraftID(username);
        return draftServices.getCurrPick(draftID);
    }

    @GetMapping(path="/getNextUserPick/")
    public int getNextUserPick(@RequestParam("username") String username) {
        int draftID = draftServices.getUserMostRecentDraftID(username);
        int draftSize = draftServices.getDraftSize(draftID);
        int userPickInOddRound = draftServices.getUserStartingDraftSpot(draftServices.getUserTeamName(draftID), draftID);
        int userPickInEvenRound = Math.abs(userPickInOddRound - draftSize) + 1;
        int currRound = draftServices.getCurrRound(draftID);
        int currPick = draftServices.getCurrPick(draftID);
        return draftServices.getNextUserPick(draftSize, userPickInOddRound, userPickInEvenRound, currRound, currPick);
    }

    @GetMapping(path="/getNextUserPickRound/")
    public int getNextUserPickRound(@RequestParam("username") String username) {
        int draftID = draftServices.getUserMostRecentDraftID(username);
        int draftSize = draftServices.getDraftSize(draftID);
        int userPickInOddRound = draftServices.getUserStartingDraftSpot(draftServices.getUserTeamName(draftID), draftID);
        int userPickInEvenRound = Math.abs(userPickInOddRound - draftSize) + 1;
        int currRound = draftServices.getCurrRound(draftID);
        int currPick = draftServices.getCurrPick(draftID);
        return draftServices.getNextUserPickRound(draftSize, userPickInOddRound, userPickInEvenRound, currRound, currPick);
    }

    @PostMapping(path="/userDraftPlayer/")
    public List<PlayerDataObject> userDraftPick(@RequestParam("username") String username,
                                           @RequestParam("playerIndex") int playerIndex) {
        return draftServices.userDraftPick(username, playerIndex);
    }

    @PostMapping(path="/deleteThisDraft/")
    public boolean deleteThisDraft(@RequestParam("username") String username) {
        return draftServices.deleteThisDraft(username);
    }

    @PostMapping(path="/checkForCurrentDraft/")
    public boolean checkForDraft(@RequestParam("username") String username) {
        boolean currentDraftExists = draftServices.checkForCurrentDraft(username);
        System.out.println("Checking for current drafts: " + username + " - " + currentDraftExists);
        return currentDraftExists;
    }

    @PostMapping(path="/userMarkCurrentDraftComplete/")
    public boolean userMarkCurrentDraftComplete(@RequestParam("username") String username) {
        draftServices.userMarkCurrentDraftComplete(username);
        return draftServices.checkForCurrentDraft(username);
    }

    @PostMapping(path="/checkForPastDraft/")
    public boolean checkForPastDrafts(@RequestParam("username") String username) {
        boolean pastDraftExists = draftServices.checkForPastDrafts(username);
        System.out.println("Checking for past drafts: " + username + " - " + pastDraftExists);
        return pastDraftExists;
    }

    @GetMapping(path="/getDraftHistoryMetaData/")
    public ArrayList<HashMap<String,String>> getDraftHistoryMetaData(@RequestParam("username") String username) {
        return draftServices.getDraftHistoryMetaData(username);
    }

    @GetMapping(path="/getDraftHistoryPlayerLog/")
    public List<PlayerDataObject> getDraftHistoryPlayerLog(@RequestParam("username") String username,
                                                      @RequestParam("draftID") int draftID) {
        return draftServices.getDraftHistoryDraftedPlayerLog(username, draftID);
    }

    @GetMapping(path="/getDraftHistoryTeamList/")
    public List<TeamDataObject> getDraftHistoryTeamList(@RequestParam("username") String username,
                                                   @RequestParam("draftID") int draftID) {
        return draftServices.getDraftHistoryTeamList(username, draftID);
    }

    @GetMapping(path="/getDraftHistoryTeamReview/")
    public TreeMap<String,ArrayList<PlayerDataObject>> getDraftHistoryTeamReview(@RequestParam("username") String username,
                                                                            @RequestParam("draftID") int draftID,
                                                                            @RequestParam("teamIndex") int teamIndex) {
        return draftServices.getDraftHistoryAllTeamsMap(username, draftID, teamIndex);
    }
}
