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
import com.hutchgrant.app.ProfileAccess;
import com.hutchgrant.app.SyncAlbumAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.tasks.TallyTask.MyListener;

public class AlbumTask extends AsyncTask<SyncObj, Void, Void>{
	
	private static final String TAG = null;
	Context ctx;
	TunaApp app; 
	SyncAlbumAccess syncAlb;
	boolean COMPLETE = false;
	int SYNC_TYPE= 0 ;
	String syncAlbumID = "";
	int syncAlbPos = 0;
	AlbListener listener;
	Album SpecAlb;
	public AlbumTask(Context cont){
		ctx = cont;
		SyncAlbumAccess.initAccess(ctx);
		syncAlb = SyncAlbumAccess.getInstance();
    	COMPLETE = false;
	}
	
	@Override
	protected Void doInBackground(SyncObj... params) {

		if(SYNC_TYPE == 0){
			receiveAlbums(params[0]);
		}else if(SYNC_TYPE == 1){
			syncAlb.ControlRemoteSync();
			syncAlb.syncAllDirtyPhotos();
		}else if(SYNC_TYPE == 2){
			LifeAlbum life = new LifeAlbum(ctx);
			life.Albums = syncAlb.getSpecificRemoteAlbumData(syncAlbumID);
			if(life.Albums.size() > 0){
				SpecAlb = new Album(ctx);
				syncAlb.receiveAllImages(life.getAllAlbums());
				SpecAlb = life.Albums.get(0);
				System.out.println(SpecAlb.photos.get(0).toString());
			}
		}
		
		return null;
	}

	public void setSyncType(int type){
		if(type == 0){
			SYNC_TYPE = 0;
		}else if(type == 1){
			SYNC_TYPE = 1;
		}else if(type == 2){
			SYNC_TYPE = 2;
		}
	}
	
	public void setSyncID(String id, int pos){
		this.syncAlbumID = id;
		this.syncAlbPos = pos;
	}
	
	public void receiveAlbums(SyncObj sync){
		ArrayList<Album> albums = new ArrayList<Album>();
		albums = syncAlb.receiveAllAlbums(sync);
		if(albums.size() > 0){
			syncAlb.receiveAllImages(albums);
		}
	}
	
    @Override
	protected void onPostExecute(Void vo) {
    	COMPLETE = true;
    	if(SYNC_TYPE == 2){
    		listener.setAlb(SpecAlb, syncAlbPos);
    	}
    }
    
    
    public void setListener(AlbListener listen){
    	this.listener = listen;
    }
    
    public static interface AlbListener{
    	public void setAlb(Album alb, int pos);
    }
}
