package com.hutchgrant.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.Gson;
import com.hutchgrant.Elements.LifeInvite;
import com.hutchgrant.Elements.LifeMessage;
import com.hutchgrant.Elements.Sync.SyncInviteObj;
import com.hutchgrant.Elements.Sync.SyncMsgObj;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.networks.gplus.HttpUtils;
import com.hutchgrant.provider.tunadatamodel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class SyncMessageAccess {
	
	
	public static SyncMessageAccess instance;
	public MessageAccess msgAccess;
	Context ctx;
	private String TAG;
	ContentResolver cr;
	Cursor cur;
	
	public SyncMessageAccess(Context context){
		ctx = context;
		MessageAccess.initAccess(context);
		msgAccess = MessageAccess.getInstance();
	}

	public static void initAccess(Context context){
		if(instance == null){
			instance = new SyncMessageAccess(context);
		}
	}

	public static SyncMessageAccess getInstance(){
		return instance;
	}

	class SmallSync {

		public String syncMessageToken = "";
	}
	
	/*
	 * SEND a sync data object TO the server, 
	 * RECEIVE a sync data object back from the server
	 * save its updated info to database
	 */
	public SyncMsgObj getRemoteMsgTally(SyncMsgObj sync) {
		
		SmallSync small = new SmallSync();
		small.syncMessageToken = sync.syncMsgToken;
		
		HttpURLConnection urlConnection = null;
		OutputStream outStream = null;
		String response = null;
		int statusCode = 0;

		try {
			URL url = new URL(Endpoints.SYNC_MESSAGE);
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
				sync = new Gson().fromJson(response, SyncMsgObj.class);
				System.out.println("SYNC MSG TOKEN = "+sync.syncMsgToken);
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

	public SyncMsgObj getLocalMsgTally() {
		
		//// get Local sync Object from sync table
		SyncMsgObj sync = new SyncMsgObj();
		
		ContentResolver cr = ctx.getContentResolver();
		Cursor cur = cr.query(tunadatamodel.TunaSync.CONTENT_URI, null,null, null, null);
		
		
		int msgDateIndex = 0, msgTimeIndex = 0, msgTokenIndex = 0, msgAmtSntIndex = 0, msgAmtRecIndex=0, msgCacheIndex = 0;

		msgDateIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCMSGDATE);
		msgTimeIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCMSGTIME);
		msgTokenIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCMSGTOKEN);
		msgAmtSntIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCMSG_SNTAMT);
		msgAmtRecIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCMSG_RECAMT); 
		msgCacheIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCMSG_CACHE);
		
		int columnCount = cur.getColumnCount();
		
		if (cur.moveToFirst()) {
		
			do {
				for(int i = 0; i < columnCount; i++){
					if(i == msgTokenIndex){
						sync.syncMsgToken = cur.getString(i);
					}else if(i == msgTimeIndex){
						sync.syncMsgTime = cur.getString(i);
					}else if(i == msgDateIndex){
						sync.syncMsgDate = cur.getString(i);
					}else if(i == msgAmtSntIndex){
						sync.syncMsgSntAmount = cur.getInt(i);
					}else if(i == msgAmtRecIndex){
						sync.syncMsgRecAmount = cur.getInt(i);
					}else if(i == msgCacheIndex){
						sync.syncMsgCached = cur.getInt(i);
					}
				}
			}while(cur.moveToNext());
		}
		
		cur.close();
		return sync;
	}
	
	public void saveLocalMsgTally(SyncMsgObj remoteSync, boolean update){
		ContentResolver cr = ctx.getContentResolver();
		ContentValues val = new ContentValues();
		if(update){
			val.put(tunadatamodel.SQL_INS_REP, true);  
		}
		val.put(tunadatamodel.TunaSync.Cols.SYNCMSGTOKEN, (remoteSync.syncMsgToken != null) ? remoteSync.syncMsgToken : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCMSGDATE, (remoteSync.syncMsgDate != null) ? remoteSync.syncMsgDate : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCMSGTIME, (remoteSync.syncMsgTime != null) ? remoteSync.syncMsgTime : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCMSG_RECAMT, (remoteSync.syncMsgSntAmount != 0) ? remoteSync.syncMsgSntAmount : 0);
		val.put(tunadatamodel.TunaSync.Cols.SYNCMSG_SNTAMT, (remoteSync.syncMsgRecAmount != 0) ? remoteSync.syncMsgRecAmount : 0);
		cr.insert(tunadatamodel.TunaSync.CONTENT_URI, val);
	}
	
	public void saveLocalMsgCache(int amount, boolean update){
		ContentResolver cr = ctx.getContentResolver();
		ContentValues val = new ContentValues();
		if(update){
			val.put(tunadatamodel.SQL_INS_REP, true);  
		}
		val.put(tunadatamodel.TunaSync.Cols.SYNCMSG_CACHE, amount);
		cr.insert(tunadatamodel.TunaSync.CONTENT_URI, val);
	}

	
	public void getMessages(SyncMsgObj syncMsg, boolean more){
		LifeMessage life = new LifeMessage();
		
		HttpURLConnection urlConnection = null;
		OutputStream outStream = null;
		String response = null;
		int statusCode = 0;
		
		try {
			URL url = new URL(Endpoints.MSG_GRAB);
			Gson gson = new Gson();
			String json = gson.toJson(syncMsg);
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
				life = new Gson().fromJson(response, LifeMessage.class);
				msgAccess.saveLifeMessage(life);
				saveLocalMsgCache((life.messages.size()+syncMsg.syncMsgCached), true);
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
	
	public void sendDirtyMessages(LifeMessage msg) {
		LifeMessage life = new LifeMessage();
		
		HttpURLConnection urlConnection = null;
		OutputStream outStream = null;
		String response = null;
		int statusCode = 0;

		try {
			URL url = new URL(Endpoints.MSG_ADD);
			Gson gson = new Gson();
			String json = gson.toJson(msg);
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
					life = new Gson().fromJson(response, LifeMessage.class);
					msgAccess.saveLifeMessage(life);
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
	/*
	public void syncAll(){
		boolean pendingData = true;  // get Pending data
		SyncMsgObj remoteTally = new SyncMsgObj();
		SyncMsgObj localTally = new SyncMsgObj();
		
		remoteTally = getRemoteMsgTally();
		localTally = getLocalMsgTally();
		
		if(!remoteTally.syncMsgToken.equals(localTally.syncMsgToken)){
			if(!remoteTally.syncMsgTime.equals(localTally.syncMsgTime)){
				SyncMsgObj sync = new SyncMsgObj();
				sync.fill(localTally.syncMsgToken, localTally.syncMsgDate, localTally.syncMsgTime, localTally.syncMsgRecAmount, localTally.syncMsgSntAmount);
				getMessages(sync, false);
			}
		}
		
		if(pendingData){
			LifeMessage life = new LifeMessage();
			life = msgAccess.getMessages(false, "", false);
			sendDirtyMessages(life);
		}
	} */
}
