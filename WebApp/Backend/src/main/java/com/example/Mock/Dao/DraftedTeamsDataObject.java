package com.example.Mock.DAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.example.Mock.StartingClasses.PlayerModel;
import com.example.Mock.StartingClasses.TeamModel;

@Repository
@Scope(value="prototype")
public class DraftedTeamsDataObject implements TeamsDAO {
    private List<TeamModel> teamsDB;
    public DraftedTeamsDataObject() {
    }

    public void updateTeams(List<TeamModel> teamsList) {
        this.teamsDB = teamsList;
    }

    public List<TreeMap<String,ArrayList<PlayerModel>>> getAllTeamsTreeMap(){
        List<TreeMap<String,ArrayList<PlayerModel>>> teams = new ArrayList<TreeMap<String,ArrayList<PlayerModel>>>();
        for (TeamModel currTeam : this.teamsDB) {
            teams.add(currTeam.getTeamTreeMap());
        }
        return teams;
    }





}
