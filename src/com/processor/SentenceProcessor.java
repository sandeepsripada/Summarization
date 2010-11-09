package com.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.constants.Constants;
import com.constants.IdProvider;
import com.model.Document;
import com.model.Sentence;

public class SentenceProcessor
{
	final static String SENTENCE_MARKER = "Sentence:";
	final static String WORD_MARKER = "\tS:";
	final static String PUNCTUATION = "\"'`!?&_/\\);][<>~@-({}:";
	final static String TERMINAL = "#";
	
	private static File getLSPPath(String filename)
	{
		String tempDir = "del";
		File path = new File(tempDir);
		path.mkdir();
		
		try
		{
			String command = "perl external/runLSP.pl " + filename + " " + tempDir;
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
		}
		catch (IOException ioe)
		{
			System.out.println("SentenceProcessor:getSentences:: Problem executing the command.");
		}
		catch (InterruptedException ie)
		{
			System.out.println("SentenceProcessor:getSentences:: Problem executing the command.");
		}
		
		return path;
	}
	
	public static List<Document> getDocuments(String filename, Map<Long, Sentence> sentenceMap)
	{
		File path = null;
		File file = null;
		if(Constants.runLSP)
		{
			path = getLSPPath(filename);
			file = path.listFiles()[0];
		}
		else
		{
			path = new File(getProcessedPath(filename));
			file = path;
		}
		List<Document> documents = new ArrayList<Document>();
		List<Sentence> sentences = new ArrayList<Sentence>();
		
		long lineNum=0;
		long documentId = IdProvider.getNextDocumentId();
		BufferedReader reader = null; 
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			boolean noSpace = false;
			// repeat until all lines are read
			text = reader.readLine();
			while(text!=null && text.equalsIgnoreCase(SENTENCE_MARKER))
			{
				//Sentence starts
				int senLength=0;
				StringBuilder sb = new StringBuilder();
				List<String> content = new ArrayList<String>();
				lineNum++;
				while (text!=null && (text = reader.readLine())!=null && !text.equalsIgnoreCase(SENTENCE_MARKER))
				{
					if(!text.startsWith(WORD_MARKER))
						continue;
					String word = text.split("S: ")[1];
					//filter spurious chars
					//Not so thrilled about this!
					if(filterPunctuation(word))
						continue;
					else
					{
						if(word.equals(",") || word.equals("."))
							noSpace=true;
					}
					
					if(word.equals(TERMINAL))
					{
						//Document ends
						Document d = new Document(sentences, filename, documentId);
						documents.add(d);
						lineNum=0;
						sentences = new ArrayList<Sentence>();
						documentId = IdProvider.getNextDocumentId();
						continue;
					}
					String[] split=null;
					if(word.contains("-"))
					{
						split=word.split("-");
						senLength++;
						for(int i=0; i<split.length; i++)
						{
							String cWord=split[i].replaceAll("\\W", "");
							if(cWord.length()>0)
								content.add(cWord);
						}
					}
					else
					{
						String cWord=word.replaceAll("\\W", "");
						if(cWord.length()>0)
						{
							senLength++;
							content.add(cWord);
						}
					}
					
					if(sb.length() == 0)
						sb.append(word);
					else
					{
						if(noSpace)
						{
							noSpace = false;
							sb.append(word);
						}
						else
							sb.append(" " + word);
					}
				}
				
				if(content.size()>0 && sb.toString().length()>0)
				{
					long id = IdProvider.getNextSentenceId();
					Sentence s = new Sentence(id, senLength, content, sb.toString(), lineNum, documentId);
					sentences.add(s);
					sentenceMap.put(id, s);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		//cleanTempDirectory(path);
		return documents;
	}
	
	private static String getProcessedPath(String filename)
	{
		int index = filename.lastIndexOf(File.separatorChar);
		String file = filename.substring(index+1, filename.length());
		file="."+File.separatorChar+"del"+File.separatorChar+file;
		return file;
	}

	public static List<Sentence> getSentences(String filename)
	{
		File path = null;
		File file = null;
		if(Constants.runLSP)
		{
			path = getLSPPath(filename);
			file = path.listFiles()[0];
		}
		else
		{
			path = new File(getProcessedPath(filename));
			file = path;
		}
		
		List<Sentence> sentences = new ArrayList<Sentence>();
		long documentId = IdProvider.getNextDocumentId();
		long lineNum=0;
		BufferedReader reader = null; 
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			boolean noSpace = false;
			// repeat until all lines are read
			text = reader.readLine();
			while(text!=null && text.equalsIgnoreCase(SENTENCE_MARKER))
			{
				//Sentence starts
				int senLength=0;
				StringBuilder sb = new StringBuilder();
				List<String> content = new ArrayList<String>();
				lineNum++;
				while ((text = reader.readLine())!=null && !text.equalsIgnoreCase(SENTENCE_MARKER))
				{
					if(!text.startsWith(WORD_MARKER))
						continue;
					String word = text.split("S: ")[1];
					//Not so thrilled about this!
					if(filterPunctuation(word))
						continue;
					else
					{
						if(word.equals(",") || word.equals("."))
							noSpace=true;
					}
					
					String[] split=null;
					if(word.contains("-"))
					{
						split=word.split("-");
						senLength++;
						for(int i=0; i<split.length; i++)
						{
							String cWord=split[i].replaceAll("\\W", "");
							if(cWord.length()>0)
								content.add(cWord);
						}
					}
					else
					{
						String cWord=word.replaceAll("\\W", "");
						if(cWord.length()>0)
						{
							senLength++;
							content.add(cWord);
						}
					}
					
					if(sb.length() == 0)
						sb.append(word);
					else
					{
						if(noSpace)
						{
							noSpace = false;
							sb.append(word);
						}
						else
							sb.append(" " + word);
					}
						
				}

				Sentence s = new Sentence(IdProvider.getNextSentenceId(), senLength, content, sb.toString(), lineNum, documentId);
				s.setPositiveSample(true);
				s.setPartOfHumanSummary(true);
				sentences.add(s);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} 

		//cleanTempDirectory(path);
		return sentences;
	}

	private static boolean filterPunctuation(String word)
	{
		if(PUNCTUATION.contains(word))
			return true;
		else
			return false;
	}

	@SuppressWarnings("unused")
	private static void cleanTempDirectory(File path)
	{
		File[] files = path.listFiles();
		for(int i=0; i<files.length; i++)
		{
			if(files[i].isDirectory())
			{
				cleanTempDirectory(files[i]);
			}
			else 
			{
				files[i].delete();
			}
		}
		path.delete();
	}
}
