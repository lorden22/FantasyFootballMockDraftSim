package com.example;

public class KickerPlayerModel extends PlayerModel {
    private static final double KICKERALLOWEDREACH = 0.5;
    public static final String POSITIONSHORTHANDLE = "K";

    public KickerPlayerModel(String firstName, String lastName, double predictedScore, double avgADP) {
        super(firstName, lastName, POSITIONSHORTHANDLE, predictedScore, avgADP, KICKERALLOWEDREACH);
    }
}
