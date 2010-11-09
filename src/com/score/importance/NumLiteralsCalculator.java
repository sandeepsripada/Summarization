package com.score.importance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.interfaces.ImportanceScorer;
import com.model.Document;
import com.model.Sentence;

public class NumLiteralsCalculator implements ImportanceScorer {

	private static NumLiteralsCalculator instance = new NumLiteralsCalculator();
	private double weightage = 1.0;
	private Map<Long, Pair>  docMap = null;
	
	class Pair {
		private double avg = 0;
		private double stdDev = 0;
	}
	
	public NumLiteralsCalculator(){
		
	}
	
	public void initialize(List<Document> docs) {
		docMap = new HashMap<Long, Pair>();
		
		for(Document doc : docs){
			Pair p = getDocStats(doc);
			docMap.put(doc.getDocumentId(), p);
		}
		
	}
	
	private Pair getDocStats(Document doc){
		Pair p = new Pair();
		
		for(Sentence sent : doc.getSentences()){
			p.avg += getSentenceSum(doc.getDocumentId(), sent);
		}
		
		p.avg /= doc.getNumberOfSentences();
		
		for(Sentence sent : doc.getSentences()){
			p.stdDev += Math.pow((getSentenceSum(doc.getDocumentId(), sent) - p.avg), 2);
		}
		
		p.stdDev /= doc.getNumberOfSentences();
		p.stdDev = Math.sqrt(p.stdDev);
		
		return p;
	}
	
	private double getSentenceSum(Long docId, Sentence s){
		double score = 0.0;
		for(String word : s.getContent()){
			score += getWordScore(s.getDocumentId(), word);
		}
		
		return score;
	}
	
	private double getWordScore(Long docId, String word){
		String temp = word.trim();
		double score = 0;
		if(temp.length() > 0){
			try{
				Double.parseDouble(temp);
				score = 1;
			} catch (Exception e){
				score = 0;
			}
		}
		return score;
	}
	
	public double getImportanceScore(Document doc, Sentence a) {
		Pair p = null;
		if(docMap.containsKey(doc.getDocumentId())){
			p = docMap.get(doc.getDocumentId());
		} else {
			p = getDocStats(doc);
			docMap.put(doc.getDocumentId(), p);
		}
		
		double score = getSentenceSum(doc.getDocumentId(), a);
		//double alpha = p.stdDev != 0 ? (double)((score - p.avg) / p.stdDev) : 0;
		
		//return (double)((1 - Math.exp(-1*alpha)) / (1 + Math.exp(-1*alpha)));
		//return score;
		return (double)score/a.getSentenceLength();
	}

	public String getName() {
		return "NumLiteralsCalculator";
	}

	public double getWeightage() {
		return this.weightage;
	}

	

	public void setWeightage(double weight) {
		this.weightage = weight;
	}

	public NumLiteralsCalculator getInstance(){
		return instance;
	}

}
