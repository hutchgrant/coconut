package com.hutchgrant.Elements.Sync;

import java.util.ArrayList;

public class GroupSyncData {
	public LifeGroup life;
	
	public static class LifeGroup {
		public ArrayList<Group> groups;
	}
	
	public static class Group {
		public ArrayList<Contact> people;
		public String groupName;
		public String groupID;
		public String groupSize;
		public String groupSyncDate;
		public String groupSyncTime;
	}
	public static class Contact {
		public String ID;
		public String Name;
		public String Email;
		public String Phone;
		public String GoogleID;
		public String ProfileImg;
	}
}
