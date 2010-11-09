package com.score.importance;

import java.util.List;

import com.interfaces.ImportanceScorer;
import com.model.Document;
import com.model.Sentence;
import java.util.Map;
import java.util.HashMap;

public class SentenceLengthCalculator implements ImportanceScorer {

	private double weightage = 1.0;
	private static SentenceLengthCalculator instance = new SentenceLengthCalculator();

	private Map<Long, Pair> docMap = null;
	
	class Pair{
		public double avg = 0;
		public double stdDev = 0;
	}
	
	public void initialize(List<Document> docs) {
		docMap = new HashMap<Long, Pair>();
		for(Document doc : docs){
			docMap.put(doc.getDocumentId(), getDocumentStats(doc));
		}
	}
	
	private Pair getDocumentStats(Document doc){
		Pair p = new Pair();
		
		for(Sentence sent : doc.getSentences()){
			p.avg += sent.getSentenceLength();
		}
		
		p.avg /= doc.getNumberOfSentences();
		
		for(Sentence sent : doc.getSentences()){
			p.stdDev += Math.pow((sent.getSentenceLength() - p.avg), 2);
		}
		
		p.stdDev /= doc.getNumberOfSentences();
		p.stdDev = Math.sqrt(p.stdDev);
		
		return p;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.interfaces.ImportanceScorer#getImportanceScore(com.model.Document, com.model.Sentence)
	 * 
	 * Returns the normalized sentence length
	 * 
	 * output: (1 - e^-alpha) / (1 + e^-alpha)
	 * 
	 */
	
	public double getImportanceScore(Document doc, Sentence a) {
		Pair p;
		if(docMap.containsKey(doc.getDocumentId())){
			p = docMap.get(doc.getDocumentId());
		} else {
			p = getDocumentStats(doc);
			docMap.put(doc.getDocumentId(), p);
		}
		
		//double alpha =  p.stdDev != 0 ? (float)((a.getSentenceLength() - p.avg)/p.stdDev) : 0;
		//return (double)((1 - Math.exp(-1*alpha)) / (1 + Math.exp(-1*alpha)));
		return a.getSentenceLength();
	}

	public double getWeightage() {
		return this.weightage;
	}

	public void setWeightage(double weight) {
		this.weightage = weight;
	}
	
	public String getName(){
		return "SentLength";
	}
	public static SentenceLengthCalculator getInstance() {
		return instance;
	}
}
