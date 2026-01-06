package com.example.common.PlayerModels;

import com.example.common.PlayerDataObject;

public class RunningBackPlayerModel extends PlayerDataObject {
    private static final double RUNNINGBACKPOSITIONREACHWEIGHT = 0.2;
    public static final String POSITIONSHORTHANDLE = "RB";

    public RunningBackPlayerModel(String firstName, String lastName, double predictedScore, double avgADP) {
        super(firstName, lastName, POSITIONSHORTHANDLE, predictedScore, avgADP, RUNNINGBACKPOSITIONREACHWEIGHT);
    }
} 