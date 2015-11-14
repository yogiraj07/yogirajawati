package com.searchengine.BM25Algorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

//BM25 : Calculates BM25 score for each query in the query.txt and returns MAX_NUMBER_OF_RESULT results.
//       The output is stored in "output.txt" file
public class BM25 {

	static final double K1 =1.2;  //K1,B,K2 are the constants for the BM25 Algorithm
	static final double B =0.75;
	static final double K2 =100;
	//index stores the inverted index, populating from the file given as an argument to the program
	static HashMap<String, HashMap<String,Integer>> index = new HashMap<String, HashMap<String,Integer>>();
	                                                       
    static HashMap<String,Integer> docIdToken = new HashMap<String,Integer>(); //stores number of valid 
                                                                              // tokens against document id
    static TreeMap<Integer, HashMap<String, Integer>> queryIdMap = new TreeMap<Integer, HashMap<String, Integer>>();
                                                                   //queryIdMap stores query id and the query
    static HashMap<Integer,Double> documentScoreMap= new HashMap<Integer, Double>(); //stores Document and its 
                                                               // BM25 score for each query entered by the user
    static final int  MAX_NUMBER_OF_RESULT=100; //top rank documents to be displayed
    static double averageDocLength; // stores average document length for the entire collection
    static int N; //stores total number of documents in the collection
    static String SYSTEM_NAME="YOGIRAJ_SYSTEM"; //system name used in the displaying the result
  	public static void main(String[] args) {
	    calculateBM25(args[0],args[1],"docIdToken.txt",Integer.parseInt(args[2]));
	}
	private static void calculateBM25(String indexFileName, String queryFileName,
			String docIdTokenFileName, int maxNumberOfResult) {
		   populateIndexFromFile(indexFileName); //populates index data structure from given file name
		   populatedocIdTokenFromFile(docIdTokenFileName);//populates doc id and total token in that doc
		   populateQueryFromFile(queryFileName); // populates query map
		   Iterator<Integer> i =queryIdMap.keySet().iterator();
		 
		   while(i.hasNext())  // for each query in the "query.txt" file
		   {
			  
			   Integer queryId=i.next();    //take one query id at a time
			   HashMap<String, Integer>queryTerms=queryIdMap.get(queryId);  //retreive all query terms for queryId
			   scoreDocumentUsingBM25(queryId,queryTerms,maxNumberOfResult); //ranks documents using BM25 for all terms 
			                                                // in the given query and display top 100 ranked docs
			   queryTerms.clear();  //resetting for the next iteration
		   }
	}
	
	//scoreDocumentUsingBM25 implements the BM25 Algorithm
	private static void scoreDocumentUsingBM25(Integer queryId,
			HashMap<String, Integer> queryTerms, int maxNumberOfResult) {
		Iterator<String> i =docIdToken.keySet().iterator();    //iterate over all documents and find 
		double docScore;                //BM25 score for each document for given terms in the query
		while(i.hasNext()) //for each document
		{   docScore=0.0;  //stores BM25 score of all documents in the collection 
		                   //for the entered queryTerms 
			
		   String currentDocId=i.next(); 
			//calculate weights for the current document for each term in the query
			double K=0;
			K=populate_K(currentDocId); //populate K component per document
			Iterator<String> j =queryTerms.keySet().iterator();
			while(j.hasNext()) //for each term in the query
			{
				String q=j.next();       //extract one term in the query
				double qf=queryTerms.get(q); //retreive frequency of the term in the given query
				double f=getFrequencyOfTermInDocument(q,currentDocId); //returns number of times the term q
				                                                      // has occurred in currentDocId
				int n = numberOfDocumentsContainingTerm(q); //number of docs that contains term q of the query
				//following are the steps to implement BM25 algorithm
			    double x= (double)((K1 + 1) * f)/ (K + f);
			    double y= (double)((K2 + 1) * qf)/(K2 + qf);
			    double logComponent= (N-n+0.5)/(n+0.5);
			    docScore=docScore+(double)((x*y)*Math.log(logComponent)); //calculates score for query term q
			                                                   // for document currentDocId
			   	    
			}
			documentScoreMap.put(Integer.parseInt(currentDocId), docScore); // populate documentScoreMap id ,score 
			                           
		
		}
     
		writeToOutputFile(documentScoreMap,maxNumberOfResult,queryId);
		documentScoreMap.clear();   //clears the map for next query
		
	
	}
	private static void writeToOutputFile(
			HashMap<Integer, Double> documentScoreMap, int maxNumberOfResult, Integer queryId) {
		    getTopMaxNumberOfResult(documentScoreMap,maxNumberOfResult,queryId);
		
	}
	private static void getTopMaxNumberOfResult(
			HashMap<Integer, Double> map, int maxNumberOfResult, Integer queryId) {
		// TODO Auto-generated method stub
		Set<Entry<Integer, Double>> set = map.entrySet();
		List<Entry<Integer, Double>> list = new ArrayList<Entry<Integer, Double>>(
				set);
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Entry<Integer, Double> o1,
					Entry<Integer, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		//till this point the documentScoreMap is sorted by scores
		//Now we will write result to output file upto maxNumberOfResult
		try {
			int y=0;
			String content;
			for (Entry<Integer, Double> entry : list) { // displays top 50 page ranks
				content=queryId+" Q0 "+entry.getKey()+" "+ ++y+" "+entry.getValue()
				                     +" "+SYSTEM_NAME;
				System.out.println(content);
				//bw.write(content);
							
				if (y == maxNumberOfResult) {
					break;
				}
			} 
			content="";
	//		bw.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private static int numberOfDocumentsContainingTerm(String term) {
		if(index.containsKey(term))
		{
			return (index.get(term).keySet().size()); //returns number of documents that contains term
		}
		return 0;
	}
	private static int getFrequencyOfTermInDocument(String term,
			String docId) {
		if(index.get(term).containsKey(docId))  // if the index of the term contains doc id
		{
			return (index.get(term).get(docId)); //return the frequency of term in the docId
		}
		return 0;
	}
	private static double populate_K(String currentDocId) {
		
		double A= (double)(docIdToken.get(currentDocId)) / averageDocLength;
		double k=K1 * ( (1-B) + (B * A));
		return k;
	}
	private static void populateQueryFromFile(String queryFileName) { //extracts queries
	
		try {
			File f = new File(queryFileName);
			Scanner sc= new Scanner(f);
			while(sc.hasNext())
			{
				String str=sc.nextLine();
				String[] temp=str.split(" ");
				HashMap<String, Integer> tempQuery =new HashMap<String, Integer>();
				for (int i = 1; i < temp.length; i++) {
					if(tempQuery.containsKey(temp[i]))  //if the term in the query is already present
						                                //add 1 to the its occurrence in the query
					{
					  int count = tempQuery.get(temp[i]);
					  tempQuery.put(temp[i], count+1);
					}
					else
					{
						tempQuery.put(temp[i], 1);  //if the term in the query occurs for the first time
					}
				}
				queryIdMap.put(Integer.parseInt(temp[0]),tempQuery);
				
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private static void populatedocIdTokenFromFile(String docIdTokenFileName) {
		try {
			int sum=0;    //used to calculate total length (words) for all documents in the collection
			File f = new File(docIdTokenFileName);
			Scanner sc= new Scanner(f);
			while(sc.hasNext())
			{
				String str=sc.nextLine();
				String[] temp=str.split(" ");
				docIdToken.put(temp[0], Integer.parseInt(temp[1])); // put id and the number of valid token in
				                                                    // that document
				sum=sum+Integer.parseInt(temp[1]);  //accumulates length of docs till current point
				str="";
				temp=null;
			}
			N=docIdToken.keySet().size();   //stores total document in the collection
			averageDocLength=(double)sum/N;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private static void populateIndexFromFile(String indexFileName) {
		try {
			File f = new File(indexFileName);
			Scanner sc= new Scanner(f);
			while(sc.hasNext())
			{
				String str=sc.nextLine();
				String []temp=str.split(" ");
				HashMap<String, Integer> tempPosting=new HashMap<String, Integer>();
				
				for (int i = 1; i <temp.length; i++) {
					tempPosting.put(temp[i], Integer.parseInt(temp[++i]));
				}
				index.put(temp[0], tempPosting);
				temp=null;
				tempPosting=null;
				str="";
				
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("Index file Not Found!!! Please Indexer Program first and then run BM25.");
		    System.exit(0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
