package com.example.Mock.DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.example.Mock.DAO.DraftedTeamsDataObject;
import com.example.Mock.StartingClasses.PlayerModel;;

public interface TeamsDAO {
    TreeMap<String,ArrayList<PlayerModel>> getTeamObject(int teamNumber);   
    String getTeamString(int teamNumber);
    List<PlayerModel> getPlayersDraftedRanked();
    List<PlayerModel> getPlayersLeft();
}
