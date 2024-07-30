package com.example.Mock.PlayerModels;

import com.example.Mock.DAO.PlayerDataObject;

public class DefensePlayerModel extends PlayerDataObject {
    private static final double DEFENSEALLOWEDREACH = 0.5;
    public static final String POSITIONSHORTHANDLE = "DST";

    public DefensePlayerModel(String firstName, String lastName, double predictedScore, double avgADP) {
        super(firstName, lastName, POSITIONSHORTHANDLE, predictedScore, avgADP, DEFENSEALLOWEDREACH);
    }
}
