package com.hutchgrant.camera;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hutchgrant.coconut.R;
import com.hutchgrant.imagesend.ImageSend;

public class CamDisplay extends Activity implements View.OnClickListener{
	
	Button Accept, Deny;
	ImageView usrProfile;
	Bitmap usrImage;
	public String photoURL;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cam_img_display);
		Bundle extras = getIntent().getExtras();
		photoURL = extras.getString("photoURL");
		
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = 5;
		usrImage = BitmapFactory.decodeFile(photoURL, options);
		usrProfile = (ImageView)findViewById(R.id.image_camera);
		usrProfile.setImageBitmap(usrImage);
		
		Accept = (Button) findViewById(R.id.btn_accept);
		Deny = (Button) findViewById(R.id.btn_sync);
		
		Accept.setOnClickListener(this);
		Deny.setOnClickListener(this);
	}


	@Override
	public void onClick(View v){
		int id = v.getId();
		if (id == R.id.btn_accept) {
			usrImage.recycle();
			Intent intent = new Intent(CamDisplay.this, ImageSend.class);
			intent.putExtra("photoURL", photoURL);
			startActivity(intent);
			finish();
		} else if (id == R.id.btn_sync) {
			File filepath = new File(photoURL);
			filepath.delete();
			finish();
		}
	}
}
