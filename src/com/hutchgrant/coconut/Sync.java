package com.hutchgrant.coconut;

import com.hutchgrant.app.TunaApp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Sync extends Activity implements OnClickListener{
	
	Button syncGrp, syncImg, syncAll;
	TunaApp app;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_test);
		app = (TunaApp) this.getApplication();
		syncGrp = (Button)findViewById(R.id.btn_sync_grp);
		syncImg = (Button)findViewById(R.id.btn_sync_img);
		syncAll = (Button)findViewById(R.id.btn_sync_all);
	
		syncGrp.setOnClickListener(this);
		syncImg.setOnClickListener(this);
		syncAll.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_sync_grp) {
		} else if (id == R.id.btn_sync_img) {
		} else if (id == R.id.btn_sync_all) {
			app.startThread();
		}
	}
}
