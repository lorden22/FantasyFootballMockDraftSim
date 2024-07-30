package com.example.Mock.PlayerModels;

import com.example.Mock.DAO.PlayerDataObject;

public class TightEndPlayerModel extends PlayerDataObject{
	private static final double TIGHTENDALLOWEDREACH = 0.0;
	public static final String POSITIONSHORTHANDLE = "TE";
	
	public TightEndPlayerModel(String firstName, String lastName, double predictedScore, double avgADP ) {
		super(firstName,lastName,POSITIONSHORTHANDLE, predictedScore,avgADP,TIGHTENDALLOWEDREACH);
	}
}