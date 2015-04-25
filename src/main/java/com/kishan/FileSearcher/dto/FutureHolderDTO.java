package com.kishan.FileSearcher.dto;

import java.nio.file.Path;
import java.util.concurrent.Future;

/**
 * The Class FutureHolderDTO.
 */
public class FutureHolderDTO
{

	/** The file path. */
	private Path filePath;

	/** The future. */
	private Future<?> future;

	/**
	 * Gets the file path.
	 *
	 * @return the file path
	 */
	public Path getFilePath()
	{
		return filePath;
	}

	/**
	 * Sets the file path.
	 *
	 * @param filePath
	 *            the new file path
	 */
	public void setFilePath(Path filePath)
	{
		this.filePath = filePath;
	}

	/**
	 * Gets the future.
	 *
	 * @return the future
	 */
	public Future<?> getFuture()
	{
		return future;
	}

	/**
	 * Sets the future.
	 *
	 * @param future
	 *            the new future
	 */
	public void setFuture(Future<?> future)
	{
		this.future = future;
	}
}
