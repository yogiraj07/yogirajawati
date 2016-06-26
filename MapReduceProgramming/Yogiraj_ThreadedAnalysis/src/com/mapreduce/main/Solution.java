package com.mapreduce.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

class Solution {
	static HashMap<String, ArrayList<Double>> globalCarrierTicketPrice = new HashMap<String, ArrayList<Double>>();
	static HashSet<String> globalCarrierInJan2015 = new HashSet<String>();
	static HashMap<String, ArrayList<Double>> carrierMean = new HashMap<String, ArrayList<Double>>();

	public static void main(String[] args) throws IOException {
		// flag is used to check whether the processing is sequential or
		// parallel
		int flag = 0;
		if (args.length != 2) {
			System.out.println("Please enter the input in the format of : -p -input=DIR");
			System.exit(0);
		}
		if (args[0].equals("-p")) {
			flag = 1;
		}
		// extract 25 filepaths available in the directory
		String fileLocation = args[1].substring(7);
		List<String> pathList = new ArrayList<String>();
		Files.walk(Paths.get(fileLocation)).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				pathList.add(filePath.toString());
			}
		});

		// spawn 25 threads of ParallelProcess
		ParallelProcess f[] = new ParallelProcess[pathList.size()];
		if (flag == 1) {
			System.out.println("Processing in Parallel!");
			for (int i = 0; i < pathList.size(); i++) {
				f[i] = new ParallelProcess(pathList.get(i));
				f[i].start();

			}
			for (int i = 0; i < pathList.size(); i++) {
				try {
					f[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Processing Sequential");
			for (int i = 0; i < pathList.size(); i++) {
				f[i] = new ParallelProcess(pathList.get(i));
				f[i].start();
				try {
					f[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

		long K = 0, F = 0;
		String key;
		ArrayList<Double> value;
		ArrayList<Double> x;
		
		// extract K,F,carrierJan2015,carrierTicketPrice from each thread
		for (int i = 0; i < pathList.size(); i++) {
			K += f[i].K;
			F += f[i].F;
			globalCarrierInJan2015.addAll(f[i].carrierJan2015);
			HashMap<String, ArrayList<Double>> temp = f[i].carrierTicketPrice;

			Iterator<String> iter = temp.keySet().iterator();

			// populates globalCarrierTicketPrice : combines CarrierTicketPrice
			// in each thread
			while (iter.hasNext()) {
				key = iter.next();
				// get ticket price list per carrier
				value = temp.get(key);
				if (globalCarrierTicketPrice.containsKey(key)) {
					// get ticket price list against that carrier
					x = globalCarrierTicketPrice.get(key);
					// add ticket price against that carrier
					x.addAll(value);
					globalCarrierTicketPrice.put(key, x);
				} else {
					ArrayList<Double> l = new ArrayList<Double>();
					// l=globalCarrierTicketPrice.get(key);
					l.addAll(value);
					globalCarrierTicketPrice.put(key, l);
				}

			}

		}
		Iterator<String> kk = globalCarrierTicketPrice.keySet().iterator();
        //iterate over globalCarrierTicketPrice to calculate 
		while (kk.hasNext()) {
			Double sum = 0.0;
			key = kk.next();
			ArrayList<Double> list = new ArrayList<>();
			x = globalCarrierTicketPrice.get(key);
			for (Double d : x) {
				sum += d;
			}
			// sort x to find the median and average of all the flights;
			Collections.sort(x);
			// list is sorted
			globalCarrierTicketPrice.put(key, x);
			int len = x.size();
			Double medianValue;
			Double val1, val2;
			int index;
            //calculate median and mean
			//list[0] contains median value and list[1] contains average value
			if (!(len % 2 == 1)) {
				val1 = globalCarrierTicketPrice.get(key).get((len / 2) + 1);
				val2 = globalCarrierTicketPrice.get(key).get(len / 2);
				medianValue = (val1 + val2) / 2;
				list.add(medianValue);
				list.add(sum / len);
				carrierMean.put(key, list);
			} else {
				index = (len / 2) + 1;
				list.add(globalCarrierTicketPrice.get(key).get(index));
				list.add(sum / len);
				carrierMean.put(key, list);
			}

		}
		System.out.println("\nK : " + K + " F: " + F+"\n");
		Iterator<String> tt = carrierMean.keySet().iterator();
		//print carrier names that are active in Jan 2015
		while (tt.hasNext()) {
			key = tt.next();
			if (globalCarrierInJan2015.contains(key))
				System.out.println(key  +" "+ carrierMean.get(key).get(1)+" " +carrierMean.get(key).get(0));
		}
	}
}
