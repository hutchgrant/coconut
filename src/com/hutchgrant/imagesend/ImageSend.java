package com.hutchgrant.imagesend;

import java.io.File;
import java.util.UUID;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.coconut.R;
import com.hutchgrant.tasks.AlbumTask;
import com.hutchgrant.Elements.LifeAlbum;
import android.widget.AdapterView.OnItemSelectedListener;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.Elements.User;
import com.hutchgrant.Elements.Sync.SyncObj;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class ImageSend extends Activity implements View.OnClickListener{

	public String imgPath;
	public String newImgUrl;
	public User user;
	public String nwTitle = "", nwDescript = "", name = "";  // image details
	public String albumID = "", albumTitle, albumSyncDate = "";  // selected album details
	public int albumSize = 0;
	HttpUploader uploader;
	private getUploadUrl MakeURL;
	public Photo photo;
	
	private ImageView imgView;
	EditText inpTitle, inpDescription;
	private Button send_btn, cancel_btn;
	Bitmap usrImage;
	@SuppressWarnings("unused")
	private String image_name;
	LifeAlbum lifeAlb;
	ContentResolver contentResolver;
	Cursor cur;
	TunaApp app;
	Spinner album_spin;
	String[] spinnerArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imgshare_main);
		Bundle extras = getIntent().getExtras();
		imgPath = extras.getString("photoURL");
		app = (TunaApp)this.getApplication();

		album_spin = (Spinner) findViewById(R.id.album_spin_msg);
		lifeAlb = new LifeAlbum(this);
		setAlbumSpinner();

		send_btn = (Button) findViewById(R.id.btn_send);
		cancel_btn = (Button) findViewById(R.id.btn_cancel);
		imgView = (ImageView)findViewById(R.id.CamResult);
		inpTitle = (EditText)findViewById(R.id.input_title);
		inpDescription = (EditText)findViewById(R.id.input_description);
		
		send_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
		
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = 4;
		usrImage = BitmapFactory.decodeFile(imgPath, options);
		imgView.setImageBitmap(usrImage);
		
		  LocalBroadcastManager.getInstance(this).registerReceiver(mAlbWriteReceiver,
		      new IntentFilter(Endpoints.EVENT_ALBUM_WRITE));
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.btn_send) {
			user = app.getProfile();
			makePhoto();
			app.newImage(photo);
			Album alb = new Album(this);
			alb.fill(albumID, user.userGID, albumTitle);
			alb.albSize = albumSize;
			if(albumSyncDate.equals("") == false){
				alb.setSync("updated", "");
			}
			app.updateAlbum(alb);
			app.setPendingData(true, true);
			
			if(app.isSyncOn() && app.checkNetworkAvail()){
				AlbumTask at = new AlbumTask(this);
				at.setSyncType(1);
				at.execute(new SyncObj());
			}
			finish();
		}
	}
	
	private void setAlbumSpinner() {
		lifeAlb.fillAlbums();
		spinnerArray = new String[lifeAlb.albAmount + 2];
		spinnerArray = lifeAlb.fillSpinner();
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
		spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		album_spin.setAdapter(spinnerArrayAdapter);
	    album_spin.setOnItemSelectedListener(new SelectAlbumListener());		
	}
	
	public void makePhoto(){
		nwTitle = inpTitle.getText().toString();
		nwDescript = inpDescription.getText().toString();
		name = new File(imgPath).getName();
		photo = new Photo("jpg", name, user.userGName, user.userGID, albumID, nwTitle, nwDescript, false);
		photo.ID = UUID.randomUUID().toString();
	}
	
	public class SelectAlbumListener implements OnItemSelectedListener {

	    @Override
		public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	    	if(pos > 1){
	    	albumID = lifeAlb.getAlbum(pos-2).ID;
	    	albumTitle = lifeAlb.getAlbum(pos-2).albName;
	    	albumSize = lifeAlb.getAlbum(pos-2).albSize;
	    	albumSyncDate = lifeAlb.getAlbum(pos-2).syncDate;
	    	System.out.println("photo will be placed in  album "+albumID + "which has a sync date of :" +albumSyncDate);
	    	}else if(pos == 1){
	    		lifeAlb.createAlbum();
	    		System.out.println("NEW ALBUM WOULD HAVE BEEN CREATED ....");
	    	}
	    }

	    @Override
		public void onNothingSelected(AdapterView parent) {
	      // Do nothing.
	    }
	}
	//handler for received Intents for the "my-event" event 
	private BroadcastReceiver mAlbWriteReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		 // Extract data included in the Intent
		 String message = intent.getStringExtra("message");
		 Log.d("receiver", "Got Network: " + message);
			if(message.equals("failure")){
				 Log.d("receiver", "Error cannot detect an internet connection -" + message);
			}else{
				setAlbumSpinner();
				album_spin.setSelection(lifeAlb.getAllAlbums().size()+1);
				
			}
		}
	};
}
