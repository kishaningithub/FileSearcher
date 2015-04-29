package com.kishan.FileSearcher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import com.kishan.FileSearcher.dto.SearchInputDTO;

/**
 * The Class FileSearchTask.
 */
public class FileSearchTask extends Task<Void>
{

	/** The search input dto. */
	private final SearchInputDTO searchInputDTO;

	/** The observable result list. */
	private final ObservableList<String> observableResultList;

	/** The threshold size. */
	private final long thresholdSize = DataSizeUnit.MB.toBytes(10);

	/** The i pattern searcher. */
	private final IPatternSearcher iPatternSearcher;
	
	/** The no of files searched. */
	private long noOfFilesSearched = 0;
	
	// Message Formats - Start
	/** The msg format search completed. */
	private final String msgFormatSearchCompleted = "Done. %s files searched (%s seconds).";
	
	/** The msg format progress msg. */
	private final String msgFormatProgressMsg = "%s files searched. Searching %s";
    // Message Formats - End
	
	/**
	 * Instantiates a new file search task.
	 *
	 * @param searchInputDTO the search input dto
	 * @param observableResultList the observable result list
	 */
	public FileSearchTask(SearchInputDTO searchInputDTO, ObservableList<String> observableResultList) 
	{
		super();
		this.searchInputDTO = searchInputDTO;
		this.observableResultList = observableResultList;
		if(searchInputDTO.isRegEx()){
			iPatternSearcher = new RegExPatternSearcher(searchInputDTO.getSearchTxt());
		}else{
			iPatternSearcher = new StringPatternSearcher(searchInputDTO.getSearchTxt());
		}
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#call()
	 */
	/**
	 * Call.
	 *
	 * @return the void
	 * @throws Exception the exception
	 */
	@Override
	protected Void call() throws Exception 
	{
		try{
			long fromTime = System.currentTimeMillis();
			if(!searchInputDTO.getStartDirectory().toFile().exists()){
				updateMessage("Invalid directory.");
				return null;
			}
			if(!searchInputDTO.getSearchTxt().isEmpty()){
				Files.walkFileTree(searchInputDTO.getStartDirectory(), new FileSearchFileVisitor());
			}
			long toTime = System.currentTimeMillis();
			double timeTakenInSecs = (toTime - fromTime) / 1000d;
			updateMessage(String.format(msgFormatSearchCompleted, noOfFilesSearched, timeTakenInSecs));
		}catch(ClosedByInterruptException e){
			if(!isCancelled()){
				e.printStackTrace();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks if is search str present.
	 *
	 * @param str the str
	 * @return true, if is search str present
	 */
	private boolean isSearchStrPresent(String str)
	{
		boolean isFound = false;
		isFound = iPatternSearcher.isPatternPresent(str);
		return isFound;
	}

	/**
	 * Search small file.
	 *
	 * @param path the path
	 */
	private void searchSmallFile(final Path path)
	{
		try {
			String fileContent = new String(Files.readAllBytes(path));
			if(isSearchStrPresent(fileContent)){
				updateUI(path);
			}
		}catch(ClosedByInterruptException e){
			if(!isCancelled()){
				e.printStackTrace();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update ui.
	 *
	 * @param path the path
	 */
	private void updateUI(Path path)
	{
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				observableResultList.add(path.toString());									
			}
		});
	}

	/**
	 * Search large file.
	 *
	 * @param path the path
	 */
	private void searchLargeFile(final Path path)
	{
		int bufferSize = 10 * 1024 * 1024;
		try (SeekableByteChannel in = Files.newByteChannel(path, StandardOpenOption.READ)){
			byte[] byteArr = new byte[bufferSize];
			ByteBuffer bytebuf = ByteBuffer.wrap(byteArr);
			int bytesCount = 0;
			String prependStr = "";
			while ((bytesCount = in.read(bytebuf)) > 0) {
				String pageStr = prependStr + new String(byteArr, 0, bytesCount);
				if(isSearchStrPresent(pageStr)){
					updateUI(path);
					break;
				}
				// Check and handle 'cut' lines - Start
				int i = pageStr.length() - 1;
				int newLinePos = i; 
				for(;i >= 0; i--){
					char c = pageStr.charAt(i);
					if(c == '\r' || c == '\n' ){
						newLinePos = i;
						break;
					}
				}
				if((newLinePos + 1) < pageStr.length()){
					prependStr = pageStr.substring(newLinePos + 1);
				}else{
					prependStr = "";
				}
				// Check and handle 'cut' lines - End
				bytebuf.clear();
			}
		} catch(ClosedByInterruptException e){
			if(!isCancelled()){
				e.printStackTrace();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
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
		public FileVisitResult visitFile(final Path path, final BasicFileAttributes attrs) throws IOException 
		{
			if(isCancelled()){
				return FileVisitResult.TERMINATE;
			}
			long fileSize = attrs.size();
			if(searchInputDTO.isAnySize() || fileSize <= searchInputDTO.getMaxFileSizeInBytes()){
				updateMessage(String.format(msgFormatProgressMsg, noOfFilesSearched, path));
				if(fileSize <= thresholdSize){
					searchSmallFile(path);
				}else{
					searchLargeFile(path);
				}
				noOfFilesSearched++;
			}
			return super.visitFile(path, attrs);
		}
	}
}
