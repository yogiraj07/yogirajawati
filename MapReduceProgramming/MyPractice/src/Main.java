import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;


public class Main {

	public static final String SEPARATOR = "@";
	public static final String COLON = ":";
	private static double portfolioNAV;
	private static double benchMarkNAV;
    /*
     * Complete the function below.
     *
 	 * Note: The questions in this test build upon each other. We recommend you
	 * copy your solutions to your text editor of choice before proceeding to
	 * the next question as you will not be able to revisit previous questions.
	 */
 

//    static int countHoldings(String input) {
//    	//data is a string array holds strings that are splitted on the separator 
//        String[] data = input.split(SEPARATOR);
//        return data.length;
//    }

//    static String printHoldings(String portfolioString) {
//    	//split the postfolioString on COLON 
//    	String []portfolioBenchmark = portfolioString.split(COLON);
//    	if(portfolioBenchmark.length!=2) return "";
//        //data is a string array holds strings that are splitted on the separator 
//        String[] data = portfolioBenchmark[1].split(SEPARATOR);
//        //temp is an array of holds per portfolio
//        String [] temp;
//        //list is used to store Portfolio objects
//        List <Portfolio> list = new ArrayList<Portfolio>();
//        StringBuilder output=new StringBuilder(); //stores output that needs to be displayed
//        for(String s: data)
//        {
//        	temp=s.split(",");
//        	Portfolio p = new Portfolio(temp[0], temp[1], Integer.parseInt(temp[2]));
//        	list.add(p); //populate array list
//        }
//        //sort the list of Portfolio objects on the basis of ticker
//        Collections.sort(list);
//        //construct output
//        String result=constructOutput(output,list);
//        return result;
//    }
    ///////////////////////////////////////////////////////
//    static String generateTransactions(String input) {
//    	// stores objects of Transactions
//    	List <Transactions> transactionList = new ArrayList<Transactions>();
//    	//split the postfolioString on COLON 
//    	String []portfolioBenchmark = input.split(COLON);
//    	String result;
//    
//    	//if benchmark is not present, return null
//    	if(portfolioBenchmark.length!=2) return "";
//    	
//    	//populate portfolio and benchmark
//    	List<Portfolio> portfolioList = new ArrayList<Portfolio>();
//    	List<Portfolio> benchmarkList = new ArrayList<Portfolio>();
//    	
//    	portfolioList=populateList(portfolioList,portfolioBenchmark[0]);
//    	benchmarkList=populateList(benchmarkList,portfolioBenchmark[1]);
//    	
//    	Collections.sort(portfolioList);
//    	Collections.sort(benchmarkList);
//    	//generate transactionList
//    	transactionList=calculateTransactions(portfolioList,benchmarkList);
//    	Collections.sort(transactionList);
//    	result = generateTransactionOutput(transactionList) ;
//        return result;    
//    }
//    
//    private static String generateTransactionOutput(List<Transactions> transactionList) {
//    	StringBuilder output=new StringBuilder();
//    	for (Transactions p : transactionList) {
//			if (output.length() == 0) {
//				output.append(p.toString());
//			} else {
//				output.append(", " + p.toString());
//			}
//		}
//		return output.toString();
//	}

//	private static List<Transactions> calculateTransactions(List<Portfolio> portfolioList,
//			List<Portfolio> benchmarkList) {
//		if (portfolioList==null || benchmarkList==null) return null;
//		Double value;
//		Portfolio p;
//		Portfolio b;
//		List<Transactions> transactionList = new ArrayList<Transactions>();
//		Transactions t;
//		TransactionType transactionType = null;
//		Iterator<Portfolio> i = portfolioList.iterator();
//		Iterator<Portfolio> j = benchmarkList.iterator();
//			
//		while(i.hasNext() && j.hasNext())
//		{
//			p=i.next();
//			b=j.next();
//			value= (double) (b.getQuantity()-p.getQuantity());
//			//value//Math.round(value * 100.0) / 100.0;
//	     	if(value>=0.00)
//			{
//				t=new Transactions(transactionType.BUY, p.getTicker(),value);
//			}
//	     	else
//	     	{
//	     		t=new Transactions(transactionType.SELL, p.getTicker(),(-1*value));
//	     	}
//	     	transactionList.add(t);
//		}
//		return transactionList;
//	}

//	private static List<Portfolio> populateList(List<Portfolio> list, String str) { // extracts data from str and populates list
//		// TODO Auto-generated method stub
//    	String[] data = str.split(SEPARATOR);
//    	String[] temp;
//    	for(String s: data)
//         {
//         	temp=s.split(",");
//         	Portfolio p = new Portfolio(temp[0], temp[1], Integer.parseInt(temp[2]));
//         	list.add(p); //populate array list
//         }
//		return list;
//	}

	//////////////////////////////////////////////////////////
//    private static String constructOutput(StringBuilder output, List<Portfolio> list) {
//		for (Portfolio p : list) {
//			if (output.length() == 0) {
//				output.append(p.toString());
//			} else {
//				output.append(", " + p.toString());
//			}
//		}
//		return output.toString();
//	}

   //*********************************************************88
    static String generateTransactions(String inputString) {
      
    	
    	//split the postfolioString on COLON 
    	String []portfolioBenchmark = inputString.split(COLON);
    	HashMap<String, Transactions> transactionMap = new HashMap<String, Transactions>();
    	String result;
        StringBuilder output = new StringBuilder();
    	//if benchmark is not present, return null
    	if(portfolioBenchmark.length!=2) return "";
    	
    	//populate portfolio and benchmark
    	HashMap<String, Portfolio> portfolioMap = new HashMap<String, Portfolio>();
    	HashMap<String, Portfolio> benchmarkMap = new HashMap<String, Portfolio>();
    	
    	benchmarkMap=populatebenchMarkMap(benchmarkMap,portfolioBenchmark[1]);
    	portfolioMap=populatePortfolioMap(portfolioMap,benchmarkMap,portfolioBenchmark[0]);
    	portfolioMap = calculateNAV(portfolioMap,portfolioNAV);
    	benchmarkMap=calculateNAV(benchmarkMap,benchMarkNAV);
    	
    	transactionMap= calculateTransactions (portfolioMap,benchmarkMap);
    	//sort the hashmap
    	TreeMap<String, Transactions> t = new TreeMap<String, Transactions>(transactionMap);
          
          // Get a set of the entries
          Set set = t.entrySet();
          // Get an iterator
          Iterator i = set.iterator();
          // Display elements
          while(i.hasNext()) {
             Map.Entry me = (Map.Entry)i.next();
             if (output.length() == 0) {
 				output.append(me.getValue().toString());
 			} else {
 				output.append(", " + me.getValue().toString());
 			}
          }
          
		return output.toString();
    }
    
    
    
    


private static HashMap<String, Transactions> calculateTransactions(HashMap<String, Portfolio> portfolioMap,
			HashMap<String, Portfolio> benchmarkMap) {
	   HashMap<String, Transactions> result = new  HashMap<String, Transactions>();
	   Transactions t;
	   TransactionType type = null;
	   Iterator <String> i = portfolioMap.keySet().iterator();
	   Double benchmarkNAV;
	   Double portfolioNAV;
	   Double value;
	   String key;
	   
	   while (i.hasNext())
	   {
		   key=i.next();
		   benchmarkNAV=benchmarkMap.get(key).getNAV();
		   portfolioNAV=portfolioMap.get(key).getNAV();
		   value = (1 - (benchmarkNAV/portfolioNAV)) * portfolioMap.get(key).getQuantity()*-1;
		   if(value>=0)
		   {
			   result.put(key, new Transactions(type.SELL, key, value));
		   }
		   else
		   {
			   result.put(key, new Transactions(type.BUY, key, value));
		   }
	   }
	   return result;
	}






private static HashMap<String, Portfolio> calculateNAV(HashMap<String, Portfolio> map, double NAV) {
		Iterator<String>i = map.keySet().iterator();
		Portfolio p;
		String key;
		Double value;
		
		while(i.hasNext())
		{
			key=i.next();
			p=map.get(key);
			value =( p.getValue() * 100) / NAV;
			p.setNAV(value);
			map.put(key, p);
		}
		
		
		return map; 
	}

private static HashMap<String, Portfolio> populatePortfolioMap(HashMap<String, Portfolio> portfolioMap,
			HashMap<String, Portfolio> benchmarkMap, String str) {
	HashMap<String, Portfolio> resultMap = new HashMap<String, Portfolio>();
	String[] data = str.split(SEPARATOR);
	String[] temp;
	Double price;
	Double value;
	Double NAV;
	String key;
	Portfolio p;
	int qty;
	for(String s: data)
     {
		temp=s.split(",");
		key=temp[0];
		p=benchmarkMap.get(key);
		price=p.getPrice();
		qty=Integer.parseInt(temp[2]);
		value= price * qty;
     	p = new Portfolio(key, temp[1], qty,price,value,0.0);
        resultMap.put(key, p);
        portfolioNAV+=value; //keeps track of the value for th profile
     }
	
	return resultMap;
	}

private static HashMap<String, Portfolio> populatebenchMarkMap(HashMap<String, Portfolio> benchmarkMap, String str) {
	// TODO Auto-generated method stub
    HashMap<String, Portfolio> resultMap = new HashMap<String, Portfolio>();
	String[] data = str.split(SEPARATOR);
	String[] temp;
	Double value=0.0;
	int qty=0;
	Double price=0.0;
	for(String s: data)
     {
     	temp=s.split(",");
     	qty=Integer.parseInt(temp[2]);
     	price=Double.parseDouble(temp[3]);
     	value=qty*price;
     	Portfolio p = new Portfolio(temp[0], temp[1], qty,price,value,0.0);
        resultMap.put(temp[0], p);
        benchMarkNAV+=value;
     }
	
	return resultMap;
}

//**********************************************************



	public static void main(String[] args) throws IOException{
		 Scanner in = new Scanner(System.in);
	        String res;
	        String _input;
	        try {
	            _input = in.nextLine();
	        } catch (Exception e) {
	            _input = null;
	        }
	        res = generateTransactions(_input);
	        System.out.println(res);
	}
	
}
class Portfolio implements Comparable<Portfolio>
{
    private String ticker;
    private String name;
    private int quantity;
    private double price;
    private double value;
    private double NAV;
    public Portfolio(String ticker, String name, int quantity , double price,double value,double NAV) {
		super();
		this.ticker = ticker;
		this.name = name;
		this.quantity = quantity;
		this.price=price;
		this.value=value;
		this.NAV=NAV;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getNAV() {
		return NAV;
	}

	public void setNAV(double nAV) {
		NAV = nAV;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("###.00");
		return "["+ticker +", "+name+", " +quantity+", " +df.format(price)+", " +df.format(value)+", " +df.format(NAV) + "]";
	}

	

	@Override
	public int compareTo(Portfolio o) {
		// TODO Auto-generated method stub
		return this.ticker.compareTo(o.ticker);
	}
	
    
}

enum TransactionType {
	 SELL,BUY
}
class Transactions implements Comparable<Transactions> {
	//Transaction type can be either sell or buy
	
	 private TransactionType type;
	 private String ticker;
	 private Double quantity;
	public Transactions(TransactionType type, String ticker, Double quantity) {
		super();
		this.type = type;
		this.ticker = ticker;
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("###.00");
		return "["+type +", "+ticker+", " +df.format(quantity) + "]";
	}
	@Override
	public int compareTo(Transactions o) {
		// TODO Auto-generated method stub
		return this.ticker.compareTo(o.ticker);
	}

	 
}