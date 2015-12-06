package TopKItemApproach;

import java.util.Comparator;

public class ProbablityComparator implements Comparator<VectorItem> {

	@Override
	public int compare(VectorItem i1, VectorItem i2) {

		double prob1 = i1.getProbility();
		double prob2 = i2.getProbility();
		if (prob1 > prob2) {
			return -1;
		} else if (prob1 < prob2) {
			return 1;
		}

		return 0;
	}
}