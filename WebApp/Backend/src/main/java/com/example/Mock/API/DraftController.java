package com.example.Mock.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

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
@CrossOrigin(origins = {"https://localhost:5500", "https://127.0.0.1:5500", "https://localhost", "https://127.0.0.1", "https://fantasy-football-draft.fly.dev"})
@RestController
public class DraftController {

    private DraftServices draftServices;

    // Username: alphanumeric and underscore only, 3-50 characters
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    // Team name: alphanumeric, spaces, and common punctuation, 1-50 characters
    private static final Pattern TEAM_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9 _\\-'.]{1,50}$");

    @Autowired
    public DraftController(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        Integer count = namedParameterJdbcTemplate.queryForObject("SELECT MAX(draft_id) FROM drafts", new HashMap<>(), Integer.class);
        if(count==null) {
            count=1;
        }
        this.draftServices = new DraftServices(count, namedParameterJdbcTemplate);

        Logger.logInfo("Database Connection established, Starting draft_id: " + count);
    }

    private void validateUsername(String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Username must be 3-50 characters, alphanumeric and underscores only");
        }
    }

    private void validateTeamName(String teamName) {
        if (teamName == null || !TEAM_NAME_PATTERN.matcher(teamName).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Team name must be 1-50 characters");
        }
    }

    private void validateDraftSize(int draftSize) {
        if (draftSize < 4 || draftSize > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Draft size must be between 4 and 20");
        }
    }

    private void validateDraftPosition(int draftPosition, int draftSize) {
        if (draftPosition < 1 || draftPosition > draftSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Draft position must be between 1 and draft size");
        }
    }

    private void validatePlayerIndex(int playerIndex) {
        if (playerIndex < 1 || playerIndex > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Player index must be a positive number");
        }
    }

    private void validateDraftID(int draftID) {
        if (draftID < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Draft ID must be a positive number");
        }
    }

    private void validateTeamIndex(int teamIndex) {
        if (teamIndex < 1 || teamIndex > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Team index must be between 1 and 20");
        }
    }
    
    @PostMapping(path="/initaizeUserAccountSetup")
    public boolean initaizeUserAccountSetup(@RequestParam("username") String username) {
        validateUsername(username);
        return true;
    }

    @GetMapping(path="/getPlayersLeft/")
    public List<PlayerDataObject> getPlayersLeft(@RequestParam("username") String username){
        validateUsername(username);
        int draftID = draftServices.getUserMostRecentDraftID(username);
        int draftSize = draftServices.getDraftSize(draftID);
        return draftServices.getPlayersLeft(draftID, draftSize);
    }

    @GetMapping(path="/getAllPlayersDrafted/")
    public List<PlayerDataObject> getAllPlayersDrafted(@RequestParam("username") String username) {
        validateUsername(username);
        int draftID = draftServices.getUserMostRecentDraftID(username);
        int draftSize = draftServices.getDraftSize(draftID);
        return draftServices.getDraftedPlayers(draftID, draftSize);
    }

    @PostMapping(path="/startDraft/")
    public List<PlayerDataObject> startDraft(@RequestParam("username") String username,
                                        @RequestParam("teamName") String teamName,
                                        @RequestParam("draftSize") int draftSize,
                                        @RequestParam("draftPosition") int draftPosition) {
        validateUsername(username);
        validateTeamName(teamName);
        validateDraftSize(draftSize);
        validateDraftPosition(draftPosition, draftSize);
        return draftServices.startDraft(username, teamName, draftSize, draftPosition);
    }

    @PostMapping(path="/simTo/")
    public List<PlayerDataObject> simtTo(@RequestParam("username") String username) {
        validateUsername(username);
        return draftServices.simTo(username);
    }

    @GetMapping(path="/getCurrRound/")
    public int getCurrRound(@RequestParam("username") String username) {
        validateUsername(username);
        int draftID = draftServices.getUserMostRecentDraftID(username);
        return draftServices.getCurrRound(draftID);
    }

    @GetMapping(path="/getCurrPick/")
    public int getCurrPick(@RequestParam("username") String username) {
        validateUsername(username);
        int draftID = draftServices.getUserMostRecentDraftID(username);
        return draftServices.getCurrPick(draftID);
    }

    @GetMapping(path="/getNextUserPick/")
    public int getNextUserPick(@RequestParam("username") String username) {
        validateUsername(username);
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
        validateUsername(username);
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
        validateUsername(username);
        validatePlayerIndex(playerIndex);
        return draftServices.userDraftPick(username, playerIndex);
    }

    @PostMapping(path="/deleteThisDraft/")
    public boolean deleteThisDraft(@RequestParam("username") String username) {
        validateUsername(username);
        return draftServices.deleteThisDraft(username);
    }

    @GetMapping(path="/checkIfUserPick/")
    public boolean checkIfUserPick(@RequestParam("username") String username) {
        validateUsername(username);
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
        validateUsername(username);
        draftServices.userMarkCurrentDraftComplete(username);
        return draftServices.checkForCurrentDraft(username);
    }

    @RequestMapping(path="/checkForCurrentDraft/", method = {RequestMethod.GET, RequestMethod.POST})
    public boolean checkForCurrentDraft(@RequestParam("username") String username) {
        validateUsername(username);
        boolean currentDraftExists = draftServices.checkForCurrentDraft(username);
        Logger.logDraft(-1, username, "CHECK_CURRENT_DRAFT", currentDraftExists ? "EXISTS" : "NOT_EXISTS");
        return currentDraftExists;
    }

    @GetMapping(path="/checkForPastDrafts/")
    public boolean checkForPastDrafts(@RequestParam("username") String username) {
        validateUsername(username);
        boolean pastDraftExists = draftServices.checkForPastDrafts(username);
        Logger.logDraft(-1, username, "CHECK_PAST_DRAFTS", pastDraftExists ? "EXISTS" : "NOT_EXISTS");
        return pastDraftExists;
    }

    @GetMapping(path="/getDraftHistoryMetaData/")
    public ArrayList<HashMap<String,String>> getDraftHistoryMetaData(@RequestParam("username") String username) {
        validateUsername(username);
        return draftServices.getDraftHistoryMetaData(username);
    }

    @GetMapping(path="/getDraftHistoryPlayerLog/")
    public List<PlayerDataObject> getDraftHistoryPlayerLog(@RequestParam("username") String username,
                                                      @RequestParam("draftID") int draftID) {
        validateUsername(username);
        validateDraftID(draftID);
        return draftServices.getDraftHistoryDraftedPlayerLog(username, draftID);
    }

    @GetMapping(path="/getDraftHistoryTeamList/")
    public List<TeamDataObject> getDraftHistoryTeamList(@RequestParam("username") String username,
                                                   @RequestParam("draftID") int draftID) {
        validateUsername(username);
        validateDraftID(draftID);
        return draftServices.getDraftHistoryTeamList(username, draftID);
    }

    @GetMapping(path="/getDraftHistoryTeamReview/")
    public TreeMap<String,ArrayList<PlayerDataObject>> getDraftHistoryTeamReview(@RequestParam("username") String username,
                                                                            @RequestParam("draftID") int draftID,
                                                                            @RequestParam("teamIndex") int teamIndex) {
        validateUsername(username);
        validateDraftID(draftID);
        validateTeamIndex(teamIndex);
        return draftServices.getDraftHistoryAllTeamsMap(username, draftID, teamIndex);
    }
}
