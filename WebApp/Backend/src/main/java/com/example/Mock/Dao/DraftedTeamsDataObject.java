package com.example.Mock.Dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.Mock.DraftSim.TeamModel;

@Repository("Teams")
public class DraftedTeamsDataObject implements TeamsDao {
    private static List<TeamModel> teamsDB;

    public DraftedTeamsDataObject() {
    }

    public static void updateTeams(List<TeamModel> teaList) {
        teamsDB = teaList;
    }

    @Override
    public String getTeam(int teamNumber) {
        return teamsDB.get(teamNumber).toString();
    }
}
