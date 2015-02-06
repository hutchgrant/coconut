package com.hutchgrant.Elements;

import java.util.ArrayList;

import com.hutchgrant.app.TunaApp;

import android.app.Activity;
import android.content.Context;

public class Group {
	
	private String Name = "";
	private String ID = "";
	private int Size = 0;
	private String Sync_Date = "n/a";
	private String Sync_Time = "n/a";
	private ArrayList<Contact> People;
	boolean selected = false;
	public Context ctx;
	
	public Group(){
		this.People = new ArrayList<Contact>();
		this.ID = "";
	}
	public Group(String id, String name, ArrayList<Contact> people) {
		setGroup(id, name, people);
	}
	public Group(String id, String name, int size, String syncdate, String synctime, ArrayList<Contact> people, boolean selected, Context cntext) {
		setGroup(id, name, people);
		this.Size = size;
		this.Sync_Date = syncdate;
		this.Sync_Time = synctime;
		this.ctx = cntext;
	}
	public Group(Context context) {
		People = new ArrayList<Contact>();
		this.ctx = context;
	}
	
	public Group(Group group) {
		setGroup(group.getID(), group.getName(), group.getAll());
		this.selected = group.isSelected();
		this.ctx = group.ctx;
		this.Sync_Date = group.getSyncDate();
		this.Sync_Time = group.getSyncTime();
	}
	
	public void setPeople(ArrayList<Contact> people){
		this.People = people;
		this.Size = people.size();
	}
	
	public void setGroup(String id, String name, ArrayList<Contact> people){
		this.People = new ArrayList<Contact>();
		
		this.People = people;
		setID(id);
		setName(name);
		this.Size = people.size();
		
	}
	
	public void addContact(Contact contact){
		synchronized(People){
		this.People.add(contact);
		this.Size++;
		}
	}
	
	public Contact getContact(int id){
		return this.People.get(id);
	}
	
	public Contact getContactByID(String id){
		Contact cont = new Contact();
		for(int i=0; i<this.People.size(); i++){
			if(this.People.get(i).getID().equals(id)){
				cont = this.People.get(i);
			}
		}
		return cont;
	}
	public Contact getContactByGoogleID(String id){
		Contact cont = new Contact();
		for(int i=0; i<this.People.size(); i++){
			if(this.People.get(i).getGoogleID().equals(id)){
				cont = this.People.get(i);
			}
		}
		return cont;
	}
	
	
	public ArrayList<Contact> getAll(){
		synchronized(People){
		return this.People;
		}
	}
	
	public void removeContact(Contact contact){
		synchronized(People){
		this.Size=People.size();
		this.People.remove(contact);
		this.Size--;
		}
	}
	
	public void setID(String id){
		this.ID = id;
	}
	public void setName(String name){
		this.Name = name;
	}
	
	public String getName(){
		return this.Name;
	}
	
	public int getSize(){
		return this.Size;
	}
	
	public String getID(){
		return this.ID;
	}
	
	public void setSize(int size){
		this.Size = size;
	}

	public void setSelected(boolean checked) {
		this.selected = checked;
	}

	public boolean isSelected() {
		return this.selected;
	}
	
	public void setSyncDate(String date){
		this.Sync_Date = date;
	}
	
	@SuppressWarnings("unused")
	private void setSync(String date, String time){
		///get date and time
		this.Sync_Date =date;
		this.Sync_Time = time;
	}
	
	public String getSyncDate(){
		return this.Sync_Date;
	}
	
	public String getSyncTime(){
		return this.Sync_Time;
	}

	public void set(String grpID, String grpNAME, String grpSYNCD, String grpSYNCT) {
		this.Name = grpNAME;
		this.ID = grpID;
		this.Sync_Date = grpSYNCD;
		this.Sync_Time = grpSYNCT;
		
	}
	
	public void display(){
		System.out.println("Group NAME = "+Name +" ID = "+ID);
	}

	public void getDBGroupData(String selGroupID){
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		Group grp = new Group(ctx);
		grp = app.readGroup(selGroupID);
		this.ID = grp.getID();
		setName(grp.getName());
		this.Sync_Date = grp.getSyncDate();
		this.Sync_Time = grp.getSyncTime();
	//	this.People = grp.getAll();
	}
	public void getDBContactData(String string){
		synchronized(People){
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		Group grp = new Group(ctx);
		grp = app.readContactsFromDB(string);
		this.People = grp.getAll();
		this.Size = this.People.size();
		}
	}
	
	public void getContactData(){
		synchronized(People){
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		this.People = app.readContactsFromPhone();
		this.Size = this.People.size();
		}
	}
	
	
	public boolean ContainsContactID(String id){
		for(int i=0; i<People.size(); i++){
			if(People.get(i).getID().equals(id) == true){
				return true;
			}
		}
		return false;
	}
	public boolean ContainsGoogleID(String id){
		for(int i=0; i<People.size(); i++){
			if(People.get(i).getGoogleID().equals(id) == true){
				return true;
			}
		}
		return false;
	}
	
	public void removeSingleContactByPos(int id){
		removeContact(People.get(id));
	}
	
	@Override
	public String toString() {
	   return "Group [ID=" + this.ID + ", Name=" + this.Name + ", Size=" + this.Size + 
			   ", Sync_Date=" + this.Sync_Date + ", Sync_Time=" +this.Sync_Time + 
			   ", People=" + People + "]";
	}
	public void removeContactByPosition(int i) {
		this.People.remove(i);
		this.Size=this.People.size();
	}
	
	public void updateGroup(){
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		if(!getSyncDate().equals("n/a")){
		setSyncDate("updated");
		app.updateGroup(this);
		}
	}
	
	public void updateContact(int pos){
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		Contact contact = new Contact();
		contact = getContact(pos);
		contact.setName("removed");
		app.updateContact(contact);
		removeContact(contact);
		if(getSyncDate().equals("n/a")){
			app.removeContacts(contact.getID());
		}
	}
	
	public ArrayList<Contact> getCleanPeople(){
		ArrayList<Contact> cleanPPL = new ArrayList<Contact>();
		for(int i=0; i<Size; i++){
			if(People.get(i).getName().equals("removed")){
				System.out.println("removed user found");
			}else{
				cleanPPL.add(getContact(i));
			}
		}
		return cleanPPL;
	}
	public String[] fillURIList() {
		String[] str = new String[Size];
		int count = 0;
		for(int i=0; i<Size; i++){
			if(People.get(i).getProfileImg() != ""){
				str[count] = People.get(i).getProfileImg();
				count++;
			}
		}
		// fix array
		String[] FinalStr = new String[count];
		for(int i=0; i< count; i++){
			FinalStr[i] = str[i];
		}
		return FinalStr;
	}
	
	
	/*  FILL AND SET GROUP DATA FROM DATABASE
	 *  read groups + contacts
	 */
	public void getFriendData(){
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		Group group = new Group(ctx);
		group = app.getGroup("Friends", false);
		group.getDBContactData(group.getID());
		this.setGroup(group.getID(), group.getName(), group.getAll());
	}

}
