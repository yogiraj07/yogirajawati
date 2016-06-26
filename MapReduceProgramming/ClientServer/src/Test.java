import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

public class Test {
static LinkedList<Integer> Record = new LinkedList<>();
static LinkedList<Integer> splitterList = new LinkedList<>();
static int clusters;
static int clusterId; 
static int receiveAt = 4000;
static int sendAt = 4005;
static TextSocket sendSock = null;

//static TextSocket conn;

public static void main(String[] args) {

	clusterId = Integer.parseInt(args[0]);
	clusters= Integer.parseInt(args[1]);
	Record.add(1);
	Record.add(3);
	//sort
	//get splitters
	splitterList.add(2);
	String tempSplitter = "2";
	
	//slave
	if(clusterId != 0)
	{
		sendSplitters(tempSplitter,"localhost");
		insertDelay(50);
		tempSplitter = receiveSplitters();
	}
	
	//master
	if(clusterId==0)
	{
		System.out.println("Master will now be receiving splitters");
		String tempsplitterList;
		tempsplitterList =receiveSplitters();
		insertDelay(150);
		System.out.println("splitters received by master......: "+tempsplitterList);
		tempsplitterList = tempsplitterList + " iiiiiiiii";
		
		
		// Send the new splitters to all the slaves (exclude the master).
		System.out.println("Now master sends splitters to all the slaves (exclude itself)..");
		sendSplitters(tempsplitterList, "localhost");
	}
	
	/////////////////////////////////////////
	
	////////////////////////////////////////
	
	
	
	
	insertDelay(200);
	if(clusterId == 0)
	{
		distributeBuckets(Record, "");
		LinkedList<Integer> r = receiveList();
		
	}
	else
	{
		LinkedList<Integer> r = receiveList();
		System.out.println("Received List from Master : "+r);
		distributeBuckets(Record, "");
	}
	
	
;
}
private static LinkedList<Integer> receiveList() {
	LinkedList<Integer> rcvdList = new LinkedList<Integer>();
	TextSocket.Server server;
	int s1;
	try {
		if(clusterId == 0)
		{
			s1=3001;
		}
		else
		{
			s1=3006;
		}
		System.out.println("Receiving at : "+s1);
		server = new TextSocket.Server(s1);
		   TextSocket conn;
		    int count = 1;

		    while (null != (conn = server.accept())) 
		    {
		    	rcvdList.addAll(conn.getIntegerList());   
		    	count++;
		        System.out.println("Received a record list..");
		        
		        if(count == clusters)
		        {
		        	System.out.println("Closing connection at : "+s1);
		        	 conn.close();
		        	 break;  
		        }
		        	
		    }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 
	
    return rcvdList;
}
private static void distributeBuckets(LinkedList<Integer> record2, String string) {
	// TODO Auto-generated method stub
	insertDelay(1111);
	sendList("localhost", record2);	
}
private static void sendList(String ip, LinkedList<Integer> record2) {
	// TODO Auto-generated method stub
	System.out.println("Distributing Bucket to IP : "+ip + " ---DATA---- "+record2);
	TextSocket conn;
	int s1;
	try {
		if(clusterId == 0)
		{
			s1=3006;
		}
		else
		{
			s1=3001;
		}
		conn = new TextSocket(ip, s1);
		conn.putIntegerList(record2);
		conn.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   
	
}
private static void insertDelay(int i) {
	// TODO Auto-generated method stub
	while(i>0)
	{
		i--;
	}
	
}
private static String receiveSplitters() {

	//String rcvdSplitters = null ;
	String allSplitters = "";
	int s1 = 3000;
	
	TextSocket.Server server = null;
	try {
		if(clusterId == 0)
		{
			s1=3000;
		}
		else
		{
			s1=3005;
		}
		server = new TextSocket.Server(s1);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    TextSocket conn;
    int count = 1;

    try {
		while (null != (conn = server.accept())) 
		{
		    String req;
			try {
				req = conn.getln();
				 count++;
			     System.out.println("Received Splitter " + req);
			     allSplitters += req;
			   
			        
			     if(count == clusters)
			     {
			    	 System.out.println("Closing connection at : "+s1);
			    	  conn.close();
			    	  break;
			     }
			        	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		   
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
   
    return allSplitters;
	
}
private static void sendSplitters(String tempSplitter, String ip) {
	// TODO Auto-generated method stub
	System.out.println("Sending Splitters to " + ip);
	if(clusterId!=0)
	{
		sendDataToMaster(ip, tempSplitter);
	}
	else
	{
		sendDataToSlave(ip, tempSplitter);
	}
	
}
private static void sendDataToSlave(String ip, String data) {

	int s1 = 3005;
    try {
    	System.out.println("Opeining posrt : "+s1);
    	TextSocket conn = new TextSocket(ip, s1);
		conn.putln(data);
		conn.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   
	
}
private static void sendDataToMaster(String ip, String data) {
	// TODO Auto-generated method stub

	int s1 = 3000;
    try {
    
    	System.out.println("Opeining posrt : "+s1);
    	TextSocket conn = new TextSocket(ip, s1);
		conn.putln(data);
		conn.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   
	
}
}
