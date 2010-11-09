package com.util;

/**
 * 
 * @author Sandeep
 *
 */
public class FilePathUtil
{
	//d3001 etc
	public static String getDocumentTopicId(String filename)
	{
		int index = filename.indexOf(".");
		String result = filename.substring(0, index);
		return result;
	}

	public static String getExtension(String name)
	{
		int index = name.lastIndexOf(".");
		String result = name.substring(index+1, name.length());
		return result;
	}
}
