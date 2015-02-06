package com.hutchgrant.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.UUID;

import com.hutchgrant.Elements.Sync.AlbumData;
import com.hutchgrant.Elements.Sync.SocialImage;
import com.hutchgrant.app.ProfileAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.coconut.R;
import com.hutchgrant.imagesend.ImageSend.SelectAlbumListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class LifeAlbum {

	public boolean AddToAlbum = false;

	public ArrayList<Album> Albums; // all groups
	private String albumID;
	Context ctx;
	public int albAmount = 0;
	Dialog dialog = null;
	public int LAST_ALBUM_ID = 0;
	public boolean diagOpen = false;
	User profile;
	TunaApp app;
	private String[] spinnerArray;
	Album createdAlbum;
	public int ImagePos = 0;

	public LifeAlbum(Context context) {
		this.Albums = new ArrayList<Album>();
		this.ctx = context;
		this.albAmount = 0;
	}

	public void addAlbum(Album album) {
		this.Albums.add(album);
		this.albAmount++;
	}

	public void removeAlbum(Album album) {
		this.Albums.remove(album);
		this.albAmount--;
	}

	public void modifyAlbum(Album album, int position) {
		app = (TunaApp) ((Activity) ctx).getApplication();
		Albums.add(position, album);
		app.updateAlbum(album);
	}

	public Album getAlbum(int index) {
		return this.Albums.get(index);
	}

	public ArrayList<Album> getAllAlbums() {
		return this.Albums;
	}

	public void fillAlbums() {
		app = (TunaApp) ((Activity) ctx).getApplication();
		Albums = app.readLocalAlbums();
	}

	public void editAlbumName() {
		for (int i = 0; i < Albums.size(); i++) {
			if (Albums.get(i).isSelected()) {
				showDialog(Endpoints.DIALOG_MODIFY_ALBUM, Albums.get(i), i);
			}
		}
	}

	public void showDialog(int i, final Album album, final int position) {
		app = (TunaApp) ((Activity) ctx).getApplication();

		profile = new User();
		profile = app.getProfile();
		switch (i) {
		case Endpoints.DIALOG_MODIFY_ALBUM:
			dialog = new Dialog(ctx);
			dialog.setContentView(R.layout.album_name_dialog);
			dialog.setTitle("Welcome");
			dialog.setCanceledOnTouchOutside(true);
			Button buttonAlb = (Button) dialog.findViewById(R.id.btn_albCreate);

			buttonAlb.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					EditText albumName = (EditText) dialog
							.findViewById(R.id.album_nameInput);
					String albName = "";
					albName = albumName.getText().toString();
					Album alb = new Album(ctx);
					alb = album;
					alb.albName = albName;
					alb.syncDate = "updated";
					alb.authorID = profile.userGID;
					modifyAlbum(alb, position);
					diagOpen = false;
					dialog.dismiss();
				}
			});
			diagOpen = true;
			dialog.show();
			break;
		case Endpoints.DIALOG_ADD_ALBUM:
			dialog = new Dialog(ctx);
			dialog.setContentView(R.layout.album_name_dialog);
			dialog.setTitle("Welcome");
			dialog.setCanceledOnTouchOutside(true);
			Button buttonAlbCreate = (Button) dialog
					.findViewById(R.id.btn_albCreate);
			buttonAlbCreate.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String albName = "";
					Album alb = new Album(ctx);
					EditText albumName = (EditText) dialog
							.findViewById(R.id.album_nameInput);
					albName = albumName.getText().toString();
					alb.fill(UUID.randomUUID().toString(), profile.userGID,
							albName);
					app.writeAlbum(alb);
					app.setPendingData(true, true);
					addAlbum(alb);
					sendMessage(Endpoints.EVENT_ALBUM_WRITE, true);
					diagOpen = false;
					dialog.dismiss();
				}
			});
			diagOpen = true;
			dialog.show();
			break;
		case Endpoints.DIALOG_CREATE_ALBUM:
			dialog = new Dialog(ctx);
			dialog.setContentView(R.layout.album_name_dialog);
			dialog.setTitle("New Album");
			dialog.setCanceledOnTouchOutside(true);
			Button buttonAlbCreate2 = (Button) dialog
					.findViewById(R.id.btn_albCreate);
			RadioButton createAlbRad = (RadioButton) dialog
					.findViewById(R.id.radio_create_album);
			RadioButton addToAlbRad = (RadioButton) dialog
					.findViewById(R.id.radio_addto_album);
			addToAlbRad
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							TextView albCreateText = (TextView) dialog
									.findViewById(R.id.txt_create_alb);
							EditText albumName = (EditText) dialog
									.findViewById(R.id.album_nameInput);

							if (!isChecked) {
								albCreateText.setVisibility(View.VISIBLE);
								albumName.setVisibility(View.VISIBLE);

							} else {
								albCreateText.setVisibility(View.GONE);
								albumName.setVisibility(View.GONE);
							}

						}
					});

			createAlbRad
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							Spinner albSpin = (Spinner) dialog
									.findViewById(R.id.alb_spinner);
							if (!isChecked) {
								fillAlbums();
								spinnerArray = new String[albAmount + 2];
								spinnerArray = fillSpinner();
								ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
										ctx,
										android.R.layout.simple_spinner_item,
										spinnerArray);
								spinnerArrayAdapter
										.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								albSpin.setAdapter(spinnerArrayAdapter);
								albSpin.setOnItemSelectedListener(new SelectAlbumListener());

								albSpin.setVisibility(View.VISIBLE);
								AddToAlbum = true;
							} else {
								albSpin.setVisibility(View.GONE);
								AddToAlbum = false;
							}
						}
					});
			buttonAlbCreate2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String albName = "";
					Album alb = new Album(ctx);

					if (!AddToAlbum) { // if we're creating an album
						EditText albumName = (EditText) dialog
								.findViewById(R.id.album_nameInput);
						albName = albumName.getText().toString();
						alb.fill(UUID.randomUUID().toString(), profile.userGID,
								albName);
						alb.addPhotos(album.photos);
						alb.setAllImagesToAlbum();
						app.writeAlbum(alb);
						System.out.println("WRITING " + alb.photos.size()
								+ " PHOTOS TO DATABASE");
						app.writeAlbumImages(alb, false);
						app.setPendingData(true, true);
						addAlbum(alb);
					} else { // if we're adding to an album
						alb.fill(albumID, profile.userGID, albName);
						album.ID = albumID;
						album.setAllImagesToAlbum();
						alb.addPhotos(album.photos);
						app.updateAlbum(alb);
						System.out.println("WRITING " + album.photos.size()
								+ " PHOTOS TO DATABASE");

						app.writeAlbumImages(album, false);
					}

					createdAlbum = new Album(ctx);
					createdAlbum = alb;

					sendMessage(Endpoints.EVENT_ALBUM_WRITE, true);
					diagOpen = false;
					dialog.dismiss();
				}
			});
			diagOpen = true;
			dialog.show();
			break;

		case Endpoints.DIALOG_DESCRIBE_IMGS:
			dialog = new Dialog(ctx);
			dialog.setContentView(R.layout.imgdesc_dialog);
			dialog.setTitle("Describe Images");
			dialog.setCanceledOnTouchOutside(true);
			Bitmap bitmap;
			ImageView createImg = (ImageView) dialog
					.findViewById(R.id.image_imgdesc);
			File pictureFile = Endpoints
					.getOutputMediaFile(album.photos.get(0).Name);
			bitmap = null;
			bitmap = decodeFile(pictureFile);
			createImg.setImageBitmap(bitmap);

			ImagePos = 0;
			Button next = (Button) dialog
					.findViewById(R.id.button_next_imgdesc);
			Button prev = (Button) dialog
					.findViewById(R.id.button_prev_imgdesc);
			Button done = (Button) dialog
					.findViewById(R.id.button_save_imgdesc);
			done.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					EditText Desc = (EditText) dialog
							.findViewById(R.id.imgdesc_input);
					EditText Title = (EditText) dialog
							.findViewById(R.id.imgtitle_input);
					CheckBox addAll = (CheckBox) dialog
							.findViewById(R.id.checkBox_addall_imgdesc);
					String sTitle = "";
					String sDesc = "";
					boolean AllImgs = false;

					sTitle = Title.getText().toString();
					sDesc = Desc.getText().toString();

					if (addAll.isChecked()) {
						AllImgs = true;
						for (int i = 0; i < album.photos.size(); i++) {
							album.photos.get(i).fill("jpg",
									album.photos.get(i).Name,
									profile.userGName, profile.userGID,
									album.ID, sTitle, sDesc, false);
							app.updateLocalImage(album.photos.get(i));
						}
						System.out.println("WRITING "
								+ createdAlbum.photos.size()
								+ " PHOTOS TO DATABASE from createdAlbum");
						// app.writeAlbumImages(album, true);
					} else {
						AllImgs = false;
						Photo photo = new Photo();
						photo = album.photos.get(ImagePos);
						photo.fill("jpg", album.photos.get(ImagePos).Name,
								profile.userGName, profile.userGID, album.ID,
								sTitle, sDesc, false);
						app.updateLocalImage(photo);
					}
					sendMessage(Endpoints.EVENT_IMAGE_WRITE, true);
					diagOpen = false;
					dialog.dismiss();

				}

			});

			diagOpen = true;
			dialog.show();
			break;

		}
	}

	public void createAlbum() {
		showDialog(Endpoints.DIALOG_ADD_ALBUM, new Album(ctx), 0);
	}

	public void createAlbum(Album alb) {
		showDialog(Endpoints.DIALOG_CREATE_ALBUM, alb, 0);
	}

	/*
	 * remove all groups that are selected
	 */
	public void removeAllAlbums() {
		app = (TunaApp) ((Activity) ctx).getApplication();
		app.setAlbumsForRemoval(this.Albums);
	}

	public String[] fillSpinner() {
		String[] spinnerArray = new String[this.Albums.size() + 2]; // add 2 for
																	// spinner
																	// menu
		spinnerArray[0] = "Select Album";
		spinnerArray[1] = "Create Album";
		for (int u = 0; u < this.Albums.size(); u++) {
			spinnerArray[u + 2] = this.Albums.get(u).albName;
		}
		return spinnerArray;
	}

	private void sendMessage(String event, boolean msg) {
		Intent intent = new Intent();
		intent = Endpoints.checkSuccess(event, msg);
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	}

	/*
	 * check all groups to see if a specific group exists, return group
	 */
	public Album getSpecifcAlbum(String id) {
		Album alb = new Album(ctx);
		for (int i = 0; i < Albums.size(); i++) {
			if (Albums.get(i).getID().equals(id) == true) {
				alb = Albums.get(i);
				return alb;
			}
		}
		return null;
	}

	public ArrayList<AlbumData> ConvertAlbumsToRemoteObject() {

		ArrayList<AlbumData> albumData = new ArrayList<AlbumData>();

		AlbumData albData;
		SocialImage photo;

		for (int i = 0; i < this.Albums.size(); i++) {
			albData = new AlbumData();
			if (this.Albums.get(i).photos.size() != 0) {
				albData.ID = this.Albums.get(i).ID;
				albData.Title = this.Albums.get(i).albName;
				albData.UserName = this.Albums.get(i).authorID;
				albData.Size = this.Albums.get(i).photos.size();
				albData.setSync(this.Albums.get(i).syncDate,
						this.Albums.get(i).syncTime);
				for (int x = 0; x < this.Albums.get(i).photos.size(); x++) {
					photo = new SocialImage();

					photo.ID = this.Albums.get(i).photos.get(x).ID;
					photo.Type = this.Albums.get(i).photos.get(x).Type;
					photo.Name = this.Albums.get(i).photos.get(x).Name;
					photo.username = this.Albums.get(i).photos.get(x).username;
					photo.userID = this.Albums.get(i).photos.get(x).userID;
					photo.AlbumID = this.Albums.get(i).photos.get(x).AlbumID;
					photo.Title = this.Albums.get(i).photos.get(x).Title;
					photo.Description = this.Albums.get(i).photos.get(x).Description;
					photo.UploadDate = this.Albums.get(i).photos.get(x).UploadDate;
					photo.UploadTime = this.Albums.get(i).photos.get(x).UploadTime;
					albData.images.add(photo);
				}
				albumData.add(albData);
			}
		}
		return albumData;
	}

	public class SelectAlbumListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (pos > 0) {
				albumID = getAlbum(pos - 2).ID;
				System.out.println("photo will be placed in  album " + albumID
						+ "which has a sync date of :" + albumID);
			}
		}

		@Override
		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}

	public void DescribeImgs() {
		showDialog(Endpoints.DIALOG_DESCRIBE_IMGS, createdAlbum, 0);
	}

	public Bitmap decodeFile(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 70;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}
}
