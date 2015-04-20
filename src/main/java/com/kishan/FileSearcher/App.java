package com.kishan.FileSearcher;

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
	private final File startDirectory;

	/** The search txt. */
	private final String searchTxt;
	
	/** The max file size in bytes. */
	private final long maxFileSizeInBytes;
	
	/** The status txt. */
	private static String statusTxt;
	
	private boolean searchCompleted;

	/**
	 * Instantiates a new file search.
	 *
	 * @param startDirectory the start directory
	 * @param searchTxt the search txt
	 */
	public App(File startDirectory, String searchTxt, long maxFileSizeInBytes) 
	{
		this.startDirectory = startDirectory;
		this.searchTxt = searchTxt.toLowerCase();
		this.maxFileSizeInBytes = maxFileSizeInBytes;
	}
	
	public String getSearchTxt() 
	{
		return searchTxt;
	}
	
	private void setSearchTxt(String statusTxt) 
	{
		App.statusTxt= statusTxt;
	}

	public boolean isSearchCompleted()
	{
		return searchCompleted;
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
			App fileSearch = new App(startDirectory, searchTxt, 1 * 1024 * 1024 /* 1MB */);
			List<String> resultLst = fileSearch.search();
			Files.write(new File("Findings.txt").toPath(), resultLst);
		}
	}

	/**
	 * Search.
	 *
	 * @return the list
	 */
	public List<String> search()
	{	
		List<String> returnLst = new ArrayList<String>();
		try {
			for(File file:startDirectory.listFiles()){
				if(file.isFile()){
					returnLst.addAll(fileSearch(file));
				}else if(file.isDirectory()){
					App fileSearch = new App(file, searchTxt, maxFileSizeInBytes);
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
	 * @return the list
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
