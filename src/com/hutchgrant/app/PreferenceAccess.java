package com.hutchgrant.app;

import com.hutchgrant.Elements.PrefObj;
import com.hutchgrant.provider.tunadatamodel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class PreferenceAccess {

	public static PreferenceAccess instance;
	static Context ctx;
	ContentResolver cr;
	
	public PreferenceAccess(Context context){
		
		ctx = context;
		cr = ctx.getContentResolver();
	}
	public static void initAccess(Context context){
		if(instance == null){
			instance = new PreferenceAccess(context);
		}
	}
	
	public static PreferenceAccess getInstance(){
		return instance;
	}
	
	/*
	 *  Preference Queries
	 */
	
	/*
	 * set Preferences
	 */
	public void setPreference(PrefObj pref, boolean update){
		cr = ctx.getContentResolver();
		ContentValues val = new ContentValues();
		if(update){
			val.put(tunadatamodel.SQL_INS_REP, true);  
		}
		val.put(tunadatamodel.TunaPref.Cols.ID, 0);
		val.put(tunadatamodel.TunaPref.Cols.PREFSYNCON, Boolean.toString(pref.synced));
		val.put(tunadatamodel.TunaPref.Cols.PREFSYNCTIME, Integer.toString(pref.syncTime));
		cr.insert(tunadatamodel.TunaPref.CONTENT_URI, val);	
	}
	
	public void setPendingData(boolean pending, boolean update){
		cr = ctx.getContentResolver();
		ContentValues val = new ContentValues();
		if(update){
			val.put(tunadatamodel.SQL_INS_REP, true);  
		}
		val.put(tunadatamodel.TunaPref.Cols.ID, 0);
		val.put(tunadatamodel.TunaPref.Cols.PENDINGDATA, Boolean.toString(pending));
		
		System.out.println("pending data set to :"+Boolean.toString(pending));
		cr.insert(tunadatamodel.TunaPref.CONTENT_URI, val);	
	}
	
	public boolean getPendingData(){
		boolean pending = false;
		int pendingIndex = 0;

		cr = ctx.getContentResolver();
		PrefObj pref = new PrefObj();
		Cursor cur;
		cur = cr.query(tunadatamodel.TunaPref.CONTENT_URI, null, null, null,
			    null);
		
		if(cur.moveToFirst()){
				pendingIndex = cur.getColumnIndex(tunadatamodel.TunaPref.Cols.PENDINGDATA);
				
					pending = Boolean.parseBoolean(cur.getString(pendingIndex));
					System.out.println("pending data read from :"+Boolean.toString(pending));
		}
		cur.close();
		return pending;
	}
	 
	/*
	 * get Preferences
	 */
	public PrefObj getPreference(){
		cr = ctx.getContentResolver();
		PrefObj pref = new PrefObj();
		Cursor cur;
		cur = cr.query(tunadatamodel.TunaPref.CONTENT_URI, null, null, null,
			    null);
		
		int prefID_index = 0, prefSyncOn_Index = 0, prefSyncTime_Index = 0;
		int prefID = 0;
		String prefSyncTime = "";
		String prefSyncOn = "";
		
		if(cur.moveToFirst()){
				prefID_index = cur.getColumnIndex(tunadatamodel.TunaPref.Cols.ID);
				prefSyncOn_Index = cur.getColumnIndex(tunadatamodel.TunaPref.Cols.PREFSYNCON);
				prefSyncTime_Index = cur.getColumnIndex(tunadatamodel.TunaPref.Cols.PREFSYNCTIME);
				
					prefID = cur.getInt(prefID_index);
					prefSyncOn = cur.getString(prefSyncOn_Index);
					prefSyncTime = cur.getString(prefSyncTime_Index);
					System.out.println("sync time read = "+Integer.parseInt(prefSyncTime));

					pref.fill(Boolean.parseBoolean(prefSyncOn), Integer.parseInt(prefSyncTime));
		}
		cur.close();
		return pref;
		
	}
	public void setPref(PrefObj pref) {
		// TODO Auto-generated method stub
		
	}
}
