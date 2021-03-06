import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CreatePartitions {

	public static void main(String[] args) {
		String input = "summary.txt";
		String ouput = "r";
		int numReducers = 2;
		createPartitions(input,ouput,numReducers);
		
	}

	private static void createPartitions(String input, String output, int numReducers) {
		String filePath = input;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			System.out.println("Fetching from: " + filePath);
			String ipLine;
			while (null != (ipLine = reader.readLine())) {
			       int i= getReducer(ipLine,numReducers);
			       System.out.println("File : "+ipLine + "  Reducer : "+i);
			       BufferedWriter wr = new BufferedWriter(new FileWriter(output+i+"", true));
			       wr.write(ipLine+"\n");
			       wr.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static int getReducer(String ipLine,int numReducers) {
		// TODO Auto-generated method stub
		return getOptimumHash((Math.abs(ipLine.hashCode())))%numReducers;
	}
	static int getOptimumHash(int hash) {
		hash ^= (hash >>> 20) ^ (hash >>> 12);
		return hash ^ (hash >>> 7) ^ (hash >>> 4);

	}
}
