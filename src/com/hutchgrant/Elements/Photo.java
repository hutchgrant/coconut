package com.hutchgrant.Elements;


public class Photo {
	
	
	public	String ID = "";
	public String AlbumID = "";
	public	String Type = "";
	public	String Name = "";
	public	String username = "";
	public	String userID = "";
	public	String Title = "";
	public	String Description = "";
	public	String UploadTime = "";
	public	String UploadDate = "";
    public String UploadToken = "";
	public boolean Synced = false;
	public boolean Selected = false;
	
    @Override
    public String toString() {
      return ID + " : " +AlbumID+" : "+Type+" : "+Name+" : "+username+" : "+userID+" : "+Title+" : "+Description
    		  +" : "+UploadTime+" : "+UploadDate + " : " + UploadToken + " : " + Synced;
    }
    
    public void fill(String type, String name, String user, String userid, String albumid, String title, String description, boolean synced){

    	this.AlbumID = albumid;
    	this.Type = type;
    	this.Name = name;
    	this.username = user;
    	this.userID = userid;
    	this.Title = title;
    	this.Description = description;
    	this.Synced = synced;
    }
    
    public Photo(String type, String name, String user, String userid, String albumid, String title, String description, boolean synced){
    	fill(type, name, user, userid, albumid, title, description, synced);
    }

	public Photo() {
	}

	public void setSelected(boolean checked) {
		this.Selected = checked;
	}
	
	public void setToken(String token){
		this.UploadToken = token;
	}
	
	public String getToken(){
		return this.UploadToken;
	}
	
	public void setSync(String date, String time){
		this.UploadDate = date;
		this.UploadTime = time;
	}
}
