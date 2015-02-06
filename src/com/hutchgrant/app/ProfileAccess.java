package com.hutchgrant.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hutchgrant.Elements.*;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.networks.gplus.GFriends;
import com.hutchgrant.networks.gplus.Gloading;
import com.hutchgrant.provider.tunadatamodel;

public class ProfileAccess {
	public static ProfileAccess instance;
	NetworkAccess networkAccess;
	ImageAccess imageAccess;
	public User profile;
	static Context ctx;
	Cursor cur;
	ContentResolver contentResolver;
	
	private ProfileAccess(Context context){
		instance = null;
		profile = new User();
		ctx = context;
		NetworkAccess.initAccess(context);
		ImageAccess.initAccess(context);
		imageAccess = ImageAccess.getInstance();
		networkAccess = NetworkAccess.getInstance();
	}
	
	public static void initAccess(Context context){
		if(instance == null)
		{
			instance = new ProfileAccess(context);
		}
	}
	
	public static ProfileAccess getInstance(){
		return instance;
	}
	public void setUser(String GID, String token, String username, String userEmail, String userPhone, String profileurl, String imgurl, String location,
			int maxAlb, int maxImg, int maxGrp, int maxCont){
		profile.fill(GID, token, username, userEmail, userPhone, profileurl, imgurl, location, maxAlb, maxImg, maxGrp, maxCont);
	}
	
	public void setUser(User user){
		Log.v("profile_access", "user ="+user.userGName);
		profile = user;
	}
	 /*
	  * start gplus login activity
	  */
	public boolean startlogin(){
		Intent b = new Intent();
		b.setClass(ctx, Gloading.class);
		b.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(b);
		return true;
	}
	
	 /*
	  * start gplus login activity
	  */
	public boolean getFriends(){
		Intent b = new Intent();
		b.setClass(ctx, GFriends.class);
		b.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(b);
		return true;
	}
	
	/*nt
	 * Write new profile  to content provider
	 */
	public void newProfile(User profile){
		byte[] bArray = imageAccess.getRemoteImage(profile.userImgUrl);
		
    	ContentValues val = new ContentValues();
    	val.put(tunadatamodel.TunaProfile.Cols.USRID, (profile.userGID != null) ? profile.userGID : null);
    	val.put(tunadatamodel.TunaProfile.Cols.USRTOKEN, (profile.userToken != null) ? profile.userToken : null);
    	val.put(tunadatamodel.TunaProfile.Cols.USRNAME, (profile.userGName != null) ? profile.userGName : null);
    	val.put(tunadatamodel.TunaProfile.Cols.USRPHONE, (profile.userPhone != null) ? profile.userPhone : null);
    	val.put(tunadatamodel.TunaProfile.Cols.USREMAIL, (profile.userEmail != null) ? profile.userEmail : null);
    	val.put(tunadatamodel.TunaProfile.Cols.USRLOCATE, (profile.userLocation != null) ? profile.userLocation : null);
    	val.put(tunadatamodel.TunaProfile.Cols.USRPROF, (profile.userProfile != null) ? profile.userProfile : null);
    	val.put(tunadatamodel.TunaProfile.Cols.USRIMGURL, (profile.userImgUrl != null) ? profile.userImgUrl : null);
    	val.put(tunadatamodel.TunaProfile.Cols.USRIMG, (bArray != null) ? bArray : null);
    	val.put(tunadatamodel.TunaProfile.Cols.MAXALBUM, profile.MaxAlbums);
    	val.put(tunadatamodel.TunaProfile.Cols.MAXIMG, profile.MaxImages);
    	val.put(tunadatamodel.TunaProfile.Cols.MAXGRP, profile.MaxGroups);
    	val.put(tunadatamodel.TunaProfile.Cols.MAXCONT, profile.MaxContacts);

    	contentResolver.insert(tunadatamodel.TunaProfile.CONTENT_URI, val);
	}
	
	/*
	 *  Get Profile from content provider
	 */
	public User getProfile(){
			User profile = new User();
			contentResolver = ctx.getContentResolver();
	        cur = contentResolver.query(tunadatamodel.TunaProfile.CONTENT_URI, null, null, null, null);
	            if  (cur.moveToFirst()) {
	            		profile.userGID  = cur.getString(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.USRID));
	                    profile.userToken = cur.getString(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.USRTOKEN));
	                    profile.userGName = cur.getString(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.USRNAME));
	                    profile.userProfile = cur.getString(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.USRPROF));
	                    profile.userImgUrl = cur.getString(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.USRIMGURL));
	                    profile.userPhone = cur.getString(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.USRLOCATE));
	                    profile.userEmail = cur.getString(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.USRLOCATE));
	                    profile.MaxAlbums = cur.getInt(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.MAXALBUM));
	                    profile.MaxImages = cur.getInt(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.MAXIMG));
	                    profile.MaxGroups = cur.getInt(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.MAXGRP));
	                    profile.MaxContacts = cur.getInt(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.MAXCONT));
	            }
	        cur.close();
			return profile;
		}

	/*
	 * Write new user  to content provider
	 */	
	 public void login(){
			 boolean logged = false;
	
			 User profile = new User();
			 profile = getProfile();
			 
			 if(profile.userToken == ""){
				 	networkAccess = NetworkAccess.getInstance();
				 Log.v("profile_app_token", "token = None here"+profile.userToken);
				 /// check network connection
				 if(networkAccess.networkAvailable()){
					 startlogin();
				 }
			 }else{
				 ProfileAccess.getInstance().setUser(profile);   //cache profile user from database
				 logged = true;
			 }
			 if(logged){
				 sendMessage(Endpoints.EVENT_LOGIN, true);
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
}
