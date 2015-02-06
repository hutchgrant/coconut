package com.hutchgrant.Elements.Sync;

import com.hutchgrant.Elements.Photo;

public class SocialImage {
		public	String ID = "";
		public	String Type = "";
		public	String Name = "";
		public	String username = "";
		public	String userID = "";
		public String AlbumID = "";
		public	String Title = "";
		public	String Description = "";
		public	String UploadTime = "";
		public	String UploadDate = "";
		public String UploadToken = "";
		
		public Photo convertToPhoto(){
			
			Photo photo = new Photo();
			photo.fill(Type, Name, username, userID, AlbumID, Title, Description, false);
			photo.setSync(UploadDate,  UploadTime);
			photo.setToken(UploadToken);
			photo.ID = this.ID;
			photo.Synced = photo.Synced;
			return photo;
			
		}
}
