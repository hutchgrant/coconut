package com.hutchgrant.Elements;

public class Invite {

	public String InviteID = "";
	public String InviteUserName = "";
	public String RecipientName = "";
	public String InviteUserGID = "";
	public String RecipientUserGID = "";
	public String RecipientEmail = "";
	public String RecipientPhone = "";
	public String InviteDate = "";
	public String InviteTime = "";
	public String InviteStatus = "";

	public boolean Selected = false;
	
	public Invite(){
		this.InviteID = "";
		this.InviteUserName = "";
		this.RecipientName = "";
		this.InviteUserGID = "";
		this.RecipientUserGID = "";
		this.RecipientEmail = "";
		this.RecipientPhone = "";
		this.InviteDate = "";
		this.InviteTime = "";
		this.InviteStatus = "";
	}
	
	public void fill(String inviteid, String inviteUser, String inviteName, String recepName, String recepUser, String recepEmail, String recepPhone, String invitedate, String invitetime,
			String inviteStatus){
		this.InviteID = inviteid;
		this.RecipientName = recepName;
		this.InviteUserGID = inviteUser;
		this.InviteUserName = inviteName;
		this.RecipientUserGID = recepUser;
		this.RecipientEmail = recepEmail;
		this.RecipientPhone = recepPhone;
		this.InviteDate = invitedate;
		this.InviteTime = invitetime;
		this.InviteStatus = inviteStatus;
	}
	
	public void display(){
		System.out.println("InviteID="+this.InviteID+" "+"InviteUserGID="+this.InviteUserGID+" "+"InviteUserName="+this.InviteUserName+" "+"RecipientName= "+this.RecipientName+" "+"ReceipientUserGID="+this.RecipientUserGID+" "
				+"RecepientEmail="+this.RecipientEmail+" "+"RecepientPhone="+this.RecipientPhone+" "+"InviteDate="+this.InviteDate+" "+"InviteTime="+this.InviteTime+
				" "+"InviteStatus="+this.InviteStatus);
	}

	public boolean isSelected() {
		return this.Selected;
	}
	
	public void setSelected(boolean select){
		this.Selected = select;
	}
	
	
}
