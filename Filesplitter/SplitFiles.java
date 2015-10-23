package pkg2;

import java.io.*;


class SplitFiles
{
	FileInputStream fin;
	FileOutputStream fout;
	int len;
	int splitlen;
	String str;

	SplitFiles(String fileName,int splitlength)
	{
		try
		{
			fin=new FileInputStream(fileName);//creates actual connection to the file 
			str=fileName;
			len=0;
			splitlen=splitlength;
			Split();

		}
		catch(FileNotFoundException e)
		{
			System.out.println("File not found.");
		}
		catch(IOException e)
		{
			System.out.println("IOException generated");
		}
	}
	void Split()
	{

		try
		{
			int i=0;
			FileInputStream fin=new FileInputStream(str);
			int c=fin.read();//it reads 1 byte from file at a time n return -1 when end of file
			while(c!=-1) // first loop is to check whether file is empty or not........if file is empty it doesn't enter loop
			{
				FileOutputStream fw=new FileOutputStream(str+"."+(i+1)+".spt");  //create a connection 
				while(c!=-1 && len<splitlen)//writing given amt of data in file
				{
					fw.write(c);   //output to created file
					c=fin.read();  //read next one byte
					len++;
				}
				len=0;   //after limit is reached reset the length
				fw.close();
				i++;
			}

		}
		catch(Exception e)
		{
			System.out.println("gg");
			e.printStackTrace();
		}
	}

}