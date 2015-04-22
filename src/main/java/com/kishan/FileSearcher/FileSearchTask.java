package com.kishan.FileSearcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * The Class FileSearchTask.
 */
public class FileSearchTask extends Task<Void>
{

	/** The start directory. */
	private final Path startDirectory;

	/** The search txt. */
	private final String searchTxt;

	/** The max file size in bytes. */
	private final long maxFileSizeInBytes;
	
	/** The observable result list. */
	private final ObservableList<String> observableResultList;

	/**
	 * Instantiates a new file search task.
	 *
	 * @param startDirectory the start directory
	 * @param searchTxt the search txt
	 * @param maxFileSize the max file size
	 * @param maxFileSizeUnit the max file size unit
	 * @param observableResultList the observable result list
	 */
	public FileSearchTask(Path startDirectory, String searchTxt, long maxFileSize, DataSizeUnit maxFileSizeUnit, ObservableList<String> observableResultList) 
	{
		super();
		this.startDirectory = startDirectory;
		this.searchTxt = searchTxt.trim().toLowerCase();
		this.maxFileSizeInBytes = maxFileSizeUnit.toBytes(maxFileSize);
		this.observableResultList = observableResultList;
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected Void call() throws Exception 
	{
		updateMessage("Searching...");
		if(!startDirectory.toFile().exists()){
			updateMessage("Invalid directory.");
			return null;
		}
		if(!searchTxt.isEmpty()){
			Files.walkFileTree(startDirectory, new FileSearchFileVisitor());
		}
		updateMessage("Search completed.");
		return null;
	}

	/**
	 * The Class FileSearchFileVisitor.
	 */
	private class FileSearchFileVisitor extends SimpleFileVisitor<Path>
	{
		
		/* (non-Javadoc)
		 * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
		 */
		@Override
		public FileVisitResult visitFile(final Path path, BasicFileAttributes attrs) throws IOException 
		{
			if(attrs.size() <= maxFileSizeInBytes){
				updateMessage("Searching file " + path.toString());
				try(BufferedReader br = new BufferedReader(new FileReader(path.toFile())); ){
					String line = "";
					while((line = br.readLine()) != null){
						if(line.toLowerCase().contains(searchTxt)){
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									observableResultList.add(path.toString());									
								}
							});
							break;
						}
					}	
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return super.visitFile(path, attrs);
		}

	}

}
