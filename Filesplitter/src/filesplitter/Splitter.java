package filesplitter;

//package pkg2;

import java.io.*;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

class Splitter 
{
	
         
	 void takeInput(String str,String assembledFileName,String length) throws IOException
	{
		
		int splitLength;

		
		try
		{
			
			{
				splitLength=Integer.parseInt(length);


				SplitFiles ob1=new SplitFiles(str,splitLength);//splitting file

				SplitListOnly ob2=new SplitListOnly(str,assembledFileName);//returns array list of splitted files for reorganising
				
                                ob2.batchFileMaker();
			}
			
		}
		catch(NumberFormatException e)
		{
			System.out.println("Problem in number format...");
			e.printStackTrace();
		}
                 
	}
}
