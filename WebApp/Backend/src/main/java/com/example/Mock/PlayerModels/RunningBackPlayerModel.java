package com.example.Mock.PlayerModels;

import com.example.Mock.DAO.PlayerDataObject;

public class RunningBackPlayerModel extends PlayerDataObject {
	private static final double RUNNINGBACKPOSITIONREACHWEIGHT = 0.2;
	public static final String POSITIONSHORTHANDLE = "RB";
	
	public RunningBackPlayerModel(String firstName, String lastName, Double predictedScore, Double avgADP) {
		super(firstName, lastName, POSITIONSHORTHANDLE, predictedScore, avgADP, RUNNINGBACKPOSITIONREACHWEIGHT);
	}
	
}
