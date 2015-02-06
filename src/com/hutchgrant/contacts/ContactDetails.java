package com.hutchgrant.contacts;

import com.hutchgrant.Elements.Contact;
import com.hutchgrant.coconut.R;
import com.hutchgrant.phone.smsMessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class ContactDetails extends Activity implements OnClickListener{
	Contact currentPerson;
	TextView txtName, txtEmail, txtPhone;
	Button btnSMS, btnPhone, btnEmail;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_details);
		currentPerson = new Contact();
		Bundle extras = getIntent().getExtras();
		currentPerson.setGoogleID(extras.getString("selContactID"));
		currentPerson.setGroupID(extras.getString("selContactGrpID"));
		currentPerson.setName(extras.getString("selContactName"));
		currentPerson.setPhone(extras.getString("selContactPhone"));
		currentPerson.setEmail(extras.getString("selContactEmail"));
		
		txtName = (TextView)findViewById(R.id.textName);
		txtEmail = (TextView)findViewById(R.id.textEmail);
		txtPhone = (TextView)findViewById(R.id.textPhone);
		
		txtName.setText(currentPerson.getName());
		txtPhone.setText(currentPerson.getPhone());
		txtEmail.setText(currentPerson.getEmail());
		
		btnSMS = (Button)findViewById(R.id.btnSMS);
		btnPhone = (Button)findViewById(R.id.btnPhone);
		btnEmail = (Button)findViewById(R.id.btnEmail);
		
		btnSMS.setOnClickListener(this);
		btnPhone.setOnClickListener(this);
		btnEmail.setOnClickListener(this); 
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btnSMS) {
			Intent i = new Intent();
			i.setClass(ContactDetails.this, smsMessage.class);
			i.putExtra("selContactID", currentPerson.getGoogleID());
			i.putExtra("selContactGrpID", currentPerson.getGroupID());
			i.putExtra("selContactName", currentPerson.getName());
			startActivity(i);
		}
	}
	
}
