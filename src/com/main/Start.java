package com.main;

import java.util.ArrayList;
import java.util.List;

import com.decoding.stackdecoder.StackDecoder;
import com.model.DataSet;
import com.model.Topic;
import com.util.StopWords;

public class Start
{
	private static String STOP_WORDS_FILE_PATH = "StopWords.txt";
	
	public static void main(String[] args) throws Exception
	{
		if(args.length!=1)
		{
			usage();
			System.exit(-1);
		}
		StopWords.initializeStopWords(STOP_WORDS_FILE_PATH);
		
		//Test dataset - Stack Decoder
		DataSet testDataSet = new DataSet(args[0]);
		testDataSet.calculateImportanceScores(getWeights());
		System.out.println("Start:main:: Running stack decoder .. ");
		long in = System.currentTimeMillis();
		for(Topic t:testDataSet.getTopics())
		{
			StackDecoder sd = new StackDecoder(t.getDocuments());
			sd.runStackDecoder();
			sd.printStack(101);
			String path = "summaries/" + testDataSet.getTopicName(t.getTopicId()).toUpperCase()+".M.100.T.S2";
			sd.dumpBestSummary(path);
		}
		long out = System.currentTimeMillis();
		System.out.println("Start:main:: Time taken by Stack decoder (s): " + ((out-in)/1000));
		
	}

	private static void usage()
	{
		System.out.println("Usage: java <main> <path to data>");
		System.out.println("Note: 'data' folder contains the sample input files.");
	}

	public static List<Double> getWeights(){
		//Obtained from training
		List<Double> res = new ArrayList<Double>();
		//Note: Set theta_0 in importance module
		//TFIDFSum,SentLength,SentPost,NumLiteralsCalculator,UpperCaseCalculator
		res.add(0.197971);res.add(0.283136);res.add(-0.300287);res.add(0.1664);res.add(0.160681);
		return res;
	}
}
