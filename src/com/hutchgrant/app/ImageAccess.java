package com.hutchgrant.app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import org.apache.http.util.ByteArrayBuffer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.provider.tunadatamodel;


public class ImageAccess{

	private static final String TAG = null;
	private static ImageAccess instance;
	public Photo image;
	Bitmap usrImage;
	static Context ctx;
	ContentResolver cr;
	Cursor cur;

	public ImageAccess(Context context){
		ctx = context;
		cr = ctx.getContentResolver();
	}

	public static void initAccess(Context context){
		if(instance == null){
			instance = new ImageAccess(context);
		}
	}

	public static ImageAccess getInstance(){
		return instance;
	}

	public Photo getPhoto(){
		return image;
	}
	public void setImage(Photo newimage){
		image = newimage;
	}
	public void setImage(String type, String name, String user, String userid, String albumid, String title, String description, boolean synced){
		image.fill(type, name, user, userid, albumid, title, description, synced);
	}


	/*
	 * Get local image data from provider
	 */
	public Bitmap getImage(){
		return null;
	}
	/*
	 * Get local image bitmap from path
	 */
	public Bitmap getLocalImage(String path){

		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = 8;
		usrImage = BitmapFactory.decodeFile(path, options);
		return usrImage;
	}	
	/*
	 * Download new profile image to content provider
	 */
	public byte[] getRemoteImage(String url){
		try {
			URL imageUrl = new URL(url);
			URLConnection ucon = imageUrl.openConnection();

			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			ByteArrayBuffer baf = new ByteArrayBuffer(500);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			return baf.toByteArray();
		} catch (Exception e) {
			Log.d("ImageManager", "Error: " + e.toString());
		}
		return null;
	}

	String SavePhoneTask(byte[] jpeg, String photoLocation) {
		File photo=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/Tuna/", photoLocation);
		String imgURL = photo.getAbsolutePath();
		System.out.println("imgurl created at: "+imgURL);

		try {
			FileOutputStream fos = new FileOutputStream(photo);
			fos.write(jpeg);
			fos.close();

		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}

		return(null);
	}

	public void saveLocalImage(Photo photo, boolean update) {
		ContentValues val = new ContentValues();
		if(update){
			val.put(tunadatamodel.SQL_INS_REP, true);  
		}
		val.put(tunadatamodel.TunaImage.Cols.IMGID, (photo.ID != null) ? photo.ID : null);
		val.put(tunadatamodel.TunaImage.Cols.IMGALBID, (photo.AlbumID != null) ? photo.AlbumID : null);
		val.put(tunadatamodel.TunaImage.Cols.IMGNAME, (photo.Name != null) ? photo.Name : null);
		val.put(tunadatamodel.TunaImage.Cols.IMGTYPE, (photo.Type != null) ? photo.Type : null);
		val.put(tunadatamodel.TunaImage.Cols.IMGUSR, (photo.username != null) ? photo.username : null);
		val.put(tunadatamodel.TunaImage.Cols.IMGUSRID, (photo.userID != null) ? photo.userID : null);
		val.put(tunadatamodel.TunaImage.Cols.IMGTITLE, (photo.Title != null) ? photo.Title : null);
		val.put(tunadatamodel.TunaImage.Cols.IMGDESC, (photo.Description != null) ? photo.Description : null);
		val.put(tunadatamodel.TunaImage.Cols.IMGUPTIME, (photo.UploadTime != null) ? photo.UploadTime : null);
		val.put(tunadatamodel.TunaImage.Cols.IMGUPDATE, (photo.UploadDate != null) ? photo.UploadDate : null);
		val.put(tunadatamodel.TunaImage.Cols.IMGUPTOKEN, (photo.UploadToken != null) ? photo.UploadToken : null);
		val.put(tunadatamodel.TunaImage.Cols.IMGSYNC, Boolean.toString(photo.Synced));
		System.out.println(photo.toString());
		cr.insert(tunadatamodel.TunaImage.CONTENT_URI, val);	
	}

	public ArrayList<Photo> readAllImages(){
		ArrayList<Photo> ListPhotos = new ArrayList<Photo>();

		ContentResolver cr = ctx.getContentResolver();
		Cursor cur2 = cr.query(tunadatamodel.TunaImage.CONTENT_URI, null, null, null,
				null);
		int imgidIndex = 0, nameIndex = 0, usernameIndex = 0, useridIndex = 0, dateIndex = 0, titleIndex = 0, descIndex = 0, imgSyncIndex = 0;
		imgidIndex = cur2.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGID);
		usernameIndex = cur2.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUSR);
		useridIndex = cur2.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUSRID);
		nameIndex = cur2.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGNAME);
		dateIndex = cur2.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUPDATE);
		titleIndex = cur2.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGTITLE);
		descIndex = cur2.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGDESC);
		imgSyncIndex = cur2.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGSYNC);
		int columnCount = cur2.getColumnCount();

		String username = null, userid = null, imgid = null, imgName = null, imgDate = null, imgTitle = null, imgDesc = null;
		String synced = null;
		if (cur2.moveToFirst()) {

			do {
				for(int i = 0; i < columnCount; i++){
					if(i == nameIndex){
						imgName = cur2.getString(i);
					}else if(i == dateIndex){
						imgDate = cur2.getString(i);
					}else if(i == titleIndex){
						imgTitle = cur2.getString(i);
					}else if(i == descIndex){
						imgDesc = cur2.getString(i);
					}else if(i == usernameIndex){
						username = cur2.getString(i);
					}else if(i == imgidIndex){
						imgid = cur2.getString(i);
					} else if(i == useridIndex){
						userid = cur2.getString(i);
					} else if(i == imgSyncIndex){
						synced = cur2.getString(i);
					}
				}
				if(imgDate.equals("removed") == false){
					Photo undatedPhoto = new Photo(null, imgName, username, userid, null, imgTitle,imgDesc,Boolean.parseBoolean(synced));
					undatedPhoto.UploadDate=imgDate;
					undatedPhoto.ID = imgid;
					ListPhotos.add(undatedPhoto);
				}
			}while(cur2.moveToNext());
		}

		cur2.close();
		return ListPhotos;
	}

	public ArrayList<Photo> getDirtyPhotos(){
		ArrayList<Photo> ListPhotos = new ArrayList<Photo>();
		ContentResolver cr;
		cr = ctx.getContentResolver();
		Cursor cur = cr.query(tunadatamodel.TunaImage.CONTENT_URI, null,tunadatamodel.TunaImage.Cols.IMGSYNC+" = ?", new String[]{"false"},
				null);
		int imgidIndex = 0, imgAlbIdIndex = 0, nameIndex = 0, usernameIndex = 0, useridIndex = 0, dateIndex = 0, tokenIndex = 0, titleIndex = 0, descIndex = 0, imgSyncIndex = 0;
		imgidIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGID);
		imgAlbIdIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGALBID);
		usernameIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUSR);
		useridIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUSRID);
		nameIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGNAME);
		dateIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUPDATE);
		tokenIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUPTOKEN);
		titleIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGTITLE);
		descIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGDESC);
		imgSyncIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGSYNC);
		int columnCount = cur.getColumnCount();

		String username = null, albid = null, userid = null, imgid = null, imgName = null, imgDate = null, imgTitle = null, imgDesc = null;
		String synced = null, token = null;
		if(cur.moveToFirst()){
			do {
				for(int i = 0; i < columnCount; i++){
					if(i == nameIndex){
						imgName = cur.getString(i);
					}else if(i == dateIndex){
						imgDate = cur.getString(i);
					}else if(i == titleIndex){
						imgTitle = cur.getString(i);
					}else if(i == descIndex){
						imgDesc = cur.getString(i);
					}else if(i == usernameIndex){
						username = cur.getString(i);
					}else if(i == imgidIndex){
						imgid = cur.getString(i);
					} else if(i == useridIndex){
						userid = cur.getString(i);
					} else if(i == imgAlbIdIndex){
						albid = cur.getString(i);
					}else if(i == imgSyncIndex){
						synced = cur.getString(i);
					}else if(i == tokenIndex){
						token = cur.getString(i);
					}
				}
			//	System.out.println("the image "+imgTitle+" synced = "+synced);

				if(imgDate.equals("update") || imgDate.equals("removed") ==true || synced.equals("false")==true ){
					Photo undatedPhoto = new Photo(null, imgName, username, userid, albid, imgTitle,imgDesc,Boolean.parseBoolean(synced));
					undatedPhoto.ID = imgid;
					undatedPhoto.setToken(token);
					ListPhotos.add(undatedPhoto);
				}
			}while(cur.moveToNext());
		}
		cur.close();
		return ListPhotos;
	}

	public ArrayList<Photo> readRemovedImages(){
		ArrayList<Photo> ListPhotos = new ArrayList<Photo>();

		ContentResolver cr = ctx.getContentResolver();
		Cursor cur = cr.query(tunadatamodel.TunaImage.CONTENT_URI, null,tunadatamodel.TunaImage.Cols.IMGUPDATE+" = ?", new String[]{"REMOVE"},
				null);
		int imgidIndex = 0, imgAlbIdIndex = 0, nameIndex = 0, usernameIndex = 0, useridIndex = 0, dateIndex = 0, titleIndex = 0, descIndex = 0, imgSyncIndex = 0;
		imgidIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGID);
		imgAlbIdIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGALBID);
		nameIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGNAME);
		imgSyncIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGSYNC);
		int columnCount = cur.getColumnCount();

		String imgid = null, albid = null, imgName = null, imgDate = null;
		String synced = null;
		if (cur.moveToFirst()) {

			do {
				for(int i = 0; i < columnCount; i++){
					if(i == nameIndex){
						imgName = cur.getString(i);
					}else if(i == dateIndex){
						imgDate = cur.getString(i);
					}else if(i == imgidIndex){
						imgid = cur.getString(i);
					} else if(i == imgAlbIdIndex){
						albid = cur.getString(i);
					} else if(i == imgSyncIndex){
						synced = cur.getString(i);
					}
				}
				if(imgDate.equals("removed") == false){
					Photo undatedPhoto = new Photo(null, imgName, null, null, albid, null, null,Boolean.parseBoolean(synced));
					undatedPhoto.UploadDate=imgDate;
					undatedPhoto.ID = imgid;
					ListPhotos.add(undatedPhoto);
				}
			}while(cur.moveToNext());
		}

		cur.close();
		return ListPhotos;
	}
	/*
	 * controller for removing images
	 */
	public void removePreRemoved(){
		ArrayList<Photo> photos = new ArrayList<Photo>();
		photos = readRemovedImages();
		for(int i=0; i<photos.size(); i++){
			if(photos.get(i).Synced);
		}
	}
	/*
	 *  removes entire album's images from database based on img id
	 */
	public void removeLocalImages(ArrayList<Photo> images) {
		ContentResolver cr;
		cr = ctx.getContentResolver();
		for(int i=0; i<images.size(); i++){
			cr.delete(tunadatamodel.TunaImage.CONTENT_URI, tunadatamodel.TunaImage.Cols.IMGID +"=?", new String [] { images.get(i).ID } );
		}
	}
	/*
	 *  remove single image from database
	 */
	public void removeSingleImage(Photo image){
		ContentResolver cr;
		cr = ctx.getContentResolver();
		cr.delete(tunadatamodel.TunaImage.CONTENT_URI, tunadatamodel.TunaImage.Cols.IMGID +"=?", new String [] { image.ID } );
	}
	/*
	 *  remove single image from database
	 */
	public void removeSingleAlbum(Album alb){
		ContentResolver cr;
		cr = ctx.getContentResolver();
		cr.delete(tunadatamodel.TunaAlbum.CONTENT_URI, tunadatamodel.TunaAlbum.Cols.ALBID +"=?", new String [] { alb.ID } );
	}
	/*
	 * set images in database for removal
	 */
	public void setForRemovalImages(ArrayList<Photo> images){
		for(int i=0; i<images.size(); i++){
			if(images.get(i).Synced == true){
				images.get(i).UploadDate = "removed";
				saveLocalImage(images.get(i), true);
			}else{
				deleteImageFromUser(images.get(i));
			}
		}
	}

	public void deleteImageFromUser(Photo photo) {
		String Path = null;
		Path = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES)+"/Tuna/"+photo.Name;
		File removefile = new File(Path);
		if(removefile.delete()){
			removeSingleImage(photo);
		}
	}



	public int getLastImageID(){
		int LastID = 0;
		ContentResolver cr = ctx.getContentResolver();

		Cursor cur = cr.query(tunadatamodel.TunaImage.CONTENT_URI, null,null, null,null);
		if(cur.moveToLast()){
			LastID = cur.getInt(0);
		}
		cur.close();
		return LastID;
	}

	public void saveImgComplete(String imageID) {
		ContentValues val = new ContentValues();
		val.put(tunadatamodel.SQL_INS_REP, true);  
		val.put(tunadatamodel.TunaImage.Cols.IMGID, (imageID != null) ? imageID : null);
		val.put(tunadatamodel.TunaImage.Cols.IMGSYNC, Boolean.toString(true));
		cr.insert(tunadatamodel.TunaImage.CONTENT_URI, val);	
	}
	
	public Photo getSpecificPhoto(String imgID){
		ContentResolver cr = ctx.getContentResolver();
		Cursor cur = cr.query(tunadatamodel.TunaImage.CONTENT_URI, null,tunadatamodel.TunaImage.Cols.IMGID+" = ?", new String[]{imgID},
				null);
		int imgidIndex = 0, imgAlbIdIndex = 0, nameIndex = 0, usernameIndex = 0, useridIndex = 0, dateIndex = 0, tokenIndex = 0, titleIndex = 0, descIndex = 0, imgSyncIndex = 0;
		imgidIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGID);
		imgAlbIdIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGALBID);
		usernameIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUSR);
		useridIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUSRID);
		nameIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGNAME);
		dateIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUPDATE);
		tokenIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUPTOKEN);
		titleIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGTITLE);
		descIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGDESC);
		imgSyncIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGSYNC);
		int columnCount = cur.getColumnCount();

		String username = null, albid = null, userid = null, imgid = null, imgName = null, imgDate = null, imgTitle = null, imgDesc = null;
		String synced = null, token = null;
		
		Photo undatedPhoto = new Photo();
		
		if (cur.moveToFirst()) {

			do {
				for(int i = 0; i < columnCount; i++){
					if(i == nameIndex){
						imgName = cur.getString(i);
					}else if(i == dateIndex){
						imgDate = cur.getString(i);
					}else if(i == titleIndex){
						imgTitle = cur.getString(i);
					}else if(i == descIndex){
						imgDesc = cur.getString(i);
					}else if(i == usernameIndex){
						username = cur.getString(i);
					}else if(i == imgidIndex){
						imgid = cur.getString(i);
					} else if(i == useridIndex){
						userid = cur.getString(i);
					} else if(i == imgAlbIdIndex){
						albid = cur.getString(i);
					}else if(i == imgSyncIndex){
						synced = cur.getString(i);
					}
				}
				if(imgid.equals(imgID)){
					undatedPhoto = new Photo(null, imgName, username, userid, albid, imgTitle, imgDesc,Boolean.parseBoolean(synced));
					undatedPhoto.UploadDate=imgDate;
					undatedPhoto.ID = imgid;
					
					System.out.println(undatedPhoto.toString());
				}
			}while(cur.moveToNext());
		}

		cur.close();
		return undatedPhoto;
	}



}
