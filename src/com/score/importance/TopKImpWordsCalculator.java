package com.score.importance;

import java.util.List;

import com.constants.Constants;
import com.interfaces.ImportanceScorer;
import com.model.Document;
import com.model.Sentence;

public class TopKImpWordsCalculator implements ImportanceScorer{

	private double weightage = 1.0;
	private TfIDFCalculator tfIdfStats = null;
	private static ImportanceScorer instance = new TopKImpWordsCalculator();
	
	public void setTfIDFCalculator(TfIDFCalculator calculator){
		this.tfIdfStats = calculator;
	}
	
	public double getImportanceScore(Document doc, Sentence a) {
		
		if(tfIdfStats == null){
			return a.getContent().size();
		} else {
			
			int sum = 0;
			for(String word : a.getContent()){
				double score = tfIdfStats.getImportanceScore(a.getDocumentId(), word);
				if(score >= Constants.TFIDFThreshold){
					sum++;
				}
			}
			return sum;
		}
	}

	public double getWeightage() {
		return this.weightage;
	}

	public void initialize(List<Document> docs) {
		// TODO DO NOTHING
		
	}

	public void setWeightage(double weight) {
		this.weightage = weight;
	}
	
	public String getName(){
		return "TopKImpWords";
	}

	public ImportanceScorer getInstance() {
		return instance;
	}

}
