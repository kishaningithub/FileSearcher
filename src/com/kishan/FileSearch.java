package com.kishan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileSearch implements Runnable
{

	private File startDirectory;
	private String searchTxt;

	public FileSearch(File startDirectory, String searchTxt) 
	{
		this.startDirectory = startDirectory;
		this.searchTxt = searchTxt.toLowerCase();
	}

	public static void main(String[] args) throws Exception 
	{
		File startDirectory = new File("G:\\My Entire Backup\\Mac BackUp");
		String searchTxt = "DocumentTagProcessor";
		new FileSearch(startDirectory, searchTxt).search();
	}

	public void search() throws Exception
	{
		new Thread(this).start();
	}

	private void fileSearch(File file)
	{
		String outputFileName = "Findings_%s.txt";
		String random = startDirectory.getName();
		outputFileName = String.format(outputFileName, "");
		try(BufferedReader br = new BufferedReader(new FileReader(file));){
			new File(outputFileName).createNewFile();
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName, true));){
				String line = "";
				while((line = br.readLine()) != null){
					if(line.toLowerCase().contains(searchTxt)){
						bw.write(file.getAbsolutePath() + " -> " + line);
						bw.newLine();
					}
				}	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() 
	{
		try {
			for(File file:startDirectory.listFiles()){
				if(file.isFile()){
					fileSearch(file);
				}else if(file.isDirectory()){
					new FileSearch(file, searchTxt).search();
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
