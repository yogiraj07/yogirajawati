import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class Solution {
	static String closestNumbers(int len, String s) {
		StringBuilder output = new StringBuilder();
		ArrayList<Long> sortedList = new ArrayList<Long>();
		long diff = 0;
		HashMap<Long, Long> difference = new HashMap<Long, Long>();
		long min = 0;
		String[] data = s.split(" ");
		TreeSet<Long> t = new TreeSet<>();
		for (int i = 0; i < data.length; i++) {
			t.add(Long.parseLong(data[i]));
		}

		sortedList.addAll(t);
		for (int i = 0; i < sortedList.size() - 1; i++) {
			diff = sortedList.get(i + 1) - sortedList.get(i);
			if (i == 0)
				min = diff;
			else if (min > diff)
				min = diff;
			difference.put(sortedList.get(i), diff);
		}
		Iterator<Long> j = difference.keySet().iterator();
		Long key;
		sortedList.clear();

		while (j.hasNext()) {
			key = j.next();
			if (difference.get(key) == min) {
				sortedList.add(key);
				// this is the logic I had to reflect in the final code which I
				// missed it due to time up
				sortedList.add(key + min);
			}

		}
		Collections.sort(sortedList);
		for (int i = 0; i < sortedList.size(); i++) {
			if (output.length() > 0) {
				output.append(" ");
			}
			output.append(sortedList.get(i));

		}
		return output.toString();
	}

	public static void main(String[] args) {
		closestNumbers(5, "-10 -1 0 100 345");
	}

}
