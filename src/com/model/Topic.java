package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.constants.Constants;
import com.constants.IdProvider;
import com.processor.SentenceProcessor;

public class Topic
{
	List<Summary> summaries;
	List<Document> documents;
	long topicId;
	Map<Long, Sentence> sentenceMap = new HashMap<Long, Sentence>();
	
	public Map<Long, Sentence> getSentenceMap()
	{
		return sentenceMap;
	}

	public Topic(String filename, long topicId)
	{
		documents = SentenceProcessor.getDocuments(filename, sentenceMap);
		if(Constants.isDUC2002)
		{
			splitDocuments();
		}
		summaries = new ArrayList<Summary>();
		this.topicId = topicId;
	}
	
	private void splitDocuments()
	{
		Document d = documents.remove(0);
		List<Sentence> senList = d.getSentences();
		int size = senList.size()/10;
		long docId = IdProvider.getNextDocumentId();
		List<Sentence> list = new ArrayList<Sentence>();
		int added=0; int senIndex=-1;
		boolean getMoreSentences=false;
		long lineNum=0;
		for(int i=0; i<Constants.DOCUMENTS_PER_TOPIC ; i++)
		{
			getMoreSentences=true;
			while(getMoreSentences)
			{
				senList.get(++senIndex).documentId = docId;
				lineNum++;
				senList.get(senIndex).setPosition(lineNum);
				list.add(senList.get(senIndex));
				added++;
				if((added==size && i!=Constants.DOCUMENTS_PER_TOPIC-1) || (i==Constants.DOCUMENTS_PER_TOPIC-1 && senIndex==senList.size()-1))
				{
					Document dNew = new Document(list, d.filename, docId);
					lineNum=0;
					documents.add(dNew);
					docId = IdProvider.getNextDocumentId();
					added=0;
					list = new ArrayList<Sentence>();
					getMoreSentences = false;
				}
			}
		}
	}

	//For J-unit purposes
	public Topic()
	{
		this.summaries = new ArrayList<Summary>();
		this.documents = new ArrayList<Document>();
		this.topicId = 123456;
	}
	
	public void addDocument(Document d)
	{
		this.documents.add(d);
	}
	
	public List<Document> getDocuments()
	{
		return documents;
	}

	public long getTopicId()
	{
		return topicId;
	}

	public void addSummary(Summary s)
	{
		summaries.add(s);
	}

	public List<Summary> getSummaries()
	{
		return summaries;
	}
}
