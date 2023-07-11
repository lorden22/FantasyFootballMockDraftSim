package com.example.Mock.DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.example.Mock.DAO.DraftedTeamsDataObject;
import com.example.Mock.StartingClasses.PlayerModel;
import com.example.Mock.StartingClasses.TeamModel;;

public interface TeamsDAO {
    void updateTeams(List<TeamModel> teamsList);
    List<TreeMap<String,ArrayList<PlayerModel>>> getAllTeamsTreeMap();
}
