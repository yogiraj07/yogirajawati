package ir.classifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;


/**
 * 
 * The nbtest class which performs the Text Classification based on given Model
 * and classifies the Documents into two classes:
 * <ul>
 * 	<li> Positive
 * 	<li> Negative
 * </ul>
 *
 */
public class nbtest
{
	// Static data structures for loading the Model
    static HashMap<String, WordProb> model = new HashMap<String, WordProb>(); 
    static double p_prior;
    static double n_prior;
    
	/**
	 *  Classify the given File and write the Positive and Negative weights in Prediction file.
	 * @param file
	 * @param predictionFile
	 * @throws IOException
	 */
	private static void classify(File file, File predictionFile) throws IOException 
	{
		int fileCount = 0;
		int count = 0;
		BufferedReader reader = null;
		BufferedWriter writer = new BufferedWriter(new FileWriter(predictionFile, true)); 
		Double posScore=0.0;
		Double negScore=0.0;
		
		// Iterate directory
		for (final File fileEntry : file.listFiles())
		{
			// If there is sub folder
			if (fileEntry.isDirectory()) 
	        {
	        	System.out.println("In folder : "+fileEntry.getName());
	        	classify(fileEntry,predictionFile);
	        } 
            else 
            {
				// This part of the code processes each document.
				posScore = 0.0;
				negScore = 0.0;
				fileCount++;
				reader = new BufferedReader(new FileReader(fileEntry));
				String line = null;
         		while ((line = reader.readLine()) != null) 
         		{
					String[] words = line.split(" ");
					for (String word : words) 
					{
						// If the word is available in the model
						if (model.containsKey(word)) 
						{
							// Calculate positive and negative weights
							posScore += model.get(word).getPosProb();
							negScore += model.get(word).getNegProb();
						}
					}
				}
         		
				reader.close();
				// Add prior values
				posScore += p_prior;
				negScore += n_prior;
				
				writer.write(fileEntry.getName() 
						+ " :- " 
						+ "Positive Review Score : " 
						+ posScore 
						+ "    Negative Review Score : "
						+ negScore 
						+ "\n");
				
				// If Positive weight is greater than Negative weight
				if (posScore > negScore) 
				{
					count++;
				}
				// End of files iteration in the given sub - folder
			}
		}
		
		writer.close();
		// Display the Classification
		if(count!=0)
		{
			System.out.println("Positive Review : " 
						+ count 
						+ " Negative Review : " 
						+ (fileCount-count)
						+ " Total Files : "
						+ fileCount);
		}
		
	}
	
	/**
	 * Load the Model.txt file into a HashMap
	 * @param file
	 * @throws IOException
	 */
	private static void loadModel(File file) throws IOException
	{
		Scanner scanner = new Scanner(file);	
		String line;
		String data[];
		WordProb pb;
		
		line = scanner.nextLine();
		data=line.split(" ");
		
		// Assign the Prior probabilitites.
		p_prior=Double.parseDouble(data[0]);
		n_prior=Double.parseDouble(data[1]);
			
		while (scanner.hasNext())
		{
			line = scanner.nextLine();
			data = line.split(" ");
			pb = new WordProb(Double.parseDouble(data[1]),Double.parseDouble(data[2]));
			
			// Read the Model.txt and write each line into the Model HashMap.
			model.put(data[0], pb);
		    pb = null;			
		}
		scanner.close();
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
		if(args.length != 3)
		{			
			System.err.println("Please enter the mandatory inputs: <training-directory> <model-file>");
		}
		else
		{
			String modelFilePath = args[0];  //Model path
			String testDirPath = args[1];   // Test directory path
			String predictionFilePath = args[2]; // Prediction path
			
			// Populates model data structure from model.txt
			loadModel(new File(modelFilePath)); 
			
			// Classify test/dev directory
			classify(new File(testDirPath),new File(predictionFilePath));
		}

	}

} 

/**
 * 
 * WordProb class is a workaround to store the
 * Positive and Negative probabilities of the Word.
 *
 */
class WordProb
{
	//Used to store positive and negative probability
	double posProb;
	double negProb;
	
	public WordProb()
	{
		posProb=0.0;
		negProb=0.0;
	}
	
	public WordProb(double posProb, double negProb) 
	{
		super();
		this.posProb = posProb;
		this.negProb = negProb;
	}
	
	public double getPosProb() 
	{
		return posProb;
	}
	
	public void setPosProb(double posProb) 
	{
		this.posProb = posProb;
	}
	
	public double getNegProb() 
	{
		return negProb;
	}
	
	public void setNegProb(double negProb) 
	{
		this.negProb = negProb;
	}
	
	/**
	 * String representation of WordProb.
	 */
	@Override
	public String toString() 
	{
		return " posProb : "+posProb+" negProb: "+negProb+"\n";
	}
}
