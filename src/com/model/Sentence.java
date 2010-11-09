package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.constants.Constants;

public class Sentence
{
	long position;
	long sentenceId;
	long documentId;
	List<String> content;
	double score;
	String refSentence;
	boolean partOfHumanSummary = false;
	boolean isPositiveSample = false;
	int sentenceLength=-1;
	Map<String, Integer> freqMap;
	
	public Sentence(long sentenceId, int senLength, List<String> content, String refSentence, long position, long documentId)
	{
		this.sentenceId = sentenceId;
		this.sentenceLength = senLength;
		this.content = preProcess(content);
		this.refSentence = refSentence;
		this.position = position;
		this.documentId = documentId;
		partOfHumanSummary = false;
		freqMap = buildMap();
	}
	
	public void setRefSentence(String refSentence)
	{
		this.refSentence = refSentence;
	}
	
	public boolean isPartOfHumanSummary()
	{
		return partOfHumanSummary;
	}

	public void setPartOfHumanSummary(boolean partOfHumanSummary)
	{
		this.partOfHumanSummary = partOfHumanSummary;
	}

	public long getPosition()
	{
		return position;
	}

	public void setPosition(long position)
	{
		this.position = position;
	}
	
	public long getDocumentId()
	{
		return documentId;
	}

	public List<String> getContent()
	{
		return content;
	}

	public double getScore()
	{
		return score;
	}
	
	public void setScore(double score){
		this.score = score;
	}

	public String getRefSentence()
	{
		return refSentence;
	}

	public boolean isPositiveSample() {
		return isPositiveSample;
	}

	public void setPositiveSample(boolean isPositiveSample) {
		this.isPositiveSample = isPositiveSample;
	}
	
	public int getSentenceLength()
	{
		return sentenceLength;
	}
	
	//Stemming and stop word removal
	private List<String> preProcess(List<String> content)
	{
		List<String> res = new ArrayList<String>();
		for(int i=0; i<content.size(); i++)
		{
			String word = content.get(i);
			//sw
			//if(StopWords.isStopWord(word))
			//	continue;
			//ps
			String stemmedWord=word;
			try
			{
				stemmedWord = Constants.porterStemmer.stem(word);
			}
			catch(Exception e)
			{
				System.out.println("Sentence:preProcess:: " + word);
			}
			if(stemmedWord.equalsIgnoreCase("Invalid term")){
				res.add(word);
			} else {
				res.add(stemmedWord);
			}
		}
		return res;
	}
	
	public Map<String, Integer> getFreqMap()
	{
		return freqMap;
	}
	
	private Map<String, Integer> buildMap()
	{
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(String word: getContent())
		{
			word=word.toLowerCase();
			if(map.containsKey(word))
				map.put(word, map.get(word)+1);
			else
				map.put(word, 1);
		}
		return map;
	}
	
	@Override
	public String toString()
	{
		return getRefSentence() + " - " + getScore();
	}
	
	public String getId()
	{
		return ""+sentenceId;
	}
}
