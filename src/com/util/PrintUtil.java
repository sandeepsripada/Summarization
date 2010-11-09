package com.util;

import java.util.List;

import com.model.Document;
import com.model.Sentence;

/**
 * 
 * @author Sandeep
 *
 */
public class PrintUtil
{
	public static void printDocument(Document d)
	{
		for(Sentence s : d.getSentences())
		{
			System.out.println("Sentence position: " + s.getPosition());
			System.out.println("Ref sentence: ");
			System.out.println(s.getRefSentence());
			
			List<String> content = s.getContent();
			for(int i=0; i<content.size(); i++)
			{
				if(i!=content.size()-1)
					System.out.print(content.get(i)+", ");
				else
					System.out.print(content.get(i));
			}
		}
	}
}
