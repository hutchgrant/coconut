package com.hutchgrant.networks.gplus;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient;
import com.hutchgrant.Elements.Group;
import com.hutchgrant.Elements.User;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

public class gplusTask extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    int runCount = 0;
    public User mPhotoUser;
    Context ctx;
	private Group finGroup;
    String usrname = "";
	private boolean mConnect;
    
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.ctx = this;
		this.mConnect = false;
		this.finGroup = new Group(this);
		 mPlusClient = new PlusClient.Builder(ctx, this, this)
         .setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
         .build();
	}
	
	public Group getFriends(){
			if(mPlusClient.isConnected()){
				usrname = mPlusClient.getAccountName();
				System.out.println("g+ username is "+usrname);
				finGroup = AuthUtil.receiveGFriends(ctx, usrname);  
				setCircled(finGroup);
			}else{
				connect();
			}
			return null;
	}
	@Override
	public void onStart(){
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		 mPlusClient.disconnect();
		
	}
	
	public void connect(){
		if(!mPlusClient.isConnected()){
			mPlusClient.connect();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		usrname = mPlusClient.getAccountName();
		this.mConnect = true;
	}

	@Override
	public void onDisconnected() {
		 mPlusClient.disconnect();		
	}
	
	
	public Group getResult(){
		return this.finGroup;
	}

	public void setCircled(Group circled) {
		this.finGroup = circled;
	}

	
}
