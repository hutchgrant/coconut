package com.hutchgrant.app;

import com.hutchgrant.coconut.Endpoints;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

public class NetworkAccess {

	private static NetworkAccess instance;
	static Context ctx;
	boolean network_available = false;

	public NetworkAccess(Context context){
		ctx = context;
	}

	public static void initAccess(Context ctx){
		if(instance == null){
			instance = new NetworkAccess(ctx);
		}
	}


	public static NetworkAccess getInstance(){
		return instance;
	}

	/*
	 *  CHECK NETWORK CONNECTION
	 */

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public boolean networkAvailable() {
		if(isNetworkAvailable()){
			network_available = true;
			System.out.println("network available");
			sendMessage(Endpoints.EVENT_NETWORK, true);
			return true;
		}else{
			network_available = false;
			System.out.println("network not available");
			sendMessage(Endpoints.EVENT_NETWORK, true);
			return false;
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
