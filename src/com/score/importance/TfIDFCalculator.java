package com.score.importance;

import java.util.List;

import com.interfaces.ImportanceScorer;
import com.model.Document;
import com.model.Sentence;
import com.util.Counter;
import com.util.CounterMap;
import java.util.Map;
import java.util.HashMap;


public class TfIDFCalculator implements ImportanceScorer{
	
	private CounterMap<String, Long> stats;
	private int totalDocs = 0;
	private double weightage = 1.0;
	private Map<Long, Pair> docMap = null;
	
	class Pair {
		public double avg = 0;
		public double stdDev = 0;
	}
	
	public TfIDFCalculator(){
		
	}
	
	public void initialize(List<Document> docs){
		initialize();
		this.totalDocs = docs.size();
		
		for(Document doc : docs){
			process(doc);
		}
		
		normalize();
		
		initDocStats(docs);
	}
	
	private void initDocStats(List<Document> docs){
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
			score += getImportanceScore(s.getDocumentId(), word);
		}
		
		return score;
	}
	
	public double getImportanceScore(long docId, String word){
		return this.stats.getCount(word, docId);
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
		return score/a.getSentenceLength();
	}
	
	private void normalize(){
		for(String word : stats.keySet()){
			Counter<Long> wordCounter = stats.getCounter(word);
			int docFreq = wordCounter.size();
			double idf = 1;
			if(this.totalDocs != docFreq)
				idf = Math.log(this.totalDocs / docFreq);
			
			for(Long wf : wordCounter.keySet()){
				double tfidf = (1 + Math.log(wordCounter.getCount(wf))) * idf;
				wordCounter.setCount(wf, tfidf);
			}
		}
	}
	
	private void process(Document doc){
		for(Sentence sent : doc.getSentences()){
			for(String word : sent.getContent()){
				this.stats.incrementCount(word, doc.getDocumentId(), 1.0);
			}
		}
	}
	
	private void initialize() {
		stats = new CounterMap<String, Long>();
	}

	public double getWeightage() {
		return this.weightage;
	}

	public void setWeightage(double weight) {
		this.weightage = weight;
	}
	
	public String getName(){
		return "TFIDFSum";
	}

	public static TfIDFCalculator getInstance() {
		return new TfIDFCalculator();
	}

}
