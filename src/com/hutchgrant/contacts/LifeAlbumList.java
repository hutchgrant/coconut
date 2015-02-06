package com.hutchgrant.contacts;

import java.util.ArrayList;

import com.hutchgrant.Elements.*;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.coconut.R;
import com.hutchgrant.gallery.ImageGallery;

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

public class LifeAlbumList extends Activity implements OnClickListener{

	MyCustomAdapter dataAdapter;
	ListView groupVw;	
	LifeAlbum life;
	Context ctx;
	Button nwGroup, remGroup, addGroup, editGroup;
	LinearLayout editLayout;
	boolean EDITMODE=false;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_list);
		life = new LifeAlbum(this);
		ctx = this;
		life.fillAlbums();
		nwGroup = (Button)this.findViewById(R.id.b_new);
		remGroup = (Button)this.findViewById(R.id.btn_remove);
		addGroup = (Button)this.findViewById(R.id.b_listall);
		editGroup = (Button)this.findViewById(R.id.b_edit);
		editLayout = (LinearLayout)this.findViewById(R.id.edit_layout);
		nwGroup.setOnClickListener(this);
		remGroup.setOnClickListener(this);
		addGroup.setOnClickListener(this);
		editGroup.setOnClickListener(this);
		
		groupVw = (ListView)this.findViewById(R.id.mainlist);
		dataAdapter = new MyCustomAdapter(this, R.layout.group_list_item, life.getAllAlbums());
		groupVw.setAdapter(dataAdapter);
		groupVw.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					String newid =null;

					Intent i = new Intent();
					i.setClass(LifeAlbumList.this, ImageGallery.class);
					newid = life.Albums.get(position).getID();
					System.out.println("SELECTED POSITION = "+position + " ID = "+newid);
					i.putExtra("selAlbumID", newid);
					startActivity(i);
					redraw();
			}
		});
		
		  LocalBroadcastManager.getInstance(this).registerReceiver(mAlbumReceiver,
			      new IntentFilter(Endpoints.EVENT_ALBUM_WRITE));
	}

	/*
	 * Custom list adapter for ContactList
	 */
	private class MyCustomAdapter extends ArrayAdapter<Album> {

		private ArrayList<Album> AlbumList;

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<Album> AlbumList) {
			super(context, textViewResourceId, AlbumList);
			this.AlbumList = new ArrayList<Album>();
			this.AlbumList.addAll(AlbumList);
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
						Album album = (Album) cb.getTag();
						album.setSelected(cb.isChecked());
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
			Album alb = AlbumList.get(position);
			holder.code.setText(" (" + alb.getID() + ")");
			holder.code.setText(alb.getName());
			holder.name.setChecked(alb.isSelected());
			holder.name.setTag(alb);

			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_remove) {
			/// check if online 
			life.removeAllAlbums();
			redraw();
		} else if (id == R.id.b_listall) {
			Intent i = new Intent();
			i.setClass(LifeAlbumList.this, MainList.class);
			i.putExtra("selGroupID", 0);
			startActivity(i);
		} else if (id == R.id.b_edit) {
			setEditMode();
			life.editAlbumName();
			redraw();
		} else if (id == R.id.b_new) {
			life.createAlbum();
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
		private BroadcastReceiver mAlbumReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
			 // Extract data included in the Intent
			 String message = intent.getStringExtra("message");
			 Log.d("receiver", "Got Album Write Message: " + message);
				if(message.equals("failure")){
					 Log.d("receiver", "Error cannot send group -" + message);
				} else if(message.equals("success")){
					redraw();
				}
			}
		};
		public void redraw(){
			life.fillAlbums();
			dataAdapter = new MyCustomAdapter(ctx, R.layout.group_list_item, life.getAllAlbums());
			groupVw.setAdapter(dataAdapter);
		}
		
		@Override
		public void onResume(){
			super.onResume();
			redraw();
		}
		

		
}