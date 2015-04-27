package com.kishan.FileSearcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class RegExPatternSearcher.
 */
public class RegExPatternSearcher implements IPatternSearcher
{
	
	/** The p. */
	private final Pattern p;
	
	/**
	 * Instantiates a new reg ex pattern searcher.
	 *
	 * @param patternStr the pattern str
	 */
	RegExPatternSearcher(String patternStr)
	{
		p = Pattern.compile(patternStr);
	}

	/* (non-Javadoc)
	 * @see com.kishan.FileSearcher.IPatternSearcher#find(java.lang.String)
	 */
	@Override
	public boolean isPatternPresent(String str)
	{
		Matcher m = p.matcher(str);
		boolean isFound = m.find();
		return isFound;
	}

}
