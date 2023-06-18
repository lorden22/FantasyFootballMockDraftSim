package com.example.Mock.Dao;

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
public class DraftedTeamsDataObject implements TeamsDao {
    private List<TeamModel> teamsDB;
    private List<PlayerModel> playersLeftDB;


    public DraftedTeamsDataObject() {
    }

    public void updateTeams(List<TeamModel> teaList) {
        this.teamsDB = teaList;
    }

    public void updatePlayers(List<PlayerModel> playersLeft) {
        this.playersLeftDB = playersLeft;
        this.playersLeftDB.sort(null);
    }

    public List<PlayerModel> getPlayersDraftedRanked() {
        ArrayList<PlayerModel> playersDrafted = new ArrayList<PlayerModel>();
        for (TeamModel currTeam : this.teamsDB) {
            for(String currPostion : currTeam.getTeamTreeMap().keySet()) {
                playersDrafted.addAll(currTeam.getTeamTreeMap().get(currPostion));
            }
        }
        playersDrafted.sort(null);
        return playersDrafted;
    }

    public List<PlayerModel> getPlayersLeft() {
        return this.playersLeftDB;
    }

    @Override
    public TreeMap<String,ArrayList<PlayerModel>> getTeamObject(int teamNumber) {
        return this.teamsDB.get(teamNumber).getTeamTreeMap();
    }

    @Override
    public String getTeamString(int teamNumber) {
        return this.teamsDB.get(teamNumber).toString();
    }
}
