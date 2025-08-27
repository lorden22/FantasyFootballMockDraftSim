package com.example.common;

import java.util.Comparator;
import java.util.Objects;

public class PlayerDataObject implements Comparable<PlayerDataObject> {
    private String firstName;
    private String lastName;
    private String fullName;
    private String position;
    private double predictedScore;
    private double avgADP;
    private double allowedReach;
    private String spotDrafted;
    private String teamDraftedBy;
    private double rank;

    public PlayerDataObject(String firstName, String lastName, String position, double predictedScore, double avgADP, double positionAllowedReach) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.predictedScore = predictedScore;
        this.avgADP = avgADP;
        this.allowedReach = this.avgADP - positionAllowedReach;
        this.fullName = this.firstName + " " + this.lastName;
    }

    public PlayerDataObject(String firstName, String lastName, String position, double predictedScore, double avgADP) {
        this(firstName, lastName, position, predictedScore, avgADP, 0.0);
    }

    public PlayerDataObject(String name, String position, Double playerRank, double predictedScore, double avgADP) {
        this.fullName = name;
        this.setFirstName(name.split(" ")[0]);
        this.setLastName(name.split(" ")[1]);
        this.position = position;
        this.rank = playerRank;
        this.predictedScore = predictedScore;
        this.avgADP = avgADP;
    }

    public String getFirstName() { return this.firstName; }
    private void setFirstName(String firstNameChange) { this.firstName = firstNameChange; }
    public String getLastName() { return this.lastName; }
    private void setLastName(String lastNameChange) { this.lastName = lastNameChange; }
    public String getFullName() { return this.fullName; }
    private void setFullNameName(String fullNameChange) { this.fullName = fullNameChange; }
    public String getPosition() { return this.position; }
    private void setPosition(String positionChange) { this.position = positionChange; }
    public double getPredictedScore() { return this.predictedScore; }
    private void setPredictedScore(double predictedScoreChange) { this.predictedScore = predictedScoreChange; }
    public double getRank() { return this.rank; }
    public void setRank(double rankChange) { this.rank = rankChange; }
    public double getAvgADP() { return this.avgADP; }
    private void setAvgADP(double avgADPChange) { this.avgADP = avgADPChange; }
    public double getAllowedReach() { return this.allowedReach; }
    private void setAllowedReach(double allowedReachChange) { this.allowedReach = allowedReachChange; }
    @Override
    public String toString() { return this.firstName + " " + this.lastName + " - Predicted 2024 score = " + this.predictedScore + ", Avg ADP = " + this.avgADP; }
    @Override
    public int compareTo(PlayerDataObject otherPlayer) { return Double.compare(this.getAllowedReach(), otherPlayer.getAllowedReach()); }
    @Override
    public boolean equals(Object obj) { if (this == obj) return true; if (obj == null || getClass() != obj.getClass()) return false; PlayerDataObject that = (PlayerDataObject) obj; return Double.compare(that.rank, rank) == 0; }
    @Override
    public int hashCode() { return Objects.hash(rank); }
    public String getSpotDrafted() { return this.spotDrafted; }
    public void setSpotDrafted(String spotDraftedChange) { this.spotDrafted = spotDraftedChange; }
    public String getTeamDraftedBy() { return this.teamDraftedBy; }
    public void setTeamDraftedBy(String teamDraftedByChange) { this.teamDraftedBy = teamDraftedByChange; }
    public static Comparator<PlayerDataObject> spotDraftedComparator = new Comparator<>() {
        @Override
        public int compare(PlayerDataObject player1, PlayerDataObject player2) {
            String[] player1Split = player1.getSpotDrafted().split("\\.");
            String[] player2Split = player2.getSpotDrafted().split("\\.");
            Double player1Round = Double.parseDouble(player1Split[0]);
            Double player2Round = Double.parseDouble(player2Split[0]);
            Double player1Pick = Double.parseDouble(player1Split[1]);
            Double player2Pick = Double.parseDouble(player2Split[1]);
            int roundComparison = player1Round.compareTo(player2Round);
            return roundComparison != 0 ? roundComparison : player1Pick.compareTo(player2Pick);
        }
    };
} 