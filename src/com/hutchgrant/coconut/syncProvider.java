package com.hutchgrant.coconut;



import com.hutchgrant.app.AlbumAccess;
import com.hutchgrant.app.GroupAccess;
import com.hutchgrant.app.ImageAccess;
import com.hutchgrant.app.NetworkAccess;
import com.hutchgrant.app.PreferenceAccess;
import com.hutchgrant.app.ProfileAccess;
import com.hutchgrant.app.SyncAlbumAccess;
import com.hutchgrant.app.SyncAllAccess;
import com.hutchgrant.app.SyncGroupAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.tasks.SyncMsgTask;
import com.hutchgrant.tasks.SyncTask;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class syncProvider extends Service {

	String usrToken = "";
	String usrProf = "";
	String usrName = "";
	ContentResolver contentResolver;
	TunaApp app;
	IBinder mSyncBinder = new SyncBinder();

	public NetworkAccess networkAccess;

	public ProfileAccess profAccess;
	public AlbumAccess albAccess;
	public ImageAccess imgAccess;
	public GroupAccess grpAccess;

	public SyncGroupAccess syncGrpAccess;
	public SyncAlbumAccess syncAlbAccess;
	public SyncAllAccess syncAllAccess;
	
	public PreferenceAccess prefAccess;


	@Override
	public void onCreate(){
		app = (TunaApp)getApplication();
		contentResolver = this.getContentResolver();
		NetworkAccess.initAccess(this);
		ProfileAccess.initAccess(this);
		AlbumAccess.initAccess(this);
		ImageAccess.initAccess(this);
		GroupAccess.initAccess(this);

		SyncGroupAccess.initAccess(this);
		SyncAlbumAccess.initAccess(this);
		SyncAllAccess.initAccess(this);
		
		PreferenceAccess.initAccess(this);
		
		networkAccess = NetworkAccess.getInstance();
		profAccess = ProfileAccess.getInstance();
		grpAccess = GroupAccess.getInstance();
		albAccess = AlbumAccess.getInstance();
		imgAccess = ImageAccess.getInstance();

		syncGrpAccess = SyncGroupAccess.getInstance();
		syncAlbAccess = SyncAlbumAccess.getInstance();
		syncAllAccess = SyncAllAccess.getInstance();

		prefAccess = PreferenceAccess.getInstance();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mSyncBinder;
	}

	@Override
	public void onDestroy(){

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent){

		return super.onUnbind(intent);
	}

	public class SyncBinder extends Binder {
		public syncProvider getServerInstance() {
			return syncProvider.this;
		}
	}

	public void testThread(){
		
	//	ControlTask ct = new ControlTask();
	//	ct.execute(this);
	//	run();
		SyncTask sy = new SyncTask();
		sy.execute(this);
	}
	
	
	public void run(){
		runnable.run();

	}
	private Runnable runnable = new Runnable() 
	{
		private Handler handler = new Handler();

	    @Override
		public void run() 
	    {
	      //  System.out.println("testing...");

	         handler.postDelayed(this, 1000);	

	     	SyncTask.execute(this);
	         
	    }
	};


	public void msgThread() {
		//	public void msgThread(){
		SyncMsgTask mt = new SyncMsgTask();
		//mt.run();
		mt.execute(this);
	}
}
