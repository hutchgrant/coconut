package com.hutchgrant.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.Gson;
import com.hutchgrant.Elements.LifeInvite;
import com.hutchgrant.Elements.User;
import com.hutchgrant.Elements.Sync.SyncInviteObj;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.networks.gplus.HttpUtils;
import com.hutchgrant.provider.tunadatamodel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class SyncInviteAccess {
	
	
	public static SyncInviteAccess instance;
	public InviteAccess invAccess;
	Context ctx;
	private String TAG;
	ContentResolver cr;
	Cursor cur;
	
	public SyncInviteAccess(Context context){
		ctx = context;
		InviteAccess.initAccess(context);
		invAccess = InviteAccess.getInstance();
	}

	public static void initAccess(Context context){
		if(instance == null){
			instance = new SyncInviteAccess(context);
		}
	}

	public static SyncInviteAccess getInstance(){
		return instance;
	}

	class SmallSync {

		public String syncInviteToken = "";
	}
	
	/*
	 * SEND a sync data object TO the server, 
	 * RECEIVE a sync data object back from the server
	 * save its updated info to database
	 */
	public SyncInviteObj getRemoteInviteTally(SyncInviteObj sync) {
		
		SmallSync small = new SmallSync();
		small.syncInviteToken = sync.syncInviteToken;
		
		HttpURLConnection urlConnection = null;
		OutputStream outStream = null;
		String response = null;
		int statusCode = 0;

		try {
			URL url = new URL(Endpoints.SYNC_INVITE);
			Gson gson = new Gson();
			String json = gson.toJson(small);
			System.out.println("CLIENT SENT TO SERVER: "+json);

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
				if(!response.equals("OK")){
				sync = new Gson().fromJson(response, SyncInviteObj.class);
				
				/// convert sync data to sync obj
				}
				 return sync;
				// saveLocalTally();
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
		return sync;

	}

	public SyncInviteObj getLocalInviteTally() {
		
		//// get Local sync Object from sync table
		SyncInviteObj sync = new SyncInviteObj();
		
		ContentResolver cr = ctx.getContentResolver();
		Cursor cur = cr.query(tunadatamodel.TunaSync.CONTENT_URI, null,null, null, null);
		
		
		int invDateIndex = 0, invTimeIndex = 0, invTokenIndex = 0, invAmtSntIndex = 0, invAmtRecIndex=0;

		invDateIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCINVDATE);
		invTimeIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCINVTIME);
		invTokenIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCINVTOKEN);
		invAmtSntIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCINV_SNTAMT);
		invAmtRecIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCINV_RECAMT); 
		
		int columnCount = cur.getColumnCount();
		
		if (cur.moveToFirst()) {
		
			do {
				for(int i = 0; i < columnCount; i++){
					if(i == invTokenIndex){
						sync.syncInviteToken = cur.getString(i);
					}else if(i == invTimeIndex){
						sync.syncInviteTime = cur.getString(i);
					}else if(i == invDateIndex){
						sync.syncInviteDate = cur.getString(i);
					}else if(i == invAmtSntIndex){
						sync.syncInvSntAmount = cur.getInt(i);
					}else if(i == invAmtRecIndex){
						sync.syncInvSntAmount = cur.getInt(i);
					}
				}
			}while(cur.moveToNext());
		}
		
		cur.close();
		return sync;
	}
	
	public void saveLocalInviteTally(SyncInviteObj sync, boolean update){
		User profile = new User();
		 ProfileAccess prof;
		 ProfileAccess.initAccess(ctx);
			prof = ProfileAccess.getInstance();
		 profile = prof.getProfile();
		 
			ContentResolver cr = ctx.getContentResolver();
			ContentValues val = new ContentValues();
			if(update){
				val.put(tunadatamodel.SQL_INS_REP, true);  
			}
			val.put(tunadatamodel.TunaSync.Cols.ID, 0);
			val.put(tunadatamodel.TunaSync.Cols.SYNCUSERID, profile.userGID);
			val.put(tunadatamodel.TunaSync.Cols.SYNCINVTOKEN, (sync.syncInviteToken != null) ? sync.syncInviteToken : null);
			val.put(tunadatamodel.TunaSync.Cols.SYNCINVDATE, (sync.syncInviteDate != null) ? sync.syncInviteDate : null);
			val.put(tunadatamodel.TunaSync.Cols.SYNCINVTIME, (sync.syncInviteTime != null) ? sync.syncInviteTime : null);
			val.put(tunadatamodel.TunaSync.Cols.SYNCINV_RECAMT, (sync.syncInvRecAmount != 0) ? sync.syncInvRecAmount : 0);
			val.put(tunadatamodel.TunaSync.Cols.SYNCINV_SNTAMT, (sync.syncInvSntAmount != 0) ? sync.syncInvSntAmount : 0);
			cr.insert(tunadatamodel.TunaSync.CONTENT_URI, val);
	}

	
	public void getLifeInvites(SyncInviteObj invite){
		LifeInvite life = new LifeInvite();
		
		HttpURLConnection urlConnection = null;
		OutputStream outStream = null;
		String response = null;
		int statusCode = 0;

		try {
			URL url = new URL(Endpoints.INVITE_GRAB);
			Gson gson = new Gson();
			String json = gson.toJson(invite);
			System.out.println("CLIENT SENT TO SERVER: "+json);

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
				if(!response.equals("OK")){
				life = new Gson().fromJson(response, LifeInvite.class);
				invAccess.saveAllInvite(life.invites, true);
				}
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
	
	public void sendDirtyInvites(LifeInvite invite) {
		LifeInvite life = new LifeInvite();
		
		HttpURLConnection urlConnection = null;
		OutputStream outStream = null;
		String response = null;
		int statusCode = 0;

		try {
			URL url = new URL(Endpoints.INVITE_ADD);
			Gson gson = new Gson();
			String json = gson.toJson(invite);
			System.out.println("CLIENT SENT TO SERVER: "+json);

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
				if(!response.equals("OK")){
					life = new Gson().fromJson(response, LifeInvite.class);
					//life.getInvite(0).display(); 
					invAccess.saveAllInvite(life.invites, true);
				}
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
	
	public void syncAll(){
		boolean pendingData = true;  // get Pending data
		SyncInviteObj remoteTally = new SyncInviteObj();
		SyncInviteObj localTally = new SyncInviteObj();
		
		localTally = getLocalInviteTally();
		remoteTally = getRemoteInviteTally(localTally);

		if(!remoteTally.syncInviteToken.equals(localTally.syncInviteToken)){
			if(!remoteTally.syncInviteTime.equals(localTally.syncInviteTime)){
				SyncInviteObj invite = new SyncInviteObj();
				invite.fill(localTally.syncInviteToken, localTally.syncInviteDate, localTally.syncInviteTime, localTally.syncInvRecAmount, localTally.syncInvSntAmount);
				getLifeInvites(invite);
			}
		}
		
		if(pendingData){
			LifeInvite invites = new LifeInvite();
			invites = invAccess.getInvites(false, "updated", "");
			sendDirtyInvites(invites);
		}
	}
}
