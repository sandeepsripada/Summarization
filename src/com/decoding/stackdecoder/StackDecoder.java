package com.decoding.stackdecoder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.constants.Constants;
import com.interfaces.SimilarityScorer;
import com.model.Document;
import com.model.Sentence;
import com.score.similarity.CosineSimilarityScorer;

public class StackDecoder
{
	//0-not used and 101 - for all summaries with length greater than 100
	Stack[] stacks = new Stack[Constants.SUMMARY_LENGTH+2];
	List<Sentence> sentences;
	List<Document> documents;
	SimilarityScorer simScorer = null;
	
	Map<String, Double> simScoreCache = new HashMap<String, Double>();
	
	class Stack
	{
		//List<Integer> represents the sentences chosen
		SpecialPQ<List<Integer>> pq = new SpecialPQ<List<Integer>>();
		
		public void add(List<Integer> key, double priority)
		{
			pq.add(key, priority);
		}
		
		public List<Integer> getBest()
		{
			return pq.peek();
		}

		public void printStackPQ()
		{
			System.out.println("StackDecoder:Stack:printStackPQ:: Stack print..");
			System.out.println(pq.toString());
		}
	}
	
	public StackDecoder(List<Document> documents)
	{
		this.documents = documents;
		this.sentences = buildSentenceList();
		setUpStacks();
		//simScorer = new TFIDFSimilarityScorer(documents);
		//simScorer = new LeskSimilarityScorer();
		//simScorer = new ResnikSimScorer();
		simScorer = new CosineSimilarityScorer();
	}
	
	private void setUpStacks()
	{
		for(int i=0; i<(Constants.SUMMARY_LENGTH+2); i++)
			stacks[i] = new Stack();
	}

	private List<Sentence> buildSentenceList()
	{
		List<Sentence> sentences = new ArrayList<Sentence>();
		int senNum=-1;
		for(int i=0; i<documents.size(); i++)
		{
			System.out.println("StackDecoder:buildSentenceList:: Doc: " + (i+1));
			Document d = documents.get(i);
			//System.out.println("StackDecoder:buildSentenceList:: Sentence list size: " + d.getNumberOfSentences());
			for(Sentence s:d.getSentences())
			{
				if(s.getSentenceLength()>=Constants.MIN_SENTENCE_LENGTH)
				{
					sentences.add(s);
					System.out.println("StackDecoder:buildSentenceList:: (" + ++senNum + ")" + s.getRefSentence() + " - " + s.getScore());
				}
			}
		}
		return sentences;
	}

	public void runStackDecoder()
	{
		initializeStack();
		
		for(int i=0; i<(Constants.SUMMARY_LENGTH+1); i++)
		{
			System.out.println("StackDecoder:runStackDecoder:: Running stack: " + i);
			if(stacks[i].pq.size()==0)
				continue;
			SpecialPQ<List<Integer>> pqClone = stacks[i].pq.clone();
			while(pqClone.hasNext())
			{
				List<Integer> summary = pqClone.next();
				for(int j=0; j<sentences.size(); j++)
				{
					if(summary.contains(j))
						continue;
					Sentence s = sentences.get(j);
					int newIndex = Constants.SUMMARY_LENGTH+1;
					if(i+s.getSentenceLength()<=Constants.SUMMARY_LENGTH)
						newIndex = i+s.getSentenceLength();
					
					if(isIncludeSentence(summary, s, j))
					{
						List<Integer> newSummary = new ArrayList<Integer>(summary);
						newSummary.add(j);
						double priority = getObjectiveFunction(newSummary);
						//TODO: What happens if the priorities are equal? Use some other measure?
						if(priority > stacks[newIndex].pq.getPriority())
							stacks[newIndex].add(newSummary, priority);
					}
				}
			}
			
			stacks[i].printStackPQ();
		}
	}

	private boolean isIncludeSentence(List<Integer> summary, Sentence s, int sIndex)
	{
		//returns whether the sentence should be included in the summary or not
		for(int i=0; i<summary.size(); i++)
		{
			String key = sIndex+","+summary.get(i);
			double sim = 0;
			if(simScoreCache.containsKey(key))
				sim = simScoreCache.get(key);
			else
			{
				sim = simScorer.similarity(s, sentences.get(summary.get(i)));
				simScoreCache.put(key, sim);
			}
			/*System.out.println("StackDecoder:isIncludeSentence:: " + sim);
			System.out.println("(A): " + s.getRefSentence());
			System.out.println("(B): " + sentences.get(summary.get(i)).getRefSentence());*/
			if(sim > Constants.SENTENCE_OVERLAP_THRESHOLD)
				return false;
		}
		return true;
	}

	private void initializeStack()
	{
		for(int i=0; i<sentences.size(); i++)
		{
			Sentence s = sentences.get(i);
			List<Integer> l = new ArrayList<Integer>();
			l.add(i);
			int index = Constants.SUMMARY_LENGTH+1;
			if(s.getSentenceLength()<=Constants.SUMMARY_LENGTH)
				index = s.getSentenceLength();
			stacks[index].add(l, getObjectiveFunction(l));
		}	
	}

	private double getObjectiveFunction(List<Integer> senRefList)
	{
		double summaryObjectiveScore = 0;
		for(int i=0; i<senRefList.size(); i++)
		{
			int index = senRefList.get(i);
			summaryObjectiveScore+=sentences.get(index).getScore();
		}
		return summaryObjectiveScore;
	}
	
	public void printStack(int num)
	{
		stacks[num].printStackPQ();
	}
	
	public List<Sentence> getBestSummary()
	{
		List<Sentence> res = new ArrayList<Sentence>();
		List<Integer> bestSummary = stacks[Constants.SUMMARY_LENGTH].getBest();
		for(int i=0; i<bestSummary.size(); i++)
		{
			res.add(sentences.get(bestSummary.get(i)));
		}
		return res;
	}
	
	public void dumpBestSummary(String fileName)
	{
		try
		{
			BufferedWriter br = new BufferedWriter(new FileWriter(new File(fileName)));
			List<Integer> bestSummary = stacks[Constants.SUMMARY_LENGTH].getBest();
			int len = Constants.SUMMARY_LENGTH;
			while(bestSummary==null)
				bestSummary = stacks[--len].getBest();
			Collections.sort(bestSummary);
			for(int i=0; i<bestSummary.size(); i++)
			{
				int senIndex = bestSummary.get(i);
				Sentence s = sentences.get(senIndex);
				br.write(s.getRefSentence());
				br.newLine();
			}
			br.close();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
