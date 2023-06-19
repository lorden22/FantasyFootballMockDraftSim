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
        @RequestParam("teamName") String teamName,
        @RequestParam("teamNumber") int teamNummber) {
            return this.allDraftServices.get(teamName).getTeamObject(teamNummber);
    }

    @GetMapping(path="/getTeamString/")
    public String getTeamString(
        @RequestParam("teamName") String teamName,
        @RequestParam("teamNumber") int teamNummber) {
       return this.allDraftServices.get(teamName).getTeamString(teamNummber);
    }

    @GetMapping(path="/getPlayersLeft/") 
    public List<PlayerModel> getPlayersLeft(
        @RequestParam("teamName") String teamName) {
        return this.allDraftServices.get(teamName).getPlayersLeft();
    }

    @GetMapping(path="/getPlayerDrafted/")
    public List<PlayerModel> getPlayerDrafted(
        @RequestParam("teamName") String teamName) {
            this.allDraftServices.get(teamName).getPlayersDraftedRanked().sort(null);
            return this.allDraftServices.get(teamName).getPlayersDraftedRanked();
    }

    @PostMapping(path="/startDraft/")
    public List<PlayerModel> startDraft(
       @RequestParam("teamName") String teamName, 
        @RequestParam("draftSize") int draftSize, 
        @RequestParam("draftPosition") int draftPositios,
        @Autowired DraftServices draftServices,
        @Autowired DraftDataObject draftDataObject,
        @Autowired DraftedTeamsDataObject draftedTeamsDataObject) {
            this.allDraftServices.put(teamName, draftServices);
            return this.allDraftServices.get(teamName).startDraft(teamName, draftSize, draftPositios, draftDataObject, draftedTeamsDataObject);

    }

    @PostMapping(path="/simTo/")
    public List<PlayerModel> simtTo(
        @RequestParam("teamName") String teamName) {
            return this.allDraftServices.get(teamName).simTo();
    }

    @GetMapping(path="/getCurrRound/")
    public int getCurrRound(
        @RequestParam("teamName") String teamName) {
            return this.allDraftServices.get(teamName).getCurrRound();
    }

    @GetMapping(path="/getCurrPick/")
    public int getCurrPick(
        @RequestParam("teamName") String teamName) {
            return this.allDraftServices.get(teamName).getCurrPick();
    }

    @GetMapping(path="/getNextUserPick/")
    public int getNextUserPick(
        @RequestParam("teamName") String teamName) {
            return this.allDraftServices.get(teamName).getNextUserPick();
    }

    @PostMapping(path="/userDraftPlayer/")
    public List<PlayerModel> userDraftPick(
        @RequestParam("teamName") String teamName,
        @RequestParam("playerIndex") int playerIndex) {
            return this.allDraftServices.get(teamName).userDraftPick(playerIndex);
    }

    @DeleteMapping(path="/deleteThisDraft/")
        public void deleteThisDraft(
            @RequestParam("teamName") String teamName) {
                this.allDraftServices.remove(teamName);
    }
}
    
