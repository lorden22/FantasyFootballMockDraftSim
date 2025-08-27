package com.example.console;

import com.example.common.PlayerDataObject;
import com.example.common.Logger;

public class PlayerModel implements Comparable<PlayerModel>{
	
	private PlayerDataObject thisPlayersData;
	
	public PlayerModel(PlayerDataObject thisPlayersData) {
		this.thisPlayersData = thisPlayersData;
		Logger.logInfo("Created PlayerModel for: " + thisPlayersData.getFullName());
	}
	
	// Wrapper methods for PlayerDataObject
	public String getFirstName() { return this.thisPlayersData.getFirstName(); }
	public String getLastName() { return this.thisPlayersData.getLastName(); }
	public String getFullName() { return this.thisPlayersData.getFullName(); }
	public String getPosition() { return this.thisPlayersData.getPosition(); }
	public double getPredictedScore() { return this.thisPlayersData.getPredictedScore(); }
	public double getAvgADP() { return this.thisPlayersData.getAvgADP(); }
	public double getAllowedReach() { return this.thisPlayersData.getAllowedReach(); }
	
	@Override
	public String toString() {
		return this.thisPlayersData.toString();
	}
	
	@Override
	public int compareTo(PlayerModel otherPlayer) {
		return this.thisPlayersData.compareTo(otherPlayer.thisPlayersData);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		PlayerModel that = (PlayerModel) obj;
		return this.thisPlayersData.equals(that.thisPlayersData);
	}
	
	@Override
	public int hashCode() {
		return this.thisPlayersData.hashCode();
	}
	
	// Getter for the wrapped PlayerDataObject (needed by TeamModel)
	public PlayerDataObject getPlayerDataObject() {
		return this.thisPlayersData;
	}
} 