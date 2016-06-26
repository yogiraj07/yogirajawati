package twitter;

import java.util.HashMap;
import java.util.Iterator;

public class Solution {
	
	
	static int countDuplicates(int[] numbers) {
		//map stores the integer of the array and number of times it appears in the array
		HashMap< Integer, Integer> map = new HashMap<>();
		int count =0;
		int oldValue=0;
		int key=0;
		//iterate over array
		for (int j=0;j<numbers.length;j++)
		{
			int i=numbers[j];
			//if the number is already present, increse the value of that number
			if(map.containsKey(i))
			{
				oldValue = map.get(i);
				map.put(i, oldValue+1);
			}
			//else make entry in the hashmap with the kay as integer and value as 1
			else
			{
				map.put(i,1);
			}
		}
		//set iterator to hashmap
		Iterator<Integer> itr =  map.keySet().iterator();
		while(itr.hasNext())
		{
			key=itr.next();
			//count numbers which appear 2 or more times
			if(map.get(key)>=2)
			{
				count++;
			}
		}
		return count;

    }
	public static void main(String[] args) {
		int a[]={1,3,1,3,2,3,4,5,4,6,5};
		System.out.println(countDuplicates(a));
	}
}
