package com.hutchgrant.tasks;

import com.hutchgrant.Elements.PrefObj;
import com.hutchgrant.Elements.Sync.SyncInviteObj;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.app.NetworkAccess;
import com.hutchgrant.app.PreferenceAccess;
import com.hutchgrant.app.SyncAllAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.tasks.TallyTask.MyListener;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class SyncTask extends AsyncTask<Context, Void, String>{
	private Handler handler = new Handler();
	Context ctx;
	TunaApp app;
	SyncAllAccess syncAccess;
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
	public SyncTask( ){
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

	      //  System.out.println("testing...");
	         handler.postDelayed(this, pref.syncTime*7000);	
	 	if(pref.synced){
			if(netAccess.isNetworkAvailable()){
	         localSync = syncAccess.getLocalTally();
	         // get remote tally
	         tt = new TallyTask(ctx);
	         tt.setType(1);
	         tt.setListener(new MyListener(){
	        	 boolean REMOTEUPDATE = false;
		         @Override
		         public void getTally(){
	 	   	         remoteSync = syncAccess.getLocalTally();
	 	        		   // create new local tally(if remote just saved)
	 	              System.out.println("remote tally saved");
	 	               System.out.println("LOCAL OBJ ALBAMOUNT= "+localSync.syncAlbAmount);
	 	               System.out.println("REMOTE OBJ ALBAMOUNT= "+remoteSync.syncAlbAmount);
	 	               System.out.println("LOCAL OBJ GRPAMOUNT= "+localSync.syncGrpAmount);
	 	               System.out.println("REMOTE OBJ GRPAMOUNT= "+remoteSync.syncGrpAmount);
	 	               System.out.println("LOCAL OBJ INVSNTAMOUNT= "+localSync.syncInvSntAmount);
	 	               System.out.println("REMOTE OBJ INVSNTAMOUNT= "+remoteSync.syncInvSntAmount);
	 	               checkAlbum();
	 	               checkGroups();
	 	               checkInvites();
	 	               checkMsgs();
		         }

	 			@Override
	 			public void checkAlbum() {
	 	 	        if(localSync.syncImgAmount != remoteSync.syncImgAmount || localSync.syncAlbAmount != remoteSync.syncAlbAmount){

	 	               at = new AlbumTask(ctx);
	 	               at.setSyncType(0);
	 	          	 	at.execute(localSync);
	 	          	 REMOTEUPDATE = true;
	 	 	         }  
	 	 	     
	 			}
	 			
	 			@Override
	 			public void checkGroups(){
	 				if(localSync.syncGrpAmount != remoteSync.syncGrpAmount 
	 						|| !localSync.syncGrpDate.equals(remoteSync.syncGrpDate)	
	 						|| !localSync.syncGrpTime.equals(remoteSync.syncGrpTime)){
	 			 				gt= new GroupTask(ctx);
	 			 				gt.setSyncType(0);
	 			 				gt.execute(localSync);
	 			 				REMOTEUPDATE = true;
	 			   } 
	 			}
	 			
	 			@Override
	 			public void checkInvites(){
	 				it = new InviteTask(ctx);
	 				 it.setSyncType(2);
					   it.execute(localSync);
	 			}
	 			
	 			@Override
	 			public void checkMsgs(){
	 				if(localSync.syncMsgSntAmount != remoteSync.syncMsgSntAmount
	 						|| !localSync.syncMsgDate.equals(remoteSync.syncMsgDate)
	 						|| !localSync.syncMsgTime.equals(remoteSync.syncMsgTime)){
		 					mt = new MessageTask(ctx);
		 					mt.setSyncType(0, false);
		 					mt.execute(localSync);
		 					REMOTEUPDATE = true;  
	 				}
	 			}
	 			
	 			
	 			@Override
	 			public void sendPending(){
	 				boolean pending = false;
				   	pending = prefAccess.getPendingData();
				   	
				      if(pending){
				            at = new AlbumTask(ctx);
				            at.setSyncType(1);
				       	 	at.execute(localSync);
	  
				       	 	gt = new GroupTask(ctx);
				       	 	gt.setSyncType(1);
				       	 	gt.execute(localSync);
				       	 	
				       	 	it = new InviteTask(ctx);
				       	 	it.setSyncType(1);
				       	 	it.execute(localSync);
				       	 	
				       	 	mt = new MessageTask(ctx);
				       	 	mt.setSyncType(1, false);
				       	 	mt.execute(localSync);
				       	 	TallyTask Taltask= new TallyTask(ctx);
				       	 	Taltask.setType(2);
				       	 	Taltask.execute(localSync);
				      }
				      

	 			}
	 			
	 			@Override
	 			public void setTally(SyncObj remote){
	 				remoteSync = remote;
	 			}
	 			
	 			@Override
	 			public void saveChanges(){
					if(REMOTEUPDATE){
				    	  REMOTEUPDATE = false;
				    	  syncAccess.saveLocalTally(remoteSync, true);
				     }
	 			}
	         });
	         
	         tt.execute(localSync);
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
   	 	prefAccess = PreferenceAccess.getInstance();
        syncAccess = SyncAllAccess.getInstance();
		netAccess = NetworkAccess.getInstance();
    	pref = prefAccess.getPreference();

        //app = (TunaApp) ((Activity) ctx.getApplication();
		run();
		
		return null;
	}
}