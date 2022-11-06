package com.example.Mock.API;

import java.util.ArrayList;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
