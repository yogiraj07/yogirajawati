package com.algorithm.customhash;
// Author : Yogiraj Awati
//Note : Name the input file as alice.txt
//       Output is written to output.txt
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;



public class Solution {
	static final int SIZE = 16;
	public KeyValue table[] = new KeyValue[SIZE];

	// Reference :
	// http://tekmarathon.com/2013/03/11/creating-our-own-hashmap-in-java/
	// Following method evenly distributes hashes with collisions.
	int getOptimumHash(int hash) {
		hash ^= (hash >>> 20) ^ (hash >>> 12);
		return hash ^ (hash >>> 7) ^ (hash >>> 4);

	}

	// Following function gives index of the hash in the table
	int getTableIndex(int hash) {
		return (hash & (SIZE - 1));
	}

	//inserts value in the hash table
	public void insert(String key, int value) {
		if (key == null)
			return;
		KeyValue newEntry = new KeyValue(key, value);
		int tempHash = newEntry.hashCode();
		int hash = getOptimumHash(tempHash);
		int index = getTableIndex(hash);
		KeyValue currentElement = table[index];
		for (; currentElement != null; currentElement = currentElement.next) {
			if (currentElement.key.equals(key)) {
				int oldValue = currentElement.getValue();
				currentElement.setValue(oldValue + 1);
				return;
			}
		}
		// key not found. need to insert new key
		newEntry.next = table[index];
		table[index] = newEntry;

	}

	//deletes key if available in the list of the hash
	public void delete(String key) {
		if (key == null)
			return;
		KeyValue newEntry = new KeyValue(key, 1);
		int tempHash = newEntry.hashCode();
		int hash = getOptimumHash(tempHash);
		int index = getTableIndex(hash);
		KeyValue currentElement = table[index];
		// if the first element in the list is the key then move the list
		// forward, remove first element
		if (currentElement.key.equals(key)) {
			table[index] = currentElement.next;
			return;
		}
		KeyValue prev = table[index];
		currentElement = currentElement.next;
		// if second element in the list is the desired key to be removed
		if (currentElement.key.equals(key)) {
			prev.next = currentElement.next;
			return;
		}

		// else find the key and point previous node to currents next
		for (; currentElement != null; currentElement = currentElement.next) {
			if (currentElement.key.equals(key)) {
				prev.next = currentElement.next;
				return;
			} else {
				prev = currentElement;
			}
		}
	}

	//Displays entire hash table
	public void printHashTable() {
		KeyValue temp;
		for (int i = 0; i < table.length; i++) {
			temp = table[i];
			printList(temp);

		}
	}

	private void printList(KeyValue temp) {
		// TODO Auto-generated method stub

		while (temp != null) {
			System.out.println("Word : " + temp.key + " Count: " + temp.value);
			;
			temp = temp.next;

		}

	}

	//lists all keys in the hash table
	private void listAllKeys() {
		// TODO Auto-generated method stub
		KeyValue temp;
		for (int i = 0; i < table.length; i++) {
			temp = table[i];
			while (temp != null) {
				System.out.println("Key : " + temp.key);
				;
				temp = temp.next;

			}

		}

	}

	//returns true if the key is present in the hash table
	private Boolean find(String key) {

		if (key == null)
			return false;
		KeyValue newEntry = new KeyValue(key, 1);
		int tempHash = newEntry.hashCode();
		int hash = getOptimumHash(tempHash);
		int index = getTableIndex(hash);
		KeyValue currentElement = table[index];
		for (; currentElement != null; currentElement = currentElement.next) {
			if (currentElement.key.equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	//returns the index of the key if the key is present otherwise -1
	private int findAndGetIndex(String key)
	{
		if (key == null)
			return -1;
		KeyValue newEntry = new KeyValue(key, 1);
		int tempHash = newEntry.hashCode();
		int hash = getOptimumHash(tempHash);
		int index = getTableIndex(hash);
		KeyValue currentElement = table[index];
		for (; currentElement != null; currentElement = currentElement.next) {
			if (currentElement.key.equals(key)) {
				return index;
			}
		}
		return -1;
	}
	
	
	/*Increase(key) : find that key and increase the value by 1. If it doesnt exist, add the key one with value 1
	  Increase(key,val) : find that key and increase the value by val. If it doesnt exist, add the key one with value "val"
	  change(key, val) : find that key, or create a new one if it doesnt exist, and change/make the value "val"
	*/
	void increaseKey (String key)
	{
		int i=0;
		if((i=findAndGetIndex(key))!=-1)
		{
			KeyValue currentElement = table[i];
			for (; currentElement != null; currentElement = currentElement.next) {
				if (currentElement.key.equals(key)) {
					int oldValue = currentElement.getValue();
					currentElement.setValue(oldValue + 1);
					return;
				}
			}
		}
		else
		{
			insert(key, 1);
		}
	}
	
	void increaseKey (String key,int val)
	{
		int i=0;
		if((i=findAndGetIndex(key))!=-1)
		{
			KeyValue currentElement = table[i];
			for (; currentElement != null; currentElement = currentElement.next) {
				if (currentElement.key.equals(key)) {
					int oldValue = currentElement.getValue();
					currentElement.setValue(oldValue + val);
					return;
				}
			}
		}
		else
		{
			insert(key, val);
		}
	}
	
	void changeKey (String key,int val)
	{
		int i=0;
		if((i=findAndGetIndex(key))!=-1)
		{
			KeyValue currentElement = table[i];
			for (; currentElement != null; currentElement = currentElement.next) {
				if (currentElement.key.equals(key)) {
					currentElement.setValue(val);
					return;
				}
			}
		}
		else
		{
			insert(key, val);
		}
	}
	
	void writeOutputToFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
		// Displays entire hash table
		KeyValue temp;
		for (int i = 0; i < table.length; i++) {
			temp = table[i];
			while (temp != null) {
				writer.write("Word : " + temp.key + "---- Count: " + temp.value + "\n");
				temp = temp.next;

			}

		}
		writer.close();

	}
	//Driver Program

	public static void main(String[] args) throws IOException {
		Solution s = new Solution();
		String inputLine = null;
		Scanner scanner = new Scanner(System.in);
	

		// Reading input file
		BufferedReader readFile = null;
		try {
			readFile = new BufferedReader(new FileReader("alice.txt"));
			while ((inputLine = readFile.readLine()) != null)
			{
				String data[] = inputLine.split(" ");
		    	for (String val : data) {
					s.insert(val.trim(), 1);
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
		readFile.close();

		
//		
		System.out.println("Testing Insert");
		s.printHashTable();
//		System.out.println("After Delete");
//		s.delete("the");
//		s.printHashTable();
//		System.out.println("Testing find");
//		System.out.println(s.find("y"));
//		System.out.println(s.find("yZ"));
//		System.out.println("Testing List All keys");
//		s.listAllKeys();
//		System.out.println("Testing increase key");
//		s.increaseKey("her");
//		s.printHashTable();
//		System.out.println("Testing increase key,val");
//		System.out.println("Testing change key,val");
//		s.changeKey("her",100);
//		s.printHashTable();
        s.writeOutputToFile();
	}
}


//Data Structure for Key Value
class KeyValue {
	String key;
	int value;
	KeyValue next = null;

	@Override
	public int hashCode() {
		char keyChar[] = this.key.toCharArray();
		int sum = 0;
		for (int i = 0; i < keyChar.length; i++) {
			sum += keyChar[i];
		}

		return sum % 16;
	}

	// constructor
	public KeyValue(String key, int value) {
		super();
		this.key = key;
		this.value = value;
	}

	// getters and setters
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public KeyValue getNext() {
		return next;
	}

	public void setNext(KeyValue next) {
		this.next = next;
	}

}
