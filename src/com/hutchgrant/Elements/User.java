package com.hutchgrant.Elements;


public class User {

    public String userToken = "";
    public String userGID = "";
    public String userGName = "";
    public String userProfile = "";
    public String userImgUrl = "";
    public String userLocation = "";
    public String userPhone = "";
    public String userEmail = "";
    
    public int MaxAlbums = 0;
    public int MaxImages = 0;
    public int MaxGroups =0;
    public int MaxContacts = 0;
    
    @Override
    public String toString() {
      return userToken + " : " + userGID + " : " + userGName + " : " + userEmail + " : " +userPhone + " : " +userProfile + " : " +userImgUrl 
    		  + " : "+ userLocation + " : " + MaxAlbums + " : " + MaxImages + " : " + MaxGroups + " : " + MaxContacts;
    }
    
    public void fill(String GID, String token, String username, String email, String phone, String profileurl, String imgurl, String location, 
    		int maxAlb, int maxImg, int maxGrp, int maxCont){
		this.userGID = GID;
		this.userToken = token;
		this.userImgUrl = imgurl;
		this.userProfile = profileurl;
		this.userLocation = location;
		this.userPhone = phone;
		this.userEmail = email;
		
		this.MaxAlbums = maxAlb;
		this.MaxImages = maxImg;
		this.MaxGroups = maxGrp;
		this.MaxContacts = maxCont;
    }
    
}
