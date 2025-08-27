package com.example.Mock.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.common.PlayerDataObject;
import com.example.Mock.DAO.TeamDataObject;
import com.example.Mock.Service.DraftServices;
import com.example.common.Logger;

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
        this.draftServices = new DraftServices(count, namedParameterJdbcTemplate);
        
        Logger.logInfo("Database Connection established, Starting draft_id: " + count);
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
        try {
            int draftID = draftServices.getUserMostRecentDraftID(username);
            int draftSize = draftServices.getDraftSize(draftID);
            int userPickInOddRound = draftServices.getUserStartingDraftSpot(draftServices.getUserTeamName(draftID), draftID);
            int userPickInEvenRound = draftSize - userPickInOddRound + 1;
            int currRound = draftServices.getCurrRound(draftID);
            int currPick = draftServices.getCurrPick(draftID);
            
            // Calculate next round based on current position
            int nextRound = currRound;
            if (currRound % 2 == 1) { // Odd round
                if (currPick > userPickInOddRound) {
                    Logger.logDraft(draftID, username, "GET_NEXT_USER_PICK_ROUND", "Odd round, currPick: " + currPick + " > userPickInOddRound: " + userPickInOddRound);
                    nextRound++;
                }
            } else { // Even round
                if (currPick > userPickInEvenRound) {
                    Logger.logDraft(draftID, username, "GET_NEXT_USER_PICK_ROUND", "Even round, currPick: " + currPick + " > userPickInEvenRound: " + userPickInEvenRound);
                    nextRound++;
                }
            }
            
            return nextRound;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error calculating next user pick round", e);
        }
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

    @GetMapping(path="/checkIfUserPick/")
    public boolean checkIfUserPick(@RequestParam("username") String username) {
        int draftID = draftServices.getUserMostRecentDraftID(username);
        int draftSize = draftServices.getDraftSize(draftID);
        int currRound = draftServices.getCurrRound(draftID);
        int currPick = draftServices.getCurrPick(draftID);
        String teamName = draftServices.getUserTeamName(draftID);
        int userPickInOddRound = draftServices.getUserStartingDraftSpot(teamName, draftID);
        int userPickInEvenRound = draftSize - userPickInOddRound + 1;
        
        Logger.logDraft(draftID, username, "CHECK_USER_PICK", 
            String.format("Round: %d, Pick: %d, UserOdd: %d, UserEven: %d", 
            currRound, currPick, userPickInOddRound, userPickInEvenRound));
        
        if (currRound % 2 == 1) {
            if (currPick > userPickInOddRound) {
                Logger.logDraft(draftID, username, "CHECK_USER_PICK", "Odd round, user turn passed");
            }
            return currPick == userPickInOddRound;
        } else {
            if (currPick > userPickInEvenRound) {
                Logger.logDraft(draftID, username, "CHECK_USER_PICK", "Even round, user turn passed");
            }
            return currPick == userPickInEvenRound;
        }
    }

    @PostMapping(path="/userMarkCurrentDraftComplete/")
    public boolean userMarkCurrentDraftComplete(@RequestParam("username") String username) {
        draftServices.userMarkCurrentDraftComplete(username);
        return draftServices.checkForCurrentDraft(username);
    }

    @GetMapping(path="/checkForCurrentDraft/")
    public boolean checkForCurrentDraft(@RequestParam("username") String username) {
        boolean currentDraftExists = draftServices.checkForCurrentDraft(username);
        Logger.logDraft(-1, username, "CHECK_CURRENT_DRAFT", currentDraftExists ? "EXISTS" : "NOT_EXISTS");
        return currentDraftExists;
    }
    
    @GetMapping(path="/checkForPastDrafts/") 
    public boolean checkForPastDrafts(@RequestParam("username") String username) {
        boolean pastDraftExists = draftServices.checkForPastDrafts(username);
        Logger.logDraft(-1, username, "CHECK_PAST_DRAFTS", pastDraftExists ? "EXISTS" : "NOT_EXISTS");
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
