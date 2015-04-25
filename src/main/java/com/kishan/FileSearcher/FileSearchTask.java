package com.kishan.FileSearcher;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import com.kishan.FileSearcher.dto.FutureHolderDTO;
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

	/** The es. */
	private final ExecutorService es = Executors.newFixedThreadPool(5);
	
	private final List<FutureHolderDTO> futureHolderDTOLst = new ArrayList<FutureHolderDTO>();
	
	private final Pattern searchPattern;

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
			searchPattern = Pattern.compile(searchInputDTO.getSearchTxt());
		}else{
			searchPattern = Pattern.compile(Pattern.quote(searchInputDTO.getSearchTxt()), Pattern.CASE_INSENSITIVE);
		}
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected Void call() throws Exception 
	{
		if(!searchInputDTO.getStartDirectory().toFile().exists()){
			updateMessage("Invalid directory.");
			return null;
		}
		if(!searchInputDTO.getSearchTxt().isEmpty()){
			Files.walkFileTree(searchInputDTO.getStartDirectory(), new FileSearchFileVisitor());
			for(FutureHolderDTO futureHolderDTO:futureHolderDTOLst){
				Future<?> future = futureHolderDTO.getFuture();
				if(!future.isDone()){
					updateMessage("Searching " + futureHolderDTO.getFilePath());
					future.get();
				}
			}
		}
		updateMessage("Search completed.");
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
		Matcher matcher = searchPattern.matcher(str);
		boolean isFound = matcher.find();
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
			updateMessage("Searching "+ path);
			String fileContent = new String(Files.readAllBytes(path));
			updateUIIfSearchStrPresent(path, fileContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update ui if search str present.
	 *
	 * @param path the path
	 * @param fileContent the file content
	 */
	private void updateUIIfSearchStrPresent(final Path path, final String fileContent)
	{
		if(isSearchStrPresent(fileContent)){
			updateUI(path);
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
		try (FileInputStream fs = new FileInputStream(path.toFile());
				FileChannel in = fs.getChannel()){
			byte[] byteArr = new byte[bufferSize];
			ByteBuffer bytebuf = ByteBuffer.wrap(byteArr);
			int bytesCount = 0;
			String prependStr = "";
			while ((bytesCount = in.read(bytebuf)) > 0) {
				String pageStr = prependStr + new String(byteArr, 0, bytesCount);
				updateUIIfSearchStrPresent(path, pageStr);
				// Check and handle 'cut' lines - Start
				int i = pageStr.length() - 1;
				int newLinePos = i; 
				for(; i >= 0; i--){
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
		} catch (IOException e) {
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
			long fileSize = attrs.size();
			if(searchInputDTO.isAnySize() || fileSize <= searchInputDTO.getMaxFileSizeInBytes()){
				updateMessage("Searching file " + path.toString());
				if(fileSize <= thresholdSize){
					searchSmallFile(path);
				}else{
					Future<?> future = es.submit(new Runnable() {
						@Override
						public void run()
						{
							searchLargeFile(path);
						}
					});
					FutureHolderDTO futureHolderDTO = new FutureHolderDTO();
					futureHolderDTO.setFilePath(path);
					futureHolderDTO.setFuture(future);
					futureHolderDTOLst.add(futureHolderDTO);
				}
			}
			return super.visitFile(path, attrs);
		}
	}
}
