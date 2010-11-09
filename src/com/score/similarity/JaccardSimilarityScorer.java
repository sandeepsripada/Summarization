package com.score.similarity;

import java.util.Map;

import com.interfaces.SimilarityScorer;
import com.model.Sentence;

public class JaccardSimilarityScorer implements SimilarityScorer {

	public double similarity(Sentence a, Sentence b) {
		Map<String, Integer> aMap = a.getFreqMap();
		Map<String, Integer> bMap = b.getFreqMap();
		
		double intersection = 0;
		double union = aMap.size();
		for(String word : bMap.keySet())
		{
			if(aMap.containsKey(word))
				intersection++;
			else
				union++;
		}
		return (intersection / union);
	}

}
