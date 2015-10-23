package filesplitter;

//package pkg2;

import java.io.*;
import java.util.*;

class SplitListOnly
{
	File f1;
	File f2;
	String[] s;
	String str;
	String assembledFileName;
	SplitListOnly(String fileName,String assembledFileName) throws IOException
	{
		try
		{
			f1=new File(fileName);
			f2=new File(f1.getParent()); //creates file at destination folder
			this.assembledFileName=assembledFileName;
			FilenameFilter only=new OnlyExt("spt",f1.getName());

			s=f2.list(only); //returns files in string array
		}
		catch(Exception e)
		{
			System.out.println("Exception occurred in SplitListOnly(String fileName,String assembledFileName) constructor.");
			e.printStackTrace();
		}
	}

	void batchFileMaker()
	{
		try
		{
			FileWriter fw=new FileWriter(f2.getPath()+"\\"+"Assemble.bat");//final batch file
			fw.write("@echo off\n");
			fw.write("copy /B ");
			for(int i=0;i<s.length;i++)//appending all files
			{
				str=new String(f1.getPath()+"."+(i+1)+".spt");
				fw.write(str);
				if((i+1)<s.length)
					fw.write("+");

			}
			str=(f1.getName()).substring((f1.getName()).indexOf(".")+1,(f1.getName()).length());
			fw.write(" "+f2.getPath()+"\\"+assembledFileName+"."+str);
			fw.write("\necho The files have been assembelled successfully..........");
			fw.write("\n pause");
			fw.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception occurred in void batchFileMaker() method.");
			e.printStackTrace();
		}
	}


	
}