package HashMapExample;

import java.util.*;
import java.util.Map.Entry;

public class HashMapDemo {

   public static void main(String args[]) {
   
      // Create a hash map
      HashMap hm = new HashMap();
      // Put elements to the map
      hm.put("Zara", new Double(3434.34));
      hm.put("Mahnaz", new Double(123.22));
      hm.put("Ayan", new Double(1378.00));
      hm.put("Daisy", new Double(99.22));
      hm.put("Qadir", new Double(-19.08));
      
      // Get a set of the entries
      Set set = hm.entrySet();
      // Get an iterator
      Iterator i = set.iterator();
      // Display elements
      while(i.hasNext()) {
         Map.Entry me = (Map.Entry)i.next();
         System.out.print(me.getKey() + ": ");
         System.out.println(me.getValue());
      }
     System.out.println("\nSort by Key : \n");
     //sort by key
      TreeMap<String, Double> t = new TreeMap<String, Double>(hm);
      
      // Get a set of the entries
      Set set1 = t.entrySet();
      // Get an iterator
      Iterator i1 = set1.iterator();
      // Display elements
      while(i1.hasNext()) {
         Map.Entry me = (Map.Entry)i1.next();
         System.out.print(me.getKey() + ": ");
         System.out.println(me.getValue());
      }
      
      //sort by value
      //Method 1 : HAshMap is converted into Arraylist and Arraylist is sorted
      //           Original Hashmap is not changed
      Set<Entry<String,Double>> s= hm.entrySet();
      List<Entry<String,Double>> l = new ArrayList<Entry<String,Double>>(s);
      Collections.sort(l, new Comparator<Entry<String,Double>>() {

		@Override
		public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
			// TODO Auto-generated method stub
			return (o1.getValue().compareTo(o2.getValue()));
		}
	});
      System.out.println(l);
//      for(Map.Entry<String, Double> entry:l){
//          System.out.println(entry.getKey()+" ==== "+entry.getValue());
//      }
      
      System.out.println("\nSort by Value\n");
        Map sortedMap = sortByValue(hm);
		System.out.println(sortedMap);
     
   }
 //Method 2 : Original Hash Map is changed
   public static Map sortByValue(Map unsortedMap) {
		Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}
}
class ValueComparator implements Comparator {
	 
	Map map;
 
	public ValueComparator(Map map) {
		this.map = map;
	}
 
	public int compare(Object keyA, Object keyB) {
		Comparable valueA = (Comparable) map.get(keyA);
		Comparable valueB = (Comparable) map.get(keyB);
		return valueB.compareTo(valueA);
	}
}