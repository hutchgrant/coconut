package com.hutchgrant.coconut;



import com.hutchgrant.app.AlbumAccess;
import com.hutchgrant.app.GroupAccess;
import com.hutchgrant.app.ImageAccess;
import com.hutchgrant.app.InviteAccess;
import com.hutchgrant.app.MessageAccess;
import com.hutchgrant.app.NetworkAccess;
import com.hutchgrant.app.PreferenceAccess;
import com.hutchgrant.app.ProfileAccess;
import com.hutchgrant.app.SyncAlbumAccess;
import com.hutchgrant.app.SyncAllAccess;
import com.hutchgrant.app.SyncGroupAccess;
import com.hutchgrant.app.SyncMessageAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.tasks.MessageTask;
import com.hutchgrant.tasks.SyncMsgTask;
import com.hutchgrant.tasks.SyncTask;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

public class provider extends Service {

	String usrToken = "";
	String usrProf = "";
	String usrName = "";
	ContentResolver contentResolver;
	TunaApp app;
	IBinder mBinder = new LocalBinder();

	public NetworkAccess networkAccess;

	public ProfileAccess profAccess;
	public AlbumAccess albAccess;
	public ImageAccess imgAccess;
	public GroupAccess grpAccess;

	public SyncGroupAccess syncGrpAccess;
	public SyncAlbumAccess syncAlbAccess;
	public SyncAllAccess syncAllAccess;
	
	public PreferenceAccess prefAccess;
	public InviteAccess invAccess;
	
	public MessageAccess msgAccess;
	public SyncMessageAccess syncMsgAccess;


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
		InviteAccess.initAccess(this);
		
		MessageAccess.initAccess(this);
		SyncMessageAccess.initAccess(this);
		
		networkAccess = NetworkAccess.getInstance();
		profAccess = ProfileAccess.getInstance();
		grpAccess = GroupAccess.getInstance();
		albAccess = AlbumAccess.getInstance();
		imgAccess = ImageAccess.getInstance();

		syncGrpAccess = SyncGroupAccess.getInstance();
		syncAlbAccess = SyncAlbumAccess.getInstance();
		syncAllAccess = SyncAllAccess.getInstance();

		prefAccess = PreferenceAccess.getInstance();
		invAccess = InviteAccess.getInstance();
		
		msgAccess = MessageAccess.getInstance();
		syncMsgAccess = SyncMessageAccess.getInstance();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
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

	public class LocalBinder extends Binder {
		public provider getServerInstance() {
			return provider.this;
		}
	}

	public void testThread(){
		SyncTask sy = new SyncTask();
		//sy.run();
		sy.execute(this);
	}
	public void msgThread(){
		SyncMsgTask mt = new SyncMsgTask();
		//mt.run();
		mt.execute(this);
	}
}
