package com.example.Mock.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Mock.DraftSim.TeamModel;
import com.example.Mock.Service.TeamsServices;

@RequestMapping("api/teams")

@RestController
public class TeamsController {
    private final TeamsServices teamsServices;
    
    @Autowired
    public TeamsController(TeamsServices teamsServices){
        this.teamsServices = teamsServices;
    }

    @GetMapping(path="{teamNumber}")
    public String getTeam(@PathVariable("teamNumber") int teamNummber) {
       return this.teamsServices.getTeam(teamNummber);
    }
}
