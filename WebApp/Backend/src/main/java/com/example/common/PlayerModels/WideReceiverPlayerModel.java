package com.example.common.PlayerModels;

import com.example.common.PlayerDataObject;

public class WideReceiverPlayerModel extends PlayerDataObject {
    private static final double WIDERECEIVERALLOWEDREACH = 0.1;
    public static final String POSITIONSHORTHANDLE = "WR";

    public WideReceiverPlayerModel(String firstName, String lastName, double predictedScore, double avgADP) {
        super(firstName, lastName, POSITIONSHORTHANDLE, predictedScore, avgADP, WIDERECEIVERALLOWEDREACH);
    }
} 