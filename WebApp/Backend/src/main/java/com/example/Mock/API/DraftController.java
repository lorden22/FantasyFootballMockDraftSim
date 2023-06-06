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

import com.example.Mock.Dao.DraftDataObject;
import com.example.Mock.Dao.DraftedTeamsDataObject;
import com.example.Mock.Dao.TeamsDao;
import com.example.Mock.Service.DraftServices;
import com.example.Mock.StartingClasses.PlayerModel;

@RequestMapping("api/teams")
@CrossOrigin
@RestController
public class DraftController {

    private HashMap<String,DraftServices> allDraftServices;

    public DraftController() {
        System.out.println("DraftController constructor");
        this.allDraftServices = new HashMap<String,DraftServices>();
    }
    

    @GetMapping(path="/getTeamObject/{teamNumber}")
    public TreeMap<String,ArrayList<PlayerModel>> getTeamObject(@PathVariable("teamNumber") int teamNummber) {
       return this.allDraftServices.get("").getTeamObject(teamNummber);
    }

    @GetMapping(path="/getTeamString/{teamNumber}")
    public String getTeamString(@PathVariable("teamNumber") int teamNummber) {
       return this.allDraftServices.get("").getTeamString(teamNummber);
    }

    @GetMapping(path="/getPlayersLeft/") 
    public List<PlayerModel> getPlayersLeft() {
        return this.allDraftServices.get("").getPlayersLeft();
    }

    @GetMapping(path="/getPlayerDrafted/")
    public List<PlayerModel> getPlayerDrafted() {
        this.allDraftServices.get("").getPlayersDraftedRanked().sort(null);
        return this.allDraftServices.get("").getPlayersDraftedRanked();
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
    public List<PlayerModel> simtTo() {
            return this.allDraftServices.get("").simTo();
    }

    @GetMapping(path="/getCurrRound/")
    public int getCurrRound() {
        return this.allDraftServices.get("").getCurrRound();
    }

    @GetMapping(path="/getCurrPick/")
    public int getCurrPick() {
        return this.allDraftServices.get("").getCurrPick();
    }

    @GetMapping(path="/getNextUserPick/")
    public int getNextUserPick() {
        return this.allDraftServices.get("").getNextUserPick();
    }

    @PostMapping(path="/userDraftPlayer/")
    public List<PlayerModel> userDraftPick(
        @RequestParam("playerIndex") int playerIndex) {
            return this.allDraftServices.get("").userDraftPick(playerIndex);
    }
}
    
