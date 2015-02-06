package com.hutchgrant.Elements.Sync;

import java.util.ArrayList;

import android.content.Context;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.app.AlbumAccess;
import com.hutchgrant.app.ImageAccess;

public class AlbumData {
		public ArrayList<SocialImage> images;
		public String ID = "";
		public String UserName = "";
		public String Title = "";
		public String SyncDate = "";
		public String SyncTime = "";
		public int Size = 0;
		
		public AlbumData(){
			images = new ArrayList<SocialImage>();
		}
		
		
	    public void fill(String id, String name, String title, ArrayList<SocialImage> imgs){
	    	this.ID = id;
	    	this.UserName = name;
	    	this.Title = title;
	    	this.images = imgs;
	    	Size = imgs.size();
	    }
	    
	    public void setSync(String syncDate, String syncTime){
	    	this.SyncDate = syncDate;
	    	this.SyncTime = syncTime;
	    }
	    
		public Album ConvertRemoteAlbumUpdate(Context ctx, AlbumAccess albumAccess, ImageAccess imgAccess){

			Album album = new Album(ctx);
			album.albSize = this.Size;
			album.albName = this.Title;
			album.authorID = this.UserName;
			album.ID = this.ID;
			album.syncDate = this.SyncDate;
			album.syncTime = this.SyncTime;
			albumAccess.saveLocalAlbum(album, true);

			Photo photo;
			for(int i=0; i< this.images.size(); i++ ){
				photo = new Photo();
				photo = this.images.get(i).convertToPhoto();
				
				imgAccess.saveLocalImage(photo, true);
			}


			return album;
		}
		
	
	
}
