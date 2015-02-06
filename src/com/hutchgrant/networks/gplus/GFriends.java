package com.hutchgrant.networks.gplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.hutchgrant.Elements.Contact;
import com.hutchgrant.Elements.Group;
import com.hutchgrant.Elements.LifeGroup;
import com.hutchgrant.Elements.Sync.gPlusSyncData;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.coconut.R;
import com.hutchgrant.contacts.ContactDetails;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class GFriends extends connectGPlus implements OnClickListener{
	private boolean EDITMODE = false;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected static final int DIALOG_GETMODE = 0;

	TunaApp app = (TunaApp) ((Activity) this).getApplication();
	Context ctx;
	String selGroupID = "";
	ListView cntList;
	Button btnEdit, btnRemove, btnAdd;
	LinearLayout linLayout;
	MyCustomAdapter dataAdapter;
	Dialog dialog;
	gPlusSyncData gcontacts;
	LifeGroup life;
	String[] imageUrls;
	DisplayImageOptions options;
	Group googleCircled;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);

		googleCircled= new Group(this);
		life = new LifeGroup(this);
		ctx = this;
		btnEdit = (Button)this.findViewById(R.id.b_edit);
		btnRemove = (Button)this.findViewById(R.id.b_removeCont);
		btnAdd = (Button)this.findViewById(R.id.b_addCont);
		btnEdit.setOnClickListener(this);
		btnRemove.setOnClickListener(this);
		btnAdd.setOnClickListener(this);
		linLayout = (LinearLayout)this.findViewById(R.id.linlayout1);
		linLayout.setVisibility(View.GONE);
		
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
		cntList = (ListView)this.findViewById(R.id.list_contacts);
		cntList.setAdapter(dataAdapter);
		cntList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showDialog(DIALOG_GETMODE, position);
			}
		});
		
		
		
	}
	
	@Override
	public void loadGList(Group receivedCircles){
		googleCircled= new Group(this);

		googleCircled = receivedCircles;
		displayMyList();
	//	redraw();
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
			/// select group to add to
			life.addToGroup(dataAdapter.ContactList, selGroupID);
			finish();
		} else if (id == R.id.b_removeCont) {
			removeContacts();
			redraw();
		} else if (id == R.id.b_addCont) {
			life.createGroup(dataAdapter.ContactList);
		}
	}

	public void removeContacts(){
		ArrayList<Contact> People = new ArrayList<Contact>();
		People = googleCircled.getAll();
		for(int i=0; i<People.size(); i++){
			if(People.get(i).isSelected()){
				System.out.println("Selected ID = "+People.get(i).getID());
				googleCircled.updateContact(i);
			}
		}
		googleCircled.updateGroup();
	}
	
	private void redraw() {
		dataAdapter = new MyCustomAdapter(this, R.layout.contact_info, googleCircled.getAll());
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
		    			i.setClass(GFriends.this, ContactDetails.class);
		    			System.out.println("selected contact name "+googleCircled.getContact(position).getName());
		    			i.putExtra("selContactName", googleCircled.getContact(position).getName());
		    			i.putExtra("selContactPhone", googleCircled.getContact(position).getPhone());
		    			i.putExtra("selContactEmail", googleCircled.getContact(position).getEmail());
		     			startActivity(i);
		     			dialog.dismiss();
	             }
	         });
	         Button buttonPhone = (Button)dialog.findViewById(R.id.btn_phone);
	         buttonPhone.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String number = "tel:" + googleCircled.getContact(position).getPhone();
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
	
	public void displayMyList(){
	
		imageUrls = new String[googleCircled.getSize()];
		imageUrls = googleCircled.fillURIList();
		cntList.setAdapter(new ItemAdapter());
	}
	
	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public TextView text;
			public ImageView image;
			public CheckBox cb;
		}

		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_list_image, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.image = (ImageView) view.findViewById(R.id.image);
				holder.cb = (CheckBox)view.findViewById(R.id.CheckBox);
				

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			if(EDITMODE){
				holder.cb.setVisibility(View.VISIBLE);

			}else{
				holder.cb.setVisibility(View.GONE);
			}
			/// reminder set to Image Name
			holder.text.setText(googleCircled.getContact(position).getName());
			holder.cb.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					Contact contact = (Contact) cb.getTag();
					contact.setSelected(cb.isChecked());
				}});
			imageLoader.displayImage(imageUrls[position], holder.image, options, animateFirstListener);

			return view;
		}
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}