package com.hutchgrant.networks.gplus;

import java.io.IOException;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.hutchgrant.Elements.User;
import com.hutchgrant.coconut.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class base extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
	
	private static final String TAG = "ExampleActivity";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	protected static final int AUTH_REQUEST_CODE = 0;
	protected static final int REQUEST_CODE = 0;

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    int runCount = 0;
    public User mPhotoUser;
	protected AsyncTask<Object, Void, User> mAuthTask;
	
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
  //      MyConnect();
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
        onSignedIn(mPlusClient);

    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }
    
    public void onSignedIn(PlusClient plusClient) {
		if (plusClient.isConnected() && runCount != 1) {
			runCount = 1;
			// Retrieve the account name of the user which allows us to retrieve
			// the OAuth access
			// token that we securely pass over to the PhotoHunt service to
			// identify and
			// authenticate our user there.
			final String usrname = plusClient.getAccountName();
			Log.v("GOOGLEACCOUNT", "User Email === = "+usrname);
			// Asynchronously authenticate with the PhotoHunt service and
			// retrieve the associated
			// PhotoHunt profile for the user.
			if(mPhotoUser == null){
				mAuthTask = new AsyncTask<Object, Void, User>() {
					@Override
					protected User doInBackground(Object... o) {
						
				           String sAccessToken = "";
			            try {
			            	 sAccessToken = GoogleAuthUtil.getToken(getApplicationContext(), usrname, AuthUtil.SCOPE_STRING);
			              //  token = GoogleAuthUtil.getToken(context, accountName, scope, bundle);
			            } catch (GooglePlayServicesAvailabilityException playEx) {
			                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
			                    playEx.getConnectionStatusCode(),
			                    null, AUTH_REQUEST_CODE);
			                // Use the dialog to present to the user.
			            } catch (UserRecoverableAuthException recoverableException) {
			                Intent recoveryIntent = recoverableException.getIntent();
			                // Use the intent in a custom dialog or just startActivityForResult.
			                startActivityForResult(recoveryIntent, REQUEST_CODE);
			            } catch (GoogleAuthException authEx) {
			                // This is likely unrecoverable.
			                Log.e(TAG, "Unrecoverable authentication exception: " + authEx.getMessage(), authEx);
			            } catch (IOException ioEx) {
			                Log.i(TAG, "transient error encountered: " + ioEx.getMessage());
			            }
						
						return AuthUtil.authenticate(base.this, usrname, sAccessToken);  
					}
	
					@Override
					protected void onPostExecute(User result) {
					
						if (result != null) {
							Log.v("USER_RESULT","result = "+result.userGName);
							setAuthenticatedProfile(result); 
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
		}
	}
	public void setAuthenticatedProfile(User profile) {
		mPhotoUser = profile;
		
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
	class UserInfo {
	    String id;
	    String email;
	    String verified_email;
	}
	
}