//Yogiraj Awati
//Sequential Analysis Analysis

package com.mapreduce.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import com.opencsv.CSVReader;

public class SequentialProcess {

	private static ArrayList<String> fileColoumns = new ArrayList<String>();
	private static HashMap<String, Double> carrierTicketPrice = new HashMap<String, Double>();
	private static HashMap<String, Integer> carrierFrequency = new HashMap<String, Integer>();

	public static void analyzeFile(String filename) {
		long corruptLines = 1;
		long validLines = 0;
		try {
			// point to file
			GZIPInputStream inputFile = new GZIPInputStream(new FileInputStream(filename));

			Reader r = new InputStreamReader(inputFile, "ASCII");
			// using CSV parser
			CSVReader input = new CSVReader(r);
			
			// extract column names from the file
			fileColoumns = new ArrayList<String>(Arrays.asList(input.readNext()));
			// Stores data of one line at a time
			String[] dataPerLine;
			// process each line
			while ((dataPerLine = input.readNext()) != null) {
				if (checkValidLine(dataPerLine)) {
					validLines++;
				} else {
					corruptLines++;
				}
			}
			// release resources
			input.close();
			r.close();

		} catch (IOException e) {
			System.out.println("Error in decompression of the file");
		}
		// Final Output
		System.out.println("F = " + validLines);
		System.out.println("K = " + corruptLines);

		Iterator<String> i = carrierTicketPrice.keySet().iterator();
		String key;
		while (i.hasNext()) {
			key = i.next();
			Double temp = (double) carrierTicketPrice.get(key) / carrierFrequency.get(key);
			carrierTicketPrice.put(key, temp);
		}

		// outputMap stores sorted mean average price for each carrier
		TreeMap<String, Double> outputMap = sortValues(carrierTicketPrice);
		for (String carrier : outputMap.keySet()) {
			System.out.println("Carrier: " + carrier + " Mean Average Price: " + carrierTicketPrice.get(carrier));
		}
	}

	private static TreeMap<String, Double> sortValues(HashMap<String, Double> carrierTicketPrice) {
		// TODO Auto-generated method stub
		SortValues sc = new SortValues(carrierTicketPrice);
		TreeMap<String, Double> map = new TreeMap<>(sc);
		map.putAll(carrierTicketPrice);
		return map;
	}

	// sanity test
	private static boolean checkValidLine(String[] dataPerLine) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm");
		// extract dataPerLine for processing
		String CRS_ARR_TIME = dataPerLine[fileColoumns.indexOf("CRS_ARR_TIME")];
		String CRS_ELAPSED_TIME = dataPerLine[fileColoumns.indexOf("CRS_ELAPSED_TIME")];
		String CRS_DEP_TIME = dataPerLine[fileColoumns.indexOf("CRS_DEP_TIME")];
		float timeZone = 0;
		try {
			float CrsElapsedTime = Float.parseFloat(CRS_ELAPSED_TIME);
			// CRSArrTime and CRSDepTime should not be zero
			Date CrsArrTime = (CRS_ARR_TIME.equals("") ? null : dateFormat.parse(CRS_ARR_TIME));
			Date CrsDepTime = (CRS_DEP_TIME.equals("") ? null : dateFormat.parse(CRS_DEP_TIME));
			if (CrsArrTime.getTime() == 0.0 || CrsDepTime.getTime() == 0.0)
				return false;

			// timeZone = CRSArrTime - CRSDepTime - CRSElapsedTime;
			// timeZone % 60 should be 0
			timeZone = calculateDateDiff(CRS_ARR_TIME, CRS_DEP_TIME) - CrsElapsedTime;
			if ((timeZone % 60) != 0)
				return false;

		} catch (NumberFormatException e) {
			return false;
		} catch (ParseException e) {
			return false;
		}

		// AirportID, AirportSeqID, CityMarketID, StateFips, Wac should be
		// larger than 0
		// So extract these field and check validation

		int originAirportId = Integer.parseInt(dataPerLine[fileColoumns.indexOf("ORIGIN_AIRPORT_ID")]);
		int originAirportSeqId = Integer.parseInt(dataPerLine[fileColoumns.indexOf("ORIGIN_AIRPORT_SEQ_ID")]);
		int originCirtMarketId = Integer.parseInt(dataPerLine[fileColoumns.indexOf("ORIGIN_CITY_MARKET_ID")]);
		int originStateFips = Integer.parseInt(dataPerLine[fileColoumns.indexOf("ORIGIN_STATE_FIPS")]);
		int originWac = Integer.parseInt(dataPerLine[fileColoumns.indexOf("ORIGIN_WAC")]);
		int destAirpotId = Integer.parseInt(dataPerLine[fileColoumns.indexOf("DEST_AIRPORT_ID")]);
		int destAirportSeqId = Integer.parseInt(dataPerLine[fileColoumns.indexOf("DEST_AIRPORT_SEQ_ID")]);
		int destCityMarketId = Integer.parseInt(dataPerLine[fileColoumns.indexOf("DEST_CITY_MARKET_ID")]);
		int destStateFips = Integer.parseInt(dataPerLine[fileColoumns.indexOf("DEST_STATE_FIPS")]);
		int destWac = Integer.parseInt(dataPerLine[fileColoumns.indexOf("DEST_WAC")]);

		if (originAirportId <= 0 || destWac <= 0 || destStateFips <= 0 || destCityMarketId <= 0 || destAirportSeqId <= 0
				|| destAirpotId <= 0 || originWac <= 0 || originStateFips <= 0 || originCirtMarketId <= 0
				|| originAirportSeqId <= 0)
			return false;

		// Origin, Destination, CityName, State, StateName should not be empty

		String origin = dataPerLine[fileColoumns.indexOf("ORIGIN")];
		String originCityName = dataPerLine[fileColoumns.indexOf("ORIGIN_CITY_NAME")];
		String originState = dataPerLine[fileColoumns.indexOf("ORIGIN_STATE_ABR")];
		String originStateNm = dataPerLine[fileColoumns.indexOf("ORIGIN_STATE_NM")];
		String dest = dataPerLine[fileColoumns.indexOf("DEST")];
		String destCityName = dataPerLine[fileColoumns.indexOf("DEST_CITY_NAME")];
		String destStateAbr = dataPerLine[fileColoumns.indexOf("DEST_STATE_ABR")];
		String destStateNm = dataPerLine[fileColoumns.indexOf("DEST_STATE_NM")];
		if (origin.equals("") || originCityName.equals("") || originState.equals("") || originStateNm.equals("")
				|| dest.equals("") || destCityName.equals("") || destStateAbr.equals("") || destStateNm.equals(""))
			return false;

		// for Flights that are not cancelled
		int cancelled = Integer.parseInt(dataPerLine[fileColoumns.indexOf("CANCELLED")]);

		if (cancelled == 0) {

			// ArrTime - DepTime - ActualElapsedTime - timeZone should be zero
			String actualElaspedTime = dataPerLine[fileColoumns.indexOf("ACTUAL_ELAPSED_TIME")];
			String arrTime = dataPerLine[fileColoumns.indexOf("ARR_TIME")];
			String deptTime = dataPerLine[fileColoumns.indexOf("DEP_TIME")];
			float temp = calculateDateDiff(arrTime, deptTime) - Float.parseFloat(actualElaspedTime) - timeZone;
			if (temp != 0)
				return false;

			// if ArrDelay > 0 then ArrDelay should equal to ArrDelayMinutes
			// if ArrDelay < 0 then ArrDelayMinutes should be zero
			// if ArrDelayMinutes >= 15 then ArrDel15 should be false
			float arrDelay = Float.parseFloat(dataPerLine[fileColoumns.indexOf("ARR_DELAY")]);
			float arrDelayMin = Float.parseFloat(dataPerLine[fileColoumns.indexOf("ARR_DELAY_NEW")]);
			float delayOf15 = Float.parseFloat(dataPerLine[fileColoumns.indexOf("ARR_DEL15")]);
			if (arrDelay > 0.0) {
				if (!(arrDelayMin == arrDelay))
					return false;
			}
			if (arrDelay < 0.0) {
				if (arrDelayMin != 0.0)
					return false;
			}
			if (arrDelayMin >= 15.0) {
				if (delayOf15 != 1)
					return false;
			}
		}

		// Populating Hashmaps
		String carrierName = dataPerLine[fileColoumns.indexOf("CARRIER")];
		Double ticketPrice = Double.parseDouble(dataPerLine[fileColoumns.indexOf("AVG_TICKET_PRICE")]);
		populatePriceFrequency(carrierName, ticketPrice);

		// Sanity test is passed
		return true;
	}

	private static void populatePriceFrequency(String carrierName, Double ticketPrice) {
		if (carrierTicketPrice.containsKey(carrierName)) {
			// increase the frequency
			carrierFrequency.put(carrierName, carrierFrequency.get(carrierName) + 1);
			// add ticket price to that carrier name
			carrierTicketPrice.put(carrierName, carrierTicketPrice.get(carrierName) + ticketPrice);
		} else {
			// populating for the first time
			carrierFrequency.put(carrierName, 1);
			carrierTicketPrice.put(carrierName, ticketPrice);
		}

	}

	private static float calculateDateDiff(String crsArrTime, String crsDepTime) {

		int hoursDiff; // hours diff
		int minDiff; // minutes diff
		if (Integer.parseInt(crsArrTime) > Integer.parseInt(crsDepTime)) {
			hoursDiff = (Integer.parseInt(crsArrTime.substring(0, 2)) - Integer.parseInt(crsDepTime.substring(0, 2)))
					* 60;
			minDiff = (Integer.parseInt(crsArrTime.substring(2, 4)) - Integer.parseInt(crsDepTime.substring(2, 4)));
			return hoursDiff + minDiff;
		} else {
			hoursDiff = (Integer.parseInt(crsArrTime.substring(0, 2)) - Integer.parseInt(crsDepTime.substring(0, 2))
					+ 24) * 60;
			minDiff = (Integer.parseInt(crsArrTime.substring(2, 4)) - Integer.parseInt(crsDepTime.substring(2, 4)));
			return hoursDiff + minDiff;
		}
	}

	public static void main(String[] args) {
		String filePath = "C:\\Users\\Yogiraj\\Downloads\\323.csv.gz";
		analyzeFile(filePath);

	}

}

class SortValues implements Comparator<String> {
	HashMap<String, Double> h;

	public SortValues(HashMap<String, Double> h) {
		// TODO Auto-generated constructor stub
		this.h = h;
	}

	@Override
	public int compare(String arg1, String arg2) {
		// TODO Auto-generated method stub
		if (h.get(arg1) > h.get(arg2))
			return 1;
		return -1;
	}

}
