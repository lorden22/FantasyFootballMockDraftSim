package com.example.Mock.Dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.springframework.stereotype.Repository;

import com.example.Mock.StartingClasses.PlayerModel;
import com.example.Mock.StartingClasses.TeamModel;

@Repository("Teams")
public class DraftedTeamsDataObject implements TeamsDao {
    private static List<TeamModel> teamsDB;
    private static List<PlayerModel> playersLeftDB;


    public DraftedTeamsDataObject() {
    }

    public static void updateTeams(List<TeamModel> teaList) {
        teamsDB = teaList;
    }

    public static void updatePlayers(List<PlayerModel> playersLeft) {
        playersLeftDB = playersLeft;
        playersLeftDB.sort(null);
    }

    public List<PlayerModel> getPlayersDraftedRanked() {
        ArrayList<PlayerModel> playersDrafted = new ArrayList<PlayerModel>();
        for (TeamModel currTeam : teamsDB) {
            for(String currPostion : currTeam.getTeamTreeMap().keySet()) {
                playersDrafted.addAll(currTeam.getTeamTreeMap().get(currPostion));
            }
        }
        playersDrafted.sort(null);
        return playersDrafted;
    }

    public List<PlayerModel> getPlayersLeft() {
        return playersLeftDB;
    }

    @Override
    public TreeMap<String,ArrayList<PlayerModel>> getTeamObject(int teamNumber) {
        return teamsDB.get(teamNumber).getTeamTreeMap();
    }

    @Override
    public String getTeamString(int teamNumber) {
        return teamsDB.get(teamNumber).toString();
    }
}
