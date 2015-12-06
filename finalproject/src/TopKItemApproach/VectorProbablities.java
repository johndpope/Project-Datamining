package TopKItemApproach;

public class VectorProbablities {
	private double trainVectorProbablity;
	private double testVectorProbablity;

	public VectorProbablities(double trainVectorProb, double testVectorProb) {
		this.trainVectorProbablity = trainVectorProb;
		this.testVectorProbablity = testVectorProb;
	}

	public void updateValues(double trainVectorProb, double testVectorProb) {
		this.trainVectorProbablity = trainVectorProb;
		this.testVectorProbablity = testVectorProb;
	}

	public double getTrainVectorProbablity() {
		return this.trainVectorProbablity;
	}

	public void setTrainVectorProbablity (double probability) {
		this.trainVectorProbablity = probability;
	}
	
	public double getTestVectoProbability () {
		return this.testVectorProbablity;
	}
	
	public void setTestVectorProbablity (double probablity) {
		this.testVectorProbablity = probablity;
	}
}