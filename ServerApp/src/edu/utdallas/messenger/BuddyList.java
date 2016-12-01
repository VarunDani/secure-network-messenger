package edu.utdallas.messenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BuddyList {

	
	public static HashMap<String,ArrayList<String>> buddyList = new HashMap<String,ArrayList<String>> ();
	
	static
	{
		ArrayList<String> newBuddyList1 = new ArrayList<String>();
		newBuddyList1.add("Jay");
		newBuddyList1.add("Ashwin");
		newBuddyList1.add("Shriroop");
		newBuddyList1.add("Askash");
		buddyList.put("Varun", newBuddyList1);
		
		
		ArrayList<String> newBuddyList2 = new ArrayList<String>();
		newBuddyList2.add("Varun");
		newBuddyList2.add("Ashwin");
		newBuddyList2.add("Shriroop");
		newBuddyList2.add("Askash");
		buddyList.put("Jay", newBuddyList2);
		
		
		
		ArrayList<String> newBuddyList3 = new ArrayList<String>();
		newBuddyList3.add("Varun");
		newBuddyList3.add("Ashwin");
		newBuddyList3.add("Jay");
		newBuddyList3.add("Askash");
		buddyList.put("Ashwin", newBuddyList3);
		
		
		ArrayList<String> newBuddyList4 = new ArrayList<String>();
		newBuddyList4.add("Varun");
		newBuddyList4.add("Ashwin");
		newBuddyList4.add("Jay");
		newBuddyList4.add("Askash");
		buddyList.put("Shriroop", newBuddyList4);
		
		
		ArrayList<String> newBuddyList5 = new ArrayList<String>();
		newBuddyList5.add("Varun");
		newBuddyList5.add("Ashwin");
		newBuddyList5.add("Shriroop");
		newBuddyList5.add("Jay");
		buddyList.put("Askash", newBuddyList5);
		
		
	}
	
	
	public static String getBuddyList(String userName)
	{
		StringBuilder ss = new StringBuilder();
		
		for (String string : buddyList.get(userName)) {
			ss.append("~"+string);
		}
		
		return ss.toString();
	}
}
