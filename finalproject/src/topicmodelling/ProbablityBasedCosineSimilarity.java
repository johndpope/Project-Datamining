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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

//calculates the cosine similarity between two texts / documents
public class ProbablityBasedCosineSimilarity {

	public static final String genre[] = { "adventure", "comedy", "crime", "drama", "family", "fantasy", "horror",
			"musical", "mystery", "romance", "thriller", "war" };
	private static final HashMap<String, HashMap<String, Double>> trainGenreProbablities = new HashMap<String, HashMap<String, Double>>();
	private static final HashMap<String, HashMap<String, Double>> testDataProbablities = new HashMap<String, HashMap<String, Double>>();
	private static StringBuilder output = null;
	
	public class VectorProbablities {
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

	public double cosineSimilarityScore(HashMap<String, Double> trainDocument, HashMap<String, Double> testDocument) {
		double sim_score = 0.0000000;
		// 1. Identify distinct words from both documents
		Hashtable<String, VectorProbablities> wordProbablityVector = new Hashtable<String, ProbablityBasedCosineSimilarity.VectorProbablities>();
		LinkedList<String> distincwordslist = new LinkedList<String>();

		// Prepare the word Probability vector for train data
		for (Map.Entry<String, Double> trainDocEntry : trainDocument.entrySet()) {
			
			String curWord = trainDocEntry.getKey();
			Double curProb = trainDocEntry.getValue();
			
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

	private static void parseTrainDataLine(String line, HashMap<String, Double> probablityList) {
		
		if (line.trim().isEmpty())
			return;
		
		String[] tabbasedtokens = line.split("\t");
		String token = null;

		if (tabbasedtokens.length == 2) {

			token = tabbasedtokens[0].trim();
			// Check if token is already present in the map then get the
			// Probability.

			Double probablityVal = probablityList.get(token);

			// If token is not already present then put this token in the hashmap
			if (probablityVal == null) {
				probablityVal = Double.parseDouble(tabbasedtokens[1]);
				probablityList.put(token, probablityVal);

				// Else find the one with maximum probability
			} else {
				Double curProbVal = Double.parseDouble(tabbasedtokens[1]);

				if (curProbVal > probablityVal) {
					probablityList.put(token, curProbVal);
				}
			}
		} else {
			throw new IllegalArgumentException("Illegal Format");
		}
	}
	
	private static void parseTestDataLine(String line, HashMap<String, Double> curTestDataProbablities) {
		if (line.trim().isEmpty())
			return;
		
		String[] tabbasedtokens = line.split("\t");
		String[] spacebasedtokens = null;

		if (tabbasedtokens.length == 3) {

			spacebasedtokens = tabbasedtokens[2].trim().split(" ");
			// Check if token is already present in the map then get the
			// Probability.
			
			if (spacebasedtokens.length>=1) {
				
				// First find the the probability token  
				Double curProbablity = Double.parseDouble(tabbasedtokens[1].trim());
				
				for (int i = 0; i < spacebasedtokens.length; i++) {
					String token = spacebasedtokens[i].trim();
					Double probablityVal = curTestDataProbablities.get(token);
					
					// If token is not already present then put this token in the hashmap
					if (probablityVal == null) {
						curTestDataProbablities.put(token, curProbablity);
						// Else find the one with maximum probability
					} else {
						if (curProbablity > probablityVal) {
							curTestDataProbablities.put(token, curProbablity);
						}
					}
				}
			} else {
				throw new IllegalArgumentException("Invalid test data format.");
			}
		} else {
			throw new IllegalArgumentException("Invalid test data format.");
		}
	}
	
	public static void main(String[] args) {
		ProbablityBasedCosineSimilarity cs1 = new ProbablityBasedCosineSimilarity();

		// Find the path of the parent Directory so that we can point to the
		// testdata and traindata
		Path path = null;
		String parentPath = null;

		try {
			path = Paths.get(ProbablityBasedCosineSimilarity.class.getResource(".").toURI());
			parentPath = path.getParent().getParent().toString();

		} catch (URISyntaxException e) {
			System.out.println("Syntex not correct.");
		}

		String testdata = parentPath.toString() + "/testdata_oldformat/";
		String traindata = parentPath.toString() + "/traindata_oldformat/";
		String cosinesummary = parentPath.toString() + "/cosinesimilaritysummary_oldformat/";

		/*********************Process Train Data*******************/
		
		// This will reference one line at a time

		for (int i = 0; i < genre.length; i++) {

			String fileName = traindata + genre[i];

			try {
				FileReader fileReader = null;
				// FileReader reads text files in the default encoding.
				fileReader = new FileReader(fileName);
				// Always wrap FileReader in BufferedReader.
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String buf = "";

				HashMap<String, Double> genreProbablity = new HashMap<String, Double>();

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
		
		/*********************Train Data Processed*******************/
		
		/*********************Process Test Data**********************/
		
		
		
		File dir = new File (testdata);
		File [] fileList = dir.listFiles();
		
		if (fileList!=null) {

			for (File file : fileList) {
				HashMap<String, Double> curTestDataProbablities = new HashMap<String, Double>(); 
				try {
					
					FileReader fileReader = new FileReader(file);
					// Always wrap FileReader in BufferedReader.
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					String buf = "";

					while ((buf = bufferedReader.readLine()) != null) {
						parseTestDataLine(buf, curTestDataProbablities);
					}
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				testDataProbablities.put(file.getName(), curTestDataProbablities);
			}
			
		} else {
			System.out.println("No test data files available");
			return;
		}
		

		/*********************Test Data Processed*******************/
		
		System.out.println("\n\n");
		
		/**
		 * Algorithm Steps:
		 * 	1. Read each processed data in the TestBuffer
		 *  2. Find the cosine similarity against each of the Genre train data
		 *  3. Prepare a summary file for each of the Test data.
		 * */
		
		try {
			
			String summaryFileName = cosinesummary + "summary.txt";
			File file = new File (summaryFileName);
			FileWriter filewriter = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(filewriter);
			writer.write(String.format("%-20s%-30s%-20s%-20s", "Approach Type", "Movie", "Genre", "Cosine Score"));
			
			for (Map.Entry<String, HashMap<String, Double>> testDataEntry : testDataProbablities.entrySet()) {
				output = new StringBuilder ("\n");
				
				
				for (Map.Entry<String, HashMap<String, Double>> trainDataEntry : trainGenreProbablities.entrySet()) {
					
					String genre = trainDataEntry.getKey();
					
					//output = new StringBuilder("");
					//output.append(String.format("%30s\n", "Processing " + genre));
					//System.out.printf("%30s\n", "Processing " + genre);
					genre = trainDataEntry.getKey();
					output.append(String.format("%-20s","Dr. Ranka"));
					output.append(String.format("%-30s",testDataEntry.getKey()));
					output.append(String.format("%-20s",genre));
					
					double sim_score = cs1.cosineSimilarityScore(trainDataEntry.getValue(), testDataEntry.getValue());
					/*System.out.println("Cosine similarity score for " + genre + "= " + sim_score);
					System.out.println("\n\n");

					output.append("\n\nCosine similarity score for " + genre + "= " + sim_score + "\n\n");*/
					output.append(String.format("%-20f\n",sim_score));
					//output.append(String.format("%-15f\n",probablity_key));
					
				}
				writer.write(output.toString());
				
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}