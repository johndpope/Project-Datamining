package topicmodelling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

import topicmodelling.ProbablityBasedCosineSimilarity.VectorProbablities;

public class TopKItemVectors {

	public static final String genre[] = { "adventure", "comedy", "crime", "drama", "family", "fantasy", "horror",
			"musical", "mystery", "romance", "thriller", "war" };
	private static final HashMap<String, PriorityQueue<VectorItem>> testDataProbablities = new HashMap<String, PriorityQueue<VectorItem>>();
	private static final HashMap<String, HashMap<Double, LinkedList<String>>> trainGenreProbablities = new HashMap<String, HashMap<Double, LinkedList<String>>>();
	private static StringBuilder output = null;

	public static class ProbablityComparator implements Comparator<VectorItem> {

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

	public static class VectorItem {

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

	public static class VectorProbablities {
		double trainVectorProbablity;
		double testVectorProbablity;

		VectorProbablities(double trainVectorProb, double testVectorProb) {
			this.trainVectorProbablity = trainVectorProb;
			this.testVectorProbablity = testVectorProb;
		}

		public void updateValues(double trainVectorProb, double testVectorProb) {
			this.trainVectorProbablity = trainVectorProb;
			this.testVectorProbablity = testVectorProb;
		}
	}

	public static double cosineSimilarityScore(double prob, LinkedList<String> trainDocument,
			HashMap<String, Double> testDocument) {
		double sim_score = 0.0000000;
		// 1. Identify distinct words from both documents
		HashMap<String, VectorProbablities> wordProbablityVector = new HashMap<String, VectorProbablities>();
		LinkedList<String> distincwordslist = new LinkedList<String>();

		// Prepare the word Probability vector for train data
		// for (Map.Entry<String, Double> trainDocEntry :
		// trainDocument.entrySet()) {

		for (String word : trainDocument) {

			String curWord = word;
			Double curProb = prob;

			if (wordProbablityVector.containsKey(curWord)) {
				VectorProbablities vals1 = wordProbablityVector.get(curWord);
				if (curProb > vals1.trainVectorProbablity) {
					vals1.trainVectorProbablity = curProb;
					wordProbablityVector.put(curWord, vals1);
				}
			} else {

				VectorProbablities vals1 = new VectorProbablities(curProb, 0.0);
				wordProbablityVector.put(curWord, vals1);
				distincwordslist.add(curWord);
			}

		}

		// Prepare the word Probability vector for test data
		for (Map.Entry<String, Double> testDocEntry : testDocument.entrySet()) {

			String curWord = testDocEntry.getKey();
			Double curProb = testDocEntry.getValue();

			if (wordProbablityVector.containsKey(curWord)) {
				VectorProbablities vals1 = wordProbablityVector.get(curWord);
				if (curProb > vals1.testVectorProbablity) {
					vals1.testVectorProbablity = curProb;
					wordProbablityVector.put(curWord, vals1);
				}
			} else {

				VectorProbablities vals1 = new VectorProbablities(0.0, curProb);
				wordProbablityVector.put(curWord, vals1);
				distincwordslist.add(curWord);
			}

		}

		// calculate the cosine similarity score.
		double VectAB = 0.0000000;
		double VectA_Sq = 0.0000000;
		double VectB_Sq = 0.0000000;

		for (int i = 0; i < distincwordslist.size(); i++) {
			VectorProbablities vals12 = wordProbablityVector.get(distincwordslist.get(i));

			double freq1 = (double) vals12.trainVectorProbablity;
			double freq2 = (double) vals12.testVectorProbablity;

			VectAB = VectAB + (freq1 * freq2);

			VectA_Sq = VectA_Sq + freq1 * freq1;
			VectB_Sq = VectB_Sq + freq2 * freq2;
		}

		//System.out.println(distincwordslist);
		//output.append(String.format("VectAB " + VectAB + " VectA_Sq " + VectA_Sq + " VectB_Sq " + VectB_Sq));
		sim_score = ((VectAB) / (Math.sqrt(VectA_Sq) * Math.sqrt(VectB_Sq)));
		return sim_score;
	}

	private static void parseTrainDataLine(String line, HashMap<Double, LinkedList<String>> probablityList) {

		if (line.trim().isEmpty())
			return;

		String[] tabbasedtokens = line.split("\t");
		String[] spacebasedtokens = null;

		if (tabbasedtokens.length == 3) {

			spacebasedtokens = tabbasedtokens[2].trim().split(" ");
			// Check if token is already present in the map then get the
			// Probability.

			if (spacebasedtokens.length >= 1) {

				// First find the the probability token
				Double curProbablity = Double.parseDouble(tabbasedtokens[1].trim());

				// Check if the probablity is already present in the hashmp
				LinkedList<String> words = probablityList.get(curProbablity);
				if (words == null) {
					words = new LinkedList<String>();
				}

				for (int i = 0; i < spacebasedtokens.length; i++) {
					String token = spacebasedtokens[i].trim();
					words.add(token);
				}
				System.out.println(words);
				probablityList.put(curProbablity, words);
			} else {
				throw new IllegalArgumentException("Invalid test data format.");
			}
		} else {
			throw new IllegalArgumentException("Invalid test data format.");
		}
	}

	private static void parseTestDataLine(String line, PriorityQueue<VectorItem> curTestDataProbablities) {
		if (line.trim().isEmpty())
			return;

		String[] tabbasedtokens = line.split("\t");
		String[] spacebasedtokens = null;

		if (tabbasedtokens.length == 3) {

			spacebasedtokens = tabbasedtokens[2].trim().split(" ");
			// Check if token is already present in the map then get the
			// Probability.

			if (spacebasedtokens.length >= 2) {

				// First find the the probability token
				Double curProbablity = Double.parseDouble(tabbasedtokens[1].trim());

				for (int i = 0; i < spacebasedtokens.length; i++) {
					String token = spacebasedtokens[i].trim();
					curTestDataProbablities.add(new VectorItem(curProbablity, token));
				}
			} else {
				throw new IllegalArgumentException("Invalid test data format.");
			}
		} else {
			throw new IllegalArgumentException("Invalid test data format.");
		}
	}

	public static void main(String args[]) {

		Scanner in = new Scanner(System.in);
		System.out.print("Enter value of K: ");
		int K = in.nextInt();

		// Parse the Test document and find the Top K Vectors out of that.
		Path path = null;
		String parentPath = null;

		try {
			path = Paths.get(ProbablityBasedCosineSimilarity.class.getResource(".").toURI());
			parentPath = path.getParent().getParent().toString();

		} catch (URISyntaxException e) {
			System.out.println("Syntex not correct.");
		}

		String testdata = parentPath.toString() + "/testdata/";
		String traindata = parentPath.toString() + "/traindata/";
		String cosinesummary = parentPath.toString() + "/cosinesimilaritysummary/";

		/********************* Process Train Data *******************/

		// This will reference one line at a time

		for (int i = 0; i < genre.length; i++) {

			String fileName = traindata + genre[i] + ".txt";

			try {
				FileReader fileReader = null;
				// FileReader reads text files in the default encoding.
				fileReader = new FileReader(fileName);
				// Always wrap FileReader in BufferedReader.
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String buf = "";

				HashMap<Double, LinkedList<String>> genreProbablity = new HashMap<Double, LinkedList<String>>();

				while ((buf = bufferedReader.readLine()) != null) {

					try {
						parseTrainDataLine(buf, genreProbablity);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}

				trainGenreProbablities.put(genre[i], genreProbablity);
				fileReader.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/********************* Train Data Processed *******************/

		/******* Process Test Data ****************/

		File dir = new File(testdata);
		File[] fileList = dir.listFiles();

		if (fileList != null) {

			for (File file : fileList) {
				HashMap<String, Double> curTestDataProbablities = new HashMap<String, Double>();
				PriorityQueue<VectorItem> topKItems = new PriorityQueue<VectorItem>(K, new ProbablityComparator());

				try {

					FileReader fileReader = new FileReader(file);
					// Always wrap FileReader in BufferedReader.
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					String buf = "";

					while ((buf = bufferedReader.readLine()) != null) {
						parseTestDataLine(buf, topKItems);
					}
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				testDataProbablities.put(file.getName(), topKItems);
			}

		} else {
			System.out.println("No test data files available");
			return;
		}

		try {
			
			String summaryFileName = cosinesummary + "summary.txt";
			FileWriter filewriter = new FileWriter(summaryFileName);
			BufferedWriter writer = new BufferedWriter(filewriter);
			
			writer.write(String.format("%-20s%-30s%-20s%-20s", "Approach Type", "Movie", "Genre", "Cosine Score"));
			
			for (Map.Entry<String, PriorityQueue<VectorItem>> testDataEntry : testDataProbablities.entrySet()) {
				output = new StringBuilder ("");
				
				String name = testDataEntry.getKey();
				

				// Transform Priority Queue into HashMap
				PriorityQueue<VectorItem> vector = testDataEntry.getValue();
				HashMap<String, Double> testDataFromPriorityQueue = new HashMap<String, Double>();

				int i = 0;
				for (VectorItem item : vector) {
					testDataFromPriorityQueue.put(item.getWord(), item.getProbility());
					if (++i == K)
						break;
				}

				for (Map.Entry<String, HashMap<Double, LinkedList<String>>> trainDataEntry : trainGenreProbablities
						.entrySet()) {
					
					String genre = trainDataEntry.getKey();
					output.append(String.format("%-20s","\nTeam"));
					output.append(String.format(" %-30s",name));
					output.append(String.format("%-20s",genre));

					double max_sim_score = 0.0;
					double probablity_key= 0.0;
					LinkedList<String> list = null;
					
					for (Map.Entry<Double, LinkedList<String>> trainLineEntry : trainDataEntry.getValue().entrySet()) {

						double sim_score = cosineSimilarityScore(trainLineEntry.getKey(), trainLineEntry.getValue(),
								testDataFromPriorityQueue);
						
						if (sim_score > max_sim_score) {
							max_sim_score = sim_score;
							probablity_key = trainLineEntry.getKey();
							list = trainLineEntry.getValue();
						}
						
						
					}
					output.append(String.format("%-20f",max_sim_score));
					//output.append(String.format("Max Cosine Score : %-30f Probablity : %-30f \nList of Words : %s\n\n", max_sim_score, probablity_key, list.toString()));
				}
				writer.write(output.toString());
			}
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
