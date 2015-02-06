package com.hutchgrant.tasks;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.LifeInvite;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.Elements.User;
import com.hutchgrant.Elements.Sync.SyncInviteObj;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.app.PreferenceAccess;
import com.hutchgrant.app.ProfileAccess;
import com.hutchgrant.app.SyncAlbumAccess;
import com.hutchgrant.app.SyncGroupAccess;
import com.hutchgrant.app.SyncInviteAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.imagesend.HttpUploader;
import com.hutchgrant.tasks.TallyTask.MyListener;

public class InviteTask extends AsyncTask<SyncObj, Void, Void>{
	
	private static final String TAG = null;
	private int SYNC_TYPE = 0;
	Context ctx;
	TunaApp app; 
	SyncInviteAccess syncInv;
	PreferenceAccess prefAccess;
	boolean COMPLETE = false;
	SyncInviteObj remoteSync, localSync;
	InvListener listener;
	
	public InviteTask(Context cont){
		ctx = cont;
		SyncAlbumAccess.initAccess(ctx);
		PreferenceAccess.initAccess(ctx);
		syncInv = SyncInviteAccess.getInstance();
		prefAccess = PreferenceAccess.getInstance();
    	COMPLETE = false;
	}
	
	@Override
	protected Void doInBackground(SyncObj... params) {

		if(SYNC_TYPE == 0){
			SyncInviteObj invite = new SyncInviteObj();
			invite.fill(params[0].syncInviteToken, params[0].syncInviteDate, params[0].syncInviteTime, params[0].syncInvRecAmount, params[0].syncInvSntAmount);
			syncInv.getLifeInvites(invite);
		}else if(SYNC_TYPE == 1){
			LifeInvite invites = new LifeInvite();
			invites = syncInv.invAccess.getInvites(false, "updated", "");
			if(invites.invites.size() > 0){
				syncInv.sendDirtyInvites(invites);
			}
		}else if(SYNC_TYPE == 2){
			remoteSync = new SyncInviteObj();
			localSync = new SyncInviteObj();
			localSync.fill(params[0].syncInviteToken, params[0].syncInviteDate, params[0].syncInviteTime, params[0].syncInvRecAmount, params[0].syncInvSntAmount);
			remoteSync = syncInv.getRemoteInviteTally(localSync);
			if(!remoteSync.syncInviteToken.equals("")){
				syncInv.saveLocalInviteTally(remoteSync, true);
				if(!remoteSync.syncInviteToken.equals(localSync.syncInviteToken)){
					syncInv.getLifeInvites(localSync);
				}
			}
		}else if(SYNC_TYPE == 3){
			LifeInvite invites = new LifeInvite();
			invites = syncInv.invAccess.getInvites(false, "updated", "");
			if(invites.invites.size() > 0){
				syncInv.sendDirtyInvites(invites);
			}
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
    	}else if(SYNC_TYPE == 2){
    		syncInv.saveLocalInviteTally(remoteSync, true);
    	}else if(SYNC_TYPE == 3){
    		this.listener.getGroups();
    	}

    }
    
    public static interface InvListener{
    	public void getGroups();
    }

	public void setListener(InvListener invListener) {
		this.listener = invListener;		
	}
}
