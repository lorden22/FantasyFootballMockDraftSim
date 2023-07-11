package com.example.Mock.API;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.example.Mock.Service.LoginServices;
import com.example.Mock.DAO.DraftDataObject;
import com.example.Mock.DAO.DraftedTeamsDataObject;
import com.example.Mock.DAO.TeamsDAO;
import com.example.Mock.Service.DraftServices;
import com.example.Mock.StartingClasses.PlayerModel;
import com.example.Mock.StartingClasses.TeamModel;

@RequestMapping("api/teams")
@CrossOrigin
@RestController
public class DraftController {

    private HashMap<String,DraftServices> allDraftServices;

    public DraftController() {
        this.allDraftServices = new HashMap<String,DraftServices>();
    }
    
    @PostMapping(path="/initaizeUserAccountSetup")
    public boolean initaizeUserAccountSetup(
        @RequestParam("username") String username,
        @Autowired DraftServices draftServices) {
            this.allDraftServices.put(username, draftServices);
            return true;
        }

    @GetMapping(path="/getPlayersLeft/") 
    public List<PlayerModel> getPlayersLeft(
        @RequestParam("username") String username) {
        return this.allDraftServices.get(username).getPlayersLeft();
    }

    @GetMapping(path="/getAllPlayersDrafted/")
    public List<PlayerModel> getAllPlayersDrafted(
        @RequestParam("username") String username) {
            return this.allDraftServices.get(username).getDraftedPlayers();
    }

    @PostMapping(path="/startDraft/")
    public List<PlayerModel> startDraft(
        @RequestParam("username") String username,
        @RequestParam("teamName") String teamName, 
        @RequestParam("draftSize") int draftSize, 
        @RequestParam("draftPosition") int draftPositios,
        @Autowired DraftDataObject draftDataObject) {
            System.out.println("username: " + username + " - has draftServices: " + this.allDraftServices.containsKey(username) + " - " + this.allDraftServices);
            return this.allDraftServices.get(username).startDraft(teamName, draftSize, draftPositios, draftDataObject);

    }

    @PostMapping(path="/simTo/")
    public List<PlayerModel> simtTo(
        @RequestParam("username") String username) {
            return this.allDraftServices.get(username).simTo();
    }

    @GetMapping(path="/getCurrRound/")
    public int getCurrRound(
        @RequestParam("username") String username) {
            return this.allDraftServices.get(username).getCurrRound();
    }

    @GetMapping(path="/getCurrPick/")
    public int getCurrPick(
        @RequestParam("username") String username) {
            return this.allDraftServices.get(username).getCurrPick();
    }

    @GetMapping(path="/getNextUserPick/")
    public int getNextUserPick(
        @RequestParam("username") String username) {
            return this.allDraftServices.get(username).getNextUserPick();
    }

    @GetMapping(path="/getNextUserPickRound/")
    public int getNextUserPickRound(
        @RequestParam("username") String username) {
            return this.allDraftServices.get(username).getNextUserPickRound();
    }

    @PostMapping(path="/userDraftPlayer/")
    public List<PlayerModel> userDraftPick(
        @RequestParam("username") String username,
        @RequestParam("playerIndex") int playerIndex) {
            return this.allDraftServices.get(username).userDraftPick(playerIndex);
    }

    @PostMapping(path="/deleteThisDraft/")
    public boolean deleteThisDraft(
        @RequestParam("username") String username) {
            return this.allDraftServices.get(username).deleteThisDraft();
    }

    @GetMapping(path="/checkForCurrentDrafts/")
    public boolean checkForDraft(
        @RequestParam("username") String username) {
            System.out.println("Checking for current drafts: " + username + " - " + this.allDraftServices.containsKey(username));
            if (this.allDraftServices.containsKey(username)) {
                System.out.println(" - " + this.allDraftServices.get(username).checkForDraft());
                return this.allDraftServices.get(username).checkForDraft();
            }
            return false;
    }

    @GetMapping(path="/checkForPastDrafts/")
    public boolean checkForPastDrafts(
        @RequestParam("username") String username) {
            if (this.allDraftServices.containsKey(username)) {
                System.out.println("Checking for past drafts: " + username + " - " + this.allDraftServices.get(username).checkForPastDrafts());
                return this.allDraftServices.get(username).checkForPastDrafts();
            }
            return false;
    }

    @GetMapping(path="/getDraftHistoryMetaData/")
    public ArrayList<HashMap<String,String>>  getDraftHistoryMetaData(
        @RequestParam("username") String username) {
            return this.allDraftServices.get(username).getDraftHistoryMetaData();
    }

    @GetMapping(path="/getDraftHistoryPlayerLog/")
    public List<PlayerModel> getDraftHistoryPlayerLog(
        @RequestParam("username") String username,
        @RequestParam("draftID") int draftID) {
            return this.allDraftServices.get(username).getDraftHistoryDraftedPlayerLog(draftID);
    }

    @GetMapping(path="/getDraftHistoryTeamList/")
    public List<TeamModel> getDraftHistoryTeamList(
        @RequestParam("username") String username,
        @RequestParam("draftID") int draftID) {
            return this.allDraftServices.get(username).getDraftHistoryTeamList(draftID);
    }


    @GetMapping(path="/getDraftHistoryTeamReview/")
    public TreeMap<String,ArrayList<PlayerModel>> getDraftHistoryTeamReview(
        @RequestParam("username") String username,
        @RequestParam("draftID") int draftID,
        @RequestParam("teamIndex") int teamIndex) {
            List<TreeMap<String,ArrayList<PlayerModel>>> allTreeMaps = this.allDraftServices.get(username).getDraftHistoryAllTeamsMap(draftID);
            return allTreeMaps.get(teamIndex);
    }

}

     
