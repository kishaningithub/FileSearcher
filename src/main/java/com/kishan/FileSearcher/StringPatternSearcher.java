package com.kishan.FileSearcher;

/**
 * The Class StringPatternSearcher.
 */
public class StringPatternSearcher implements IPatternSearcher
{
	
	/** The pattern str. */
	private final String patternStr;
	
	/**
	 * Instantiates a new string pattern searcher.
	 *
	 * @param patternStr the pattern str
	 */
	StringPatternSearcher(final String patternStr)
	{
		this.patternStr = patternStr.toLowerCase();
	}
	
	/* (non-Javadoc)
	 * @see com.kishan.FileSearcher.IPatternSearcher#find(java.lang.String)
	 */
	@Override
	public boolean isPatternPresent(final String str)
	{
		String lowerStr = str.toLowerCase();
		boolean isFound = lowerStr.contains(patternStr);
		return isFound;
	}

}
