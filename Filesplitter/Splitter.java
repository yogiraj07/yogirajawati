package pkg2;

import java.io.*;

class Splitter
{
	public static void main(String[] args) throws IOException
	{
		takeInput(args);
	}

	static void takeInput(String[] args) throws IOException
	{
		String str;
		String assembledFileName;
		int splitLength;

		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));//takes filename from keyboard

		try
		{
			if(args.length==0)
			{
				System.out.println("Enter the filename (with extension):");
				str=br.readLine();
				System.out.println("Enter the name of the file after assembling(without extension):");
				assembledFileName=br.readLine();
				System.out.println("Enter the split size (in bytes):");
				splitLength=Integer.parseInt(br.readLine());


				SplitFiles ob1=new SplitFiles(str,splitLength);//splitting file

				SplitListOnly ob2=new SplitListOnly(str,assembledFileName);//returns array list of splitted files for reorganising
				ob2.batchFileMaker();
			}
			else if(args.length>1)
			{
				System.out.println("Invalid arguments.");
				System.exit(1);
			}
		}
		catch(NumberFormatException e)
		{
			System.out.println("Problem in number format...");
			e.printStackTrace();
		}
	}
}
