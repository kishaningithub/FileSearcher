package com.kishan.FileSearcher.dto;

import java.io.File;
import java.io.Serializable;

/**
 * The Class SearchResultDTO.
 */
public class SearchResultDTO implements Serializable
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The file. */
	File file;

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Sets the file.
	 *
	 * @param file the new file
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
}
