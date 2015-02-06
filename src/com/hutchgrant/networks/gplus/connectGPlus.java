package com.hutchgrant.networks.gplus;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.hutchgrant.Elements.Group;
import com.hutchgrant.Elements.User;
import com.hutchgrant.coconut.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class connectGPlus extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
	
	private static final String TAG = "ExampleActivity";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    int runCount = 0;
    public User mPhotoUser;
	protected AsyncTask<Object, Void, Group> mAuthTask;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newloading);
        mPlusClient = new PlusClient.Builder(this, this, this)
                .setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
                .build();
        // Progress bar to be displayed if the connection failure is not resolved.
    //    mConnectionProgressDialog = new ProgressDialog(this);
    //    mConnectionProgressDialog.setMessage("Tuna is sig in...");
     //   MyConnect();
        
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
     // if (mConnectionProgressDialog.isShowing()) {
        // The user clicked the sign-in button already. Start to resolve
        // connection errors. Wait until onConnected() to dismiss the
        // connection dialog.
        if (result.hasResolution()) {
          try {
                   result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
           } catch (SendIntentException e) {
                   mPlusClient.connect();
           }
        }
   //   }
      // Save the result and resolve the connection failure upon a user click.
      mConnectionResult = result;
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        String accountName = mPlusClient.getAccountName();
        Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG).show();
   //     mConnectionProgressDialog.dismiss();
        getGFriends(mPlusClient);
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }
   
	public void setAuthenticatedProfile(User profile) {
		mPhotoUser = profile;
		
	}
	
	public void getGFriends(PlusClient plusClient){
		final String usrname = plusClient.getAccountName();
		mAuthTask = new AsyncTask<Object, Void, Group>() {
			@Override
			protected Group doInBackground(Object... o) {
				return AuthUtil.receiveGFriends(connectGPlus.this, usrname);  
			}

			@Override
			protected void onPostExecute(Group result) {
			
				if (result != null) {
					loadGList(result);
					//finish();
				//	executePendingActions();
				//	update();
				} //else {
				//	setAuthenticatedProfile(null);
				//	mPlus.signOut();
				//}
			}


		};

		mAuthTask.execute();
	}
	public void sendMessage(String event, boolean msg){

	}

	public void MyConnect() {
		// TODO Auto-generated method stub
	    if (!mPlusClient.isConnected()) {
	        if (mConnectionResult == null) {
	 //           mConnectionProgressDialog.show();
	        } else {
	            try {
	                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
	            } catch (SendIntentException e) {
	                // Try connecting again.
	                mConnectionResult = null;
	                mPlusClient.connect();
	            }
	        }
	    }
	}
	
	/**
	 * Resets the state of asynchronous tasks used by this activity.
	 */
	protected void resetTaskState() {
		if (mAuthTask != null) {
			mAuthTask.cancel(true);
			mAuthTask = null;
		}
	} 
	public void endTask(){
		mAuthTask.cancel(true);
	}
	
	public void logout(){
		mPlusClient.disconnect();
	}

	public void loadGList(Group receivedCircles) {
		// TODO Auto-generated method stub
		
	}
}