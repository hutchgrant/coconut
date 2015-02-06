package com.hutchgrant.tasks;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.LifeAlbum;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.Elements.User;
import com.hutchgrant.Elements.Sync.AlbumData;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.app.ImageAccess;
import com.hutchgrant.app.ProfileAccess;
import com.hutchgrant.app.SyncAlbumAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.tasks.TallyTask.MyListener;

public class ImageTask extends AsyncTask<String, Void, Photo>{
	
	private static final String TAG = null;
	Context ctx;
	TunaApp app; 
	SyncAlbumAccess syncAlb;
	boolean COMPLETE = false;
	int SYNC_TYPE= 0 ;
	DataListener listener;
	private int imgPosition = 0;
	
	
	public ImageTask(Context cont){
		ctx = cont;
		SyncAlbumAccess.initAccess(ctx);
		syncAlb = SyncAlbumAccess.getInstance();
    	COMPLETE = false;
	}
	
	@Override
	protected Photo doInBackground(String... imageID) {
		Photo photo = new Photo();
		photo = syncAlb.getSpecificRemoteImageData(imageID[0]);
		
		if(!photo.ID.equals("")){
			syncAlb.receiveASingleJPG(photo.ID, photo.Name);
		}
		return photo;
	}

	public void setSyncType(int type){
		if(type == 0){
			SYNC_TYPE = 0;
		}else if(type == 1){
			SYNC_TYPE = 1;
		}
	}
	
	public void receiveAlbums(SyncObj sync){
		ArrayList<Album> albums = new ArrayList<Album>();
		albums = syncAlb.receiveAllAlbums(sync);
		syncAlb.receiveAllImages(albums);
	}
	
	public static interface DataListener {
		void setAlb(Photo photo, int position);
	}
	
	public void setListener(DataListener data){
		this.listener = data;
	}
	
    @Override
	protected void onPostExecute(Photo photo) {
    	COMPLETE = true;
    	if(!photo.ID.equals("")){
    		this.listener.setAlb(photo, this.imgPosition);
    	}
    }

	public void setImgPos(int pos) {
		this.imgPosition  = pos;
	}

}
