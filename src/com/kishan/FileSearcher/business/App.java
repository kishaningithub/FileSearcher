package com.kishan.FileSearcher.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class FileSearch.
 */
public class App 
{

	/** The start directory. */
	private File startDirectory;

	/** The search txt. */
	private String searchTxt;
	
	private final long maxFileSizeInBytes = 50 * 1024 * 1024; // 50 MB

	/**
	 * Instantiates a new file search.
	 *
	 * @param startDirectory the start directory
	 * @param searchTxt the search txt
	 */
	public App(File startDirectory, String searchTxt) 
	{
		this.startDirectory = startDirectory;
		this.searchTxt = searchTxt.toLowerCase();
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception 
	{
		if(args.length == 2){
			File startDirectory = new File(args[0]);
			String searchTxt = args[1];
			App fileSearch = new App(startDirectory, searchTxt);
			List<String> resultLst = fileSearch.search();
			System.out.println(resultLst);
			Files.write(new File("Findings.txt").toPath(), resultLst);
		}
	}

	/**
	 * Search.
	 *
	 * @throws Exception the exception
	 */
	public List<String> search()
	{	
		List<String> returnLst = new ArrayList<String>();
		try {
			for(File file:startDirectory.listFiles()){
				if(file.isFile()){
					returnLst.addAll(fileSearch(file));
				}else if(file.isDirectory()){
					App fileSearch = new App(file, searchTxt);
					returnLst.addAll(fileSearch.search());
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnLst;
	}

	/**
	 * File search.
	 *
	 * @param file the file
	 */
	private List<String> fileSearch(File file)
	{
		List<String> returnLst = new ArrayList<String>();
		if(file.length() <= maxFileSizeInBytes){
			try(BufferedReader br = new BufferedReader(new FileReader(file));){
				String line = "";
				while((line = br.readLine()) != null){
					if(line.toLowerCase().contains(searchTxt)){
						returnLst.add(file.getAbsolutePath() + " -> " + line);
					}
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return returnLst;
	}
}
