package com.pagerank.CalculatePageRank;
import com.pagerank.perplexity.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;


public class CalculatePageRank {
    static HashSet<String> inlinks=new HashSet<String>();  // Used to store all inlinks 
    static HashMap<String, HashSet> pages = new HashMap<String, HashSet>(); // stores pages and its inlinks
    static HashMap<String, Double> page_inlinks_count=new HashMap<String, Double>(); //stores page and its inlink count
    static HashMap<String, Integer> outlinks = new HashMap<String, Integer>(); // stores outlinks for each node
    static HashMap<String, Double> pR = new HashMap<String, Double>(); //Stores page and its page rank
    static HashSet<String> sink_nodes=new HashSet<String>(); //stores sink nodes which have outlink = 0
    static List<Double> ranks=new ArrayList<Double>();  //Stores all the ranks for perplexity calculations
    static double d= 0.85; //Teleportation factor
    static double N;      //denotes size of pages 
    static 	int sink_nodes_size=0; // denotes
    static double sinkPR=0;        // Page Rank of Sink nodes
    static double perplexity;           //stores perplexity of current iteration
    static int consecutiveIterations=0; //used to count number of consecutive iterations of page rank with
                                        //perplexity difference of less than 1
    static double[] perplexity_array = {0}; //used to store perplexity
    static int no_inlinks;              //counts number of pages with no links
    static HashSet<String> temp_childs=new HashSet<String>(); // stores all inlinks for 1 page
    static int sum=0;   //to calculates page rank less than uniform
	public static void main(String[] args) throws FileNotFoundException {
     	pages = populatePages(pages);  //take data from text file
       	N=pages.size();               //N denotes total number of pages available for processing
   		sink_nodes_size=calculate_sinkNodes(); //populate sink nodes for the algorithm
	    pageRank();
	    sum=Rank_less_than_uniform();
	   //displayTop50(pR);
	  //  displayTop50(page_inlinks);
	    System.out.println("Total Page Ranks Calculated:- "+pR.size());
	  
	}

	private static int Rank_less_than_uniform() {   //returns total number of ranks less than uniform value
		// TODO Auto-generated method stub
		Iterator<String> i = pR.keySet().iterator();
		String key;
		double value = 1 / N; // uniform value
		while (i.hasNext()) {
			key = i.next();
			if (pR.get(key) < value) {
				sum++;
			}
		}

		return sum;
	}

	private static void displayTop50(HashMap<String, Double> map) {   //display Top50 Page ranks
		Set<Entry<String, Double>> set = map.entrySet();
		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(
				set);
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {

			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				// TODO Auto-generated method stub
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		System.out.println("Final :");
		int y = 0;
		for (Entry<String, Double> entry : list) { // displays top 50 page ranks
			System.out.println(entry.getKey() + " ==== " + entry.getValue()
					+ " PageNo:- " + ++y);
			if (y == 50) {
				break;
			}
		}

	}
	private static HashMap<String, Double> pageRank() {   //calculates page rank of all pages until
		                                                  //convergence
		// TODO Auto-generated method stub
		HashMap<String, Double> newpR = new HashMap<String, Double>(); //Used to store temporary page ranks
		initialize_pR();     //initializing each node with page rank =1/N
		int flag=0;  
		while(true)
		{
			flag ++;   // keeps track of iteration
			sinkPR=0;  // initializing at the start of each iteration
			total_sink_pR(); //calculates total sink_pR
			newpR=calculate_newpR(newpR); // returns newpR
			assignTo_pR(newpR);  //assign value of newpR to pR	
		    perplexity=CalculatePerplexity.perplexity(pR);   //calculate perplexity after each iteration
		 	System.out.println("perplexity:- "+perplexity + " Iteration:- "+flag);
		    if(isLimitReached (perplexity))  // convergence condition
			{   System.out.println("Convergence Limit Reached!!!");
				return pR;                  // breaks the loop and returns the pR for the current iteration
			}
		}
		
	}

	private static boolean isLimitReached(double perplexity) { 
   // if difference of perplexities is less than 1 for 4 consecutive  iterations - isLimitReached 
  //returns true
		if(perplexity_array[0]==0 || Math.abs(perplexity-perplexity_array[0])<1)
		{
			perplexity_array[0]=perplexity;  
			consecutiveIterations++;
			if(consecutiveIterations==5) 
				return true;
			else
				return false;
		}
		perplexity_array[0]=perplexity; // storing the value to compare it with next iteration
		consecutiveIterations=0;
		return false;
	}

	

	private static HashMap<String, Double> calculate_newpR(//calculates newpR per iteration
			HashMap<String, Double> newpR) { 
		
		String key;
		double teleportation=0.15 * (1/N);
		double value=teleportation+((0.85 *sinkPR)/N);
		Iterator<String> i =pages.keySet().iterator();		
		while(i.hasNext())  //traverse pages
		{
			key=i.next();
			newpR.put(key, value);
			HashSet<String> temp =new HashSet<String>();
			temp.addAll(pages.get(key));      // get inlinks for the page "key"
			Iterator<String> k=temp.iterator();
			String incoming_link;
			double shareValue=0.0;
			while(k.hasNext())              //process each inlink for the page key
			{
				
				incoming_link=k.next();
				//If the node has no incoming links
				if(pR.get(incoming_link) == null || outlinks.get(incoming_link)==null) continue;   
				//page rank formula
				shareValue=((0.85*pR.get(incoming_link))/outlinks.get(incoming_link))+newpR.get(key);
				newpR.put(key, shareValue);
			}
		}
	
		return newpR;
	}

	private static void assignTo_pR(HashMap<String, Double> newpR) { //assigns newpR to pR
		pR.putAll(newpR);

	}

	private static void total_sink_pR() {
		// TODO Auto-generated method stub
		Iterator<String> i = sink_nodes.iterator();
		while (i.hasNext()) {   //for each sink node
			String key = i.next();
			sinkPR += pR.get(key); //add the page rank 
		}

	}

	private static void initialize_pR() { //assign page rank= 1/N to all pages
		// TODO Auto-generated method stub
		Iterator<String> i = pages.keySet().iterator();
		String parent;
		double t = 1 / N;
		while (i.hasNext()) {
			parent = i.next();
			pR.put(parent, t); // initialize each page rank by 1/N
		}
	}
	

	private static int calculate_sinkNodes() { //calculates sinknodes count with outlink=0 from the dataset
		// TODO Auto-generated method stub
		int flag = 0;
		int k = 0;
		Iterator<String> i = pages.keySet().iterator();
		while (i.hasNext()) {             
			String key = i.next();
			if (inlinks.contains(key)) {
				continue;
			}
			k++;
			sink_nodes.add(key);  // if key is not appearing in any of the inlink then its a sink node
		}

		return k;
	}


	static HashMap<String, HashSet> populatePages(HashMap<String, HashSet> nodes) {
		File f = new File("wt2g_inlinks.txt"); //point to desired file
		try {
			Scanner sc = new Scanner(f);
			int i = 0;
			String key;
			while (sc.hasNextLine()) {
				i++;
				String data = sc.nextLine();
				String[] temp = data.split(" ");
				if (temp != null) // if data available on the line
				{
					String parent = temp[0];
					HashSet<String> links = new HashSet<String>();
					if (temp.length == 1) // no Inlinks present, only parent
					{
						nodes.put(parent, links);
						page_inlinks_count.put(parent, (double) 0);  //populate for calculating proportion
						if (!outlinks.containsKey(parent)) {
							outlinks.put(parent, 0);
						}
						no_inlinks++;

					}

					if (temp.length > 1) // Inlinks present
					{
						for (int j = 1; j < temp.length; j++) {
							links.add(temp[j]);
							inlinks.add(temp[j]); // stores all inlinks in hashset for calculating
												 // sink nodes
							temp_childs.add(temp[j]);

						}
						Iterator<String> k = temp_childs.iterator(); //logic for calculating outlinks per page
						while (k.hasNext()) {
							key = k.next();
							if (outlinks.containsKey(key)) {
								outlinks.put(key, (outlinks.get(key) + 1));
							} else {
								outlinks.put(key, 1);
							}
						}
						temp_childs.removeAll(temp_childs);
						nodes.put(parent, links); // populating hash map
						page_inlinks_count.put(parent, (double) links.size());
					}

				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error with Processing File!!");
			e.printStackTrace();
		}
		return nodes;
	}
}
