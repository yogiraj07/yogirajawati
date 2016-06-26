package Pred.PredictionImplementation;

import java.io.DataInput;

import java.io.DataOutput;

import java.io.IOException;

import java.util.ArrayList;

import java.util.List;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.conf.Configured;

import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.BooleanWritable;

import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.io.LongWritable;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.io.Writable;

import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.mapreduce.Mapper;

import org.apache.hadoop.mapreduce.Reducer;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.util.GenericOptionsParser;

import weka.classifiers.trees.RandomForest;

import weka.core.Attribute;

import weka.core.DenseInstance;

import weka.core.Instance;

import weka.core.Instances;

import weka.core.SerializationHelper;

/**
 ** The Map-Reduce Job to read History files and generate a Model
 * 
 * @author Yogiraj Awati and Ashish Kalbhor
 * 
 */

public class Prediction

{
	static String SEPERATOR = ",";

	public static void main(String args[]) throws Exception

	{
		Configuration config = new Configuration();
		// setting format of seperator

		config.set("mapred.textoutputformat.separator", SEPERATOR);
		String[] arguments = new GenericOptionsParser(config, args).getRemainingArgs();
		if (arguments.length != 3) {
			System.err.println("Usage: Prediction <input> <output>");
			System.exit(4);
		}
		Job job = new Job(config, "Prediction");
		job.setJarByClass(Prediction.class);
		job.setMapperClass(PredictionMapper.class);
		job.setReducerClass(PredictionReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(FlightDetails.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(2);
		FileInputFormat.addInputPath(job, new Path(arguments[1]));
		FileOutputFormat.setOutputPath(job, new Path(arguments[2]));
		job.waitForCompletion(true);

	}

	static class FlightDetails implements Writable {
		IntWritable year;
		IntWritable month;
		Text carrierID;
		IntWritable day_of_week;
		IntWritable day_of_month;
		IntWritable originID;;
		IntWritable destinationID;
		IntWritable crsArrTime;
		IntWritable crsDepTime;
		Text flightNumber;
		BooleanWritable Flag;
		BooleanWritable Holiday;
		IntWritable delay;

		// Default constructor for initialization
		public FlightDetails() {

			year = new IntWritable();
			month = new IntWritable();
			day_of_week = new IntWritable();
			day_of_month = new IntWritable();
			originID = new IntWritable();
			destinationID = new IntWritable();
			crsArrTime = new IntWritable();
			crsDepTime = new IntWritable();
			flightNumber = new Text();
			carrierID = new Text();
			Flag = new BooleanWritable();
			Holiday = new BooleanWritable();
			delay = new IntWritable();
		}

		// Parameterized Constructor

		public FlightDetails(int y, int m, int dw, int dm, int oId, int dId, int arr, int dep, String fno, String cId,

				boolean f, boolean h, int del) {
			year = new IntWritable(y);
			month = new IntWritable(m);
			day_of_week = new IntWritable(dw);
			day_of_month = new IntWritable(dm);
			originID = new IntWritable(oId);
			destinationID = new IntWritable(dId);
			crsArrTime = new IntWritable(arr);
			crsDepTime = new IntWritable(dep);
			flightNumber = new Text(fno);
			carrierID = new Text(cId);
			Flag = new BooleanWritable(f);
			Holiday = new BooleanWritable(h);
			delay = new IntWritable(del);
		}
		
		@Override

		public void readFields(DataInput in) throws IOException {

			year.readFields(in);
			month.readFields(in);
			day_of_week.readFields(in);
			day_of_month.readFields(in);
			originID.readFields(in);
			destinationID.readFields(in);
			crsArrTime.readFields(in);
			crsDepTime.readFields(in);
			flightNumber.readFields(in);
			carrierID.readFields(in);
			Flag.readFields(in);
			Holiday.readFields(in);
			delay.readFields(in);
		}

		@Override

		public void write(DataOutput dataoutput) throws IOException

		{
			year.write(dataoutput);
			month.write(dataoutput);
			day_of_week.write(dataoutput);
			day_of_month.write(dataoutput);
			originID.write(dataoutput);
			destinationID.write(dataoutput);
			crsArrTime.write(dataoutput);
			crsDepTime.write(dataoutput);
			flightNumber.write(dataoutput);
			carrierID.write(dataoutput);
			Flag.write(dataoutput);
			Holiday.write(dataoutput);
			delay.write(dataoutput);
		}

		

		public IntWritable getYear() {

			return year;

		}

		public IntWritable getMonth() {

			return month;

		}

		public Text getCarrierID() {

			return carrierID;

		}

		public IntWritable getDay_of_week() {

			return day_of_week;

		}

		public IntWritable getDay_of_month() {

			return day_of_month;

		}

		public IntWritable getOriginID() {

			return originID;

		}

		public IntWritable getDestinationID() {

			return destinationID;

		}

		public IntWritable getCrsArrTime() {

			return crsArrTime;

		}

		public IntWritable getCrsDepTime() {

			return crsDepTime;

		}

		public Text getFlightNumber() {

			return flightNumber;

		}

		public BooleanWritable getFlag() {

			return Flag;

		}

		public BooleanWritable getHoliday() {

			return Holiday;

		}

		public IntWritable getDelay() {

			return delay;

		}

	}

	/**
	 * Mapper class to generate all the fields.
	 * 
	 * @author Yogiraj Awati
	 * 
	 */

	public static class PredictionMapper extends Mapper<Object, Text, Text, FlightDetails>

	{
		static int count;

		public void map(Object offset, Text value, Context context) throws IOException, InterruptedException

		{
			String carrierID = null; // 6
			int year = 0;
			int month = 0;
			int day_of_month = 0;
			int day_of_week = 0;
			int originId = 0;
			int destinationId = 0;
			int crsArrTime = 0;
			int crsDepTime = 0;
			String flightNumber = null;
			boolean Flag = true;
			boolean Holiday = true;
			String line = value.toString();
			String newLine = line.replaceAll("\"", "");
			String formattedLine = newLine.replaceAll(", ", ":");
			String[] flightDetails = formattedLine.split(",");
			int delay = 0;
			boolean nullCheck = true;
			try {
				// if the record is valid
				if (isRecordValid(flightDetails)) {
					day_of_month = Integer.parseInt(flightDetails[3]);
					month = Integer.parseInt(flightDetails[2]);
					year = Integer.parseInt(flightDetails[0]);
					carrierID = flightDetails[6];
					day_of_week = Integer.parseInt(flightDetails[4]);
					originId = Integer.parseInt(flightDetails[11]);
					destinationId = Integer.parseInt(flightDetails[20]);
					flightNumber = flightDetails[10];
					crsArrTime = Integer.parseInt(flightDetails[40]);
					crsDepTime = Integer.parseInt(flightDetails[29]);
					Flag = true;
					Holiday = true;
					delay = (int) Double.parseDouble(flightDetails[42]);
				} else {
					nullCheck = false;

				}

			} catch (Exception e)

			{
				nullCheck = false;
			}

			if (nullCheck) {
				FlightDetails details = new FlightDetails(year, month, day_of_week, day_of_month, originId,
						destinationId, crsArrTime, crsDepTime, flightNumber, carrierID, Flag, Holiday, delay);
				Text mappingKey = new Text(month + "");
				context.write(mappingKey, details);
				count++;
			}

		}

		// returns false if the field is empty
		private boolean isRecordValid(String[] record) {

			if (record.length < 110 || record[0].isEmpty() || record[6].isEmpty() || record[3].isEmpty()
					|| record[2].isEmpty() || record[4].isEmpty() || record[41].isEmpty() || record[11].isEmpty()
					|| record[10].isEmpty() || record[20].isEmpty() || record[30].isEmpty() || record[42].isEmpty())
				return false;
			return true;

		}

		@Override

		protected void cleanup(Mapper<Object, Text, Text, FlightDetails>.Context context)

				throws IOException, InterruptedException {

			// TODO Auto-generated method stub

			super.cleanup(context);
			System.out.println("Total Values from Mapper :::::::::::::::::::::::::::" + count);

		}

	}

	// Key: month

	// Value: year, month, day_of_week, day_of_month, originId, destinationId,

	// crsArrTime, crsDepTime, flightNumber, carrierID, Flag, Holiday

	// Author : Yogiraj Awati

	public static class PredictionReducer extends Reducer<Text, FlightDetails, Text, Text>

	{
		@Override
		protected void reduce(Text key, Iterable<FlightDetails> values,

				Reducer<Text, FlightDetails, Text, Text>.Context context)

				throws IOException, InterruptedException {

			// create attribute list

			Attribute yearAttr = new Attribute("year");
			Attribute monthAttr = new Attribute("month");
			Attribute dow = new Attribute("dow");
			Attribute dom = new Attribute("dom");
			Attribute oid = new Attribute("oid");
			Attribute did = new Attribute("did");
			Attribute crsarrtime = new Attribute("crsarrtime");
			Attribute crsdeptime = new Attribute("crsdeptime");
			Attribute flno = new Attribute("flno");
			Attribute flagAttr = new Attribute("flagAttr");
			Attribute holidy = new Attribute("holidy");
			Attribute classAttribute = new Attribute("classAttribute");
			List<String> fvClassVal = new ArrayList<>(2);
			fvClassVal.add("Delayed");
			fvClassVal.add("NotDelayed");
			Attribute delayAttribute = new Attribute("delay", fvClassVal);
			ArrayList<Attribute> ls = new ArrayList<>();
			ls.add(yearAttr);
			ls.add(monthAttr);
			ls.add(dow);
			ls.add(dom);
			ls.add(oid);
			ls.add(did);
			ls.add(crsarrtime);
			ls.add(crsdeptime);
			ls.add(flno);
			ls.add(flagAttr);
			ls.add(holidy);
			ls.add(delayAttribute);
			// create training instance
			Instances isTrainingSet = new Instances("Model", ls, 0);
			isTrainingSet.setClassIndex(11);
			int c = 0;
			for (FlightDetails f : values) {
				c++;
				Instance iExample = new DenseInstance(12);
				iExample.setValue((Attribute) ls.get(0), f.getYear().get());
				iExample.setValue((Attribute) ls.get(1), f.getMonth().get());
				iExample.setValue((Attribute) ls.get(2), f.getDay_of_week().get());
				iExample.setValue((Attribute) ls.get(3), f.getDay_of_month().get());
				iExample.setValue((Attribute) ls.get(4), f.getOriginID().get());
				iExample.setValue((Attribute) ls.get(5), f.getDestinationID().get());
				iExample.setValue((Attribute) ls.get(6), f.getCrsArrTime().get());
				iExample.setValue((Attribute) ls.get(7), f.getCrsDepTime().get());
				iExample.setValue((Attribute) ls.get(8), f.getFlightNumber().hashCode());
				iExample.setValue((Attribute) ls.get(9), f.getFlag().hashCode());
				iExample.setValue((Attribute) ls.get(10), f.getHoliday().hashCode());

				// if delay > 0, set the value to 1 else 0
				if (Integer.parseInt(f.delay.toString()) > 0)

				{
					iExample.setValue((Attribute) ls.get(11), 1);

				}

				else {
					iExample.setValue((Attribute) ls.get(11), 0);
				}

				// add the instance

				isTrainingSet.add(iExample);

			}

			try {

				// create random forest per key and write to file
				RandomForest r = new RandomForest();
				r.setNumTrees(4);
				r.setMaxDepth(5);
				r.buildClassifier(isTrainingSet);
				weka.core.SerializationHelper.write("./models/" + key + ".model", r);

			} catch (Exception e) {
				e.printStackTrace();

			}

		}

	}

}
