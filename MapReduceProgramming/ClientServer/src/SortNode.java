import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

//aws s3 cp s3://cs6240sp16/climate/199704hourly.txt.gz .

//Arguments: "Dry Bulb Temp" (Sorting field) , input path, offset , output path, number of clusters

public class SortNode 
{
	static String BUCKETPATH = "s3://cs6240sp16/climate/";
	static String SEPERATOR = ",";
	static String WBAN_COLOUMN = "Wban Number";
	static String DATE_COLOUMN = "YearMonthDay";
	static String TIME_COLOUMN = "Time";
	static String TEMP_COLOUMN;
	static LinkedList<Record> recordList = new LinkedList<>();
	static LinkedList<Record> splitterList = new LinkedList<>();
	static LinkedList<Record> receivedSplitterList = new LinkedList<>();
	static LinkedList<Record> finalList = new LinkedList<>();
	static ArrayList<String> fileColoumns = new ArrayList<String>();
	static LinkedList<Integer> insertIndexList = new LinkedList<>();
	static String[] ec2IpAddrs = null;
	static int clusterId = 0;
	static int clusters = 0;

	public static void main(String[] args) throws IOException, ClassNotFoundException 
	{
		Record r;
		GZIPInputStream gzip = null;
		if (args.length != 6)
		{
			System.out.println("Please input <Sorting Field> : <input path> : <output path> : <Number of clusters> : <Cluster Id> : <DNS File Path> ");
			System.exit(0);
		}

		String sortingField = args[0];
		String inputPath = args[1];
		String outputPath = args[2];
		clusters = Integer.parseInt(args[3]);
		ec2IpAddrs = new String[clusters];
		clusterId = Integer.parseInt(args[4]);
		String dnsFilePath = args[5];

		TEMP_COLOUMN = sortingField;

		int i = 0;
		int j = 0;
		final File folder = new File(inputPath);
		
		// Load all the EC2 Ip Addresses
		loadEc2IpAddrs(dnsFilePath);

		for (final File fileEntry : folder.listFiles())
		{
			String singleFile = fileEntry.getName();
			System.out.println(singleFile);

			try

			{

				gzip = new GZIPInputStream(new FileInputStream(inputPath + "/"+ singleFile));

				BufferedReader br = new BufferedReader(new InputStreamReader(gzip));
				String line;
				line = br.readLine();
				String newLine = line.replaceAll("\"", "");
				String correctLine = newLine.replaceAll(", ", ",");
				String[] coloumns = correctLine.split(SEPERATOR);
				coloumns[0] = WBAN_COLOUMN;
				fileColoumns = new ArrayList<>(Arrays.asList(coloumns));

				while ((line = br.readLine()) != null)
				{
					j++;
					String[] data = line.split(SEPERATOR);
					if (data.length < 21)
					{
						continue;
					}

					String wban = data[fileColoumns.indexOf(WBAN_COLOUMN)].trim();
					String date = data[fileColoumns.indexOf(DATE_COLOUMN)].trim();
					String time = data[fileColoumns.indexOf(TIME_COLOUMN)].trim();
					String temp = data[fileColoumns.indexOf(TEMP_COLOUMN)].trim();

					if (isRecordValid(wban, date, time, temp))
					{
						r = new Record(wban, date, time, Double.parseDouble(temp));
						i++;
						recordList.add(r);
					}
				}

				br.close();

			} catch (FileNotFoundException e) {

				// e.printStackTrace();

			} catch (IOException e) {

				// e.printStackTrace();

			}
			System.out.println("Records in list  till now: "
					+ recordList.size());

		} // Laoded all the files

		System.out.println("started sorting...");
		recordList = sortData(recordList);

		// select p-1 (CLUSTERS - 1) splitters from this data set

		splitterList = getSplitters(recordList, clusters);
		
		System.out.println("\n SPLITTERS : ");

		for (Record rec : splitterList)
		{
			System.out.print(" Splitter " + rec.getTemp());
		}

		// send the splitters of this node to master node
		if(clusterId != 0)
		{
			System.out.println("Sending splitters from slave to Master");
			sendSplitters(splitterList, new String[]{ec2IpAddrs[0]});
			// TODO Receive new p-1 splitters from Master - HAVE TO WAIT
			splitterList = receiveSplitters();
		}
		
		if(clusterId == 0)	// MASTER first receives then sends to all
		{
			// TODO Wait to receive p-1 splitters from each Slave.
			System.out.println("Master will now be receiving splitters");
			splitterList.addAll(receiveSplitters());
			System.out.println("splitters received by master.. now sort them");
			Collections.sort(splitterList, Record.TempComparator);
			System.out.println("Get more splitters on these");
			splitterList = getSplitters(splitterList, clusters);
			// TODO Here send is for ALL the Slaves
			String[] tempAddr = new String[ec2IpAddrs.length-1];
			for(int ip = 1; ip < ec2IpAddrs.length; ip++)
				tempAddr[ip] = ec2IpAddrs[ip];
			// Send the new splitters to all the slaves (exclude the master).
			System.out.println("Now master sends splitters to all the slaves (exclude itself)..");
			sendSplitters(splitterList, tempAddr);
		}
		
		// insert new splitters received by the master node into this recordlist
		System.out.println("Inserting the splitters into record list");
		insertSplitters(recordList, splitterList);
		
		///
		System.out.println("Distributing the buckets");
		distributeBuckets(recordList, insertIndexList);
	
		// TODO Now collect all the sorted records from all the slaves.
		finalList.addAll(receiveList());
		System.out.println("Sorting the final list..");
		Collections.sort(finalList, Record.TempComparator);

		writeOutputToFile(finalList, outputPath);

		System.out.println("\n Total : " + j + " V : " + i);

		// TODO Send the client an acknowledgement that all is done
		
	}
	
	public static void loadEc2IpAddrs(String dnsFile) throws IOException
	{
		BufferedReader read = new BufferedReader(new FileReader(dnsFile));
		String ipLine = read.readLine();
		ec2IpAddrs = ipLine.split(" ");	
	}

	private static void writeOutputToFile(LinkedList<Record> recordList2,
			String ouputPath) {

		BufferedWriter recordWrite = null;

		try {

			recordWrite = new BufferedWriter(new FileWriter(ouputPath));

			for (Record rec : recordList2)

			{

				recordWrite.write(rec.toString());

			}

			recordWrite.close();

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

	}

	// This function inserts splitter received into the current sorted
	// recordList

	private static void insertSplitters(LinkedList<Record> recordList2,
			LinkedList<Record> receivedSplitterList2) {

		double t;

		int insertIndex;

		// ArrayList<Integer> insertIndexList = new ArrayList<>();

		// get all splitter indexes that needs to be inserted

		int offset = 0;

		// for each splitter

		for (Record r : receivedSplitterList2)

		{

			t = r.getTemp();

			// get the correct index that the splitter needs to be inserted into
			// the list

			insertIndex = getCorrectPosition(recordList2, t);

			// Keep track of the index of the splitters that are going to be
			// inserted in the sorted list

			// offset is used to manage insertions

			insertIndexList.add(insertIndex + offset);

			offset++;

		}

		int j = 0;

		// for each splitter index

		for (int i : insertIndexList)

		{

			// get the splitter and insert at index i

			recordList2.add(i, receivedSplitterList2.get(j));

			j++;

		}

		// System.out.println("\n");

		// for(Record r : recordList2)

		// {

		// System.out.print(" "+r.getTemp());

		// }

		// send block to instances

	}

	// getCorrectPosition : returns the index of the splitter t that is going to
	// be inserted into recordlist

	private static int getCorrectPosition(LinkedList<Record> recordList2,
			Double t) {

		// double prev = recordList2.get(0).getTemp();

		// double current = 0.0;

		// int i;

		// //traverse recordlist

		// for ( i=0;i<recordList2.size();i++)

		// {

		// current = recordList2.get(i).getTemp();

		// //until appropriate position of t is found, iterate forward in the
		// sorted record list

		// if(current <= t )

		// {

		// continue;

		// }

		// return i;

		// }

		// return (i);

		int lo = 0;

		int hi = recordList2.size() - 1;

		while (lo <= hi) {

			int mid = lo + (hi - lo) / 2;

			if (t < recordList2.get(mid).getTemp())
				hi = mid - 1;

			else if (t > recordList2.get(mid).getTemp())
				lo = mid + 1;

			else
				return mid;

		}

		return lo;

	}

	// getSplitters : This function returns array of splitters on recordlist
	// with number of splitters = clusters - 1

	private static LinkedList<Record> getSplitters(
			LinkedList<Record> recordList, int clusters) {

		int sizeOfList = recordList.size();

		// Following maths used to pick splitters at equal distance from record
		// list

		int quotient = sizeOfList / (clusters);
		int increment = quotient;
		int index = increment;

		LinkedList<Record> splitterList = new LinkedList<Record>();

		for (int k = 0; k < clusters - 1 && k < sizeOfList; k++)
		{
			splitterList.add(recordList.get(index));
			index = index + increment;
		}

		return splitterList;
	}

	/**
	 * Sorts the given List of Records based on the Dry Bulb Temp column.
	 * @param recordList
	 * @return
	 */
	private static LinkedList<Record> sortData(LinkedList<Record> recordList) 
	{
		Collections.sort(recordList, Record.TempComparator);
		return recordList;
	}

	// sends splitters of this EC2 Node to master node

	private static void sendSplitters(LinkedList<Record> splitterList, String[] ec2IpAddrs) throws IOException 
	{
		// Accumulate the Splitters in a single packet
		StringBuilder sb = new StringBuilder();
		for(Record eachSplitter : splitterList)
		{
			sb.append(eachSplitter.getTemp() + ",");
		}
		
		for(String eachIp : ec2IpAddrs)
		{
			System.out.println("Sending Splitters to " + eachIp);
			sendData(eachIp, sb.toString());
		}
	}
	
	private static void sendData(String ipAddr, String data) throws IOException 
	{
		TextSocket conn = new TextSocket(ipAddr, 3000);
        conn.putln(data);
        conn.close();
	}
	
	private static void sendList(String ipAddr, LinkedList<Record> data) throws IOException 
	{
		TextSocket conn = new TextSocket(ipAddr, 3000);
        conn.putList(data);
        conn.close();
	}
	
	private static LinkedList<Record> receiveList() throws IOException, ClassNotFoundException
	{
		LinkedList<Record> rcvdList = new LinkedList<Record>();
		TextSocket.Server server = new TextSocket.Server(3000);
        TextSocket conn;
        int count = 1;

        while (null != (conn = server.accept())) 
        {
        	rcvdList.addAll(conn.getList());   
        	count++;
            System.out.println("Received a record list..");
            conn.close();
            
            if(count == clusters)
            	break;  
        }
		
        return rcvdList;
	}
	
	private static LinkedList<Record> receiveSplitters() throws IOException
	{
		LinkedList<Record> rcvdSplitters = new LinkedList<Record>();
		String allSplitters = "";
		TextSocket.Server server = new TextSocket.Server(3000);
        TextSocket conn;
        int count = 1;

        while (null != (conn = server.accept())) 
        {
            String req = conn.getln();   
            count++;
            System.out.println("Received Splitter " + req);
            allSplitters += req;
            conn.close();
            
            if(count == clusters)
            	break;
        }
		
        String[] splitters = allSplitters.split(",");
        
        for(String eachSplitter : splitters)
        {
        	Record r = new Record("a", "a", "a", Double.parseDouble(eachSplitter));
        	rcvdSplitters.add(r);
        }
        
        return rcvdSplitters;
	}

	// isRecordValid : Sanity test to check whether record is valid or not

	private static boolean isRecordValid(String wban, String date, String time,
			String temp) {
       if(wban.isEmpty() || date.isEmpty() || time.isEmpty() || temp.isEmpty() || temp.equals("-"))
		{
			return false;
		}
		try
		{
			Double.parseDouble(temp);
			return true;
		}catch (Exception e){
			return false;
		}

	}

	// distributeBuckets : Sends data chunks to ec2 instance

	private static void distributeBuckets(LinkedList<Record> recordList2,
			LinkedList<Integer> insertIndexList) throws IOException {

		int i = 0, j = 0;
		int ipCount = 0;
		int k = 1;

		LinkedList<Record> sendRecordChunk = new LinkedList<>();

		System.out.print("\nChunk = " + k++ + " : ");

		for (i = 0; i < recordList2.size(); i++)

		{

			if (i < insertIndexList.get(j))
			{
				// System.out.print( " "+recordList2.get(i).getTemp());
				sendRecordChunk.add(recordList2.get(i));
			}
			else
			{
				// send recordChunk to node .....
				//print(sendRecordChunk);
				if(ipCount != clusterId) 	// Dont send the chunk to yourself
				{
					sendList(ec2IpAddrs[ipCount], sendRecordChunk);	
					ipCount++;
				}else{
					// TODO Store my this current chunk.
					finalList = sendRecordChunk;
					//receive
					ipCount++;
				}
				sendRecordChunk.clear();
				sendRecordChunk = null;
				sendRecordChunk = new LinkedList<>();
				System.out.print("\nChunk = " + k++ + " : ");
				i--;
				j++;
				if (j == insertIndexList.size())
				{
					break;
				}
			}
		}

		i++;

		// send last chunk to node
		while (i < recordList2.size())
		{
			// System.out.print( " "+recordList2.get(i).getTemp());
			sendRecordChunk.add(recordList2.get(i));			
			i++;
		}

		//print(sendRecordChunk);
		sendList(ec2IpAddrs[ipCount], sendRecordChunk);	
		sendRecordChunk.clear();
		sendRecordChunk = null;
	}

	private static void print(LinkedList<Record> sendRecordChunk) 
	{
		for (Record r : sendRecordChunk)
		{
			System.out.print(" " + r.getTemp());
		}
	}

}

class Record 
{
	private String wban;
	private String date;
	private String time;
	private Double temp;
	
	public String getWban() 
	{
		return wban;
	}

	public void setWban(String wban)
	{
		this.wban = wban;
	}

	public String getDate() 
	{
		return date;
	}

	public void setDate(String date) 
	{
		this.date = date;
	}

	public String getTime() 
	{
		return time;
	}

	public void setTime(String time) 
	{
		this.time = time;
	}

	public Double getTemp() 
	{
		return temp;
	}

	public void setTemp(Double temp) 
	{
		this.temp = temp;
	}

	public Record(String wban, String date, String time, Double temp) 
	{
		super();
		this.wban = wban;
		this.date = date;
		this.time = time;
		this.temp = temp;
	}

	@Override
	public String toString() 
	{
		return "Record [wban=" + wban + ", date=" + date + ", time=" + time
				+ ", temp=" + temp + "]" + "\n";
	}

	public static final Comparator<Record> TempComparator = new Comparator<Record>() {

		@Override
		public int compare(Record o1, Record o2) 
		{
			if (o1.getTemp() > o2.getTemp())
			{
				return 1;
			}
			else if (o1.getTemp() < o2.getTemp())
			{
				return -1;
			}
			else
				return 0;
		}

	};
}