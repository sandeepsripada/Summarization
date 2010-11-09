package com.model;

import com.processor.SentenceProcessor;

public class Summary extends Document
{
	boolean isHuman;
	
	public Summary(String filename, long documentId, boolean isHuman)
	{
		super(SentenceProcessor.getSentences(filename), filename, documentId);
		this.isHuman = isHuman;
	}
	
	public Summary()
	{	
	}

	public boolean isHuman()
	{
		return isHuman;
	}
}
