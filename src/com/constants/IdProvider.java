package com.constants;

public class IdProvider
{
	static long topicId=-1;
	static long documentId=-1;
	static long sentenceId=-1;
	
	public static long getNextTopicId()
	{
		return ++topicId;
	}
	
	public static long getNextDocumentId()
	{
		return ++documentId;
	}
	
	public static long getNextSentenceId()
	{
		return ++sentenceId;
	}
}
