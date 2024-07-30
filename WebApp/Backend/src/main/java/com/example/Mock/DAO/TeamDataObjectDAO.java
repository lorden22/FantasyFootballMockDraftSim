package com.example.Mock.DAO;

import java.util.ArrayList;
import java.util.TreeMap;

public interface TeamDataObjectDAO {
    void addPlayer(String playerClassString, PlayerDataObject playerToAdd);
    String getTeamName();
    int getTeamSize();
    boolean isUserTeam();
    TreeMap<String, ArrayList<PlayerDataObject>> getTeamTreeMap();
}
