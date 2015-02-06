package com.hutchgrant.coconut;

import com.hutchgrant.Elements.PrefObj;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.networks.gplus.GFriends;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemSelectedListener;

public class preferences extends Activity implements OnClickListener{
	boolean syncToggleOn = true;
	
	Button prefSave, syncPref, syncAll, gplusFriends;
	ToggleButton syncOn;
	Spinner syncPrefSpinner;
	PrefObj mPref;
	TunaApp app;
	String[] spinnerArray;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preference_main);
		app = (TunaApp) ((Activity) this).getApplication();
		mPref = new PrefObj();
		
		syncOn = (ToggleButton)findViewById(R.id.toggleSync);
		syncAll = (Button)findViewById(R.id.btn_sync_all);
		prefSave = (Button)findViewById(R.id.btn_save_pref);
		gplusFriends = (Button)findViewById(R.id.btn_gplus);
		
		syncPrefSpinner = (Spinner)findViewById(R.id.sync_spin);
		
		syncAll.setOnClickListener(this);
		syncOn.setOnClickListener(this);
		prefSave.setOnClickListener(this);
		gplusFriends.setOnClickListener(this);
		
		mPref = app.getPref();
		syncOn.setChecked(mPref.synced);
		fillPrefSpinner();
	}

	public void fillPrefSpinner() {
		int position = 0;
		spinnerArray = new String[5];
		spinnerArray = getResources().getStringArray(R.array.PreferenceSyncTimes);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray );
		spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		/// get our cached, selected, sync pref time, position in the array
		for(int i=0; i<= 3; i++){
			System.out.println("spinner array sync time = "+spinnerArray[i]);
			System.out.println("current sync time = "+mPref.syncTime);
			if(i > 0){
				if(Integer.parseInt(spinnerArray[i]) == mPref.syncTime){
					position = i;
					syncPrefSpinner.setSelection(position);
	
				}
			}
		}
		syncPrefSpinner.setAdapter(spinnerArrayAdapter);
		syncPrefSpinner.setOnItemSelectedListener(new SelectPrefSyncListener());		
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.toggleSync) {
			if(mPref.synced){
				mPref.synced = false;
			}else{
				mPref.synced = true;
			}
		} else if (id == R.id.btn_sync_all) {
			app.getAllSync();
		} else if (id == R.id.btn_gplus) {
			//	app.getGFriends();
			Intent i = new Intent();
			i.setClass(this, GFriends.class);
			startActivity(i);
		} else if (id == R.id.btn_save_pref) {
			app.setPref(mPref, true);
		}
	}
	
	public class SelectPrefSyncListener implements OnItemSelectedListener {

	    @Override
		public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	    	if(pos > 0){
	    		System.out.println("Item selected = "+Integer.parseInt(spinnerArray[pos]));
	    		mPref.syncTime = Integer.parseInt(spinnerArray[pos]);
	    	}
	    }
	    @Override
		public void onNothingSelected(AdapterView parent) {
	      // Do nothing.
	    }
	}

}
