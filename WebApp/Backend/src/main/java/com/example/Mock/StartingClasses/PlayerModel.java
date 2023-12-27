package com.example.Mock.StartingClasses;
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
	
	public PlayerModel(String firstName, String lastName, String position, double predictedScore, double avgADP, double positionAllowedReach) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.predictedScore = predictedScore;
		this.position = position;
		this.avgADP = avgADP;
		this.allowedReach = this.avgADP - positionAllowedReach;
		this.fullName = this.firstName + " " + this.lastName;
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
}
