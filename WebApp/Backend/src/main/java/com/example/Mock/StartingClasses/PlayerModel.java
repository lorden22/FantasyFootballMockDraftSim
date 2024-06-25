package com.example.Mock.StartingClasses;

import java.util.Comparator;
import java.util.Objects;

public class PlayerModel implements Comparable<PlayerModel>{
	private String firstName;
	private String lastName;
	private String fullName;
	private String position;
	private double predictedScore;
	private double avgADP;
	private double allowedReach;
	private String spotDrafted;
	private String teamDraftedBy;
	private double rank;
	
	public PlayerModel(String firstName, String lastName, String position, double predictedScore, double avgADP, double positionAllowedReach) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.predictedScore = predictedScore;
		this.position = position;
		this.avgADP = avgADP;
		this.allowedReach = this.avgADP - positionAllowedReach;
		this.fullName = this.firstName + " " + this.lastName;
	}

	public PlayerModel (String firstName, String lastName, String position, double predictedScore, double avgADP) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.predictedScore = predictedScore;
		this.position = position;
		this.avgADP = avgADP;
	}

	public PlayerModel (String name, String position, Double playerRank, double predictedScore, double avgADP) {
		this.fullName = name;
		this.setFirstName(name.split(" ")[0]);
		this.setLastName(name.split(" ")[1]);
		this.position = position;
		this.rank = playerRank;
		this.predictedScore = predictedScore;
		this.avgADP = avgADP;
	}
	 





	public String getFirstName() {
		return this.firstName;
	}
	private void setFirstName(String firstNameChange) {
		this.firstName = firstNameChange;
	}

	private String getLastName() {
		return this.lastName;
	}
	private void setLastName(String lastNameChange) {
		this.lastName = lastNameChange;
	}

	public String getFullName() {
		return this.fullName;
	}
	private void setFullNameName(String fullNameChange) {
		this.fullName = fullNameChange;
	}
	
	public String getPosition() {
		return this.position;
	}
	
	private void setPostion(String positionChange) {
		this.position = positionChange;
	}
	
	public double getPredictedScore() {
		return this.predictedScore;
	}
	private void setPredictedScore(double predictedScoreChange) {
		 this.predictedScore = predictedScoreChange;
	}

	public double getRank() {
		return this.rank;
	}

	public void setRank(double rankChange) {
		this.rank = rankChange;
	}
	
	public double getAvgADP() {
		return this.avgADP;
	}
	private void setAvgADP(double avgADPChange) {
		this.avgADP = avgADPChange;
	}
	
	protected double getAllowedReach() {
		return this.allowedReach;
	}
	private void setAllowedReach(double allowedReachChange) {
		this.allowedReach = allowedReachChange;
	}
	
	public String toString() {
		return this.firstName + " " + this.lastName + " - Predicted 2023 score = "
		+ this.predictedScore + ", Avg ADP = " + this.avgADP;
	}
	
	public int compareTo (PlayerModel otherPlayer) {
		if(this.getAllowedReach() < otherPlayer.getAllowedReach()) {
			return -1;
		}
		else if (this.getAllowedReach() > otherPlayer.getAllowedReach()) {
			return 1;
		}
		else return 0;
	}

		@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		PlayerModel that = (PlayerModel) obj;
		return Objects.equals(rank, that.rank); // Assuming 'rank' is a unique identifier
	}

	@Override
	public int hashCode() {
		return Objects.hash(rank);
	}

	public String getSpotDrafted() {
		return this.spotDrafted;
	}
	public void setSpotDrafted(String spotDraftedChange) {
		this.spotDrafted = spotDraftedChange;
	}
	
	public String getTeamDraftedBy() {
		return this.teamDraftedBy;
	}

	public void setTeamDraftedBy(String teamDraftedByChange) {
		this.teamDraftedBy = teamDraftedByChange;
	}

	public static Comparator<PlayerModel> spotDraftedComparator = new Comparator<PlayerModel>() {
		@Override
		public int compare(PlayerModel player1, PlayerModel player2) {
			String[] player1Split = player1.getSpotDrafted().split("\\.");
			String[] player2Split = player2.getSpotDrafted().split("\\.");
			Double player1Round = Double.parseDouble(player1Split[0]);
			Double player2Round = Double.parseDouble(player2Split[0]);
			Double player1Pick = Double.parseDouble(player1Split[1]);
			Double player2Pick = Double.parseDouble(player2Split[1]);
			if (player1Round < player2Round) {
				return -1;
			}
			else if (player1Round > player2Round) {
				return 1;
			}
			else {
				if (player1Pick < player2Pick) {
					return -1;
				}
				else if (player1Pick > player2Pick) {
					return 1;
				}
				else {
					return 0;
				}
			}
		}
	};
}
