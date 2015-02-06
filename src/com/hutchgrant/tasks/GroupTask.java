package com.hutchgrant.tasks;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.Elements.User;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.app.PreferenceAccess;
import com.hutchgrant.app.ProfileAccess;
import com.hutchgrant.app.SyncAlbumAccess;
import com.hutchgrant.app.SyncGroupAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.imagesend.HttpUploader;

public class GroupTask extends AsyncTask<SyncObj, Void, Void>{
	
	private static final String TAG = null;
	private int SYNC_TYPE = 0;
	Context ctx;
	TunaApp app; 
	SyncGroupAccess syncGrp;
	PreferenceAccess prefAccess;
	boolean COMPLETE = false;

	public GroupTask(Context cont){
		ctx = cont;
		SyncAlbumAccess.initAccess(ctx);
		PreferenceAccess.initAccess(ctx);
		syncGrp = SyncGroupAccess.getInstance();
		prefAccess = PreferenceAccess.getInstance();
    	COMPLETE = false;
	}
	
	@Override
	protected Void doInBackground(SyncObj... params) {

		if(SYNC_TYPE == 0){
			syncGrp.SyncReceiveGroups(params[0]);
		}else if(SYNC_TYPE == 1){
			syncGrp.sendRemoteGroups();
		}
		
		return null;
	}
	
	public void setSyncType(int type){
		this.SYNC_TYPE = type;
	}
	
    @Override
	protected void onPostExecute(Void vo) {
    	COMPLETE = true;	
    	if(SYNC_TYPE == 1){
    		prefAccess.setPendingData(false, true);
    	}

    }
}
