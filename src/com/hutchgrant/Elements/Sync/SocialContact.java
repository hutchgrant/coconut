package com.hutchgrant.Elements.Sync;

public class SocialContact {

	public String id;
	public String groupID;
	public String googleID;
	public String name;
	public String last_name;
	public String email;
	public String phone;
	public String img;
	
	public SocialContact(){
		this.id = "";
		this.groupID = "";
		this.googleID = "";
		this.name = "";
		this.email = "";
		this.phone = "";
		this.img = "";
	}
	
	public void fill(String ID, String GroupID, String GoogleID, String NAME, String EMAIL, String PHONE, String IMAGE){
		this.groupID = GroupID;
		this.id = ID;
		this.googleID = GoogleID;
		this.name = NAME;
		this.email = EMAIL;
		this.phone = PHONE;
		this.img = IMAGE;
	}
}
