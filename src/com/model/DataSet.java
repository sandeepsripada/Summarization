package com.model;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.constants.IdProvider;
import com.interfaces.SimilarityScorer;
import com.score.importance.ImportanceModule;
import com.score.similarity.CosineSimilarityScorer;
import com.util.FilePathUtil;

public class DataSet
{
	Map<String, Topic> topicMap = new HashMap<String, Topic>();
	Map<Long, String> nameMap = new HashMap<Long, String>();
	SimilarityScorer simScorer = new CosineSimilarityScorer();;

	String EXT_SUMMARY = "sum";
	String EXT_SOURCE = "txt";

	public DataSet(){
		//do nothing
	}

	public DataSet(String dirPath)
	{
		long topicId=0;
		File path = new File(dirPath);
		List<File> files = getFiles(path);
		Topic t = null;
		Summary s = null;
		for(int i=0; i<files.size(); i++)
		{
			System.out.println("Processing: " + files.get(i).getAbsolutePath());
			String extension = FilePathUtil.getExtension(files.get(i).getName());

			if(extension.equals(EXT_SUMMARY))
			{
				s = new Summary(files.get(i).getAbsolutePath(), IdProvider.getNextDocumentId(), true);
				t.addSummary(s);
			}
			else if(extension.equals(EXT_SOURCE))
			{
				topicId = IdProvider.getNextTopicId();
				t = new Topic(files.get(i).getAbsolutePath(), topicId);
			}

			String topic = FilePathUtil.getDocumentTopicId(files.get(i).getName());
			topicMap.put(topic, t);
			nameMap.put(topicId, topic);
		}
	}

	public List<Topic> getTopics()
	{
		List<Topic> res = new ArrayList<Topic>();
		for(Topic t:topicMap.values())
		{
			res.add(t);
		}
		return res;
	}

	private List<File> getFiles(File path)
	{
		List<File> result = new ArrayList<File>();
		List<String> extensions = new ArrayList<String>();
		extensions.add(EXT_SOURCE);
		extensions.add(EXT_SUMMARY);
		FilenameFilter filter = new Filter(extensions);
		String[] children = path.list(filter); 
		Arrays.sort(children, new Comparator<String>() 
		{
			//Custom sort field
			@Override
			public int compare(String o1, String o2) 
			{
				Pattern p = Pattern.compile("d(\\d+)(.*)");
				Matcher m1 = p.matcher(o1);
				Matcher m2 = p.matcher(o2);
				if(m1.find() && m2.find())
				{
					double d1 = Double.parseDouble(m1.group(1));
					double d2 = Double.parseDouble(m2.group(1));
					if(d1<d2)
						return -1;
					else if(d1==d2)
					{
						return -1*FilePathUtil.getExtension(o1).compareTo(FilePathUtil.getExtension(o2));
					}else return 1;
				}
				return 0;
			}
		});

		if (children != null) 
		{ 
			for (int i=0; i<children.length; i++) 
			{ 
				// Get filename of file or directory 
				String filename = children[i];
				filename = path.getAbsolutePath() + File.separator + filename;
				result.add(new File(filename));
			}
		}

		return result;
	}

	public int getTopicMapSize()
	{
		return topicMap.size();
	}

	public Topic getTopic(long key)
	{
		return topicMap.get(key);
	}

	class Filter implements FilenameFilter 
	{ 
		private List<String> extensions; 

		public Filter(List<String> extensions) 
		{
			this.extensions = extensions;
		}

		public boolean accept(File directory, String filename) 
		{
			boolean fileOK = false;
			for(int i=0; i<extensions.size(); i++)
			{
				fileOK = fileOK || filename.endsWith('.' + extensions.get(i));
			}
			return fileOK;
		}
	}

	public String getTopicName(long topicId)
	{
		return nameMap.get(topicId);
	}

	public Map<String, Topic> getTopicMap()
	{
		return topicMap;
	}
	
	public void calculateImportanceScores(List<Double> weights) 
	{
		for(Topic t : this.topicMap.values())
		{
			List<Document> totalSet = new ArrayList<Document>();
			totalSet.addAll(t.getDocuments());
			totalSet.addAll(t.getSummaries());
			ImportanceModule impModule = new ImportanceModule(totalSet);
			impModule.setWeightsForImpScorers(weights);
			impModule.setValues(totalSet);
		}
	}
}
