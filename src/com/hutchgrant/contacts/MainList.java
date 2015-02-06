package com.hutchgrant.contacts;

import java.util.ArrayList;

import com.hutchgrant.Elements.*;
import com.hutchgrant.coconut.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainList extends Activity implements OnClickListener {

	MyCustomAdapter dataAdapter = null;

	Button btnSync, btnList, btnCheck;
	ListView listView;

	LifeGroup life;
	
	ContentResolver cr;
	Cursor cur, pCur;
	SimpleCursorAdapter adapter;
	boolean EDITGROUP = false;
	String selGroupID = "";
	String selGroupName = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_contact_list);
		Bundle extras = getIntent().getExtras();

		selGroupID = extras.getString("selGroupID");
		System.out.println("Adding contacts to group:"+selGroupID);
		btnCheck = (Button) this.findViewById(R.id.b_send);
		if(selGroupID.equals("n/a") == false){
			selGroupName = extras.getString("selGroupName");
			EDITGROUP = true;
			btnCheck.setText("add to " +selGroupName + " Group");
		}
		btnCheck.setOnClickListener(this);

		life = new LifeGroup(this);
		
		life.init();
		
		listView = (ListView) this.findViewById(R.id.list_contacts);
		dataAdapter = new MyCustomAdapter(this, R.layout.contact_info, life.groups.get(0).getAll());
		listView.setAdapter(dataAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_list, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.b_send) {
			if(!EDITGROUP){
				life.createGroup(dataAdapter.ContactList);
			}else{
				life.addToGroup(dataAdapter.ContactList, selGroupID);
				finish();
			}
		}
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
			holder.name.setVisibility(View.VISIBLE);
			Contact contact = ContactList.get(position);
			holder.code.setText(contact.getName());
			holder.name.setChecked(contact.isSelected());
			holder.name.setTag(contact);

			return convertView;
		}
	}


	/*
	 * List grouped users
	 */
	private void DisplayGroup(Group cntGroup) {
		/// show new group
		dataAdapter = new MyCustomAdapter(this, R.layout.contact_info, cntGroup.getAll());
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
	}
}
