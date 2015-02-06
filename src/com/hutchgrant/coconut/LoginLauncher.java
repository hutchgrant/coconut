package com.hutchgrant.coconut;



import com.hutchgrant.coconut.R;
import com.hutchgrant.app.TunaApp;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LoginLauncher extends Activity implements OnClickListener{
	Button btn_gplus, btn_fb;
	boolean logged = false;
	TunaApp app;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsignin);
		app = (TunaApp)getApplication();
		btn_gplus = (Button)findViewById(R.id.gplus_btn);
		btn_fb 	= (Button)findViewById(R.id.fb_btn);
		
		btn_gplus.setOnClickListener(this);
		  // Register mMessageReceiver to receive messages.
		  LocalBroadcastManager.getInstance(this).registerReceiver(mLoginReceiver,
		      new IntentFilter("event_login"));
		  LocalBroadcastManager.getInstance(this).registerReceiver(mNetworkReceiver,
			      new IntentFilter("event_network"));
	}
	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.gplus_btn) {
			app.login();
			Toast.makeText(this, "ATTEMPTING TO LOGIN", 20);
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(logged){
			Intent z = new Intent();
   	    	z.setClass(this, LauncherMain.class);
   	    	startActivity(z); 
		}
	}
  
  @Override
  public void onDestroy(){
	  super.onDestroy();
	  finish();
  }
  public void logged(){
		logged = true;
		
	    Intent w = new Intent();
		    w.setClass(this, LauncherMain.class);
		    startActivity(w);
  }
  
	//handler for received Intents for the "my-event" event 
	private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
	 @Override
	 public void onReceive(Context context, Intent intent) {
	   // Extract data included in the Intent
	   String message = intent.getStringExtra("message");
	   Log.d("receiver", "Got message: " + message);
	   logged();
	 }
	};
	//handler for received Intents for the "my-event" event 
	private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		 // Extract data included in the Intent
		 String message = intent.getStringExtra("message");
		 Log.d("receiver", "Got Network: " + message);
			if(message.equals("failure")){
				warning();
				 Log.d("receiver", "Error cannot detect an internet connection -" + message);
			}
		}
	};
	
	public void warning(){
		Toast.makeText(this, "Unable to detect internet connection", Toast.LENGTH_SHORT).show();
	}
}
