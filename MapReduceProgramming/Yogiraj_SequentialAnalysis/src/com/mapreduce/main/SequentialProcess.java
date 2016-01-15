package com.mapreduce.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import com.opencsv.CSVReader;

public class SequentialProcess {

	private ArrayList<String> fileColoumns = new ArrayList<String>();
	private String DELIMITER = ",";
//	private HashMap<String, Float> ticketPriceMap = new HashMap<String, Float>();
	public void analyzeFile(String filename)
	{
		long corruptLines=1;
		long validLines=0;
		try {
			//point to file
			GZIPInputStream inputFile = new GZIPInputStream(
					new FileInputStream("C:\\Users\\Yogiraj\\Downloads\\323.csv.gz"));

			Reader r = new InputStreamReader(inputFile, "ASCII");
			//using CSV parser
			CSVReader input = new CSVReader(r);
			//extract column names from the file
			fileColoumns=new ArrayList(Arrays.asList(input.readNext()));
			//Stores data of one line at a time
			String[] dataPerLine;
			//process each line
			while ((dataPerLine=input.readNext()) != null) {
			  if(checkValidLine(dataPerLine))
			  {
				    validLines++;
			  }
			  else
			  {
				  corruptLines++;
			  }
			}
			//release resources
			input.close();
			r.close();

		} catch (IOException e) {
		     System.out.println("Error in decompression of the file");
		}
		//Final Output
		 System.out.println("F = " + validLines);
	     System.out.println("K = " + corruptLines);
	}
	
	//sanity test
	private boolean checkValidLine(String[] dataPerLine) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm");
		//extract fields for processing
		String CRS_ARR_TIME = dataPerLine[fileColoumns.indexOf("CRS_ARR_TIME")];
		String CRS_ELAPSED_TIME = dataPerLine[fileColoumns.indexOf("CRS_ELAPSED_TIME")];
		String CRS_DEP_TIME = dataPerLine[fileColoumns.indexOf("CRS_DEP_TIME")];
		float timeZone=0;
		try {
			float CrsElapsedTime = Float.parseFloat(CRS_ELAPSED_TIME);
			//CRSArrTime and CRSDepTime should not be zero
			Date CrsArrTime = (CRS_ARR_TIME.equals("") ? null : dateFormat.parse(CRS_ARR_TIME));
			Date CrsDepTime = (CRS_DEP_TIME.equals("") ? null : dateFormat.parse(CRS_DEP_TIME));
			if(CrsArrTime.getTime() ==0.0 || CrsDepTime.getTime()==0.0) return false;
			
			//timeZone = CRSArrTime - CRSDepTime - CRSElapsedTime;
			//timeZone % 60 should be 0
			timeZone = calculateDateDiff(CRS_ARR_TIME,CRS_DEP_TIME)-CrsElapsedTime;
			if((timeZone % 60) !=0) return false;
			
		} 
		catch ()
		{
			
		}
		catch (ParseException e) {
			return false;
		}
		
		return false;
	}
	
	
	private float calculateDateDiff(String crsArrTime, String crsDepTime) {
		
		if (Integer.parseInt(crsArrTime) > Integer.parseInt(crsDepTime)){
			return (Integer.parseInt(crsArrTime.substring(0, 2)) - Integer.parseInt(crsDepTime.substring(0, 2))) * 60 +
					(Integer.parseInt(crsArrTime.substring(2, 4)) - Integer.parseInt(crsDepTime.substring(2, 4)));
		} else {
			return (Integer.parseInt(crsArrTime.substring(0, 2)) - Integer.parseInt(crsDepTime.substring(0, 2)) + 24) * 60 +
					(Integer.parseInt(crsArrTime.substring(2, 4)) - Integer.parseInt(crsDepTime.substring(2, 4)));
		}
	}

	public static void main(String[] args) {
		

	}

}
