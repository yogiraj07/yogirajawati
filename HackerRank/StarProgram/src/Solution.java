import java.io.*;
import java.util.*;

public class Solution {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
       Scanner sc= new Scanner(System.in);
       int steps= sc.nextInt();
       StringBuilder output=new StringBuilder();
       for (int i= steps-1;i>=0;i--)
       {
    	   output=addData(i,output," ");
    	   output=addData(steps-i,output,"#");
    	   
       }
       System.out.println(output.toString());
	}

	private static StringBuilder addData(int i, StringBuilder output, String character) {
		for(int k=1;k<=i;k++)
		{
			output.append(character);
		}
		return character.equals("#") ?output.append("\n"):output;
	}

}
