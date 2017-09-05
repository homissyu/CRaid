package com.jay.util;

import java.io.*;
import java.util.*;
import java.util.Scanner;
import java.io.BufferedWriter;

public class Encoder4Ham {
	private static ArrayList<String> list = new ArrayList<String>();		//each string will be put into this list after being read from file
	public static boolean even;											
	public static boolean odd;		
	
	public static void main(String args[])
	{
		System.out.println("Enter input filename: ");
	    Scanner scanner = new Scanner(System.in);
	    String iname = scanner.nextLine();
	
	    
	    System.out.println("Enter output filename: ");
	    String oname = scanner.nextLine();
	    System.out.println("even or odd: ");
	    String parity = scanner.nextLine();
	    scanner.close();
	    if(parity.equals("even"))								//finds out bits should be encoded with even or odd parity
	    	even=true;
	    else
	    	odd=true;
	    
		File file = new File("c://"+iname+".txt");
		Scanner read;	
		String line;
		try
		{
			read = new Scanner(file);
			while(read.hasNextLine())
			{			
				line=read.nextLine();								//reads in each line
				line=ham(line);										//send line to method ham to have parity bits inserted
				list.add(line);
			}
			read.close();											//inserts bits into data to make codeword
		} 
		catch(FileNotFoundException fe) {
			System.out.println("File not found!");
		}
		try {
	    	 
			File f = new File("c://"+oname+".txt");
 
			
			if (!f.exists()) {						
				f.createNewFile();									// if file doesnt exists, then creates it
			}
 
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i=0;i<list.size();i++)							//writes every string in list to file
			{
				bw.write(list.get(i));
				bw.newLine();
			}
			bw.close();
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
		System.exit(1);												//closes program
	}
	
	
	public static ArrayList<Integer> stringToIntArr(String s)			//turns string into int arraylist
	{
		ArrayList<Integer> a= new ArrayList<Integer>();
		for(int i=0;i<s.length();i++)
		{
			a.add(Character.getNumericValue(s.charAt(i)));
			System.out.println(Integer.toBinaryString(s.charAt(i)));
		}
		return a;
	}
	
	public static ArrayList<Integer> createParityWord(ArrayList<Integer> data){
		int totalBits=data.size();
		int value=0;
		ArrayList<Integer> codeword=data;		
		int temp=0;
		for(int i=1;i<totalBits+1;i=i*2)							//sets 1st 2nd 4th 8th 16th.... element to -1 because that's where the parity bits go
		{
			codeword.add(i-1, -1);
			totalBits++;
		}	
		for(int i=1;i<totalBits+1;i=i*2)							//array starts at 0 but i starts at 1 
		{
			value=0;
			temp=i;
			for (int j=i;j<totalBits+1;j=j+(temp*2))				//skips ahead index elements
			{
					for(int p=j;p<j+i && p<totalBits+1;p++)			//counts amount of 1's in index elements before skipping ahead index elements
					{
						if(data.get(p-1)!=-1)						//if data.get(p-1)==-1 then its at current element and shouldn't be checked
						{
							value+=data.get(p-1);
						}
					}
			}
			value=value%2;
			if(odd==true)										//if odd is true then the value should be changed but if its true modding value by 2 will give correct answer
			{
				if(value==1)
				{
					value=0;
				}
				else
				{
					value=1;
				}
			}
			codeword.set(i-1, value);									//since loop starts at 1 assigns i-1 value
		}

		return codeword;
	}
	
	public static ArrayList<Integer> createCodeWord(ArrayList<Integer> data)
	{
		System.out.println(data);
		
		int totalBits=data.size();
		int value=0;
		ArrayList<Integer> codeword=data;		
		int temp=0;
		for(int i=1;i<totalBits+1;i=i*2)							//sets 1st 2nd 4th 8th 16th.... element to -1 because that's where the parity bits go
		{
			codeword.add(i-1, -1);
			totalBits++;
		}	
		for(int i=1;i<totalBits+1;i=i*2)							//array starts at 0 but i starts at 1 
		{
			value=0;
			temp=i;
			for (int j=i;j<totalBits+1;j=j+(temp*2))				//skips ahead index elements
			{
					for(int p=j;p<j+i && p<totalBits+1;p++)			//counts amount of 1's in index elements before skipping ahead index elements
					{
						if(data.get(p-1)!=-1)						//if data.get(p-1)==-1 then its at current element and shouldn't be checked
						{
							value+=data.get(p-1);
						}
					}
			}
			value=value%2;
			if(odd==true)										//if odd is true then the value should be changed but if its true modding value by 2 will give correct answer
			{
				if(value==1)
				{
					value=0;
				}
				else
				{
					value=1;
				}
			}
			codeword.set(i-1, value);									//since loop starts at 1 assigns i-1 value
		}

		return codeword;
	}
	public static String arrToString(ArrayList<Integer> a)				//turns arraylist of ints into string
	{
		int [] c = new int[a.size()];
		for(int i=0;i<a.size();i++)						
		{
			c[i]=a.get(i);
		}
		String numbers= Arrays.toString(c);
		numbers = numbers.replaceAll(", ", "").replace("[", "").replace("]", "");			//Arrays.toString(c) left some chars and I needed to replace them
		return numbers;
	}
	
	public static String parity(String s) {
		ArrayList<Integer> data= new ArrayList<Integer>();
		ArrayList<Integer> codeword= new ArrayList<Integer>();
		data=stringToIntArr(s);										//turns string into int arraylist
		codeword=createParityWord(data);								//calls method createCodeWord to turn the arraylist into an arraylist parity bits removed and data bits corrected if needed
		return arrToString(codeword);	
	}
	
	public static String ham(String s)								//returns string with parity bits inserted
	{
		ArrayList<Integer> data= new ArrayList<Integer>();
		ArrayList<Integer> codeword= new ArrayList<Integer>();
		data=stringToIntArr(s);										//turns string into int arraylist
		codeword=createCodeWord(data);								//calls method createCodeWord to turn the arraylist into an arraylist parity bits removed and data bits corrected if needed
		return arrToString(codeword);								
	}

}