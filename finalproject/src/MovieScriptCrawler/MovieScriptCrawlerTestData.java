package MovieScriptCrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MovieScriptCrawlerTestData {

	public static void main(String args[]) {

		
		String src_dir = null;
		String dest_dir = null;
		
		
		if (args.length < 2){
			System.out.println(
					"This application takes two parameters.\n\t1.Source Directory path containing  raw HTML filess.\n\t2.Destination Directory Path, where We need to put all the script Files.\n\n\t Please Try again.");
			return;
		}
		
		src_dir = args[0]+"/";
		dest_dir = args[1]+"/";

		if (src_dir == null || dest_dir == null) {
			System.out.println(
					"This application takes two parameters.\n\t1.Source Directory path containing  raw HTML filess.\n\t2.Destination Directory Path, where We need to put all the script Files.\n\n\t Please Try again.");
			return;
		}
		
		
		
		File dir = new File(src_dir);
		File[] files = dir.listFiles();
		
		BufferedWriter csvFileWriter = null;
		
		try { 
			csvFileWriter  = new BufferedWriter(new FileWriter(new File(dest_dir+"genreStatistics.csv")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		for (File f : files) {

			Document htmlFile = null;
			try {
				htmlFile = Jsoup.parse(f, "ISO-8859-1");
			} catch (IOException e) { // TODO Auto-generated catch
				e.printStackTrace();
			}

			Elements elements = htmlFile.body().getElementsByClass("scrtext");
			BufferedWriter out = null;

			ArrayList<String> genreList = new ArrayList<String>();
			String data = "";

			for (Element element : elements) {
				data += element.text();
			}

			String[] genTags = data.split("Genres :");

			if (genTags[1] == null) {
				System.out.println(genTags);
			}
			String trimGenTags = genTags[1].replace('\t', ' ').trim();

			trimGenTags = trimGenTags.replace((char) 160, ' ');
			trimGenTags = trimGenTags.replace('.', ' ');

			while (trimGenTags.charAt(0) == 160) {
				trimGenTags = trimGenTags.substring(1);
			}

			genTags = trimGenTags.split(" ");
			for (String tag : genTags) {

				if (tag.isEmpty())
					continue;

				tag = tag.trim();
				if (tag.equals("User"))
					break;
				genreList.add(tag);
			}

			String csvoutput =  f.getName().split(".html")[0];

			
			if (csvoutput.equals("Family-Man,-The")) {
				
				System.out.println(csvoutput);
			}
			
			csvoutput = csvoutput.replaceAll("[,'()/?/.-]+", "");
			
			String cleanFileName = csvoutput;
			
			System.out.println(csvoutput);
			
			for (String genre : genreList) {

				String dirPath = dest_dir;

				File dirFile = new File(dirPath);

				if (!dirFile.exists()) {
					try {
						dirFile.mkdir();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
				}

				String fileName = cleanFileName + ".txt";
				fileName = dirPath + fileName;
				csvoutput += ","+genre;

				
				try {
					out = new BufferedWriter(new FileWriter(new File(fileName)));
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					out.write(data);
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				csvoutput+="\n";
				csvFileWriter.write(csvoutput);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		try {
			csvFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Completed");
	}

}
