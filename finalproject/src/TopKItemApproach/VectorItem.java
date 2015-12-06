package TopKItemApproach;

public class VectorItem {

	private double probability;
	private String word;

	public VectorItem(double probability, String word) {
		this.probability = probability;
		this.word = word;
	}

	public double getProbility() {
		return probability;
	}

	public String getWord() {
		return word;
	}
}