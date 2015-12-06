package TopKItemApproach;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

public class CosineSimilarityTopKItemApproach {

	public static final String genre[] = { "adventure", "comedy", "crime", "drama", "family", "fantasy", "horror",
			"musical", "mystery", "romance", "thriller", "war" };
	private static final HashMap<String, PriorityQueue<VectorItem>> testDataProbablities = new HashMap<String, PriorityQueue<VectorItem>>();
	private static final HashMap<String, HashMap<Double, LinkedList<String>>> trainGenreProbablities = new HashMap<String, HashMap<Double, LinkedList<String>>>();
	private static StringBuilder output = null;

	static String testdata = null;
	static String traindata = null;
	static String applicationdata = null;
	static String cosinesummary = null;
	static int topK = 0;

	public static double cosineSimilarityScore(double prob, LinkedList<String> trainDocument,
			HashMap<String, Double> testDocument) {

		double sim_score = 0.0000000;
		// 1. Identify distinct words from both documents
		HashMap<String, VectorProbablities> wordProbablityVector = new HashMap<String, VectorProbablities>();
		LinkedList<String> distincwordslist = new LinkedList<String>();

		for (String word : trainDocument) {

			String curWord = word;
			Double curProb = prob;

			if (wordProbablityVector.containsKey(curWord)) {
				VectorProbablities vals1 = wordProbablityVector.get(curWord);

				if (curProb > vals1.getTrainVectorProbablity()) {
					vals1.setTrainVectorProbablity(curProb);
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

				// if (curProb > vals1.testVectorProbablity) {
				if (curProb > vals1.getTestVectoProbability()) {
					vals1.setTestVectorProbablity(curProb);
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

			double freq1 = (double) vals12.getTrainVectorProbablity();
			double freq2 = (double) vals12.getTestVectoProbability();

			VectAB = VectAB + (freq1 * freq2);

			VectA_Sq = VectA_Sq + freq1 * freq1;
			VectB_Sq = VectB_Sq + freq2 * freq2;
		}

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

	private static void initializeDataPaths() {
		// Parse the Test document and find the Top K Vectors out of that.
		Path path = null;
		String parentPath = null;

		try {
			path = Paths.get(CosineSimilarityTopKItemApproach.class.getResource(".").toURI());
			parentPath = path.getParent().getParent().toString();
		} catch (URISyntaxException e) {
			System.out.println("Syntex not correct.");
		}

		applicationdata = parentPath.toString() + "/applicationdata/";
		testdata = applicationdata + "testdata/";
		traindata = applicationdata + "traindata/";

		cosinesummary = parentPath.toString() + "/cosinesimilaritysummary/";
	}

	private static void processTrainData() {

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
	}

	private static void processTestData() {

		File dir = new File(testdata);
		File[] fileList = dir.listFiles();

		if (fileList != null) {

			for (File file : fileList) {
				// HashMap<String, Double> curTestDataProbablities = new
				// HashMap<String, Double>();
				PriorityQueue<VectorItem> topKItems = new PriorityQueue<VectorItem>(topK, new ProbablityComparator());

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

	}

	public static void main(String args[]) {

		Scanner in = new Scanner(System.in);
		System.out.print("Enter value of K: ");
		topK = in.nextInt();

		/******** Process Train Data **************/

		processTrainData();

		/******** Train Data Processed ***********/

		/******* Process Test Data ***************/

		processTestData();

		/******* Process Test Data **************/

		try {

			String summaryFileName = cosinesummary + "summary.txt";
			FileWriter filewriter = new FileWriter(summaryFileName);
			BufferedWriter writer = new BufferedWriter(filewriter);

			writer.write(String.format("%-20s%-30s%-20s%-20s", "Approach Type", "Movie", "Genre", "Cosine Score"));

			for (Map.Entry<String, PriorityQueue<VectorItem>> testDataEntry : testDataProbablities.entrySet()) {
				output = new StringBuilder("");

				String name = testDataEntry.getKey();

				// Transform Priority Queue into HashMap
				PriorityQueue<VectorItem> vector = testDataEntry.getValue();
				HashMap<String, Double> testDataFromPriorityQueue = new HashMap<String, Double>();

				int i = 0;
				for (VectorItem item : vector) {
					testDataFromPriorityQueue.put(item.getWord(), item.getProbility());
					if (++i == topK)
						break;
				}

				for (Map.Entry<String, HashMap<Double, LinkedList<String>>> trainDataEntry : trainGenreProbablities
						.entrySet()) {

					String genre = trainDataEntry.getKey();
					output.append(String.format("%-20s", "\nTeam"));
					output.append(String.format(" %-30s", name));
					output.append(String.format("%-20s", genre));

					double max_sim_score = 0.0;
					double probablity_key = 0.0;
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
					output.append(String.format("%-20f", max_sim_score));
				}
				writer.write(output.toString());
			}
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
