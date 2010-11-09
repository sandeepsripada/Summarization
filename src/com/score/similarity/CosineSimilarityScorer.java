package com.score.similarity;

import java.util.Map;

import com.interfaces.SimilarityScorer;
import com.model.Sentence;

public class CosineSimilarityScorer implements SimilarityScorer {

	public double similarity(Sentence a, Sentence b) 
	{
		Map<String, Integer> freqA = a.getFreqMap();
		Map<String, Integer> freqB = b.getFreqMap();
		double cosSim=0;
		double aMag=0;
		double bMag=0;
		for(String word:freqB.keySet())
			bMag+=Math.pow(freqB.get(word), 2);
		for(String word : freqA.keySet())
		{
			aMag+=Math.pow(freqA.get(word), 2);
			if(freqB.containsKey(word))
			{
				cosSim+=freqA.get(word)*freqB.get(word);
			}
		}
		
		if(cosSim==0){
			//System.out.println("CosineSimilarityScorer:similarity:: score = " + 0);
			return 0;
		}
		else
		{
			aMag = Math.sqrt(aMag);
			bMag = Math.sqrt(bMag);
			cosSim = cosSim/(aMag*bMag);
			//System.out.println("CosineSimilarityScorer:similarity:: score = " + cosSim);
			return cosSim;
		}
	}
}
