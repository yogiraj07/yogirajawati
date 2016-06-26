import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;


//Author: Yogiraj Awati , Ashish Kalbhor
public class ConfusionMatrix {
    //map populates data of output.txt which contains prediction data
	static HashMap<String, String> map = new HashMap<>();

public static void main(String[] args) {
	if(args.length!=2)
	{
		System.out.println("Please input <validationfilepath> : <output.txt>");
		System.exit(0);
	}
	String fileName = args[0]; //contains validationfilepath
	GZIPInputStream gzip = null;
	int truePositive = 0,falsePositive=0,trueNegative=0,falseNegative=0;
	try {
		
		//read output.txt
		String outputFile = args[1];
		BufferedReader outputReader = null;
		String l = "";
		outputReader = new BufferedReader(new FileReader(outputFile));
		//populate hashmap
		while ((l = outputReader.readLine()) != null)
		{
			String[] data = l.split(",");
			String k = data[0]; 
			String v = data[1];
			map.put(k, v);
		}
		
		//read 98Validate.csv.gz
		gzip = new GZIPInputStream(new FileInputStream(fileName));
		String separator = ",";
		BufferedReader br = new BufferedReader(new InputStreamReader(gzip));
		String line;
		int actualCounter=0;
		//calculate confusion matrix parameters 
		//iterate over each row of validation file
		while ((line = br.readLine()) != null)
		{
			String[] data = line.split(separator);
			String key = data[0];
			String value = data[1];
			actualCounter++;
			if(map.containsKey(key))
			{
				String predictionValue = (String) map.get(key);
				
				if(predictionValue.isEmpty() || predictionValue.equals("") || predictionValue == null)
						continue;
				
				if(value.equals(predictionValue))
				{
					
					if(predictionValue.equalsIgnoreCase("TRUE") && value.equalsIgnoreCase("TRUE"))
						{
							truePositive++;
						}
					else
						if(predictionValue.equalsIgnoreCase("FALSE") && value.equalsIgnoreCase("FALSE"))
							trueNegative++;
				}
				else
				{
					if(predictionValue.equalsIgnoreCase("TRUE") && value.equalsIgnoreCase("FALSE"))
						falsePositive++;
					else
						if(predictionValue.equalsIgnoreCase("FALSE") && value.equalsIgnoreCase("TRUE"))
							falseNegative++;
				}
			}
			else 
				continue;
			
		}
		System.out.println("truePositive: "+truePositive+" trueNegative: "+trueNegative+" falsePositive: "+falsePositive+" falseNegative: "+falseNegative);
		int totalMatchedRecords = truePositive+trueNegative+falsePositive+falseNegative;
		
		float accuracy = ((float)(truePositive+trueNegative)/totalMatchedRecords)*100;
		System.out.println("Accurracy : "+accuracy+"%");
		System.out.println("Validation Records : "+actualCounter);
		System.out.println("Prediction Records :  "+map.size());
		System.out.println("Matched Records : "+totalMatchedRecords);
		BufferedWriter matrixWrite = new BufferedWriter(new FileWriter("Confusion Matrix.txt"));
		matrixWrite.write("\ntruePositive: "+truePositive+" trueNegative: "+trueNegative+" falsePositive: "+falsePositive+" falseNegative: "+falseNegative);
		matrixWrite.write("\nAccuracy : "+accuracy+"%");
		matrixWrite.write("\nValidation Records : "+actualCounter);
		matrixWrite.write("\nPrediciton Records :  "+map.size());
		matrixWrite.write("\nMatched Records : "+totalMatchedRecords);
		matrixWrite.close();
		
		
	}
	catch(IOException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}
}
