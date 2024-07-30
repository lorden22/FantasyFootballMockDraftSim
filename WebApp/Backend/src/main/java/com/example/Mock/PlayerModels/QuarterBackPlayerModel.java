package com.example.Mock.PlayerModels;

import com.example.Mock.DAO.PlayerDataObject;

public class QuarterBackPlayerModel extends PlayerDataObject{
	private static final double QUARTERBACKALLOWEDREACH = -0.1;
	public static final String POSITIONSHORTHANDLE = "QB";
	
	public QuarterBackPlayerModel(String firstName, String lastName, double predictedScore, double avgADP) {
		super(firstName,lastName,POSITIONSHORTHANDLE, predictedScore,avgADP,QUARTERBACKALLOWEDREACH);
	}

}
