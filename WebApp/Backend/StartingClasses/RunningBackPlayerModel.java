

public class RunningBackPlayerModel extends PlayerModel {
	private static final double RUNNINGBACKPOSITIONREACHWEIGHT = 0.2;
	public static final String POSITIONSHORTHANDLE = "RB";
	
	public RunningBackPlayerModel(String firstName, String lastName, Double predictedScore, Double avgADP) {
		super(firstName, lastName, POSITIONSHORTHANDLE, predictedScore, avgADP, RUNNINGBACKPOSITIONREACHWEIGHT);
	}
	
}
