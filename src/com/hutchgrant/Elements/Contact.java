package com.hutchgrant.Elements;

import android.util.Log;

public class Contact {

	private String ID;	
	private String GroupID;
	private String GoogleID;
	private String Name;
	private String Email;
	private String Phone;
	private String ProfileImg;
	boolean Selected;
	
	public Contact(){
		setContact("", "", "", "", "", "", "", false);
	}
	
	public Contact(String nID, String groupid, String googleID, String name, String email, String phone, String profileImg, boolean select ){
		setID(nID);
		setGroupID(groupid);
		setGoogleID(googleID);
		setName(name);
		setEmail(email);
		setPhone(phone);
		setSelected(select);
		setProfileImg(profileImg);
	}
	public void setProfileImg(String profileImg) {
		this.ProfileImg = profileImg;
	}

	public Contact(Contact cnt){
		setContact(cnt);
	}
	
	private void setID(String nID){
		this.ID = nID;
	}
	public void setGroupID(String groupid) {
		this.GroupID = groupid;
	}
	public void setName(String name){
		this.Name = name;
	}
	public void setEmail(String email){
		this.Email = email;
	}
	public void setPhone(String phone){
		this.Phone = phone;
	}
	public void setSelected(boolean sel){
		this.Selected = sel;
	}
	
	public void setContact(String nID, String groupid, String googleID, String name, String email, String phone, String profileImg, boolean select ){
		setID(nID);
		setGroupID(groupid);
		setName(name);
		setEmail(email);
		setPhone(phone);
		setSelected(select);
		setGoogleID(googleID);
		setProfileImg(profileImg);
	}

	public void setGoogleID(String googleID) {
		this.GoogleID = googleID;
	}

	public void setContact(Contact set){
		setContact(set.getID(), set.getGroupID(), set.getGoogleID() ,set.getName(), set.getEmail(), set.getPhone(), set.getProfileImg(), set.isSelected());
	}
	
	public String getGoogleID() {
		return this.GoogleID;
	}

	public String getID(){
		return this.ID;
	}
	public String getGroupID(){
		return this.GroupID;
	}
	public String getName(){
		return this.Name;
	}
	public String getEmail(){
		return this.Email;
	}
	public String getPhone(){
		return this.Phone;
	}
	public String getImg(){
		return this.ProfileImg;
	}
	public boolean isSelected() {
		return this.Selected;
	}
	
	public String getProfileImg(){
		return this.ProfileImg;
	}
	
	public void Display(){
		Log.v("ContactDetails", "id:"+ID+" name:"+Name+" email:"+Email+" phone:"+Phone );

	}
	
	@Override
	public String toString() {
	   return "Contact [ID=" + this.ID + ", Name=" + this.Name + ", Email=" + this.Email + ", Phone=" + this.Phone + "]";
	}
	
}
