package com.kishan.FileSearcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.kishan.FileSearcher.dto.SearchResultDTO;

/**
 * The Class FileSearch.
 */
public class AppBusiness 
{

	/** The start directory. */
	private final File startDirectory;

	/** The search txt. */
	private final String searchTxt;
	
	/** The max file size in bytes. */
	private final long maxFileSizeInBytes;
	
	/**
	 * Instantiates a new file search.
	 *
	 * @param startDirectory the start directory
	 * @param searchTxt the search txt
	 * @param maxFileSizeInBytes the max file size in bytes
	 */
	public AppBusiness(File startDirectory, String searchTxt, long maxFileSizeInBytes) 
	{
		this.startDirectory = startDirectory;
		this.searchTxt = searchTxt.toLowerCase();
		this.maxFileSizeInBytes = maxFileSizeInBytes;
	}
	
	/**
	 * Search.
	 *
	 * @return the list
	 */
	public List<SearchResultDTO> search()
	{	
		List<SearchResultDTO> returnLst = new ArrayList<SearchResultDTO>();
		try {
			for(File file:startDirectory.listFiles()){
				if(file.isFile()){
					returnLst.addAll(fileSearch(file));
				}else if(file.isDirectory()){
					AppBusiness fileSearch = new AppBusiness(file, searchTxt, maxFileSizeInBytes);
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
	private List<SearchResultDTO> fileSearch(File file)
	{
		List<SearchResultDTO> returnLst = new ArrayList<SearchResultDTO>();
		if(file.length() <= maxFileSizeInBytes){
			try(BufferedReader br = new BufferedReader(new FileReader(file));){
				String line = "";
				while((line = br.readLine()) != null){
					if(line.toLowerCase().contains(searchTxt)){
						SearchResultDTO searchResultDTO = new SearchResultDTO();
						searchResultDTO.setFile(file);
						returnLst.add(searchResultDTO);
					}
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return returnLst;
	}
	
	/**
	 * Gets the search results as string lst.
	 *
	 * @param searchResultDTOLst the search result dto lst
	 * @return the search results as string lst
	 */
	public List<String> getSearchResultsAsStringLst(List<SearchResultDTO> searchResultDTOLst)
	{
		List<String> stringLst = new ArrayList<String>();
		for(SearchResultDTO searchResultDTO:searchResultDTOLst){
			stringLst.add(searchResultDTO.getFile().getAbsolutePath());
		}
		return stringLst;
	}
}
