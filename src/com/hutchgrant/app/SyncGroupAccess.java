package com.hutchgrant.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.hutchgrant.Elements.Contact;
import com.hutchgrant.Elements.Group;
import com.hutchgrant.Elements.Sync.GroupSyncData;
import com.hutchgrant.Elements.Sync.SpecialGroup;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.Elements.LifeGroup;
import com.hutchgrant.Elements.Sync.SocialGroup;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.networks.gplus.HttpUtils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SyncGroupAccess {

	private static final String EVENT_GRPWRITE = "event_group_write";
	private static final String EVENT_GRPSYNC = "event_group_sync";
	public static SyncGroupAccess instance;
	GroupAccess groupAccess;
	ProfileAccess prof;
	ImageAccess img;
	Context ctx;
	private String TAG;
	public String EVENT_LOGIN = "event_login";
	
	public SyncGroupAccess(Context context){
		ctx = context;
		GroupAccess.initAccess(context);
		ProfileAccess.initAccess(context);
		ImageAccess.initAccess(context);
		groupAccess = GroupAccess.getInstance();
		prof = ProfileAccess.getInstance();
		img = ImageAccess.getInstance();
	}
	
	public static void initAccess(Context context){
		if(instance == null){
			instance = new SyncGroupAccess(context);
		}
	}
	
	public static SyncGroupAccess getInstance(){
		return instance;
	}
	
	public void SyncSendGroups(SocialGroup group){
		HttpURLConnection urlConnection = null;
	    OutputStream outStream = null;
	    String response = null;
	    int statusCode = 0;
		 
	    try {
	    	URL url = new URL(Endpoints.GROUP_UPLOAD);
	    	Gson gson = new Gson();
	        String json = gson.toJson(group);
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
	      //      Log.v(TAG, "Tuna Server Response: " + response);
	            System.out.println(response);
	            if(response != null){
	                GroupSyncData data = new Gson().fromJson(response, GroupSyncData.class);
	                
	                LifeGroup life = new LifeGroup(ctx);
	                life = ConvertRemoteGroup(data);
	     
	            	groupAccess.saveToLife(life);
	            	groupAccess.removeFromLife(life);
	            }	            
	            sendMessage(EVENT_GRPWRITE, true);
	            
	        } else { 
	          //  response = HttpUtils.getErrorResponse(urlConnection);
	        } 
	    } catch (MalformedURLException e) {
	        Log.e(TAG, e.getMessage(), e);
	    } catch (IOException e) {
	        System.out.println("WIFI DISCONNECTED?");
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
		
	public void SyncReceiveGroups(SyncObj syncData){
		HttpURLConnection urlConnection = null;
        OutputStream outStream = null;
        String response = null;
        int statusCode = 0;

        try {
            URL url = new URL(Endpoints.SYNC_GROUPS);
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
                
                GroupSyncData data = new Gson().fromJson(response, GroupSyncData.class);
                
                LifeGroup life = new LifeGroup(ctx);
                life = ConvertRemoteGroup(data);

            	groupAccess.saveToLife(life);
                sendMessage(EVENT_GRPWRITE, true);
                
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
	
	private LifeGroup ConvertRemoteGroup(GroupSyncData data) {
        String groupID = "", groupName = "", groupSyncD = "", groupSyncT = "", personId = "", Name = "", Email = "", Phone = "", googleID = "";
        String image = "";
        LifeGroup life = new LifeGroup(ctx);
        for(int i=0; i<data.life.groups.size(); i++){
        	Group grp = new Group(ctx);
    		groupID = data.life.groups.get(i).groupID;
    		groupName = data.life.groups.get(i).groupName;
    		groupSyncD = data.life.groups.get(i).groupSyncDate;
    		groupSyncT = data.life.groups.get(i).groupSyncTime;
        	grp.set(groupID, groupName, groupSyncD, groupSyncT);
      	for(int m = 0; m<data.life.groups.get(i).people.size(); m++){
        		personId = data.life.groups.get(i).people.get(m).ID;
        		Name = data.life.groups.get(i).people.get(m).Name;
        		Email = data.life.groups.get(i).people.get(m).Email;
        		Phone = data.life.groups.get(i).people.get(m).Phone;
        		googleID = data.life.groups.get(i).people.get(m).GoogleID;
        		image = data.life.groups.get(i).people.get(m).ProfileImg;
        		Contact person = new Contact();
        		person.setContact(personId, groupID, googleID, Name, Email, Phone, image, true);
        		grp.addContact(person);
        	}  
      		life.addGroup(grp);
        }
        
        return life;
	}

	public void removeRemoteGroup(String string) {
		HttpURLConnection urlConnection = null;
        OutputStream outStream = null;
        String response = null;
        int statusCode = 0;
		 
        try {
            URL url = new URL(Endpoints.REMOVE_GROUP);
            Gson gson = new Gson();
            String json = string;
     //       System.out.println(json);
            
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
                
                sendMessage(EVENT_GRPWRITE, true);
                
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
	
	
	

	public void sendRemoteGroups() {
		ArrayList<Group> groups = new ArrayList<Group>();
		groups = new ArrayList<Group>();
		groups = groupAccess.getGroupData();
		for(int i =0; i<groups.size(); i++){
			Group grp = new Group(ctx);
			groups.get(i).ctx = ctx;
			grp = groupAccess.getGroupContacts(groups.get(i).getID());
			groups.get(i).setPeople(grp.getAll());
		}
		for(int i=0; i< groups.size(); i++){
			if(groups.get(i).getSyncDate().equals("n/a") || 
				groups.get(i).getSyncDate().equals("updated") || 
				groups.get(i).getSyncDate().equals("removed")){
				
				SpecialGroup spec= new SpecialGroup(groups.get(i));
				SocialGroup sc = new SocialGroup();
				sc = spec.getSocialGroup();
				SyncSendGroups(sc);
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
}
