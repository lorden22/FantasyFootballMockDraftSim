package com.example.Mock.StartingClasses;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;



public class MockDraftDriver {

	private static double getADP(int totalDraftPicksInRound, double rank) { 
		double pick;
		double round = Math.floor(rank / totalDraftPicksInRound);
		if(rank % totalDraftPicksInRound == 0) {
			pick = totalDraftPicksInRound / 100.0;
			round -= 1;
		}
		else pick = (rank % totalDraftPicksInRound) / 100.0;
		return round+pick;
	}

	public static ArrayList<TeamModel> main(String[] args ) {
		System.out.println("---------Reading Starting File In Now----------");
		File playerStatFile = new File("WebApp/Backend/src/main/java/com/example/Mock/StartingClasses/WebScraping/PlayerData.txt");
		TreeMap<String,ArrayList<Object>> allPlayers = new TreeMap<String,ArrayList<Object>>();
		
		try {
			
			Scanner fileReader = new Scanner(playerStatFile);
			
			while(fileReader.hasNextLine()) {
				String currPlayerStatsString = fileReader.nextLine();
				String[] currPlayerStatsArray = currPlayerStatsString.split(" ");
				ArrayList<Object> otherPlayStats = new ArrayList<Object>(0);
				
				for(String nextVal : Arrays.copyOfRange(currPlayerStatsArray, 2, currPlayerStatsArray.length)) {
					try {
						otherPlayStats.add(Double.parseDouble(nextVal));
					}
					catch (NumberFormatException error) {
						otherPlayStats.add("" + nextVal);
					}
				}
				
				allPlayers.put(currPlayerStatsArray[0] + " " + currPlayerStatsArray[1], otherPlayStats);
			}
			fileReader.close();
		}
		catch (FileNotFoundException error) {
			System.out.println("File Not Found - Check File Name");
		}

		catch (Exception error) {
			System.out.println("Other Error Found - See Below /n ------------------------------------------");
			error.printStackTrace();
		}
		
		System.out.println("Done\n----------Configuring Setup----------");
		Scanner readScanner = new Scanner(System.in);
		System.out.println("How many teams do you want to draft with?");
		int desiredNumTeams = Integer.parseInt(readScanner.nextLine());

		System.out.println("Enter your team name?");
		String desiredTeamName = readScanner.nextLine();

		System.out.println("Enter 'R'/'r' or number between 1-"+desiredNumTeams+ " to chose a wanted draft postion");
		String stringDesiredDraftPick = readScanner.nextLine();
		
		System.out.println("Done\n---------Creating Player Models Now----------");
		ArrayList<PlayerModel> allPlayerModels = new ArrayList<PlayerModel>();
		
		for(String nextPlayer : allPlayers.keySet()) {
			ArrayList<Object> nextPlayerStats = allPlayers.get(nextPlayer);
			String nextPlayerPos = nextPlayerStats.get(0).toString();
			
			if(nextPlayerPos.equals("RB")) {
				allPlayerModels.add(new RunningBackPlayerModel(nextPlayer.substring(0, 
				nextPlayer.indexOf(" ")),nextPlayer.substring(nextPlayer.indexOf(" ")+1) ,Double.valueOf(nextPlayerStats.get(1).toString()), 
				getADP(desiredNumTeams,desiredNumTeams+Double.valueOf(nextPlayerStats.get(2).toString()))));
			}
			else if(nextPlayerPos.equals("WR")) {
				allPlayerModels.add(new WideReceiverPlayerModel(nextPlayer.substring(0, 
				nextPlayer.indexOf(" ")),nextPlayer.substring(nextPlayer.indexOf(" ")+1), Double.valueOf(nextPlayerStats.get(1).toString()), 
				getADP(desiredNumTeams,desiredNumTeams+Double.valueOf(nextPlayerStats.get(2).toString()))));
			}
			else if(nextPlayerPos.equals("TE")) {
				allPlayerModels.add(new TightEndPlayerModel(nextPlayer.substring(0, 
				nextPlayer.indexOf(" ")),nextPlayer.substring(nextPlayer.indexOf(" ")+1), Double.valueOf(nextPlayerStats.get(1).toString()), 
				getADP(desiredNumTeams,desiredNumTeams+Double.valueOf(nextPlayerStats.get(2).toString()))));
			}
			else if(nextPlayerPos.equals("QB")) {
				allPlayerModels.add(new QuarterBackPlayerModel(nextPlayer.substring(0, 
				nextPlayer.indexOf(" ")),nextPlayer.substring(nextPlayer.indexOf(" ")+1), Double.valueOf(nextPlayerStats.get(1).toString()), 
				getADP(desiredNumTeams,desiredNumTeams+Double.valueOf(nextPlayerStats.get(2).toString()))));
			}
			else if(nextPlayerPos.equals("K")) {
				allPlayerModels.add(new KickerPlayerModel(nextPlayer.substring(0, 
				nextPlayer.indexOf(" ")),nextPlayer.substring(nextPlayer.indexOf(" ")+1), Double.valueOf(nextPlayerStats.get(1).toString()), 
				getADP(desiredNumTeams,desiredNumTeams+Double.valueOf(nextPlayerStats.get(2).toString()))));
			}
			else if (nextPlayerPos.equals("DST")) {
				allPlayerModels.add(new DefensePlayerModel(nextPlayer.substring(0, 
				nextPlayer.indexOf(" ")),nextPlayer.substring(nextPlayer.indexOf(" ")+1), Double.valueOf(nextPlayerStats.get(1).toString()), 
				getADP(desiredNumTeams,desiredNumTeams+Double.valueOf(nextPlayerStats.get(2).toString()))));
			}
		}
		allPlayerModels.sort(null);

		System.out.println("Done\n--------Creating Draft Now-------------------");
		DraftHandler draftHandler = new DraftHandler(allPlayerModels,desiredNumTeams,desiredTeamName,stringDesiredDraftPick);
		
		System.out.println("Done\n----------Staring Draft Now----------------------");
		draftHandler.startDraft();

		System.out.println("\nDraft is finsh...\n-----------Printing Final Teams----------");
		draftHandler.printTeams();
		return draftHandler.returnTeams();
	}
	
}
