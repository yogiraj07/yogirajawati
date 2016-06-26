
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
/**
 * ThreadedAnalysis Class that reads multiple files in parallel.
 * 
 * @author Ashish Kalbhor, Yogiraj Awati
 *
 */

public class ThreadedAnalysis 
{
	
	// Global accumulator of all the Avg Ticket Prices per Carrier.
	public static HashMap<String, ArrayList<Double>> totalTicketPrices = new HashMap<String, ArrayList<Double>>();
	public static HashSet<String> globalValidCarrierList = new HashSet<String>();

	public static String LOCAL_PATH = "/home/hduser/mr/timeoutput/";
	/**
	 * Main method.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		int processing = 0;
		String dirPath = "";
		String type = "";
		String mode = "";
		FileWriter fw = null;
		
		if(args.length != 3)
		{
			System.out.println("Usage <mode> <type> <-input=DIR>");
		}
		else
		{
			if(args[0].equals("-p"))
			{
				processing = 1;
				dirPath = args[2];
				type = args[1];
				mode = "Parallel";
				fw = new FileWriter(new File(LOCAL_PATH +"parallelOutput.csv"),true);
				System.out.println("Parallel Mode");
			}
			else if(args[0].equals("-s"))
			{
				processing = 2;
				dirPath = args[2];
				type = args[1];
				mode = "Sequential";
				fw = new FileWriter(new File(LOCAL_PATH+"sequentialOutput.csv"),true);
				System.out.println("Sequential Mode");
			}
			else
			{
				System.out.println("Usage <mode> <type> <-input=DIR>");
				processing = 0;
			}
		}
		
		long start = System.currentTimeMillis();
		
		if (processing > 0) 
		{
			ArrayList<String> paths = new ArrayList<String>();
			
			 File dir = new File(dirPath);
			  File[] directoryListing = dir.listFiles();
			  if (directoryListing != null) {
			    for (File child : directoryListing) {
			      paths.add(child.toString());
			    }
			  } 
			
//			Files.walk(Paths.get(dirPath)).forEach(filePath -> {
//				if (Files.isRegularFile(filePath)) {
//					paths.add(filePath.toString());
//				}
//			});
			int i = 0;
			MyThread t[] = new MyThread[paths.size()];
			
			// Run in parallel mode
			if (processing == 1) 
			{
				for (String path : paths) {
					t[i] = new MyThread(path, type);
					t[i].start();
					i++;
				}
				i--;
				while (i >= 0) {
					t[i].join();
					i--;
				}
			}
			else if(processing == 2)
			{
				// Run in sequential mode
				for (String path : paths) 
				{
					t[i] = new MyThread(path, type);
					t[i].start();
					t[i].join();
					i++;
				}
			}
			// Combine all the Ticket Prices.
			totalTicketPrices = t[0].ticketPrice;
			for (int ii = 1; ii < paths.size(); ii++) 
			{
				for (String carrier : t[ii].ticketPrice.keySet()) 
				{
					if (totalTicketPrices.containsKey(carrier)) 
					{
						totalTicketPrices.get(carrier).addAll(t[ii].ticketPrice.get(carrier));
					} else 
					{
						totalTicketPrices.put(carrier,t[ii].ticketPrice.get(carrier));
					}
				}
			}
			
			HashMap<String, String> resultCPM = null;
			if(type.equals("median"))
			{
				resultCPM = MyThread.processMedian(totalTicketPrices);
			}
			else if(type.equals("fmedian"))
			{
				resultCPM = MyThread.processFMedian(totalTicketPrices);				
			}
			else
			{
				resultCPM = MyThread.processMean(totalTicketPrices);
			}
			
			// Filter out the Carriers which are not in Jan 2015
			for(String ca : resultCPM.keySet())
			{  
				String[] temp= ca.split("\t");
				if(globalValidCarrierList.contains(temp[0]))
				{
					System.out.println(ca + "\t" + resultCPM.get(ca));
				}
			}
			
			long end = System.currentTimeMillis();
			long runTime =  (end-start);
			fw.write(mode + "," + type + "," + String.valueOf(runTime)+"\n");
			fw.close();
		}				
	} 
	
		
	public static boolean isRecordValid(String[] record) 
	{
		if (record.length != 112 || record[0] == "" || record[2] == "" || record[6] == "" || record[111] == "")
			return false;
		return true;
	}
	

	/**
	 * Merges the given Carrier set from Thread to GlobalCarrierSet. 
	 * @param threadSet
	 */
	synchronized public static void mergeInto(HashSet<String> threadSet)
	{
		for(String ca : threadSet)
		{
			if(!globalValidCarrierList.contains(ca))
				globalValidCarrierList.add(ca);
		}
	}
}

/**
 * MyThread which when spawned, reads the given CSV and processes it for Sanity.
 * The values calculated are stored per Thread.
 * @author Ashish , Yogiraj
 *
 */
class MyThread extends Thread
{
	String path;
	String type;
	HashMap<String, ArrayList<Double>> ticketPrice = new HashMap<String, ArrayList<Double>>();
	HashSet<String> validCarrierList = new HashSet<String>();
	 static final String SEPERATOR = "\t";
	public MyThread (String p, String type) 
	{ 
	    this.path = p;
	    this.type = type;
	}

	//calculates FMedian
	public static HashMap<String, String> processFMedian(HashMap<String, ArrayList<Double>> ticketPrice) {

		HashMap<String, String> resultCPM = new HashMap<String, String>();
			
		for(String carrier : ticketPrice.keySet())
		{
			ArrayList<Double> temp = ticketPrice.get(carrier);
			int size = temp.size();
			Double fmedian;
			
			// If odd, return the middle element
			if(size%2 == 1)
			{
				fmedian = quickSelect(temp, (size/2 + 1));
			}
			else // If even, take the median as average of middle and next element.
			{
				fmedian = (quickSelect(temp, (size/2)) + quickSelect(temp, (size/2+1)))/2;
			}
			resultCPM.put(carrier, "" + fmedian);
		}
		return resultCPM;
	
	}

	public void run() 
	{ 
		try 
		{
			GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(path));
			BufferedReader br = new BufferedReader( new InputStreamReader(gzip, "UTF-8"));
			
			String ipline = br.readLine();
			//for each input line
			while(null != ipline)
			{
				String[] line = ipline.split(",");
				if(ThreadedAnalysis.isRecordValid(line))
				{
					String carrier = line[6];
					Double meanPrice = Double.valueOf(line[111]);
					String year = line[0];
					String month = line[2];
					String key = carrier.toString()+SEPERATOR+month;
					ticketPrice = calcTicketMedian(ticketPrice, key, meanPrice);
					if(year.equals("2015") && month.equals("1"))
					{
						validCarrierList.add(carrier);
					}			
				}
				ipline = br.readLine();
			}		
			
			br.close();
			System.out.println("Processed " + path);
			// Assign values to thread
			ThreadedAnalysis.mergeInto(validCarrierList);
			
		} catch (Exception e) 
		{
			e.printStackTrace();
		} 
	} 
	/**
	 * We have referred this solution from Corey Harman Analysis for fast median
	 * @param values
	 * @param k
	 * @return
	 */
	
	public static Double quickSelect(List<Double> values, int k)
    {
        int left = 0;
        int right = values.size() - 1;
        Random rand = new Random();
        while(true)
        {
            int partionIndex = rand.nextInt(right - left + 1) + left;
            int newIndex = partition(values, left, right, partionIndex);
            int q = newIndex - left + 1;
            if(k == q)
            {
                return values.get(newIndex);
            }
            else if(k < q)
            {
                right = newIndex - 1;
            }
            else
            {
                k -= q;
                left = newIndex + 1;
            }
        }
    }
	
    private static int partition(List <Double> values, int left, int right, int partitionIndex)
    {
    	Double partionValue = values.get(partitionIndex);
        int newIndex = left;
        Double temp = values.get(partitionIndex);
        values.set(partitionIndex, values.get(right));
        values.set(right, temp);
        for(int i = left; i < right; i++)
        {
            if(values.get(i).compareTo(partionValue) < 0)
            {
                temp = values.get(i);
                values.set(i, values.get(newIndex));
                values.set(newIndex, temp);
                newIndex++;
            }
        }
        temp = values.get(right);
        values.set(right, values.get(newIndex));
        values.set(newIndex, temp);
        return newIndex;
    }
	
	/**
	 * Calculates the Ticket Median for given.
	 * @param ticketPrice
	 * @param carrier
	 * @param meanPrice
	 * @return
	 */
	synchronized private HashMap<String, ArrayList<Double>> calcTicketMedian(HashMap<String, ArrayList<Double>> ticketPrice, String carrier, Double meanPrice) 
	{
		if(ticketPrice.containsKey(carrier))
		{
			ArrayList<Double> prices = ticketPrice.get(carrier);
			prices.add(meanPrice);

		}else
		{
			ArrayList<Double> pricesNew = new ArrayList<Double>();
			pricesNew.add(meanPrice);	
			ticketPrice.put(carrier, pricesNew);
		}		
		return ticketPrice;
	}
	
	//Calculates MEdian
	public static HashMap<String,String> processMedian(HashMap<String, ArrayList<Double>> ticketPrice)
	{
		HashMap<String, String> resultCPM = new HashMap<String, String>();
		// Sort the ArrayList
		for(String carrier : ticketPrice.keySet())
		{
			ArrayList<Double> temp = ticketPrice.get(carrier);
			Collections.sort(temp);
			
			ticketPrice.put(carrier, temp);
		}
		
		for(String carrier : ticketPrice.keySet())
		{
			ArrayList<Double> temp = ticketPrice.get(carrier);
			int size = temp.size();
			Double median;
			
			// If odd, return the middle element
			if(size%2 == 1)
			{
				median = temp.get(size/2 + 1);
			}
			else // If even, take the median as average of middle and next element.
			{
				median = (temp.get(size/2) + temp.get(size/2+1))/2;
			}
			resultCPM.put(carrier, "" + median);
		}
		return resultCPM;
	}
	
	//calculates Mean
	public static HashMap<String,String> processMean(HashMap<String, ArrayList<Double>> ticketPrice)
	{
		HashMap<String, String> resultCPM = new HashMap<String, String>();
		// Sort the ArrayList
		for(String carrier : ticketPrice.keySet())
		{
			ArrayList<Double> temp = ticketPrice.get(carrier);
			Collections.sort(temp);
			
			ticketPrice.put(carrier, temp);
		}
		
		for(String carrier : ticketPrice.keySet())
		{
			ArrayList<Double> temp = ticketPrice.get(carrier);
			double sum = 0;
			for(double d : temp)
			{
				sum = sum + d;
			}
			
			double average = sum / temp.size();
			
			resultCPM.put(carrier, average + "");
		}
		return resultCPM;
	}
}
