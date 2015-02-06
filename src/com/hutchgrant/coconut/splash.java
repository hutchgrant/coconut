package com.hutchgrant.coconut;


import com.hutchgrant.coconut.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class splash extends Activity implements OnClickListener{

	Button nextButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		nextButton = (Button) findViewById(R.id.btn_next);
		
		nextButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		Intent intent = new Intent();
		int id = v.getId();
		if (id == R.id.btn_next) {
			intent.setClass(splash.this, LoginLauncher.class);
			startActivity(intent);
		}
	}
}
