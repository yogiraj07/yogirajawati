package ir.classifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * The nbtrain class which generates the Model from the given Training Data.
 * 
 */
public class nbtrain 
{
	// Static data structures for storing Bag of Words
	public static HashMap<String, Double> posWords = new HashMap<String, Double>();
	public static HashMap<String, Double> negWords = new HashMap<String, Double>();
	public static HashSet<String> vocab = new HashSet<String>();
	public static int posCounter = 0;
	public static int negCounter = 0;
	
	// CONSTANTS
	public static int VOCAB_THRESHOLD = 5;
	
	/**
	 * Load the Positive Directory words into HashMap
	 * @param posTrainPath
	 * @throws IOException
	 */
	public static int loadPosWords(File posTrainPath) throws IOException
	{
		int fileCount = 0;
		BufferedReader reader = null;
		System.out.println(posTrainPath);
		
		// Read all the files from Positive Training folder.
		for (final File fileEntry : posTrainPath.listFiles()) 
		{
	        if (fileEntry.isDirectory()) 
	        {
	        	System.out.println("In subfolder..");
	        	loadPosWords(fileEntry);
	        } 
	        else 
	        {
	        	fileCount++;
	            reader = new BufferedReader(new FileReader(fileEntry));
	            
	            String line = null;
	            while( (line = reader.readLine())!= null )
	            {
	                // s+ means any number of white spaces between tokens
	                String [] words = line.split("\\s+");
	                
	                for(String word : words)
	                {
	                	if(null != posWords.get(word))
                		{
                			posWords.put(word, (double)(posWords.get(word) + 1));
                		}
                		else
                		{
                			posWords.put(word, (double)1);
                		}           	
	                }	                	                
	            }	 
	            reader.close();
	        }
	    }	
		return fileCount;
	}
	
	/**
	 * Load the Negative Directory words into HashMap
	 * @param negTrainPath
	 * @throws IOException
	 */
	public static int loadNegWords(File negTrainPath) throws IOException 
	{
		int fileCount = 0;
		BufferedReader reader = null;
		System.out.println(negTrainPath);
		// Read each file in this Negative training files.
		for (final File fileEntry : negTrainPath.listFiles()) 
		{
	        if (fileEntry.isDirectory()) 
	        {
	        	System.out.println("In subfolder..");
	        	loadNegWords(fileEntry);
	        } 
	        else 
	        {
	            fileCount++;
	            reader = new BufferedReader(new FileReader(fileEntry));
	            
	            String line = null;
	            while( (line = reader.readLine())!= null )
	            {
	                // s+ means any number of white spaces between tokens
	                String [] words = line.split("\\s+");
	                
	                for(String word : words)
	                {              	
	                	if(null != negWords.get(word))
                		{
                			negWords.put(word, (double)(negWords.get(word) + 1));
                		}
                		else
                		{
                			negWords.put(word, (double)1);
                		}
	                	
	                }	                	                
	            }	   
	            reader.close();
	        }
	    }	
		return fileCount;
	}
	
	/**
	 * Stores the total frequency of Positive terms and Negative terms.
	 */
	public static void getCounts()
	{
		Iterator<String> iterator = posWords.keySet().iterator();
		while(iterator.hasNext())
		{
			String word = iterator.next();
			posCounter += posWords.get(word);
		}
		
		iterator = negWords.keySet().iterator();
		// Iterate Negative words and replace frequency with tk
		while(iterator.hasNext())
		{
			String word = iterator.next();
			negCounter += negWords.get(word);
		}	
	}
	
	/**
	 * Clean the Positive and Negative words whose frequencies are below 5.
	 */
	public static void clearVocabByThreshold()
	{
		int counter = 0;
		Iterator<String> iterator = vocab.iterator();
		
		while(iterator.hasNext())
		{
			counter = 0;
			String word = iterator.next();
			if(posWords.containsKey(word))
			{
				counter += posWords.get(word);
			}
			if(negWords.containsKey(word))
			{
				counter += negWords.get(word);
			}
			
			// If below the THRESHOLD frequency, drop the Words.
			if(counter < VOCAB_THRESHOLD)
			{
				if(posWords.containsKey(word))
				{
					posWords.remove(word);
				}
				
				if(negWords.containsKey(word))
				{
					negWords.remove(word);
				}
				iterator.remove();
			} else 
			{
				// Else, smooth the Probability distribution by evenly distributing
				// the terms across both the HashMaps.
				if(!posWords.containsKey(word))
				{
					posWords.put(word, 0.0);
				}
				
				if(!negWords.containsKey(word))
				{
					negWords.put(word, 0.0);
				}
			}
		}
	}
		
	/**
	 * Load the Positive and Negative terms with Probabilities.
	 */
	public static void loadProbability()
	{
		int vocabLen = vocab.size();
		
		Iterator<String> iterator = posWords.keySet().iterator();
		// Iterate Positive words and store the tk
		while(iterator.hasNext())
		{
			String word = iterator.next();
			double tk = (double)((posWords.get(word) + 1)/(posCounter + vocabLen));
			posWords.put(word, tk);
		}
		
		iterator = negWords.keySet().iterator();
		// Iterate Negative words and store the tk
		while(iterator.hasNext())
		{
			String word = iterator.next();
			double tk = (double)((negWords.get(word) + 1)/(negCounter + vocabLen));
			negWords.put(word, tk);
		}			
	}
	
	/**
	 * Write the Model file.
	 * 
	 * @param pPrior
	 * @param nPrior
	 * @param modelFilePath
	 * @throws IOException
	 */
	public static void writeModel(double pPrior, double nPrior, String modelFilePath) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(modelFilePath));
		double posProb = 0;
		double negProb = 0;
		
		// Write the first line.
		writer.write(Math.log(pPrior) + " " + Math.log(nPrior) + "\n");
		
		Iterator<String> words = vocab.iterator();
		
		while(words.hasNext())
		{
			String word = words.next();
						
			// The Weight is calculated as (log) probability of term
			if(posWords.get(word) != null)
			{
				posProb = Math.log(posWords.get(word));
			}
			else
			{
				posProb = 0;
			}
			if(negWords.get(word) != null)
			{
				negProb = Math.log(negWords.get(word));
			}
			else
			{
				negProb = 0;
			}
					
			writer.write(word 
					+ " " 
					+ posProb
					+ " " 
					+ negProb + "\n");
		}
		writer.close();
	}
		
	/**
	 * Generate the list of the top 20 terms with the (log) ratio of positive to negative weight.
	 * and also ratio of negative to positive weight.
	 */
	public static void generateTop20()
	{
		HashMap<String, Double> pWeight = new HashMap<String, Double>();
		HashMap<String, Double> nWeight = new HashMap<String, Double>();
		
		Iterator<String> iterator = posWords.keySet().iterator();
		while(iterator.hasNext())
		{
			String word = iterator.next();
			
			if(negWords.get(word) != 0)
			{
				pWeight.put(word, Math.log(posWords.get(word)/negWords.get(word)));
			}
			
			if(posWords.get(word) != 0)
			{
				nWeight.put(word, Math.log(negWords.get(word)/posWords.get(word)));
			}			
		}
		
		TreeMap<String,Double> sortedPWeight = SortByValue(pWeight);
		TreeMap<String,Double> sortedNWeight = SortByValue(nWeight);
		
		// Print the Top 20 Positive/Negative weight
		System.out.println("Top 20 terms with the highest (log) ratio of positive to negative weight:");
		
		Iterator<String> pIterator = sortedPWeight.keySet().iterator();
		int counter = 0;
		while(pIterator.hasNext() && counter < 20)
		{
				System.out.println(pIterator.next());
				counter++;
		}
		counter = 0;
		// Print the Top 20 Positive/Negative weight
		System.out.println("Top 20 terms with the highest (log) ratio of negative to positive weight.");
		
		Iterator<String> nIterator = sortedNWeight.keySet().iterator();
		while(nIterator.hasNext() && counter < 20)
		{
				System.out.println(nIterator.next());	
				counter++;
		}		
	}
	
	/**
	 * 
	 * Returns a Sorted TreeMap of the given HashMap.
	 * Used to sort the PageRank values.
	 */
	public static TreeMap<String, Double> SortByValue (HashMap<String, Double> map) 
	{
		ValueComparator vc =  new ValueComparator(map);
		TreeMap<String,Double> sortedMap = new TreeMap<String,Double>(vc);
		sortedMap.putAll(map);
		return sortedMap;
	}
	
	/**
	 * The Main function which will read the command line arguments
	 * process the Training data.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException 
	{
		if(args.length != 2)
		{			
			System.err.println("Please enter the mandatory inputs: <training-directory> <model-file>");
		}
		else
		{
			String trainingDirPath = args[0];
			String modelFilePath = args[1];
			int pFileCount = 0;
			int nFileCount = 0;
			double pPrior = 0;
			double nPrior = 0;
			
			// Load the Positive and Negative words.
			pFileCount = loadPosWords(new File(trainingDirPath+"\\pos\\"));
			nFileCount = loadNegWords(new File(trainingDirPath+"\\neg\\"));
			
			// Load Positive and Negative priori
			pPrior = (double) pFileCount / (pFileCount + nFileCount);
			nPrior = (double) nFileCount / (pFileCount + nFileCount);
			
			// Load all the words into Vocabulary
			vocab.addAll(posWords.keySet());
			vocab.addAll(negWords.keySet());
			
			// Remove the less frequent words and distribute the weights evenly.
			clearVocabByThreshold();
			
			// REMOVING THE BLANK TERMS
			vocab.remove("");
			posWords.remove("");
			negWords.remove("");
			
			// Store the frequencies of Positive Terms and Negative Terms
			getCounts();
			
			// Load probabilities
			loadProbability();
			
			// Write the Model output
			writeModel(pPrior, nPrior, modelFilePath);
			
			// List the Top 20 terms
			generateTop20();			
		}	
	}
}

/**
 * 
 * @author Ashish Kalbhor
 * Comparator class to sort PageRank values.
 *
 */
class ValueComparator implements Comparator<String> 
{
    Map<String, Double> map;

    public ValueComparator(Map<String, Double> base){
        this.map = base;
    }
 
    public int compare(String a, String b) {
        if (map.get(a) >= map.get(b)) {
            return -1;
        } else {
            return 1;
        } 
    }
}
