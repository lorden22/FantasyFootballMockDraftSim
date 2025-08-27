package com.example.Mock.DAO;

import java.util.ArrayList;
import java.util.TreeMap;
import com.example.common.PlayerDataObject;
import com.example.common.Logger;

public class TeamDataObject implements Comparable<TeamDataObject>, TeamDataObjectDAO {
    private String teamName;
    private boolean userTeam = false;
    private TreeMap<String,ArrayList<PlayerDataObject>> thisTeamPlayers;
    private int teamNumber;

    public TeamDataObject(String teamName, Boolean userTeam, int teamNumber){
        this.teamName = teamName;
        this.thisTeamPlayers = new TreeMap<>();
        this.userTeam = userTeam;
        this.teamNumber = teamNumber;
        this.thisTeamPlayers.put("QB", new ArrayList<>());
        this.thisTeamPlayers.put("RB", new ArrayList<>());
        this.thisTeamPlayers.put("TE", new ArrayList<>());
        this.thisTeamPlayers.put("WR", new ArrayList<>());
        this.thisTeamPlayers.put("K", new ArrayList<>());
        this.thisTeamPlayers.put("DST", new ArrayList<>());
    }
    public void addPlayer(String playerClassString, PlayerDataObject playerToAdd) {
        this.thisTeamPlayers.get(playerClassString).add(playerToAdd);
        this.thisTeamPlayers.get(playerClassString).sort(null);
    }
    public String getTeamName() { return this.teamName; }
    public int getTeamSize() { return this.thisTeamPlayers.values().stream().mapToInt(ArrayList::size).sum(); }
    private int getTeamNumber() { return this.teamNumber; }
    public boolean isUserTeam() { return this.userTeam; }
    @Override
    public int compareTo(TeamDataObject otherTeam) { return Integer.compare(this.getTeamNumber(), otherTeam.getTeamNumber()); }
    private PlayerDataObject getStartersForPosition(ArrayList<PlayerDataObject> playersAtThisPosition) { return playersAtThisPosition.stream().max((p1, p2) -> Double.compare(p1.getPredictedScore(), p2.getPredictedScore())).orElse(null); }
    private String getSpecialPositionStarters(TreeMap<String,ArrayList<PlayerDataObject>> copyOfThisTeamPlayers) {
        String kickerStarterString = "K - None";
        String defenseStarterString = "DST - None";
        for(String currPosition : copyOfThisTeamPlayers.keySet()) {
            if((currPosition.equals("K") || currPosition.equals("DST")) && !copyOfThisTeamPlayers.get(currPosition).isEmpty()) {
                PlayerDataObject currSpecialStarter = this.getStartersForPosition(copyOfThisTeamPlayers.get(currPosition));
                if(currPosition.equals("DST")) {
                    defenseStarterString = currPosition + " - " + currSpecialStarter;
                } else {
                    kickerStarterString = currPosition + " - " + currSpecialStarter;
                }
                copyOfThisTeamPlayers.get(currPosition).remove(currSpecialStarter);
            }
        } 
        return kickerStarterString + "\n" + defenseStarterString;
    }
    public TreeMap<String,ArrayList<PlayerDataObject>> getTeamTreeMap() { return this.thisTeamPlayers; }
    @Override
    public String toString() {
        TreeMap<String,ArrayList<PlayerDataObject>> copyOfThisTeamPlayers = new TreeMap<>();
        this.thisTeamPlayers.forEach((key, value) -> copyOfThisTeamPlayers.put(key, new ArrayList<>(value)));
        PlayerDataObject flexPlayer = null;
        StringBuilder returnString = new StringBuilder("\n       " + this.teamName + "      " + "\n---------------------------------------\n");
        for(String currPosition : copyOfThisTeamPlayers.keySet()) {
            if(currPosition.equals("K") || currPosition.equals("DST")) { continue; }
            int amountOfStarters = currPosition.equals("RB") || currPosition.equals("WR") ? 2 : 1;
            ArrayList<PlayerDataObject> playersAtThisPosition = copyOfThisTeamPlayers.get(currPosition);
            for (int currStarters = 1; currStarters <= amountOfStarters; currStarters++) {
                PlayerDataObject currStarer = this.getStartersForPosition(playersAtThisPosition);
                if(currStarer != null) { returnString.append(currPosition).append(" - ").append(currStarer).append("\n"); playersAtThisPosition.remove(currStarer); }
                else { returnString.append(currPosition).append(" - None\n"); }
            }
            if(currPosition.equals("RB") || currPosition.equals("WR") || currPosition.equals("TE")) {
                PlayerDataObject firstPlayerOnBench = this.getStartersForPosition(playersAtThisPosition);
                if(firstPlayerOnBench != null) {
                    if(flexPlayer == null || (flexPlayer.getPredictedScore() / 16 < firstPlayerOnBench.getPredictedScore() /16)) {
                        flexPlayer = firstPlayerOnBench;
                    }
                } 
            }
        }
        returnString.append(flexPlayer == null ? "Flex - None \n" : "Flex - " + flexPlayer + "\n");
        if (flexPlayer != null) {
            copyOfThisTeamPlayers.get(flexPlayer.getPosition()).remove(flexPlayer);
        }
        returnString.append(this.getSpecialPositionStarters(copyOfThisTeamPlayers)).append("\n\n");
        copyOfThisTeamPlayers.forEach((currPosition, players) -> players.forEach(player -> returnString.append("Bench/").append(currPosition).append(" - ").append(player).append("\n")));
        return returnString.toString();
    }
} 