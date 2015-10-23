package com.pagerank.perplexity;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CalculatePerplexity {  //calculates perplexity per iteration of page rank algorithm

	public static Double perplexity (HashMap<String,Double> pR)
	{
		Double entropy=calculateShannonEntropy(pR); //returns shannon entropy for page ranks
		
		return Math.pow(2, entropy);  // perplexity= 2 raise to shannon entropy
	}
	public static Double calculateShannonEntropy(HashMap<String,Double> pR) {  // calculate the entropy
		  Double result = 0.0;
		  Iterator<String> k=pR.keySet().iterator();
		  Double rank=0.0;
		  String key;
		  while(k.hasNext()) { //iterate each page
			    key=k.next();
			    rank=pR.get(key); //extracts it page rank
		 	    result =result- rank * (Math.log(rank) / Math.log(2)); // formula = -rank * log(rank)
		  }
		  return result;
		}
}
