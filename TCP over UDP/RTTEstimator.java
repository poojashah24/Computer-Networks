public class RTTEstimator {
	private static double estimatedRTT = 100;
	private static double devRTT;
	
	private static double ALPHA = Constants.ALPHA;
	private static double BETA = Constants.BETA;
	
	/**
	 * TimeoutInterval = EstimatedRTT + 4 * DevRTT
	 * @author Pooja @param sampleRTT
	 * @author Pooja @return timeout
	 */
	public static double getTimeout(double sampleRTT)
	{
		double timeout = 0;
		calculateEstimatedRTT(sampleRTT);
		calculatedRTTDeviation(sampleRTT);
		
		timeout = estimatedRTT + (4 * devRTT);
		return timeout;
	}
	
	/**
	 * EstimatedRTT = 0.875 * EstimatedRTT + 0.125 * SampleRTT
	 * @author Pooja @param sampleRTT
	 */
	private static void calculateEstimatedRTT(double sampleRTT)
	{
		double weightedSampleRTT = ALPHA * sampleRTT;
		double weightedEstimatedRTT = (1-ALPHA) * estimatedRTT;
		estimatedRTT = weightedEstimatedRTT + weightedSampleRTT;
	}
	
	/**
	 * DevRTT = 0.75 * DevRTT + 0.25 * |SampleRTT - EstimatedRTT|
	 * @author Pooja @param sampleRTT
	 */
	private static void calculatedRTTDeviation(double sampleRTT)
	{
		double weightedRTTDifference = BETA * Math.abs(estimatedRTT - sampleRTT);
		double weightedDevRTT = (1-BETA) * devRTT;
		
		devRTT = weightedDevRTT + weightedRTTDifference;
	}
}
