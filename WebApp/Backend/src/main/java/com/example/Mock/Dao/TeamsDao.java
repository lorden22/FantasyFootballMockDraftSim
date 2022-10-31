package com.example.Mock.Dao;

import java.util.ArrayList;
import java.util.TreeMap;

import com.example.Mock.Dao.DraftedTeamsDataObject;
import com.example.Mock.StartingClasses.PlayerModel;;

public interface TeamsDao {
    TreeMap<String,ArrayList<PlayerModel>> getTeamObject(int teamNumber);   
    String getTeamString(int teamNumber);
}
