package edu.utdallas.messenger;

import java.util.ArrayList;
import java.util.Iterator;

public class BuddyList {

	
	public static ArrayList<String> buddyList = new ArrayList<String>();
	
	static
	{
		buddyList.add("Jay");
		buddyList.add("Ashwin");
		buddyList.add("Shriroop");
		buddyList.add("Askash");
	}
	
	
	public static String getBuddyList()
	{
		StringBuilder ss = new StringBuilder();
		
		for (String string : buddyList) {
			ss.append("~"+string);
		}
		
		return ss.toString();
	}
}
