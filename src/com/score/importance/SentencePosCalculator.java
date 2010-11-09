package com.score.importance;

import java.util.List;

import com.interfaces.ImportanceScorer;
import com.model.Document;
import com.model.Sentence;

public class SentencePosCalculator implements ImportanceScorer {

	private double weightage = 1.0;
	private static SentencePosCalculator instance = new SentencePosCalculator();
	
	/*
	 * (non-Javadoc)
	 * @see com.interfaces.ImportanceScorer#getImportanceScore(com.model.Document, com.model.Sentence)
	 * Returns the normalized sentence position in the document
	 * output: index(a) / total no. of sent in doc
	 */
	public double getImportanceScore(Document doc, Sentence a) {
		int totSentences = doc.getNumberOfSentences();
		return (double)(a.getPosition() / (double)totSentences);
		//return (double)a.getPosition();
	}

	public String getName() {
		return "SentPost";
	}

	public void initialize(List<Document> docs) {
		// TODO Do nothing
	}

	public double getWeightage() {
		return this.weightage;
	}

	public void setWeightage(double weight) {
		this.weightage = weight;
	}

	public static SentencePosCalculator getInstance() {
		return instance;
	}

}
