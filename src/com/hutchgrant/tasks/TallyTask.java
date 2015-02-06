package com.hutchgrant.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.app.SyncAllAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.networks.gplus.HttpUtils;

public class TallyTask extends AsyncTask<SyncObj, Void, Void>{
	
	MyListener listener;
	
	private static final String TAG = null;

	private int TYPE = 0;
	Context ctx;
	TunaApp app; 
	SyncAllAccess syncAccess;
	boolean COMPLETE = false;
	SyncObj remoteSync;
	public TallyTask(Context cont){
		ctx = cont;
		SyncAllAccess.initAccess(ctx);
		syncAccess = SyncAllAccess.getInstance();
    	COMPLETE = false;
	}
	
	class SmallSync {

		public String syncToken = "";
	}
	
	@Override
	protected Void doInBackground(SyncObj... params) {
		remoteSync = new SyncObj();
		remoteSync = syncAccess.getRemoteTally();
		if(TYPE == 1){  // main tally sync
		listener.setTally(remoteSync);
		listener.checkAlbum();
    	listener.checkGroups();
    	listener.checkInvites();
    	listener.checkMsgs();
        listener.saveChanges();
    	listener.sendPending();
		}
			return null;
		}
	
    @Override
	protected void onPostExecute(Void vo) {
    	COMPLETE = true;
    //	listener.getTally();
    //	listener.checkAlbum();
    //	listener.checkGroups();
    ///	listener.checkInvites();
    	if(TYPE == 1){
        
    	}else{
    		syncAccess.saveLocalTally(remoteSync, true);
    	}
    }
    
    public void setType(int ty){
    	TYPE = ty;
    }
    
    public static interface MyListener {
    	void getTally();
    	void setTally(SyncObj remoteSync);
		void checkAlbum();
    	void checkGroups();
    	void checkInvites();
    	void sendPending();
		void checkMsgs();
		void saveChanges();
    }

	public void setListener(MyListener myListener) {
    	this.listener = myListener;
		
	}
}
