import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Comparison {
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub
		Configuration config = new Configuration();
		String[] arguments = new GenericOptionsParser(config, args).getRemainingArgs();
		if (arguments.length != 4) {
			System.err.println("Usage: FlightClusterAnalysis <input> <output> <type of program to run> <mean,median.fast median>");
			System.exit(4);
		}
		Job job = new Job(config, "Air Flight Analysis ");
		job.setJarByClass(Comparison.class);
		job.setMapperClass(FlightClusterAnalysisMapper.class);
		boolean isMean = false, isMedian = false, isFastMedian = false;
		if(args[3].equals("mean"))
			isMean=true;
		else if (args[3].equals("median"))
			isMedian = true;
		else if(args[3].equals("fmedian"));
			isFastMedian = true;
		
		// Change the reducer logic per configuration: Mean/Median/Fast Median
		if(isMean)
		{
			job.setReducerClass(FCAMeanReducer.class);			
		}else if(isMedian)
		{
			System.out.println("Median job started...");
			job.setReducerClass(FCAMedianReducer.class);			
		}else if(isFastMedian)
		{
			job.setReducerClass(FCAFastMedianReducer.class);
		}
		else
		{
			System.out.println("Error in Settiinnnnngg!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
		}
		
		job.setNumReduceTasks(3);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(arguments[0]));
		FileOutputFormat.setOutputPath(job, new Path(arguments[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

	public static class FlightClusterAnalysisMapper extends Mapper<Object, Text, Text, Text> {

		// set csvReader to detect comma seperated values
		// key : carrierName , month
		// value : ticketPrice, Year
		//private CSVParser csvReader = new CSVParser(',', '"');

		// offset is the pointer pointing to file
		// value is single line at a time present in the data
		public void map(Object offset, Text value, Context context) throws IOException, InterruptedException {
			String[] record = value.toString().split(","); //this.csvReader.parseLine(value.toString());
			Text ticketPrice = new Text();
			boolean isMonth = true;
			Text carrierName = new Text();
			Text month = new Text();
			Text year = new Text();

			// isRecordValid : checks if the required fields are not null
			
			if (record.length > 0 && isRecordValid(record)) {
				carrierName.set(record[6].getBytes()); // coloumn 6
				month.set(record[2].getBytes()); // coloumn 2
				year.set(record[0].getBytes()); // coloumn 0
				ticketPrice.set(record[111]); // coloumn 109

				try {
					Integer.parseInt(record[2].toString());
				} catch (Exception e) {
					isMonth = false;
				}

				if (isMonth) {
					String ticketYesr = ticketPrice.toString() + "\t" + year.toString();
					context.write(new Text(carrierName.toString() + "\t" + month.toString()), new Text(ticketYesr)); // 109
				}
			}
		}

		private boolean isRecordValid(String[] record) {
			if (record.length != 112 || record[0] == "" || record[2] == "" || record[6] == "" || record[111] == "")
				return false;
			return true;
		}

	}

	public static class FCAFastMedianReducer extends Reducer<Text, Text, Text, Text> {

		HashSet<String> activeIn2015 = new HashSet<String>();

		// outputList stores the final output where carrier is present in 2015
		static List<String> outputList = new ArrayList<String>();
		static List<Double> priceList = new ArrayList<Double>();

		// key : CarrierName, Month
		// Values : tuples in the form (ticketprice, year) mapped to key
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

			int size = 0;
		//	double totalPrice = 0;
			int year = 1;
			StringBuilder output = new StringBuilder();
			String[] keyData = key.toString().split("\t");
			String[] data;
			// for each value
			for (Text value : values) {
				data = value.toString().split("	");
				size++;
				year = Integer.parseInt(data[1]);
			//	totalPrice += Double.parseDouble(data[0]);
				priceList.add(Double.parseDouble(data[0]));
				// for flights active in 2015, populate activeIn2015
				if (year == 2015) {
					// put carrier name
					activeIn2015.add(keyData[0]);
				}
			}

			Double fastMedian = 0.0;
			
			
			if(size%2 == 0)
			{
				fastMedian = (quickSelect(priceList, size/2) + quickSelect(priceList, size/2 + 1))/2;
			}else{
				fastMedian = quickSelect(priceList, (size/2 + 1));
			}
			// create output string
			output.append(keyData[0] + "\t" + keyData[1] + "\t" + fastMedian + "\t" + size);
			// putting carrier name and output (key , average, count)
			outputList.add(output.toString());
			
			// Clean the sorted price list
			priceList = new ArrayList<Double>();

		}

		// cleanup is called once entire processing is completed on this
		// processing
		// only flights active in Jan 2015 is given out
		@Override
		protected void cleanup(Reducer<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			super.cleanup(context);
			for (int i = 0; i < outputList.size(); i++) {
				String str = outputList.get(i);
				String[] d = str.split("\t");
				// if the record is present in the 2015 write it to output
				if (activeIn2015.contains(d[0])) {
					context.write(new Text(d[0] + "\t" + d[1]), new Text(d[2] + "\t" + d[3]));
				}
			}
		}
	}

	public static class FCAMedianReducer extends Reducer<Text, Text, Text, Text> {

		HashSet<String> activeIn2015 = new HashSet<String>();

		// outputList stores the final output where carrier is present in 2015
		static List<String> outputList = new ArrayList<String>();
		static List<Double> sortedPriceList = new ArrayList<Double>();

		// key : CarrierName, Month
		// Values : tuples in the form (ticketprice, year) mapped to key
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

			int size = 0;
		//	double totalPrice = 0;
			int year = 1;
			StringBuilder output = new StringBuilder();
			String[] keyData = key.toString().split("\t");
			String[] data;
			// for each value
			for (Text value : values) {
				data = value.toString().split("\t");
				size++;
				year = Integer.parseInt(data[1]);
			//	totalPrice += Double.parseDouble(data[0]);
				sortedPriceList.add(Double.parseDouble(data[0]));
				// for flights active in 2015, populate activeIn2015
				if (year == 2015) {
					// put carrier name
					activeIn2015.add(keyData[0]);
				}
			}

			Double median = 0.0;
			Collections.sort(sortedPriceList);
			if(size%2 == 0)
			{
				median = (sortedPriceList.get(size/2) + sortedPriceList.get(size/2 + 1)) /2;
			}else{
				median = sortedPriceList.get(size/2 + 1) /2;
			}
			// create output string
			output.append(keyData[0] + "\t" + keyData[1] + "\t" + median + "\t" + size);
			// putting carrier name and output (key , average, count)
			outputList.add(output.toString());
			
			// Clean the sorted price list
			sortedPriceList = new ArrayList<Double>();
		}

		// cleanup is called once entire processing is completed on this
		// processing
		// only flights active in Jan 2015 is given out
		@Override
		protected void cleanup(Reducer<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			super.cleanup(context);
			System.out.println("Clean up called");
			System.out.println("outputList.size(): "+outputList.size()+"activeIn2015: "+activeIn2015.size());
			for (int i = 0; i < outputList.size(); i++) {
				String str = outputList.get(i);
				String[] d = str.split("\t");
				// if the record is present in the 2015 write it to output
				if (activeIn2015.contains(d[0])) {
					context.write(new Text(d[0] + "\t" + d[1]), new Text(d[2] + "\t" + d[3]));
					System.out.println("Output!!!!: "+d[0] + "\t" + d[1] + "\t" + d[2] + "\t" + d[3]);
				}
			}
		}
	}

	public static Double quickSelect(List<Double> values, int k)
    {
        int left = 0;
        int right = values.size() - 1;
        Random rand = new Random();
        while(true)
        {
            int partionIndex = rand.nextInt(right - left + 1) + left;
            int newIndex = partition(values, left, right, partionIndex);
            int q = newIndex - left + 1;
            if(k == q)
            {
                return values.get(newIndex);
            }
            else if(k < q)
            {
                right = newIndex - 1;
            }
            else
            {
                k -= q;
                left = newIndex + 1;
            }
        }
    }
	
    private static int partition(List <Double> values, int left, int right, int partitionIndex)
    {
    	Double partionValue = values.get(partitionIndex);
        int newIndex = left;
        Double temp = values.get(partitionIndex);
        values.set(partitionIndex, values.get(right));
        values.set(right, temp);
        for(int i = left; i < right; i++)
        {
            if(values.get(i).compareTo(partionValue) < 0)
            {
                temp = values.get(i);
                values.set(i, values.get(newIndex));
                values.set(newIndex, temp);
                newIndex++;
            }
        }
        temp = values.get(right);
        values.set(right, values.get(newIndex));
        values.set(newIndex, temp);
        return newIndex;
    }
	
	public static class FCAMeanReducer extends Reducer<Text, Text, Text, Text> {

		HashSet<String> activeIn2015 = new HashSet<String>();

		// outputList stores the final output where carrier is present in 2015
		static List<String> outputList = new ArrayList<String>();

		// key : CarrierName, Month
		// Values : tuples in the form (ticketprice, year) mapped to key
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

			double size = 0.0;
			double totalPrice = 0;
			int year = 1;
			StringBuilder output = new StringBuilder();
			String[] keyData = key.toString().split("	");
			String[] data;
			// for each value
			for (Text value : values) {
				data = value.toString().split("	");
				size++;
				year = Integer.parseInt(data[1]);
				totalPrice += Double.parseDouble(data[0]);

				// for flights active in 2015, populate activeIn2015
				if (year == 2015) {
					// put carrier name
					activeIn2015.add(keyData[0]);
				}
			}

			// create output string
			output.append(keyData[0] + "\t" + keyData[1] + "\t" + (double) totalPrice / size + "\t" + size);
			// putting carrier name and output (key , average, count)
			outputList.add(output.toString());
			totalPrice = 0.0;

		}

		// cleanup is called once entire processing is completed on this
		// processing
		// only flights active in Jan 2015 is given out
		@Override
		protected void cleanup(Reducer<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			super.cleanup(context);
			for (int i = 0; i < outputList.size(); i++) {
				String str = outputList.get(i);
				String[] d = str.split("	");
				// if the record is present in the 2015 write it to output
				if (activeIn2015.contains(d[0])) {
					context.write(new Text(d[0] + "\t" + d[1]), new Text(d[2] + "\t" + d[3]));
				}
			}
		}
	}

}

