package com.mapreduce.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

public class SequentialProcess {
	public static void main(String[] args) {
		try {
			GZIPInputStream inputFile=new GZIPInputStream(new FileInputStream("C:\\Users\\Yogiraj\\Downloads\\323.csv.gz"));
		    
			BufferedReader  r = new BufferedReader (new InputStreamReader(inputFile));
		    String temp = r.readLine();
		    //String[] data;
		    int i =1;
			while(temp !=null)
		    {
		    i++;
		    	temp = r.readLine();
		    }
			System.out.println(i);
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
