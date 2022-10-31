package com.example.Mock.Dao;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.springframework.stereotype.Repository;

import com.example.Mock.StartingClasses.PlayerModel;
import com.example.Mock.StartingClasses.TeamModel;

@Repository("Teams")
public class DraftedTeamsDataObject implements TeamsDao {
    private static List<TeamModel> teamsDB;

    public DraftedTeamsDataObject() {
    }

    public static void updateTeams(List<TeamModel> teaList) {
        teamsDB = teaList;
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
