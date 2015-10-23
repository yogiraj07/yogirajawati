package filesplitter;

//4.txtpackage pkg2;

import java.io.*;
import javax.swing.JOptionPane;


class SplitFiles extends Splitter
{
	FileInputStream fin;
	FileOutputStream fout;
	int len;
	int splitlen;
	String str;

	SplitFiles(String fileName,int splitlength) throws IOException
	{       //super();
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
                    JOptionPane.showMessageDialog(null, "My Goodness,File Doesn't Exists!!");
                   // FileSplitterView.reset();
                     
                  //FileSplitterView f = new FileSplitterView();
      
                    //f.reset();
                     //f.jTextField1.setText("");
       
                    
                   
                    
                    //System.out.println("File not found.");
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
			int c=fin.read();//it reads 1 byte from file and return -1 when end of file
			while(c!=-1)
			{
				FileOutputStream fw=new FileOutputStream(str+"."+(i+1)+".spt");
				while(c!=-1 && len<splitlen)//writing given amt of data in file
				{
					fw.write(c);
					c=fin.read();
					len++;
				}
				len=0;
				fw.close();
				i++;
			}
                        JOptionPane.showMessageDialog(null, "File Successfully Splitted!!");
                         
		}
		catch(Exception e)
		{
			System.out.println("gg");
			e.printStackTrace();
		}
	}

}