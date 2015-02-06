package com.hutchgrant.Elements;

public class Message {

	
	public String MID = "";
	public String AuthorID = "";
	public String AuthorName = "";
	public String Type = "";
	public String Content = "";
	public String ReceiverID = "";
	public String ReceiverGrpID = "";
	public String mDate = "";
	public String mTime = "";

	
	
	public Message(){
		this.MID = "";
		this.AuthorID = "";
		this.AuthorName = "";
		this.Type = "";
		this.Content = "";
		this.ReceiverID = "";
		this.ReceiverGrpID = "";
		this.mDate = "";
		this.mTime = "";
	}
	
	public void fill(String id, String userID, String userName, String type, String content, String receiveID, String receiveGrpID, String date, String time){
		
		this.MID = id;
		this.AuthorID = userID;
		this.AuthorName = userName;
		this.Type  = type;
		this.Content = content;
		this.ReceiverID = receiveID;
		this.ReceiverGrpID = receiveGrpID;
		this.mDate = date;
		this.mTime = time;
	}
	
	public void display(){
		System.out.println("Message: ID="+this.MID+" AuthorID="+this.AuthorID+" AuthorName="+this.AuthorName+" Type="+this.Type+" Content="+this.Content
				+" ReceiverID="+this.ReceiverID+ " ReceiverGrpID="+this.ReceiverGrpID+" Date="+this.mDate+" Time="+this.mTime);
	}
}
