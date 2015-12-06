package MovieScriptCrawler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MovieScriptCrawlerTrainData {

	public static void main(String args[]) {

		String target_dir = "/home/rkmalik/work/allworkspaces/semester3/dm/finalproject/data/scriptdatabase/scripts/"; 
		String output_dir = "/home/rkmalik/work/allworkspaces/semester3/dm/finalproject/data/scriptdatabase/output/";
		File dir = new File(target_dir);
		File[] files = dir.listFiles();

		
		for (File f : files) {
			
		
			Document htmlFile = null;
			try {
				htmlFile = Jsoup.parse(f, "ISO-8859-1");
			} catch (IOException e) { // TODO Auto-generated catch
				e.printStackTrace();
			}

			Elements elements = htmlFile.body().getElementsByClass("scrtext");
			BufferedWriter out = null;

			

			ArrayList<String> genreList = new ArrayList<String> ();
			String data = "";
			
			for (Element element : elements) {
				data += element.text();
			}
			
			
			String []  genTags = data.split("Genres :");
			
			if (genTags[1] == null) {
				System.out.println(genTags);
			}
			String trimGenTags= genTags[1].replace('\t', ' ').trim();

			trimGenTags = trimGenTags.replace((char)160, ' ');
			trimGenTags = trimGenTags.replace('.', ' ');
			
			
			
			while (trimGenTags.charAt(0)==160) {
				trimGenTags = trimGenTags.substring(1);
			}
			
			System.out.println((int)trimGenTags.charAt(0));
			
			genTags = trimGenTags.split(" ");
			for (String tag : genTags) {
				
				if (tag.isEmpty())
					continue;
				
				tag = tag.trim();
				if (tag.equals("User"))
					break;
				genreList.add(tag);
			}
			

			for (String genre : genreList) {
				
				String dirPath = output_dir + genre+"/";
				
				
				File dirFile = new File(dirPath);
				
				if (!dirFile.exists()) {
					try {
						dirFile.mkdir();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
				}
				
				
				String fileName = f.getName().split(".html")[0]+".txt";
				fileName = dirPath + fileName;
				
				
				try {
					out = new BufferedWriter(new FileWriter(new File (fileName)));
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
		}

		System.out.println("Completed");
	}

}
