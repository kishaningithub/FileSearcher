package com.kishan.FileSearcher.dto;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.kishan.FileSearcher.DataSizeUnit;

/**
 * The Class SearchInputDTO.
 */
public class SearchInputDTO implements Serializable
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The start directory. */
	private Path startDirectory;

	/** The search txt. */
	private String searchTxt;
	
	/** The is any size. */
	private boolean isAnySize;

	/** The max file size in bytes. */
	private long maxFileSizeInBytes;

	/** The is reg ex. */
	private boolean isRegEx;

	/**
	 * Gets the start directory.
	 *
	 * @return the start directory
	 */
	public Path getStartDirectory()
	{
		return startDirectory;
	}

	/**
	 * Sets the start directory.
	 *
	 * @param startDirectory the new start directory
	 */
	public void setStartDirectory(Path startDirectory)
	{
		this.startDirectory = startDirectory;
	}
	
	/**
	 * Sets the start directory.
	 *
	 * @param startDirectory the new start directory
	 */
	public void setStartDirectory(String startDirectory)
	{
		this.startDirectory = Paths.get(startDirectory);
	}

	/**
	 * Gets the search txt.
	 *
	 * @return the search txt
	 */
	public String getSearchTxt()
	{
		return searchTxt;
	}
	
	/**
	 * Checks if is any size.
	 *
	 * @return true, if is any size
	 */
	public boolean isAnySize()
	{
		return isAnySize;
	}

	/**
	 * Sets the any size.
	 *
	 * @param isAnySize the new any size
	 */
	public void setAnySize(boolean isAnySize)
	{
		this.isAnySize = isAnySize;
	}

	/**
	 * Sets the search txt.
	 *
	 * @param searchTxt the new search txt
	 */
	public void setSearchTxt(String searchTxt)
	{
		this.searchTxt = searchTxt.trim();
	}

	/**
	 * Gets the max file size in bytes.
	 *
	 * @return the max file size in bytes
	 */
	public long getMaxFileSizeInBytes()
	{
		return maxFileSizeInBytes;
	}

	/**
	 * Sets the max file size in bytes.
	 *
	 * @param maxFileSizeInBytes the new max file size in bytes
	 */
	public void setMaxFileSizeInBytes(long maxFileSizeInBytes)
	{
		this.maxFileSizeInBytes = maxFileSizeInBytes;
	}
	
	/**
	 * Sets the max file size in bytes.
	 *
	 * @param maxFileSize the max file size
	 * @param maxFileSizeUnit the max file size unit
	 */
	public void setMaxFileSizeInBytes(long maxFileSize, DataSizeUnit maxFileSizeUnit)
	{
		this.maxFileSizeInBytes = maxFileSizeUnit.toBytes(maxFileSize);
	}

	/**
	 * Checks if is reg ex.
	 *
	 * @return true, if is reg ex
	 */
	public boolean isRegEx()
	{
		return isRegEx;
	}

	/**
	 * Sets the reg ex.
	 *
	 * @param isRegEx the new reg ex
	 */
	public void setRegEx(boolean isRegEx)
	{
		this.isRegEx = isRegEx;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "SearchInputDTO [startDirectory=" + startDirectory
				+ ", searchTxt=" + searchTxt + ", maxFileSizeInBytes="
				+ maxFileSizeInBytes + ", isRegEx=" + isRegEx + "]";
	}

}
