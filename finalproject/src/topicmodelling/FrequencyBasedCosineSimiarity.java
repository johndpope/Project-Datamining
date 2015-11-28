package topicmodelling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.plaf.synth.SynthSeparatorUI;

//calculates the cosine similarity between two texts / documents etc., (having each word separated by space)
public class FrequencyBasedCosineSimiarity {

	public static final String genre[] = { "adventure", "comedy", "crime", "drama", "family", "fantasy", "horror",
			"musical", "mystery", "romance", "thriller", "war"};

	public class VectorFrequencies {
		int freqVector1;
		int freqVector2;

		VectorFrequencies(int v1, int v2) {
			this.freqVector1 = v1;
			this.freqVector2 = v2;
		}

		public void updateValues(int v1, int v2) {
			this.freqVector1 = v1;
			this.freqVector2 = v2;
		}
	}// end of class values

	public double cosineSimilarityScore(String Text1, String Text2, StringBuilder output) {
		double sim_score = 0.0000000;
		// 1. Identify distinct words from both documents
		String[] wordSeqText1 = Text1.split(" ");
		String[] wordSeqText2 = Text2.split(",");
		Hashtable<String, VectorFrequencies> wordFrequenciesVector = new Hashtable<String, FrequencyBasedCosineSimiarity.VectorFrequencies>();
		LinkedList<String> distincwordslist = new LinkedList<String>();

		// prepare word frequency vector by using Text1
		for (int i = 0; i < wordSeqText1.length; i++) {
			String curWord = wordSeqText1[i].trim();
			if (curWord.length() > 0) {
				if (wordFrequenciesVector.containsKey(curWord)) {
					VectorFrequencies vals1 = wordFrequenciesVector.get(curWord);
					int freq1 = vals1.freqVector1 + 1;
					int freq2 = vals1.freqVector2;
					vals1.updateValues(freq1, freq2);
					wordFrequenciesVector.put(curWord, vals1);
				} else {
					VectorFrequencies vals1 = new VectorFrequencies(1, 0);
					wordFrequenciesVector.put(curWord, vals1);
					distincwordslist.add(curWord);
				}
			}
		}

		// prepare word frequency vector by using Text2
		for (int i = 0; i < wordSeqText2.length; i++) {
			String curWord = wordSeqText2[i].trim();
			if (curWord.length() > 0) {
				if (wordFrequenciesVector.containsKey(curWord)) {
					VectorFrequencies vals1 = wordFrequenciesVector.get(curWord);
					int freq1 = vals1.freqVector1;
					int freq2 = vals1.freqVector2 + 1;
					vals1.updateValues(freq1, freq2);
					wordFrequenciesVector.put(curWord, vals1);
				} else {
					VectorFrequencies vals1 = new VectorFrequencies(0, 1);
					wordFrequenciesVector.put(curWord, vals1);
					distincwordslist.add(curWord);
				}
			}
		}

		// calculate the cosine similarity score.
		double VectAB = 0.0000000;
		double VectA_Sq = 0.0000000;
		double VectB_Sq = 0.0000000;
		
		/*StringBuilder output = new StringBuilder ("");
		try {
			
			
			FileWriter filewriter = new FileWriter(new File("/home/rkmalik/Downloads/vectorOutput/finaloutput/finaloutput.txt"));
			BufferedWriter writer = new BufferedWriter(filewriter);*/
			
			for (int i = 0; i < distincwordslist.size(); i++) {
				VectorFrequencies vals12 = wordFrequenciesVector.get(distincwordslist.get(i));

				double freq1 = (double) vals12.freqVector1;
				double freq2 = (double) vals12.freqVector2;
				
				//output.append(String.format("%-20s  #%-10.1f   #%-10.1f\n", distincwordslist.get(i), freq1, freq2));
				
				//System.out.printf("%-20s  #%-10.1f   #%-10.1f\n", distincwordslist.get(i), freq1, freq2);
				// System.out.println(Distinct_words_text_1_2.get(i) + " #" + freq1
				// + " #" + freq2);

				VectAB = VectAB + (freq1 * freq2);

				VectA_Sq = VectA_Sq + freq1 * freq1;
				VectB_Sq = VectB_Sq + freq2 * freq2;
			}
			
			output.append(String.format("VectAB " + VectAB + " VectA_Sq " + VectA_Sq + " VectB_Sq " + VectB_Sq));
			
			
			//System.out.println("VectAB " + VectAB + " VectA_Sq " + VectA_Sq + " VectB_Sq " + VectB_Sq);
			sim_score = ((VectAB) / (Math.sqrt(VectA_Sq) * Math.sqrt(VectB_Sq)));
			/*writer.write(output.toString());
			filewriter.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}*/
		
		return (sim_score);
	}

	public static void main(String[] args) {
		FrequencyBasedCosineSimiarity cs1 = new FrequencyBasedCosineSimiarity();

		String path = "/home/rkmalik/Downloads/vectorOutput/finaldata/";
		HashMap<String, String> allgenre = new HashMap<String, String> ();

		// This will reference one line at a time
		String line = null;
		
		

		for (int i = 0; i < genre.length; i++) {
			
			String fileName = path + "/" + genre[i];
			
			try {
				FileReader fileReader = null;
				// FileReader reads text files in the default encoding.
				fileReader = new FileReader(fileName);
				// Always wrap FileReader in BufferedReader.
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String buffer = "";
				String buf = "";

				while ((buf = bufferedReader.readLine()) != null) {
					buffer += buf;
				}
				
				allgenre.put(genre[i], buffer);
				System.out.println("-----");
				System.out.println(buffer);
				fileReader.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//fileName +=
		
		StringBuilder testdata = new StringBuilder ("");
		
		try {
			FileReader fileReader = null;
			// FileReader reads text files in the default encoding.
			//fileReader = new FileReader("/home/rkmalik/Downloads/vectorOutput/test/500DaysofSummer.txt");
			//fileReader = new FileReader("/home/rkmalik/Downloads/vectorOutput/test/dracula.txt");
			fileReader = new FileReader("/home/rkmalik/Downloads/vectorOutput/test/thePianist.txt");
			
			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			//String buffer = "";
			String buf = "";

			while ((buf = bufferedReader.readLine()) != null) {
				testdata.append(buf);
				//testdata += buf;
			}
			
			//allgenre.put(genre[i], buffer);
			//System.out.println("-----");
			//System.out.println(buffer);
			fileReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		
		/*System.out.println("[Word # VectorA # VectorB]");
		String bow_romance = new String(
				"baby bedroom breath card chest child coffee clothes kiss expression red pretty love wife tight tonight");
		String bow_scifi = new String(
				"alien apes lightsaber spider anakin signal predators nasa laser launch obi spacecraft information pod jedi stars robot raptor solar cables human");
		String bow_war = new String(
				"armed alive attack bows broken castle burning debris explodes grenade guns horses sword mission refugees samurai soldiers");
		String bow_horror = new String(
				"dripping buried tearing corpse killers knives murdered vine scissors zombies burned needle tearing survivors limbs werewolf");
		String bow_comedy = new String(
				"laughter enjoy applause aladdin funny rolling embarrassed glad joke stupid chicken weird yells");
		String bow_crime = new String(
				"agent aims badge jacket footsteps steel involved victims suspect witness security fbi costume cell burn");

		LinkedList<String> bagofwords = new LinkedList<String>();
		bagofwords.add(bow_romance);
		bagofwords.add(bow_scifi);
		bagofwords.add(bow_war);
		bagofwords.add(bow_horror);
		bagofwords.add(bow_comedy);
		bagofwords.add(bow_crime);

		LinkedList<String> genre = new LinkedList<String>();
		genre.add("romance");
		genre.add("scifi");
		genre.add("war");
		genre.add("horror");
		genre.add("comedy");
		genre.add("crime");*/


		
		System.out.println("\n\n");
		
		
		
		
		try {
			
			
			FileWriter filewriter = new FileWriter(new File("/home/rkmalik/Downloads/vectorOutput/finaloutput/finaloutput.txt"));
			BufferedWriter writer = new BufferedWriter(filewriter);
			
			
			for (int i = 0; i < allgenre.size(); i++) {
				
				StringBuilder output = new StringBuilder ("");
				
				output.append(String.format("%30s\n", "Processing " + genre[i]));

				System.out.printf("%30s\n", "Processing " + genre[i]);
				
				//System.out.println(allgenre.get(genre[i]));
				/*double sim_score = cs1.cosineSimilarityScore(
						"time line alright that's sitting stick call governor minister alex treatment v.o long alex's fumbling lips ext draw family reply what's regard play imaginable boy victims responsibility crast eggs deeply heavy miserable ghastly half shut drops tradition exit chateau started tea magic horrible rest spaghetti straightens lucky nurse rubinstein slooshy speaking interest fortnight's couldn't salary laughing concentration poisoned harm familiar takes peacock meet tracks lovely waiting remembered today bible cartoon badly snuffed move tolchock slides prisoner nest mum inadvertently arch munching officer mad secret we'll legs i'd recorder parents offence jolly medicine beethoven woke belongs police complaints year's welly troubling beak aides owww questions realising practised rare years sick opposed encounter wrapped full cages hearts voice find here's possibly drinkers vitamins holding notebook accept side obsequious shoe sense wearing suppose chosen dimmed youth socially pulls block involved huh feet trolley putting facing beating meal life you'd carrying afternoon lets providence watch pops white phone trailed reaction parcel twitches fed alexander's girl begins understanding fell escort inserts ready noticed didn't christ bursts headlines fine pushed corridor light grave world spare battle ludwig film eating breaking visual rubbed reporters cries leans carving moans days you'll audible vino bandages buttoning snuff slowly concerned problem show cup verdict wore party punished book safe lies damned mend horrorshow lesson device called beg too.very surprised chest popularity",
						allgenre.get(genre[i]));*/
				double sim_score = cs1.cosineSimilarityScore(testdata.toString(), allgenre.get(genre[i]), output);
				System.out.println("Cosine similarity score for " + genre[i] + "= " + sim_score);
				System.out.println("\n\n");
				
				output.append("\n\nCosine similarity score for " + genre[i] + "= " + sim_score + "\n\n");
				
				 writer.write(output.toString());
				
			}
			
			
			
			
			
			
				filewriter.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		 

	}
}