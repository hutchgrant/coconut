package com.hutchgrant.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.LifeAlbum;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.Elements.User;
import com.hutchgrant.app.ProfileAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.coconut.R;
import com.hutchgrant.contacts.LifeAlbumList;
import com.hutchgrant.imagesend.ImageSend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class CamActivity extends Activity implements View.OnClickListener {
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final int IMAGE_MAX_SIZE = 0;
	private int pictureCount = 0, MaxImages = 1;
	private Camera mCamera;
	private CamPreview mCameraPreview;
	public String imgURL = "", userID = "", user = "", UStoken = "";
	Bitmap bitmap;
	Button uploadButton, captureButton;
	FrameLayout preview;
	User profile;
	LifeAlbum lifeAlb;
	Album life;

	TunaApp app;
	Context ctx;
	ImageView mPrevImg;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_screen);
		app = (TunaApp) this.getApplication();
		life = new Album(this);
		lifeAlb = new LifeAlbum(this);
		ctx = this;
		setProfile();

		mCamera = getCameraInstance();
		mCameraPreview = new CamPreview(this, mCamera);
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mCameraPreview);
		captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(this);
		mPrevImg = (ImageView) findViewById(R.id.IcamPrevImg1);
		bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.profilebutton, options);
		mPrevImg.setImageBitmap(bitmap);

		LocalBroadcastManager.getInstance(this).registerReceiver(
				mAlbumReceiver, new IntentFilter(Endpoints.EVENT_ALBUM_WRITE));
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mImageReceiver, new IntentFilter(Endpoints.EVENT_IMAGE_WRITE));

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.button_capture) {

			if (pictureCount < MaxImages) {
				mCamera.takePicture(null, null, mPicture);

			} 
		}

	}

	public void startUpload() {
		Intent intent = new Intent(CamActivity.this, ImageSend.class);
		intent.putExtra("imageURL", imgURL);
		startActivity(intent);
	}

	/**
	 * Helper method to access the camera returns null if it cannot get the
	 * camera or does not exist
	 * 
	 * @return
	 */
	private Camera getCameraInstance() {
		Camera camera = null;

		try {
			camera = Camera.open();
		} catch (Exception e) {
			// cannot get camera or does not exist
		}
		return camera;
	}

	Camera.PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			if (pictureCount < MaxImages) {
				String imgName = "";
				imgName = nameImage();
				File pictureFile = Endpoints.getOutputMediaFile(imgName);
				setImage(imgName);
				imgURL = pictureFile.getAbsolutePath();
				System.out.println("imgurl created at: " + imgURL);

				try {
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();

				} catch (FileNotFoundException e) {

				} catch (IOException e) {

				}

				pictureCount++;
				bitmap = null;
				bitmap = lifeAlb.decodeFile(pictureFile);
				mPrevImg.setImageBitmap(bitmap);
				
			} 
			if (MaxImages == 1) {
				pictureCount = 0;
				lifeAlb.createAlbum(life);

			}else if(MaxImages == 5) {
				pictureCount = 0;
				lifeAlb.createAlbum(life);
			}
			camera.startPreview();
		}
	};

	public void setProfile() {
		profile = new User();
		profile = app.getProfile();
	}

	public String nameImage() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imgName = "IMG_" + timeStamp + ".jpg";
		return imgName;
	}

	public void setImage(String imgName) {
		Photo photo = new Photo();
		photo.fill("jpg", imgName, profile.userGName, profile.userGID, "", "",
				"", false);
		life.addPhoto(photo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.camoptions, menu);
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.Take_1_images) {
			MaxImages = 1;
		} else if (id == R.id.Take_5_images) {
			MaxImages = 5;
		}
		return (true);
	}

	// handler for received Intents for the "my-event" event
	private BroadcastReceiver mAlbumReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Extract data included in the Intent
			String message = intent.getStringExtra("message");
			Log.d("receiver", "Got Album Write Message: " + message);
			if (message.equals("failure")) {
				Log.d("receiver", "Error cannot send group -" + message);
			} else if (message.equals("success")) {
				lifeAlb.DescribeImgs();
			}
		}
	};
	// handler for received Intents for the "my-event" event
	private BroadcastReceiver mImageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Extract data included in the Intent
			String message = intent.getStringExtra("message");
			Log.d("receiver", "Got Image description Write Message: " + message);
			if (message.equals("failure")) {
				Log.d("receiver", "Error cannot send group -" + message);
			} else if (message.equals("success")) {
				lifeAlb = new LifeAlbum(ctx);
				life = new Album(ctx);
			}
		}
	};
}
