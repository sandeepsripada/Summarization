package com.score.importance;

import java.util.ArrayList;
import java.util.List;

import com.interfaces.ImportanceScorer;
import com.model.Document;
import com.model.Sentence;

public class ImportanceModule {

	private List<ImportanceScorer> scorers;
	
	public ImportanceModule(List<Document> docs){
		scorers = new ArrayList<ImportanceScorer>();
		
		// TF IDF Importance Calculator
		TfIDFCalculator is1 = TfIDFCalculator.getInstance();
		is1.setWeightage(1.0);
		is1.initialize(docs);
		scorers.add(is1);
		
		// Sentence Length Calculator
		SentenceLengthCalculator is2 = SentenceLengthCalculator.getInstance();
		is2.setWeightage(1.0);
		is2.initialize(docs);
		scorers.add(is2);
		
		// Num of Words Above Threshold
		/*TopKImpWordsCalculator is3 = new TopKImpWordsCalculator();
		is3.setWeightage(1.0);
		is3.initialize(docs);
		is3.setTfIDFCalculator((TfIDFCalculator) is1);
		scorers.add(is3);*/
		
		// Num of Named Entities present
		/*NERImportanceCalculator is4 = NERImportanceCalculator.getInstance();
		is4.setWeightage(1.0);
		is4.initialize(docs);
		scorers.add(is4);*/
		
		// Position of the sentence calculator
		SentencePosCalculator is5 = new SentencePosCalculator();
		is5.setWeightage(1.0);
		is5.initialize(docs);
		scorers.add(is5);
		
		//Number of literals (number)
		NumLiteralsCalculator is6 = new NumLiteralsCalculator();
		is6.setWeightage(1.0);
		is6.initialize(docs);
		scorers.add(is6);
		
		//Uppercase calculator
		UpperCaseCalculator is7 = new UpperCaseCalculator();
		is7.setWeightage(1.0);
		is7.initialize(docs);
		scorers.add(is7);
		
		//Nouns Calculator
		/*NounsCalculator is8 = new NounsCalculator();
		is8.clearAll();	//first time base pos is called - see if it can be done in a better way!
		is8.initialize(docs);
		is8.setWeightage(1.0);
		scorers.add(is8);*/
		
		//Verbs Calculator
		/*VerbsCalculator is9 = new VerbsCalculator();
		is9.initialize(docs);
		is9.setWeightage(1.0);
		scorers.add(is9);*/
		
		//Adjectives Calculator
		/*AdjectivesCalculator is10 = new AdjectivesCalculator();
		is10.initialize(docs);
		is10.setWeightage(1.0);
		scorers.add(is10);*/
	}
	
	public void setValues(List<Document> docs) {
		for(Document doc : docs){
			for(Sentence s : doc.getSentences()){
				s.setScore(getSentenceScore(doc, s));
			}
		}
	}
	
	public double getSentenceScore(Document doc, Sentence s){
		double hyp = -0.839757;
		for(ImportanceScorer is : this.scorers){
			hyp += is.getImportanceScore(doc, s) * is.getWeightage();
		}
		
		return hyp;
	}

	public void setWeightsForImpScorers(List<Double> weights){
		for(int i=0; i < weights.size() && i < this.scorers.size(); i++){
			this.scorers.get(i).setWeightage(weights.get(i));
		}
	}
}
