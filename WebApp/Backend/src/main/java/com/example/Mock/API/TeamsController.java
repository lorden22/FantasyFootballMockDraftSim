package com.example.Mock.API;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Mock.Service.TeamsServices;
import com.example.Mock.StartingClasses.PlayerModel;

@RequestMapping("api/teams")

@RestController
public class TeamsController {
    private final TeamsServices teamsServices;
    
    @Autowired
    public TeamsController(TeamsServices teamsServices){
        this.teamsServices = teamsServices;
    }

    @GetMapping(path="/getTeamObject/{teamNumber}")
    public TreeMap<String,ArrayList<PlayerModel>> getTeamObject(@PathVariable("teamNumber") int teamNummber) {
       return this.teamsServices.getTeamObject(teamNummber);
    }

    @GetMapping(path="/getTeamString/{teamNumber}")
    public String getTeamString(@PathVariable("teamNumber") int teamNummber) {
       return this.teamsServices.getTeamString(teamNummber);
    }

    @GetMapping(path="/getPlayersLeft/") 
    public List<PlayerModel> getPlayersLeft() {
        return this.teamsServices.getPlayersLeft();
    }

    @GetMapping(path="/getPlayerDrafted/")
    public List<PlayerModel> getPlayerDrafted() {
        this.teamsServices.getPlayersDraftedRanked().sort(null);
        return this.teamsServices.getPlayersDraftedRanked();
    }

    @PostMapping(path="/startDraft/")
    public List<PlayerModel> startDraft(
       @RequestParam("teamName") String teamName, 
        @RequestParam("draftSize") int draftSize, 
        @RequestParam("draftPosition") int draftPosition) {
           return this.teamsServices.startDraft(teamName, draftSize, draftPosition);
    }

    @PostMapping(path="/simTo/")
    public List<PlayerModel> simtTo() {
            return this.teamsServices.simTo();
    }

    @GetMapping(path="/getCurrRound/")
    public int getCurrRound() {
        return this.teamsServices.getCurrRound();
    }

    @GetMapping(path="/getCurrPick/")
    public int getCurrPick() {
        return this.teamsServices.getCurrPick();
    }

    @GetMapping(path="/getNextUserPick/")
    public int getNextUserPick() {
        return this.teamsServices.getNextUserPick();
    }

    @PostMapping(path="/userDraftPlayer/")
    public List<PlayerModel> userDraftPick(
        @RequestParam("playerIndex") int playerIndex) {
            return this.teamsServices.userDraftPick(playerIndex);
    }
}
    
