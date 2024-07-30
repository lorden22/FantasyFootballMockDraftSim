package com.example.Mock.PlayerModels;

import com.example.Mock.DAO.PlayerDataObject;

public class WideReceiverPlayerModel extends PlayerDataObject {
	private static final double WIDERECEIVERALLOWEDREACH = 0.1;
	public static final String POSITIONSHORTHANDLE = "WR";
	
	public WideReceiverPlayerModel(String firstName, String lastName, Double predictedScore, Double avgADP) {
		super(firstName,lastName,POSITIONSHORTHANDLE, predictedScore,avgADP,WIDERECEIVERALLOWEDREACH);
	}
	

}
