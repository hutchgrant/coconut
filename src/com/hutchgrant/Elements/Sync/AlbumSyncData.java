package com.hutchgrant.Elements.Sync;

import java.util.ArrayList;

import android.content.Context;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.Photo;


public class AlbumSyncData {

	public LifeAlbum life;
	
	public static class LifeAlbum{
		public ArrayList<AlbumData> albums;
	}
	

	public ArrayList<Album> ConvertRemoteAlbumLife(Context ctx) {
		Album albumFresh = new Album(ctx);
		Photo photoFresh = new Photo();

		ArrayList<Album> albumList = new ArrayList<Album>();

		String type = null, name = null, user =null, userid = null, albumid = null, 
				title = null, description = null, uploadDate = null, uploadTime = null,
				uploadToken = null, id = null; 
		boolean synced = false;
		for(int i=0; i< this.life.albums.size(); i++){
			albumFresh = new Album(ctx);
			albumFresh.fill(this.life.albums.get(i).ID,this.life.albums.get(i).UserName, 
					this.life.albums.get(i).Title);
			albumFresh.setSync(this.life.albums.get(i).SyncDate, this.life.albums.get(i).SyncTime);
			for(int x=0; x< this.life.albums.get(i).images.size(); x++){
				id = this.life.albums.get(i).images.get(x).ID;
				type =  this.life.albums.get(i).images.get(x).Type;
				name = this.life.albums.get(i).images.get(x).Name;
				user = this.life.albums.get(i).images.get(x).username;
				userid = this.life.albums.get(i).images.get(x).userID;
				albumid = this.life.albums.get(i).images.get(x).AlbumID;
				title = this.life.albums.get(i).images.get(x).Title;
				description = this.life.albums.get(i).images.get(x).Description;
				uploadDate = this.life.albums.get(i).images.get(x).UploadDate;
				uploadTime = this.life.albums.get(i).images.get(x).UploadTime;
				uploadToken = this.life.albums.get(i).images.get(x).UploadToken;

				photoFresh = new Photo();
				photoFresh.ID = id;
				photoFresh.fill(type, name, user, userid, albumid, title, description, synced);
				photoFresh.setSync(uploadDate, uploadTime);
				photoFresh.setToken(uploadToken);
				albumFresh.addPhoto(photoFresh);
			}
			albumList.add(albumFresh);
		}
		return albumList;
	}
}
