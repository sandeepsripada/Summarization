package com.constants;

import com.util.PorterStemmer;

public class Constants {
	
	//Class: Scorers
	public static double TFIDFThreshold = 0.0;
	public static final String trainingFilePrefix = "Train_";
	
	//Class: SentenceProcessor
	public static final boolean runLSP = false;
	
	//Class: Topic
	public static final boolean isDUC2002 = false;
	public static final int DOCUMENTS_PER_TOPIC = 10;
	
	//Class: StackDecoder
	public static final int SUMMARY_LENGTH = 100;
	public static final int MIN_SENTENCE_LENGTH=6;
	public static final double SENTENCE_OVERLAP_THRESHOLD = 0.5;	//cos-sim (0.5 - normal, 0.4-train)
	
	//Generic
	public static final PorterStemmer porterStemmer = new PorterStemmer();
}
