package com.hutchgrant.tasks;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.LifeInvite;
import com.hutchgrant.Elements.LifeMessage;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.Elements.User;
import com.hutchgrant.Elements.Sync.SyncInviteObj;
import com.hutchgrant.Elements.Sync.SyncMsgObj;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.app.PreferenceAccess;
import com.hutchgrant.app.ProfileAccess;
import com.hutchgrant.app.SyncAlbumAccess;
import com.hutchgrant.app.SyncGroupAccess;
import com.hutchgrant.app.SyncInviteAccess;
import com.hutchgrant.app.SyncMessageAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.imagesend.HttpUploader;

public class MessageTask extends AsyncTask<SyncObj, Void, Void>{
	
	private static final String TAG = null;
	private int SYNC_TYPE = 0;
	Context ctx;
	TunaApp app; 
	SyncMessageAccess syncMsg;
	PreferenceAccess prefAccess;
	boolean COMPLETE = false;
	boolean REFRESH = false;
	SyncMsgObj remoteSync, localSync;

	MTDataListener listener;
	
	public MessageTask(Context cont){
		ctx = cont;
		SyncMessageAccess.initAccess(ctx);
		PreferenceAccess.initAccess(ctx);
		syncMsg = SyncMessageAccess.getInstance();
		prefAccess = PreferenceAccess.getInstance();
    	COMPLETE = false;
    	REFRESH = false;
	}
	
	@Override
	protected Void doInBackground(SyncObj... params) {

		if(SYNC_TYPE == 0){  /// receive synced images
			SyncMsgObj sync = new SyncMsgObj();
			sync.fill(params[0].syncMsgToken, params[0].syncMsgDate, params[0].syncMsgTime, params[0].syncMsgRecAmount, params[0].syncMsgSntAmount);
			syncMsg.getMessages(sync, false);
		}else if(SYNC_TYPE == 1){  /// send dirty images 
			LifeMessage life = new LifeMessage();
			life = syncMsg.msgAccess.getMessages(false, "", false);
			if(life.messages.size() > 0){
				syncMsg.sendDirtyMessages(life);
	    		if(REFRESH){
	    			this.listener.refresh();
	    		}
			}
		} else if(SYNC_TYPE == 2){   // get more based on cached
			SyncMsgObj sync = new SyncMsgObj();
			sync = syncMsg.getLocalMsgTally();
			syncMsg.getMessages(sync, true); 
		} else if(SYNC_TYPE == 3){
			localSync = new SyncMsgObj();
			remoteSync = new SyncMsgObj();
			localSync.fill(params[0].syncMsgToken, params[0].syncMsgDate, params[0].syncMsgTime, params[0].syncMsgRecAmount, params[0].syncMsgSntAmount);
			remoteSync = syncMsg.getRemoteMsgTally(localSync);
			if(!remoteSync.syncMsgToken.equals("")){
				syncMsg.saveLocalMsgTally(remoteSync, true);
				if(!remoteSync.syncMsgToken.equals(localSync.syncMsgToken)){
					syncMsg.getMessages(localSync, false);
				}
			}
		}
		
		return null;
	}
	
	public void setSyncType(int type, boolean list){
		this.SYNC_TYPE = type;
		if(list){
			REFRESH = true;
		}
	}
	
    @Override
	protected void onPostExecute(Void vo) {
    	COMPLETE = true;	
    	if(SYNC_TYPE == 0){
    		
    	}else if(SYNC_TYPE == 1){
    		prefAccess.setPendingData(false, true);
    		if(REFRESH){
    			this.listener.refresh();
    		}
    	}

    }
    
    public static interface MTDataListener {

		public void refresh();
    //	public void refresh();
    }
    
    public void setListener(MTDataListener data){
    	this.listener = data;
    }
}
