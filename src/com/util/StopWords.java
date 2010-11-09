package com.util;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

public class StopWords
{
	private static ArrayList<String> stopWords = null;
	
	public static boolean isStopWord(String word)
	{
		return stopWords.contains(word.toLowerCase().trim());
	}
	
	@SuppressWarnings("deprecation")
    public static void initializeStopWords(String fileName)
	{
		try
		{
			stopWords = new ArrayList<String>();
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			
			while(in.available() !=0)
			{
				String line = in.readLine();
				if(line != null && line.length() > 0)
				{
					String [] tokens = line.split(" ");
					for(String word : tokens)
					{
						String trimmed = word.trim().toLowerCase();
						if(trimmed.length() != 0)
						{
							stopWords.add(trimmed);
						}
					}
				}
				
			}
			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}