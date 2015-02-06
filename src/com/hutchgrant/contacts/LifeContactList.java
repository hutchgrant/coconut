package com.hutchgrant.contacts;

import java.util.ArrayList;

import com.hutchgrant.Elements.Contact;
import com.hutchgrant.Elements.Group;
import com.hutchgrant.coconut.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LifeContactList extends Activity implements OnClickListener{

	MyCustomAdapter dataAdapter;
	ListView cntList;
	Button btnEdit, btnRemove, btnAdd;
	Group group;
	ContentResolver cr;
	Cursor cur;
	public final int DIALOG_GETMODE = 100;
	Dialog dialog = null;
	private boolean EDITMODE = false;;
	Context ctx;
	String selGroupID = "";
	LinearLayout linLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);
		Bundle extras = getIntent().getExtras();

		selGroupID = extras.getString("selGroupID");
		getGroup(selGroupID);
		ctx = this;
		btnEdit = (Button)this.findViewById(R.id.b_edit);
		btnRemove = (Button)this.findViewById(R.id.b_removeCont);
		btnAdd = (Button)this.findViewById(R.id.b_addCont);
		btnEdit.setOnClickListener(this);
		btnRemove.setOnClickListener(this);
		btnAdd.setOnClickListener(this);
		linLayout = (LinearLayout)this.findViewById(R.id.linlayout1);
		linLayout.setVisibility(View.GONE);
		
		cntList = (ListView)this.findViewById(R.id.list_contacts);
		dataAdapter = new MyCustomAdapter(this, R.layout.contact_info, group.getCleanPeople());
		cntList.setAdapter(dataAdapter);
		cntList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showDialog(DIALOG_GETMODE, position);
			}
		});
	}

	private void getGroup(String selGroupID) {
		group = new Group(this);
		group.getDBGroupData(selGroupID);
		group.getDBContactData(selGroupID);		
		
		System.out.println("group name = "+group.getName());
		System.out.println("first group contact ="+group.getAll().get(0).getName());
	}
	/*
	 * Custom list adapter for ContactList
	 */
	private class MyCustomAdapter extends ArrayAdapter<Contact> {

		private ArrayList<Contact> ContactList;

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<Contact> ContactList) {
			super(context, textViewResourceId, ContactList);
			this.ContactList = new ArrayList<Contact>();
			this.ContactList.addAll(ContactList);
		}

		private class ViewHolder {
			TextView code;
			CheckBox name;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.contact_info, null);

				holder = new ViewHolder();
				holder.code = (TextView) convertView
						.findViewById(R.id.DisplayName);
				holder.name = (CheckBox) convertView
						.findViewById(R.id.checkbox);
				convertView.setTag(holder);

				holder.name.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						Contact contact = (Contact) cb.getTag();
						contact.setSelected(cb.isChecked());
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			

			Contact contact = ContactList.get(position);
			holder.code.setText(contact.getName());
			if(EDITMODE){
				contact.setSelected(false);
				holder.name.setVisibility(View.VISIBLE);
				holder.name.setChecked(contact.isSelected());
				holder.name.setTag(contact);
			}else{
				holder.name.setVisibility(View.GONE);
			}


			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.b_edit) {
			setEditMode();
			redraw();
		} else if (id == R.id.b_removeCont) {
			removeContacts();
			redraw();
		} else if (id == R.id.b_addCont) {
			Intent i = new Intent();
			i.setClass(LifeContactList.this, MainList.class);
			i.putExtra("selGroupID", group.getID());
			i.putExtra("selGroupName", group.getName());
			startActivity(i);
		}
	}

	public void removeContacts(){
		ArrayList<Contact> People = new ArrayList<Contact>();
		People = group.getAll();
		for(int i=0; i<People.size(); i++){
			if(People.get(i).isSelected()){
				System.out.println("Selected ID = "+People.get(i).getID());
				group.updateContact(i);
			}
		}
		group.updateGroup();
	}
	
	private void redraw() {
		getGroup(selGroupID);
		dataAdapter = new MyCustomAdapter(this, R.layout.contact_info, group.getCleanPeople());
		cntList.setAdapter(dataAdapter);
	}


	
	public void setEditMode(){
		if(EDITMODE){
			EDITMODE = false;
			linLayout.setVisibility(View.GONE);
		}else{
			EDITMODE = true;
			linLayout.setVisibility(View.VISIBLE);
		}
	}
	
	public void showDialog(int i, final int position) {
		switch(i) {
			case DIALOG_GETMODE:
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.contact_method_dialog);
			dialog.setTitle("Welcome");
			dialog.setCanceledOnTouchOutside(true);
			
	         Button buttonSMS = (Button)dialog.findViewById(R.id.btn_sms);
	         buttonSMS.setOnClickListener(new View.OnClickListener() {
	             @Override
				public void onClick(View v) {
		     			Intent i = new Intent();
		    			i.setClass(LifeContactList.this, ContactDetails.class);
		    			System.out.println("selected contact name "+group.getContact(position).getName());
		    			i.putExtra("selContactName", group.getContact(position).getName());
		    			i.putExtra("selContactPhone", group.getContact(position).getPhone());
		    			i.putExtra("selContactEmail", group.getContact(position).getEmail());
		     			startActivity(i);
		     			dialog.dismiss();
	             }
	         });
	         Button buttonPhone = (Button)dialog.findViewById(R.id.btn_phone);
	         buttonPhone.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String number = "tel:" + group.getContact(position).getPhone();
			        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number)); 
			        startActivity(callIntent);
	     			dialog.dismiss();
				}
			});
			dialog.show();
			break;
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		redraw();
	}
}
