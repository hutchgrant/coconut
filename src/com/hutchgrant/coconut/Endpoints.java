/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hutchgrant.coconut;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import com.hutchgrant.Elements.User;
import com.hutchgrant.app.ProfileAccess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

/**
 * The PhotoHunt API endpoints.
 */
public class Endpoints {
	
	public static final int DIALOG_GROUPNAME = 77;
	public static final int DIALOG_ADD_ALBUM = 88;
	public static final int DIALOG_MODIFY_ALBUM = 99;
	public static final int DIALOG_CREATE_ALBUM = 101;
	public static final int DIALOG_DESCRIBE_IMGS = 111;

	public static final String EVENT_NETWORK = "event_network";
	public static final String EVENT_GRPWRITE = "event_group_write";
	public static final String EVENT_GRPSYNC = "event_group_sync";
	public static final String EVENT_LOGIN = "event_login";
	public static final String EVENT_ALBWRITE = "event_group_write";
	public static final String EVENT_ALBSYNC = "event_group_sync";
	public static final String EVENT_ALBSYNC_SEND = "event_album_sync_send";
	public static final String EVENT_ALBUM_WRITE = "event_album_write";
	public static final String EVENT_IMAGE_WRITE = "event_image_write";

    private static final String TAG = Endpoints.class.getSimpleName();
    
    /** The UserAgent string supplied with all Tuna HTTP requests. */
    public static final String USER_AGENT = "Tuna Agent";

    /** The protocol and hostname used to access the PhotoHunt service. */
    public static final String API_HOST;

    /** The URL root used to access the PhotoHunt API. */
    public static final String API_ROOT;
    
    /** The link that is embedded when sharing a photo on Google+. */
    public static final String API_PHOTO_LINK;
    
    /** The API URL used to retrieve metadata about a single photo. */
    public static final String PHOTO;
    
	
    /** The API URL used to retrieve an upload URL for a photo. */
    public static final String PHOTO_UPLOAD;
 
    /** The API URL used to connect to the PhotoHunt service. */
    public static final String API_CONNECT;

    /** The API URL used to disconnect from the PhotoHunt service. */
    public static final String API_DISCONNECT;

	public static final String GROUP_UPLOAD;
	public static final String SYNC_GROUPS;
	public static final String REMOVE_GROUP;
	public static final String SYNC_RECEIVE_ALBUM;
	public static final String SYNC_RECEIVE_IMG;
	public static final String SYNC_RECEIVE_IMGDATA;
	public static final String SYNC_RECEIVE_ALBDATA;
	public static final String SYNC_SEND_ALBUM;
	public static final String GPLUS_FRIENDS;
	public static final String SYNC_ALL;
	public static final String SYNC_INVITE;
	public static final String INVITE_ADD;
	public static final String INVITE_GRAB;
	public static final String SYNC_MESSAGE;
	public static final String MSG_ADD;
	public static final String MSG_GRAB;
    
    static {
        Properties config = new Properties();
        String apiHost = null;
        
        try {
            config.load(Endpoints.class.getClassLoader().getResourceAsStream("config.properties"));
 
            apiHost = config.getProperty("api_host");
        } catch (Exception e) {
            Log.e(TAG, "Failed to load configuration properties file", e);
        } finally {
            API_HOST = apiHost;
            
            if (API_HOST != null) {
                API_ROOT = API_HOST;
                
                API_CONNECT = API_ROOT + "/Social/connect_gplus.php";
                API_DISCONNECT = API_ROOT + "/Social/disconnect";
                API_PHOTO_LINK = API_ROOT + "/image?id=%s";
                PHOTO_UPLOAD = API_ROOT + "/Images/image_upload.php";
                PHOTO = API_ROOT + "/image?id=%s";
                GROUP_UPLOAD = API_ROOT + "/Social/groupadd.php";
                SYNC_GROUPS = API_ROOT + "/Social/groupgrab.php";
                REMOVE_GROUP = API_ROOT + "/Social/grouprem.php";
                SYNC_RECEIVE_ALBUM = API_ROOT + "/Images/getAllImages.php";
                SYNC_RECEIVE_IMG = API_ROOT + "/Images/outputImgById.php";
                SYNC_RECEIVE_IMGDATA = API_ROOT + "/Images/getSpecImg.php";
                SYNC_RECEIVE_ALBDATA = API_ROOT + "/Images/getSpecAlb.php";
                SYNC_SEND_ALBUM = API_ROOT + "/Images/submitAlbums.php";
                SYNC_ALL = API_ROOT + "/Social/sync_social.php";
                GPLUS_FRIENDS = API_ROOT + "/Social/gplus_friends.php";
                SYNC_INVITE = API_ROOT + "/Social/syncInvite.php";
                INVITE_ADD = API_ROOT + "/Social/inviteadd.php";
                INVITE_GRAB = API_ROOT + "/Social/invitegrab.php";
                SYNC_MESSAGE = API_ROOT + "/Social/Message/syncMessage.php";
                MSG_ADD = API_ROOT + "/Social/Message/msgAdd.php";
                MSG_GRAB = API_ROOT + "/Social/Message/msgGrab.php";
            } else {
                API_ROOT = null;
                
                API_CONNECT = null;
                API_DISCONNECT = null;
                API_PHOTO_LINK = null;
                PHOTO_UPLOAD = null;
                PHOTO = null;
                GROUP_UPLOAD = null;
                SYNC_GROUPS = null;
                REMOVE_GROUP = null;
                SYNC_RECEIVE_ALBUM =  null;
                SYNC_RECEIVE_IMG = null;
                SYNC_RECEIVE_IMGDATA = null;
                SYNC_RECEIVE_ALBDATA = null;
                SYNC_SEND_ALBUM = null;
                SYNC_ALL = null;
                GPLUS_FRIENDS = null;
                SYNC_INVITE = null;
                INVITE_ADD = null;
                INVITE_GRAB = null;
                SYNC_MESSAGE = null;
                MSG_ADD = null;
                MSG_GRAB = null;
            }
        }
    }
	/*
	 *  Headers for our http connections between mobile client and server through json
	 */
	public static HttpURLConnection setHeader(URL url, Context ctx) throws IOException{
		String UStoken = ""; 
		User profile = new User();
		 ProfileAccess prof;
		 ProfileAccess.initAccess(ctx);
			prof = ProfileAccess.getInstance();
		 profile = prof.getProfile();
		 UStoken = profile.userToken;
		
		HttpURLConnection urlConnection = null;
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setAllowUserInteraction(false);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("User-Agent", Endpoints.USER_AGENT);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("UStoken", UStoken);
        System.out.println("header token set to "+UStoken);
		return urlConnection;
	}
	
	public static Intent checkSuccess(String event, boolean msg){
		Intent intent = new Intent();
			intent.setAction(event);
		// determine message
		if(msg){
			intent.putExtra("message", "success");
		}else{
			intent.putExtra("message", "failure");
		}
		return intent;
	}
	
	public static Intent checkSuccess(String event, String msg){
		Intent intent = new Intent();
			intent.setAction(event);
			intent.putExtra("message", msg);
			return intent;
	}
	
    /** Create a File for saving the image */
    @SuppressLint("SimpleDateFormat")
	public	static File getOutputMediaFile(String imgName){
 
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "Tuna");
 
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Tuna", "failed to create directory");
                return null;
            }
        }

        File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +imgName);	        
        return mediaFile;
    }
}
