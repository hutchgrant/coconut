package com.hutchgrant.networks.gplus;


import com.hutchgrant.coconut.R;
import com.hutchgrant.Elements.PrefObj;
import com.hutchgrant.Elements.User;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.app.TunaApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Gloading extends base implements OnClickListener{
	
	String type = "";
	TunaApp app;
	TextView text;
	Button next;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gplus_loading);
		app = (TunaApp)getApplication();
		text = (TextView)this.findViewById(R.id.name_tag);
		next = (Button)this.findViewById(R.id.btn_end);
		next.setEnabled(false);
		next.setOnClickListener(this);
	}
	
    @Override
    public void setAuthenticatedProfile(User profile) {
        super.setAuthenticatedProfile(profile);
		resetTaskState();
		
        app.newProfile(profile);
        next.setEnabled(true);
        text.setText("Tuna Registration successful");

    }
    @Override
	public void sendMessage(String event, boolean msg) {
		
		if(event.equals("EVENT_LOGIN")){
		
		  Intent intent = new Intent("event_login");
		  // Add data
		  if(msg){
			  intent.putExtra("message", "success");
		  }else{
			  intent.putExtra("message", "failure");
		  }
		  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		  
		}
	}
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	finish();
    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_end) {
			sendMessage("EVENT_LOGIN", true);
			PrefObj pref = new PrefObj();
			pref.fill(true, 5);
			app.setPref(pref, false);
			app.setPendingData(false, true);
			SyncObj sync = new SyncObj();  /// blank sync cache tally
			sync.fill(this.mPhotoUser.userGID, "na", "na", "na", "na", "na", "na", "na",0,0,0,0);
			sync.fillInvite("na", "na", "na", 0, 0);
			sync.fillMsg("na", "na", "na", 0, 0);
			app.writeTally(sync, false);
		//	app.getAllSync();
		//	new SyncTask().execute(this);
			finish();
		}
	}
}
