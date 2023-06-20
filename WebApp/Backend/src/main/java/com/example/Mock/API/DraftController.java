package com.example.Mock.API;

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

import com.example.Mock.DAO.DraftDataObject;
import com.example.Mock.DAO.DraftedTeamsDataObject;
import com.example.Mock.DAO.TeamsDAO;
import com.example.Mock.Service.LoginServices;
import com.example.Mock.Service.DraftServices;
import com.example.Mock.StartingClasses.PlayerModel;

@RequestMapping("api/teams")
@CrossOrigin
@RestController
public class DraftController {

    private HashMap<String,DraftServices> allDraftServices;

    public DraftController() {
        this.allDraftServices = new HashMap<String,DraftServices>();
    }
    
    @GetMapping(path="/getTeamObject/")
    public TreeMap<String,ArrayList<PlayerModel>> getTeamObject(
        @RequestParam("username") String username,
        @RequestParam("teamnumber") int teamNummber) {
            return this.allDraftServices.get(username).getTeamObject(teamNummber);
    }

    @GetMapping(path="/getTeamString/")
    public String getTeamString(
        @RequestParam("username") String username,
        @RequestParam("teamNumber") int teamNummber) {
       return this.allDraftServices.get(username).getTeamString(teamNummber);
    }

    @GetMapping(path="/getPlayersLeft/") 
    public List<PlayerModel> getPlayersLeft(
        @RequestParam("username") String username) {
        return this.allDraftServices.get(username).getPlayersLeft();
    }

    @GetMapping(path="/getPlayerDrafted/")
    public List<PlayerModel> getPlayerDrafted(
        @RequestParam("username") String username) {
            this.allDraftServices.get(username).getPlayersDraftedRanked().sort(null);
            return this.allDraftServices.get(username).getPlayersDraftedRanked();
    }

    @PostMapping(path="/startDraft/")
    public List<PlayerModel> startDraft(
        @RequestParam("username") String username,
        @RequestParam("teamName") String teamName, 
        @RequestParam("draftSize") int draftSize, 
        @RequestParam("draftPosition") int draftPositios,
        @Autowired DraftServices draftServices,
        @Autowired DraftDataObject draftDataObject,
        @Autowired DraftedTeamsDataObject draftedTeamsDataObject) {
            this.allDraftServices.put(username, draftServices);
            return this.allDraftServices.get(username).startDraft(teamName, draftSize, draftPositios, draftDataObject, draftedTeamsDataObject);

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

    @PostMapping(path="/userDraftPlayer/")
    public List<PlayerModel> userDraftPick(
        @RequestParam("username") String username,
        @RequestParam("playerIndex") int playerIndex) {
            return this.allDraftServices.get(username).userDraftPick(playerIndex);
    }

    @DeleteMapping(path="/deleteThisDraft/")
    public void deleteThisDraft(
        @RequestParam("username") String username) {
            this.allDraftServices.remove(username);
    }

    @GetMapping(path="/checkForDraft/") 
    public boolean checkForDraft(
        @RequestParam("username") String username) {
            return this.allDraftServices.containsKey(username);
    }
}

    
