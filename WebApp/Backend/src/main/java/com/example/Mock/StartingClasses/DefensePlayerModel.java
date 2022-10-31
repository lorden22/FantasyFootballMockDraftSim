package com.example.Mock.StartingClasses;
public class DefensePlayerModel extends PlayerModel {
    private static final double DEFENSEALLOWEDREACH = 0.5;
    public static final String POSITIONSHORTHANDLE = "DEF";

    public DefensePlayerModel(String firstName, String lastName, double predictedScore, double avgADP) {
        super(firstName, lastName, POSITIONSHORTHANDLE, predictedScore, avgADP, DEFENSEALLOWEDREACH);
    }
}
