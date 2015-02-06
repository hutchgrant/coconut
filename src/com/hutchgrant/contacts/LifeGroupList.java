package com.hutchgrant.contacts;

import java.util.ArrayList;

import com.hutchgrant.Elements.*;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.coconut.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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

public class LifeGroupList extends Activity implements OnClickListener{

	MyCustomAdapter dataAdapter;
	ListView groupVw;	
	LifeGroup life;
	Context ctx;
	Button remGroup, addGroup, editGroup;
	LinearLayout editLayout;
	boolean EDITMODE=false;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_list);
		life = new LifeGroup(this);
		ctx = this;
		life.getGroupData();
		remGroup = (Button)this.findViewById(R.id.btn_remove);
		addGroup = (Button)this.findViewById(R.id.b_listall);
		editGroup = (Button)this.findViewById(R.id.b_edit);
		editLayout = (LinearLayout)this.findViewById(R.id.edit_layout);
		remGroup.setOnClickListener(this);
		addGroup.setOnClickListener(this);
		editGroup.setOnClickListener(this);
		
		groupVw = (ListView)this.findViewById(R.id.mainlist);
		dataAdapter = new MyCustomAdapter(this, R.layout.group_list_item, life.getCleanGroupData());
		groupVw.setAdapter(dataAdapter);
		groupVw.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					Intent i = new Intent();
					i.setClass(LifeGroupList.this, LifeContactList.class);
					
					String newid ="";
					newid = life.groups.get(position).getID();
					System.out.println("SELECTED POSITION = "+position + " ID = "+newid);
					i.putExtra("selGroupID", newid);
					startActivity(i);
					redraw();
			}
		});
		
		  LocalBroadcastManager.getInstance(this).registerReceiver(mGroupSendReceiver,
			      new IntentFilter(Endpoints.EVENT_GRPWRITE));
	}

	/*
	 * Custom list adapter for ContactList
	 */
	private class MyCustomAdapter extends ArrayAdapter<Group> {

		private ArrayList<Group> GroupList;

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<Group> GroupList) {
			super(context, textViewResourceId, GroupList);
			this.GroupList = new ArrayList<Group>();
			this.GroupList.addAll(GroupList);
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
				convertView = vi.inflate(R.layout.group_list_item, null);

				holder = new ViewHolder();
				holder.code = (TextView) convertView
						.findViewById(R.id.grp_name);
				holder.name = (CheckBox) convertView
						.findViewById(R.id.checkbox);
				convertView.setTag(holder);

				holder.name.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						Group group = (Group) cb.getTag();
						group.setSelected(cb.isChecked());
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if(EDITMODE){
				holder.name.setVisibility(View.VISIBLE);
			}else{
				holder.name.setVisibility(View.GONE);
			}
			Group grp = GroupList.get(position);
			holder.code.setText(" (" + grp.getID() + ")");
			holder.code.setText(grp.getName());
			holder.name.setChecked(grp.isSelected());
			holder.name.setTag(grp);

			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_remove) {
			/// check if online 
			life.setRemoveAllGroups();
			redraw();
		} else if (id == R.id.b_listall) {
			Intent i = new Intent();
			i.setClass(LifeGroupList.this, MainList.class);
			i.putExtra("selGroupID", 0);
			startActivity(i);
		} else if (id == R.id.b_edit) {
			setEditMode();
			life.editGroupName();
			redraw();
		}
	}

	private void setEditMode() {
		if(EDITMODE){
			EDITMODE=false;
			editLayout.setVisibility(View.GONE);
		}else{
			EDITMODE=true;
			editLayout.setVisibility(View.VISIBLE);
		}
	}

		//handler for received Intents for the "my-event" event 
		private BroadcastReceiver mGroupSendReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
			 // Extract data included in the Intent
			 String message = intent.getStringExtra("message");
			 Log.d("receiver", "Got Group Write Message: " + message);
				if(message.equals("failure")){
					 Log.d("receiver", "Error cannot send group -" + message);
				} else if(message.equals("success")){
					redraw();
				}
			}
		};
		
		public void redraw(){
			life.getGroupData();
			dataAdapter = new MyCustomAdapter(ctx, R.layout.group_list_item, life.getCleanGroupData());
			groupVw.setAdapter(dataAdapter);
		}
		
		@Override
		public void onResume(){
			super.onResume();
			redraw();
		}
}