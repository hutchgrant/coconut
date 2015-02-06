package com.hutchgrant.phone;

import com.hutchgrant.coconut.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class phoneCall extends Activity implements OnClickListener{

		String ContactName = "";
		String ContactPhone = "";
		String FinalNumber = "";
		
		Button bCall, bEnd;
		TextView callTitle;
		
		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			setContentView(R.layout.call_screen);
			Bundle extras = getIntent().getExtras();
			ContactName = extras.getString("selContactName");
			ContactPhone = extras.getString("selContactPhone");
			
			callTitle = (TextView)this.findViewById(R.id.calling_title);
			bCall = (Button)this.findViewById(R.id.b_call);
			bEnd = (Button)this.findViewById(R.id.b_hangup);
			bCall.setOnClickListener(this);
			bEnd.setOnClickListener(this);
		
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.b_call) {
				makeCall callListener = new makeCall();
				TelephonyManager mTM = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
				mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
				callTitle.setText("Now Calling: "+ContactName+ " Phone: "+ContactPhone);
				String number = "tel:" + ContactPhone;
				Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
				startActivity(callIntent);
			}
		}
		

}
