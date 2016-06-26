import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Solution {
	static HashMap<String, ArrayList<String>> listOfFiles = new HashMap<>();
	static HashSet<String> filesProcessed = new HashSet<>();
	
	
	 public static void main(String[] args) throws IOException {
		int numReducers = 2;
		generateOutputForReducers("output", "FinalOutput", numReducers);
		

	}

	private static void generateOutputForReducers(String inputPath, String outputFolder, int numReducers)
			throws IOException {
		// TODO Auto-generated method stub
       
        File[] f = new File(inputPath).listFiles();
		iterateOutputFolder(f,new File(inputPath));

	}

	public static void iterateOutputFolder(File[] f, File parentDirectory) {
		// TODO Auto-generated method stub
		
		for ( File fileEntry : f) {
			
			if(fileEntry.isDirectory())
			{
                  iterateOutputFolder(fileEntry.listFiles(), fileEntry);
			}
			
			System.out.println(parentDirectory.getName());
			String key =parentDirectory.getName(); //get key
			
	        
			if(listOfFiles.containsKey(key))
			{
				listOfFiles.get(key).add(fileEntry.getName());
			}
			else
			{
				ArrayList<String> l = new ArrayList<>();
				l.add(fileEntry.getName());
				listOfFiles.put(key, l);
			}

		}
		System.out.println(listOfFiles);
		
	}
}

//String filePath = inputPath + "/" + fileEntry.getName();
//BufferedReader reader = new BufferedReader(new FileReader(filePath));
//System.out.println("Fetching from: " + filePath);
//String ipLine;
//while (null != (ipLine = reader.readLine())) {
//
//}
