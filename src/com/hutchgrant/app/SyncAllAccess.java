package com.hutchgrant.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.LifeInvite;
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

public class SyncAllAccess {
	
	
	public static SyncAllAccess instance;
	public SyncAlbumAccess syncAlb;
	public SyncGroupAccess syncGrp;
	public SyncInviteAccess syncInv;
	public PreferenceAccess prefAccess;
	Context ctx;
	private String TAG;
	ContentResolver cr;
	Cursor cur;
	
	class SmallSync {

		public String syncToken = "";
	}
	
	public SyncAllAccess(Context context){
		ctx = context;
		SyncAlbumAccess.initAccess(context);
		SyncGroupAccess.initAccess(context);
		PreferenceAccess.initAccess(context);
		SyncInviteAccess.initAccess(context);
		
		syncAlb = SyncAlbumAccess.getInstance();
		syncGrp = SyncGroupAccess.getInstance();
		syncInv = SyncInviteAccess.getInstance();
		prefAccess = PreferenceAccess.getInstance();
	}

	public static void initAccess(Context context){
		if(instance == null){
			instance = new SyncAllAccess(context);
		}
	}

	public static SyncAllAccess getInstance(){
		return instance;
	}

	/*
	 * SEND a sync data object TO the server, 
	 * RECEIVE a sync data object back from the server
	 * save its updated info to database
	 */
	public SyncObj getRemoteTally() {
		SyncObj sync = new SyncObj();
		sync = getLocalTally();
		
		SmallSync small = new SmallSync();
		small.syncToken = sync.syncToken;
		
		HttpURLConnection urlConnection = null;
		OutputStream outStream = null;
		String response = null;
		int statusCode = 0;

		try {
			URL url = new URL(Endpoints.SYNC_ALL);
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
				sync = new Gson().fromJson(response, SyncObj.class);
				//saveLocalTally(sync, true);
				//System.out.println("SYNC USER = "+sync.syncUserID);
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

	public SyncObj getLocalTally() {
		
		//// get Local sync Object from sync table
		SyncObj sync = new SyncObj();
		
		ContentResolver cr = ctx.getContentResolver();
		Cursor cur = cr.query(tunadatamodel.TunaSync.CONTENT_URI, null,null, null, null);
		
		int userIndex = 0, imgDateIndex = 0, imgTimeIndex = 0, grpDateIndex = 0, grpTimeIndex= 0, profDateIndex = 0, profTimeIndex = 0;
		int imgAmtIndex = 0, grpAmtIndex = 0, albAmtIndex = 0, conAmtIndex = 0, tokenIndex = 0;
		int invDateIndex = 0, invTimeIndex = 0, invTokenIndex = 0, invAmtSntIndex = 0, invAmtRecIndex=0;
		int msgDateIndex = 0, msgTimeIndex = 0, msgTokenIndex = 0, msgAmtSntIndex = 0, msgAmtRecIndex=0;
		
		userIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCUSERID);
		tokenIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCTOKEN);
		imgDateIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCIMGDATE);
		imgTimeIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCIMGTIME);
		grpDateIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCGRPDATE);
		grpTimeIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCGRPTIME);
		profDateIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCPROFDATE);
		profTimeIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCPROFTIME);

		albAmtIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCALBAMT);
		imgAmtIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCIMGAMT);
		grpAmtIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCGRPAMT);
		conAmtIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCCONAMT);

		invDateIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCINVDATE);
		invTimeIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCINVTIME);
		invTokenIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCINVTOKEN);
		invAmtSntIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCINV_SNTAMT);
		invAmtRecIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCINV_RECAMT); 

		msgDateIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCMSGDATE);
		msgTimeIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCMSGTIME);
		msgTokenIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCMSGTOKEN);
		msgAmtSntIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCMSG_SNTAMT);
		msgAmtRecIndex = cur.getColumnIndex(tunadatamodel.TunaSync.Cols.SYNCMSG_RECAMT); 
		
		int columnCount = cur.getColumnCount();
		
		if (cur.moveToFirst()) {
		
			do {
				for(int i = 0; i < columnCount; i++){
					if(i == userIndex){
						sync.syncUserID = cur.getString(i);
					}else if(i == tokenIndex){
						sync.syncToken = cur.getString(i);
					}else if(i == imgDateIndex){
						sync.syncImgDate = cur.getString(i);
					}else if(i == imgTimeIndex){
						sync.syncImgTime = cur.getString(i);
					} else if(i == grpDateIndex){
						sync.syncGrpDate = cur.getString(i);
					}else if(i == grpTimeIndex){
						sync.syncGrpTime = cur.getString(i);
					}else if(i == profDateIndex){
						sync.syncProfDate = cur.getString(i);
					}else if(i == profTimeIndex){
						sync.syncProfTime = cur.getString(i);
					}
					//////////////////////
					else if(i == albAmtIndex){
						sync.syncAlbAmount = Integer.parseInt(cur.getString(i));
					}else if(i == imgAmtIndex){
						sync.syncImgAmount = Integer.parseInt(cur.getString(i));
					}else if(i == grpAmtIndex){
						sync.syncGrpAmount = Integer.parseInt(cur.getString(i));
					}else if(i == conAmtIndex){
						sync.syncConAmount = Integer.parseInt(cur.getString(i));
					}
					///////////////////////////
					else if(i == invTokenIndex){
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
					//////////////////////////
					else if(i == msgTokenIndex){
						sync.syncMsgToken = cur.getString(i);
					}else if(i == msgDateIndex){
						sync.syncMsgDate = cur.getString(i);
					}else if(i == msgTimeIndex){
						sync.syncMsgTime = cur.getString(i);
					}else if(i == msgAmtSntIndex){
						sync.syncMsgSntAmount = cur.getInt(i);
					}else if(i == msgAmtRecIndex){
						sync.syncMsgRecAmount = cur.getInt(i);
					}
				}
			}while(cur.moveToNext());
		}
		
		cur.close();
		return sync;
	}
	
	public void saveLocalTally(SyncObj sync, boolean update){
		ContentResolver cr = ctx.getContentResolver();
		ContentValues val = new ContentValues();
		if(update){
			val.put(tunadatamodel.SQL_INS_REP, true);  
		}
		val.put(tunadatamodel.TunaSync.Cols.SYNCUSERID, (sync.syncUserID != null) ? sync.syncUserID : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCTOKEN, (sync.syncToken != null) ? sync.syncToken : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCGRPDATE, (sync.syncGrpDate != null) ? sync.syncGrpDate : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCGRPTIME, (sync.syncGrpTime != null) ? sync.syncGrpTime : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCIMGDATE, (sync.syncImgDate != null) ? sync.syncImgDate : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCIMGTIME, (sync.syncImgDate != null) ? sync.syncImgTime : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCPROFDATE, (sync.syncProfDate != null) ? sync.syncProfDate : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCPROFTIME, (sync.syncProfTime != null) ? sync.syncProfTime : null);

		val.put(tunadatamodel.TunaSync.Cols.SYNCALBAMT, (sync.syncAlbAmount != 0) ? sync.syncAlbAmount : 0);
		val.put(tunadatamodel.TunaSync.Cols.SYNCIMGAMT, (sync.syncImgAmount != 0) ? sync.syncImgAmount : 0);
		val.put(tunadatamodel.TunaSync.Cols.SYNCGRPAMT, (sync.syncGrpAmount != 0) ? sync.syncGrpAmount : 0);
		val.put(tunadatamodel.TunaSync.Cols.SYNCCONAMT, (sync.syncConAmount != 0) ? sync.syncConAmount : 0);
		
		val.put(tunadatamodel.TunaSync.Cols.SYNCINVTOKEN, (sync.syncInviteToken != null) ? sync.syncInviteToken : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCINVDATE, (sync.syncInviteDate != null) ? sync.syncInviteDate : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCINVTIME, (sync.syncInviteTime != null) ? sync.syncInviteTime : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCINV_RECAMT, (sync.syncInvRecAmount != 0) ? sync.syncInvRecAmount : 0);
		val.put(tunadatamodel.TunaSync.Cols.SYNCINV_SNTAMT, (sync.syncInvSntAmount != 0) ? sync.syncInvSntAmount : 0);
		
		val.put(tunadatamodel.TunaSync.Cols.SYNCMSGTOKEN, (sync.syncMsgToken != null) ? sync.syncMsgToken : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCMSGDATE, (sync.syncMsgDate != null) ? sync.syncMsgDate : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCMSGTIME, (sync.syncMsgTime != null) ? sync.syncMsgTime : null);
		val.put(tunadatamodel.TunaSync.Cols.SYNCMSG_RECAMT, (sync.syncMsgRecAmount != 0) ? sync.syncMsgRecAmount : 0);
		val.put(tunadatamodel.TunaSync.Cols.SYNCMSG_SNTAMT, (sync.syncMsgSntAmount != 0) ? sync.syncMsgSntAmount : 0);
		cr.insert(tunadatamodel.TunaSync.CONTENT_URI, val);
	}
	
	
	public void getAllSync() {
		boolean fillTally = false;
		boolean pending = false;
		 
		
		// check and compare local tally with remote tally
		SyncObj localSync = new SyncObj();
	   SyncObj remoteSync = new SyncObj();
	   remoteSync = getRemoteTally();
	   localSync = getLocalTally();
	   
	   if(localSync.syncImgAmount != remoteSync.syncImgAmount || localSync.syncAlbAmount != remoteSync.syncAlbAmount){
		   
			ArrayList<Album> albums = new ArrayList<Album>();
			albums = syncAlb.receiveAllAlbums(localSync);
			syncAlb.receiveAllImages(albums);
		   fillTally = true;
	   }
	   if(localSync.syncGrpAmount != remoteSync.syncGrpAmount){
		   if(localSync.syncGrpDate != remoteSync.syncGrpDate){
			   if(localSync.syncGrpTime != remoteSync.syncGrpTime){
				   syncGrp.SyncReceiveGroups(localSync);
				   fillTally = true;
			   }
		   }
	   } 
	   if(localSync.syncToken != remoteSync.syncToken){
		   fillTally = true;
	   }
	   if(!localSync.syncInviteToken.equals(remoteSync.syncInviteToken)){
		   if(localSync.syncInvRecAmount != remoteSync.syncInvRecAmount || localSync.syncInvSntAmount != remoteSync.syncInvRecAmount){
			   SyncInviteObj invite = new SyncInviteObj();
				invite.fill(localSync.syncInviteToken, localSync.syncInviteDate, localSync.syncInviteTime, localSync.syncInvRecAmount, localSync.syncInvSntAmount);
				syncInv.getLifeInvites(invite);
		   }
	   }
	   
	   
	   
	   if(fillTally){
		   /// update local tally to reflect the server's status
		   if(!remoteSync.syncUserID.equals("")){
			   saveLocalTally(remoteSync, true);
	   		}
	   }
	   
	   /// if there is any pending data to send, send it.
		pending = prefAccess.getPendingData();

	   if(pending){
				syncAlb.ControlRemoteSync();
				syncAlb.syncAllDirtyPhotos();
				syncGrp.sendRemoteGroups();
				LifeInvite invite = new LifeInvite();
				invite = syncInv.invAccess.getInvites(false, "updated", "");
				syncInv.sendDirtyInvites(invite);
				
				   prefAccess.setPendingData(false, true);
				   
				   remoteSync = getRemoteTally();
				   if(!remoteSync.syncUserID.equals("")){
					   saveLocalTally(remoteSync, true);
				   }
				
		}
	}
}
