package com.example.Mock.StartingClasses;

public class VaribleOddsPicker {
	private int randomGenValue;
	private int maxPickPossible;
	
	public VaribleOddsPicker() {

	}
	
	public int newOdds(int sizeOfListOfPontenialPicks) {
		this.maxPickPossible = sizeOfListOfPontenialPicks;
		double random = Math.random();
		if(random <= 0.75) {
			this.randomGenValue = 1;
		}
		else if (random <= 0.85) {
			this.randomGenValue = 2;
		}
		else if (random <= 0.90) {
			this.randomGenValue = 3;
		}
		else if (random <= 0.945) {
			this.randomGenValue = 4;
		}
		else if(random <= 0.975) {
			this.randomGenValue = 5;
		}
		else this.randomGenValue = 6;
		return this.returnInt();

	}
	
	private int returnInt() {
		if(this.randomGenValue > this.maxPickPossible) {
			return this.maxPickPossible;
		}
		else return this.randomGenValue;
	}
}
