package filesplitter;

//package pkg2;

import java.io.*;

public class OnlyExt implements FilenameFilter
{
	String ext,fileName;

	OnlyExt(String ext,String fileName)
	{
		try
		{
			this.ext="."+ext;
			this.fileName=fileName;
		}
		catch(Exception e)
		{
			System.out.println("Exception occurred in OnlyExt(String ext,String fileName) +construcctor.");
		}
	}
	public boolean accept(File dir,String name)
	{
		return name.endsWith(ext) && name.startsWith(fileName);

	}

}
