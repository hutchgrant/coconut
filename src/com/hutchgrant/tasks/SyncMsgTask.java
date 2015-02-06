package com.hutchgrant.tasks;

import com.hutchgrant.Elements.PrefObj;
import com.hutchgrant.Elements.Sync.SyncInviteObj;
import com.hutchgrant.Elements.Sync.SyncMsgObj;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.app.NetworkAccess;
import com.hutchgrant.app.PreferenceAccess;
import com.hutchgrant.app.SyncAllAccess;
import com.hutchgrant.app.SyncMessageAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.tasks.TallyTask.MyListener;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class SyncMsgTask extends AsyncTask<Context, Void, String>{
	private Handler handler = new Handler();
	Context ctx;
	TunaApp app;
	SyncMessageAccess syncMsgAccess;
	SyncAllAccess syncAllAccess;
	PreferenceAccess prefAccess;
	NetworkAccess netAccess;
	TallyTask tt;
	AlbumTask at;
	GroupTask gt;
	InviteTask it;
	MessageTask mt;
	SyncObj localSync, remoteSync;
	boolean unlocked = true;
	PrefObj pref;
	public SyncMsgTask( ){
		pref = new PrefObj();
		localSync = new SyncObj();
		remoteSync = new SyncObj();
	}
	public void run(){
		runnable.run();

	}
	private Runnable runnable = new Runnable() 
	{

	    @Override
		public void run() 
	    {
	         handler.postDelayed(this, pref.syncTime*2550);	
	         if(pref.synced){
	        	 if(netAccess.isNetworkAvailable()){
			         localSync = syncAllAccess.getLocalTally();
			         // get remote tally
			         mt = new MessageTask(ctx);
			         mt.setSyncType(0, false);
			         mt.execute(localSync);
	        	 }
	         }
	    }
	};

	@Override
	protected String doInBackground(Context... params) {
		ctx = params[0];
        SyncAllAccess.initAccess(ctx);
   	 	PreferenceAccess.initAccess(ctx);
   	 	NetworkAccess.initAccess(ctx);
   	 	SyncMessageAccess.initAccess(ctx);
   	 	SyncAllAccess.initAccess(ctx);
   	 	prefAccess = PreferenceAccess.getInstance();
        syncMsgAccess = SyncMessageAccess.getInstance();
        syncAllAccess = SyncAllAccess.getInstance();
		netAccess = NetworkAccess.getInstance();
    	pref = prefAccess.getPreference();

        //app = (TunaApp) ((Activity) ctx.getApplication();
		run();
		
		return null;
	}
}