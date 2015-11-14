package com.searchengine.invertedindex;

//Indexer : Generates inverted index and stores in index file

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Indexer {
	static HashMap<String, HashMap<String,Integer>> index = new HashMap<String, HashMap<String,Integer>>();
    static HashMap<String,Integer> docIdToken = new HashMap<String,Integer>(); //stores number of valid 
                                                                              // tokens per document
	public static void main(String[] args) {

		populateIndex(args[0]); // generates index from file : TCCORPUS.TXT
		writeIndexToFile(args[1]); //store in index.out file 
		writeDocIdTokenToFile(); 
	}
	private static void populateIndex(String args1) { //reads stemmed document TCCORPUS.TXT and populates index
		File f = new File(args1); // point to stemmed words file
		try {
			Scanner sc = new Scanner(f);
			String str;
			String patternForWordMatch=".*[a-zA-Z]+.*";   //regex to identify valid words that contain at least
			                                              // 1 alphabet
			Pattern wordMatch=Pattern.compile(patternForWordMatch);
			String patternForDocID="# [0-9]+";         // regex to identify document number e.g: # 1
			Pattern docIdMatch=Pattern.compile(patternForDocID);
			String docId = null;   //stores document id for current iteration
			String[] data;
			while (sc.hasNextLine()) {
				str=sc.nextLine();   // take each line at a time
				if (docIdMatch.matcher(str).find())  //if the line is in the format of # 1
				{
					String[] temp=str.split(" ");
					docId=temp[1];    //storing docId for further processing
					docIdToken.put(docId, 0);  //add docId entry in the docIdToken hash map
				}
				data=str.split(" ");  //split line on spaces 
				for(String word:data)
				{
					if (wordMatch.matcher(word).find())  // if the string contains at least one alphabet then
					                                   // its valid word that needs to be indexed
					{
						 addToIndex(word,docId);
						 docIdToken.put(docId, docIdToken.get(docId)+1); //increment token count for the
						                              // docId
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void addToIndex(String word, String docId) {
     
		HashMap<String,Integer> posList=index.get(word);
		if(posList != null)  //if the word is already indexed, check if the docID is already
                              // present in its Posting
        {
			Integer oldFrequencyForDocId=posList.get(docId); //get old frequency for docId 
			if(oldFrequencyForDocId != null)  
			{
				posList.put(docId, oldFrequencyForDocId + 1);
				index.put(word, posList);
				oldFrequencyForDocId=null;
			}
			else
			{
				posList.put(docId, 1);
				index.put(word,posList);
			}
        }
        else
        {   HashMap<String,Integer> newPosting=new HashMap<String, Integer>(); //else create new posting
                                                             //and add to posting list of index = "word"
            newPosting.put(docId, 1);
        	index.put(word,newPosting);
        	newPosting=null;
        }
		
	}

    private static void writeDocIdTokenToFile() {
		try {
			File f = new File("docIdToken.txt");
			if (!f.exists()) // if file is not created then create a new file
			{
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			Iterator<String> i= docIdToken.keySet().iterator();  //iterate over each docId
			String content;
			while(i.hasNext())
			{
				String id=i.next();
				int tokenCount=docIdToken.get(id);
				content=id+ " "+tokenCount+"\n";  //construct entire line that needs to be put in the file
				bw.write(content);
				content="";
			}
			bw.close();    //close buffer writer
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void writeIndexToFile(String args) {
		try {
			File f = new File(args);
			if (!f.exists()) // if file is not created then create a new file
			{
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			Iterator<String> i= index.keySet().iterator();  //iterate over index
			while (i.hasNext())
			{   String content;
				String indexKey=i.next();  // get each word
				content=indexKey;
				HashMap<String,Integer> postingsForIndexKey=index.get(indexKey); //retreive all postings 
				                                       //for given word
				Iterator<String> j=postingsForIndexKey.keySet().iterator();
				while(j.hasNext())
				{
					String docId=j.next();  //retreive docID for each posting 
					int frequencyOfDocId=postingsForIndexKey.get(docId);
					content=content+ " "+docId+ " "+ frequencyOfDocId;
				} 
				content=content+"\n";
				bw.write(content);
				content="";
		}
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
