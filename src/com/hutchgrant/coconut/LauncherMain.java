package com.hutchgrant.coconut;


import com.hutchgrant.app.TunaApp;
import com.hutchgrant.camera.CamActivity;
import com.hutchgrant.contacts.InviteDisplay;
import com.hutchgrant.contacts.LifeAlbumList;
import com.hutchgrant.gallery.Constants.Extra;
import com.hutchgrant.provider.tunatestdisplay;
import static com.hutchgrant.gallery.Constants.IMAGES;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LauncherMain extends Activity implements View.OnClickListener{
    public static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT = 0;

    Button btnCam, btnCircles, btnUserFeed, btnFriendFeed, btnSync, btnImgGallery;
	String signInUID = "", signInUser = "", userImgUrl = "";
	TunaApp app;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher_main);
		
		btnCam = (Button) findViewById(R.id.camera_btn);
		btnCircles = (Button) findViewById(R.id.circles_btn);
		btnUserFeed = (Button) findViewById(R.id.user_feed_btn);
		btnFriendFeed = (Button) findViewById(R.id.friend_feed_btn);
		btnImgGallery = (Button) findViewById(R.id.ImgGal_btn);
		btnSync = (Button) findViewById(R.id.btn_sync);
		
		btnCam.setOnClickListener(this);
		btnCircles.setOnClickListener(this);
		btnUserFeed.setOnClickListener(this);
		btnFriendFeed.setOnClickListener(this);
		btnImgGallery.setOnClickListener(this);
		btnSync.setOnClickListener(this);
		
	//	System.out.println("test");
		app = (TunaApp) ((Activity) this).getApplication();
	//	app.getAllSync();
		
		app.startThread();
		app.startMsgThread();

	}

	@Override
	public void onClick(View v){
		Intent intent = new Intent();
		int id = v.getId();
		if (id == R.id.camera_btn) {
			intent.setClass(LauncherMain.this, CamActivity.class);
			startActivity(intent);
		} else if (id == R.id.circles_btn) {
			///intent.setClass(LauncherMain.this, LifeGroupList.class);
			//intent.setClass(LauncherMain.this, LifeInviteList.class);
			intent.setClass(LauncherMain.this, InviteDisplay.class);
			startActivity(intent);
		} else if (id == R.id.user_feed_btn) {
			intent.setClass(LauncherMain.this, tunatestdisplay.class);
			startActivity(intent);
		} else if (id == R.id.friend_feed_btn) {
			intent.setClass(this, Communicate.class);
			intent.putExtra("viewNum", 0);
			///	intent.putExtra("selGroupID", "n/a");
		//	intent.putExtra("selGroupName", "n/a");
			startActivity(intent);
		} else if (id == R.id.ImgGal_btn) {
			intent.setClass(LauncherMain.this, LifeAlbumList.class);
			intent.putExtra(Extra.IMAGES, IMAGES);
			startActivity(intent);
		} else if (id == R.id.btn_sync) {
			intent.setClass(LauncherMain.this, preferences.class);
			startActivity(intent);
		}
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		finish();
	}
	
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    new MenuInflater(this).inflate(R.menu.option, menu);
	    

	    return(super.onCreateOptionsMenu(menu));
	  }
	  

}
	

