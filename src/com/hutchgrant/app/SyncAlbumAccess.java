package com.hutchgrant.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.LifeAlbum;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.Elements.Sync.AlbumData;
import com.hutchgrant.Elements.Sync.AlbumSyncData;
import com.hutchgrant.Elements.Sync.SocialImage;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.Elements.User;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.imagesend.HttpUploader;
import com.hutchgrant.imagesend.HttpUploader.DataListener;
import com.hutchgrant.imagesend.getUploadUrl;
import com.hutchgrant.networks.gplus.HttpUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SyncAlbumAccess {

	public static SyncAlbumAccess instance;
	public AlbumAccess albumAccess;
	public ImageAccess imgAccess;
	Context ctx;
	private String TAG;

	public SyncAlbumAccess(Context context){
		ctx = context;
		AlbumAccess.initAccess(context);
		ImageAccess.initAccess(context);
		albumAccess = AlbumAccess.getInstance();
		imgAccess = ImageAccess.getInstance();
	}

	public static void initAccess(Context context){
		if(instance == null){
			instance = new SyncAlbumAccess(context);
		}
	}

	public static SyncAlbumAccess getInstance(){
		return instance;
	}

	/*
	 * Sync all photos regardless of when or where it was created
	 */
	public void syncAllPhotos(ArrayList<SocialImage> images){

		String imgPath = null;

		for(int i =0; i<images.size(); i++){
			imgPath = Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_PICTURES)+"/Tuna/"+images.get(i).Name;
			sendImgToServer(imgPath, images.get(i).convertToPhoto(), getUserToken());
		}
	}

	/*
	 * Sync all photos that newly created, edited, removed, updated
	 */
	public void syncAllDirtyPhotos() {
		String imgPath = null;
		HttpUploader uploader;

		ArrayList<Photo> unsentPhotos = new ArrayList<Photo>();
		unsentPhotos = imgAccess.getDirtyPhotos();
		if(unsentPhotos.size() != 0){
			for(int i =0; i<unsentPhotos.size(); i++){
				imgPath = Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_PICTURES)+"/Tuna/"+unsentPhotos.get(i).Name;
				//System.out.println("img path is "+imgPath.toString());
				//System.out.println("the file url is: "+imgPath);
				
				uploader = new HttpUploader(unsentPhotos.get(i), getUserToken(), ctx);
				uploader.setListener(new DataListener(){
						@Override
						public void saveImage(Photo unsaved) {
						   imgAccess.saveLocalImage(unsaved, true);

						}
					});
				uploader.execute(imgPath); 
				
			}
		}

	}

	/*
	 * Get User's Tuna Profile Token
	 */
	public String getUserToken(){
		User user = new User();
		ProfileAccess.initAccess(ctx);
		user = ProfileAccess.getInstance().getProfile();
		return user.userToken;
	}
	
	/*
	 * Main Album Sync Control 
	 * grabs all dirty images and albums, 
	 * convert each album and image to syncable object
	 * sends each album to server 
	 */
	public void ControlRemoteSync(){
		LifeAlbum life = new LifeAlbum(ctx);
		ArrayList<AlbumData> albumData = new ArrayList<AlbumData>();

		life.Albums = albumAccess.readAllDirtyAlbumsPhotos();
		albumData = life.ConvertAlbumsToRemoteObject();

		for(int x=0; x< albumData.size(); x++){
			sendRemoteAlbums(albumData.get(x));
		}
		//send broadcast message that all albums and image DATA have been sent and updated.
	//	sendMessage(Endpoints.EVENT_ALBSYNC_SEND, true);
	}
 
	/*
	 * SyncPhoto - retreives a unique uploadURL with token and photo info
	 * We could connect to a server and generate a unique URL, but we've already done so with tokens
	 */
	public void syncPhoto(User user, Photo photo, String imgPath){
		getUploadUrl MakeURL;
		String newImgUrl = null;
		/// get new image upload token from tuna token
		MakeURL = new getUploadUrl(photo);
		try {
			newImgUrl = MakeURL.execute(user.userToken).get();
		} catch (InterruptedException e) {
			System.out.println("error getting upload url");
			e.printStackTrace();
		} catch (ExecutionException e) {
			System.out.println("error getting upload url");
			e.printStackTrace();
		}
		/// send img using upload token
		//	sendImgToServer(imgPath, newImgUrl, user.userToken);
	}

	/*
	 * Send a single image's raw file from a given path, with a user's token, to the server.
	 */
	public void sendImgToServer(String imgSendPath, Photo unsentPhoto, String usrToken) {
		HttpUploader uploader;
		uploader = new HttpUploader(unsentPhoto, usrToken, ctx);
		try {
			System.out.println("the file url is: "+imgSendPath);
			uploader.execute(imgSendPath).get();        
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		};
		System.out.println("image from = "+imgSendPath+" uploaded");
	}

	/*
	 * SEND a list of converted sync album data objects TO the server, 
	 * convert that remote sync Album object to one we're used to
	 * save its updated info to database, including all it's images updated info
	 */
	public void sendRemoteAlbums(AlbumData albumData) {
		HttpURLConnection urlConnection = null;
		OutputStream outStream = null;
		String response = null;
		int statusCode = 0;

		try {
			URL url = new URL(Endpoints.SYNC_SEND_ALBUM);
			Gson gson = new Gson();
			String json = gson.toJson(albumData);
			//System.out.println(json);

			byte[] postBody = String.format(json).getBytes();

			urlConnection = Endpoints.setHeader(url, ctx);
			outStream = urlConnection.getOutputStream();
			outStream.write(postBody);

			statusCode = urlConnection.getResponseCode();
			Log.v("RESPONSECODE", "code = "+statusCode);
			if (statusCode == 200) {

				InputStream responseStream = urlConnection.getInputStream();
				byte[] responseBytes = HttpUtils.getContent(responseStream).toByteArray();
				response = new String(responseBytes, "UTF-8");
				System.out.println(response);
				AlbumData albData = new Gson().fromJson(response, AlbumData.class);
				albData.ConvertRemoteAlbumUpdate(ctx, albumAccess, imgAccess);
			} else { 
				response = HttpUtils.getErrorResponse(urlConnection);
				System.out.println(response);

			} 
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}

			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		}
	}
	/*
	 * Retrieve a list of albums from the server, 
	 * convert that remote sync Album object to one we're used to
	 * validate album and images - dates and time
	 * save it to database, including all it's images.
	 */
	public ArrayList<Album> receiveAllAlbums(SyncObj syncData){
		HttpURLConnection urlConnection = null;
		OutputStream outStream = null;
		String response = "";
		int statusCode = 0;
		ArrayList<Album> albumList = new ArrayList<Album>();
		try {
			URL url = new URL(Endpoints.SYNC_RECEIVE_ALBUM);
			Gson gson = new Gson();
			String json = gson.toJson(syncData);
			System.out.println(json);

			byte[] postBody = String.format(json).getBytes();

			urlConnection = Endpoints.setHeader(url, ctx);
			outStream = urlConnection.getOutputStream();
			outStream.write(postBody);

			statusCode = urlConnection.getResponseCode();
			Log.v("RESPONSECODE", "code = "+statusCode);
			if (statusCode == 200) {
				InputStream responseStream = urlConnection.getInputStream();
				byte[] responseBytes = HttpUtils.getContent(responseStream).toByteArray();
				response = new String(responseBytes, "UTF-8");
				 System.out.println(response);
				AlbumSyncData albSyncData = new Gson().fromJson(response, AlbumSyncData.class);

				albumList = albSyncData.ConvertRemoteAlbumLife(ctx);
				albumList = albumAccess.saveToLife(albumList);

				return albumList;

			} else { 
				response = HttpUtils.getErrorResponse(urlConnection);
				System.out.println(response);

			} 
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}

			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		}
		return albumList;

	} 

/*
 * Main Image File Sync Receive Control
 * grabs each image in the album from the server
 */
	public void receiveAllImages(ArrayList<Album> albums) {
		for(int i=0; i< albums.size(); i++){
			for(int x=0; x<albums.get(i).photos.size(); x++){
				receiveASingleJPG(albums.get(i).photos.get(x).ID, albums.get(i).photos.get(x).Name);
			}
		}
	}

/*
 * Retrieves a single Image file from server and stores it with the correct image name and location
 */
	public void receiveASingleJPG(String imageID, String imgName){
		HttpURLConnection urlConnection = null;
		OutputStream outStream = null;
		String response = "";
		int statusCode = 0;

		try {
			URL url = new URL(Endpoints.SYNC_RECEIVE_IMG);
			String json = "{ \"imageID\" : \""+imageID+"\" }";
		//	System.out.println(json);

			byte[] postBody = String.format(json).getBytes();

			urlConnection = Endpoints.setHeader(url, ctx);
			outStream = urlConnection.getOutputStream();
			outStream.write(postBody);

			statusCode = urlConnection.getResponseCode();
			Log.v("RESPONSECODE", "code = "+statusCode);
			if (statusCode == 200) {
				InputStream is = urlConnection.getInputStream();
				File photo=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/Tuna/", imgName);
				String imgURL = photo.getAbsolutePath();
				OutputStream os = new FileOutputStream(imgURL);

				byte[] b = new byte[2048];
				int length;

				while ((length = is.read(b)) != -1) {
					os.write(b, 0, length);
				}
				is.close();
				os.close();
				
				imgAccess.saveImgComplete(imageID);
			} else { 
				response = HttpUtils.getErrorResponse(urlConnection);
				System.out.println(response);
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}

			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		} 
	}

	/*
	 * 
	 * Broadcast messages
	 */
	 private void sendMessage(String event, boolean msg) {
			  Intent intent = new Intent();
			  intent =  Endpoints.checkSuccess(event, msg);
			LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	 }

	public Photo getSpecificRemoteImageData(String imageID) {
		HttpURLConnection urlConnection = null;
		OutputStream outStream = null;
		String response = "";
		int statusCode = 0;
		Photo photo = new Photo();
		try {
			URL url = new URL(Endpoints.SYNC_RECEIVE_IMGDATA);
			String json = "{ \"imageID\" : \""+imageID+"\" }";
		//	System.out.println(json);

			byte[] postBody = String.format(json).getBytes();

			urlConnection = Endpoints.setHeader(url, ctx);
			outStream = urlConnection.getOutputStream();
			outStream.write(postBody);

			statusCode = urlConnection.getResponseCode();
			Log.v("RESPONSECODE", "code = "+statusCode);
			if (statusCode == 200) {;
				InputStream responseStream = urlConnection.getInputStream();
				byte[] responseBytes = HttpUtils.getContent(responseStream).toByteArray();
				response = new String(responseBytes, "UTF-8");		
				photo = new Gson().fromJson(response, Photo.class);
				imgAccess.saveLocalImage(photo, false);
				return photo;
			} else { 
				response = HttpUtils.getErrorResponse(urlConnection);
				System.out.println(response);
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}

			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		} 
		return photo;
	}

	public 		ArrayList<Album> getSpecificRemoteAlbumData(String syncAlbumID) {
		HttpURLConnection urlConnection = null;
		OutputStream outStream = null;
		String response = "";
		int statusCode = 0;
		ArrayList<Album> albumList = new ArrayList<Album>();

		try {
			URL url = new URL(Endpoints.SYNC_RECEIVE_ALBDATA);
			String json = "{ \"albumID\" : \""+syncAlbumID+"\" }";
			System.out.println(json);

			byte[] postBody = String.format(json).getBytes();

			urlConnection = Endpoints.setHeader(url, ctx);
			outStream = urlConnection.getOutputStream();
			outStream.write(postBody);

			statusCode = urlConnection.getResponseCode();
			Log.v("RESPONSECODE", "code = "+statusCode);
			if (statusCode == 200) {;
				InputStream responseStream = urlConnection.getInputStream();
				byte[] responseBytes = HttpUtils.getContent(responseStream).toByteArray();
				response = new String(responseBytes, "UTF-8");	
				System.out.println(response);
				AlbumSyncData albSyncData = new Gson().fromJson(response, AlbumSyncData.class);

				albumList = albSyncData.ConvertRemoteAlbumLife(ctx);
				albumList = albumAccess.saveToLife(albumList);
				
			//	alb = new Gson().fromJson(response, Album.class);
			//	albumAccess.saveLocalAlbum(alb, false);
			//	albumAccess.saveImagesByAlb(alb, false);
				return albumList;
			} else { 
				response = HttpUtils.getErrorResponse(urlConnection);
				System.out.println(response);
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}

			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		} 
		return albumList;
		
	}
}
