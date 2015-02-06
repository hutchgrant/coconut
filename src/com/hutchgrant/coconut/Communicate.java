/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
*/

package com.hutchgrant.coconut;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import com.hutchgrant.Elements.Contact;
import com.hutchgrant.Elements.Group;
import com.hutchgrant.Elements.Invite;
import com.hutchgrant.Elements.LifeGroup;
import com.hutchgrant.Elements.LifeInvite;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.contacts.ContactDetails;
import com.hutchgrant.networks.gplus.connectGPlus;
import com.hutchgrant.phone.smsMessage;
import com.hutchgrant.tasks.InviteTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

public class Communicate extends connectGPlus implements OnTouchListener, OnItemClickListener, OnItemLongClickListener{


private static final int DIALOG_GETMODE = 0;
private boolean INGRP = false;
int GRP_POS = 0;  /// position of contact group selected within overall groups

ViewFlipper flipper;
  protected ImageLoader imageLoader = ImageLoader.getInstance();
private Animation slide_in_left, slide_in_right, slide_out_left, slide_out_right;
float downXValue;
boolean EDITGROUP = false;
boolean EDITMODE = false;
String imageUrls[];
LifeGroup phoneGrp, lifeGrp;
LifeInvite lifeInv;
ArrayList<Group> groups = new ArrayList<Group>();
Group googleCircled;
Context ctx;
ListView phoneList, groupList, gplusList;
DisplayImageOptions options;
ItemAdapter phoneAdapter, grpAdapter, gplusAdapter;
private Dialog dialog;
int FlipTo = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
    setContentView(R.layout.flipper_main);
    ctx = this;
    Bundle extras = getIntent().getExtras();
	FlipTo = extras.getInt("viewNum");
	
	options = new DisplayImageOptions.Builder()
	//.showStubImage(R.drawable.ic_launcher)
	.showImageForEmptyUri(R.drawable.ic_launcher)
	//.showImageOnFail(R.drawable.ic_launcher)
	//.cacheInMemory(true)
	//.cacheOnDisc(true)
	.displayer(new RoundedBitmapDisplayer(20))
	.build();

    phoneList = (ListView)this.findViewById(R.id.phoneView);
    groupList = (ListView)this.findViewById(R.id.groupView);
    gplusList = (ListView)this.findViewById(R.id.gplusView);
    
    phoneList.setOnItemClickListener(this);
    groupList.setOnItemClickListener(this);
    gplusList.setOnItemClickListener(this);
    phoneList.setOnItemLongClickListener(this);
    groupList.setOnItemLongClickListener(this);
    gplusList.setOnItemLongClickListener(this);

    
  //create animations
    slide_in_left = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
    slide_in_right = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
    slide_out_left = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
    slide_out_right = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
    
    phoneGrp = new LifeGroup(this);
    lifeGrp = new LifeGroup(this);
    googleCircled = new Group(this);
    lifeInv = new LifeInvite();

	//lifeInv.getCleanInviteData(this, "sent");
	
    flipper=(ViewFlipper)findViewById(R.id.details);
    flipper.setInAnimation(slide_in_right );
    flipper.setOutAnimation(slide_out_right);
    flipper.setOnTouchListener(this);
    
    checkPos();
    flipSpecific(FlipTo);

  }
  
  @Override
  public void onStart(){
	  super.onStart();
  }
  
  public void flip(View v) {
    flipper.showNext();
    checkPos();
    setInGroup();
  }
  
  /// switch for whether we're browsing within grop
  private void setInGroup() {
	if(INGRP){
		INGRP = false;
	}
}

public void flipSpecific(int pos){
	  System.out.println("setting view to position :"+pos);
	  switch(pos){
	  case 0:
		  flipper.setDisplayedChild(flipper.indexOfChild(findViewById(R.id.view1)));
	  }
	 
	  checkPos();
  }
  
  public int getFlipPos(){
	  return flipper.getDisplayedChild();
  }
  
  public void checkPos(){
	  	switch(getFlipPos()){
  		case 0:
  		    phoneGrp.init();
  			phoneAdapter = new ItemAdapter(phoneGrp.groups.get(0).getAll());
  		    phoneList.setAdapter(phoneAdapter);
  		    INGRP = false;
		  break;
  		case 1:
  			lifeGrp.getGroupData();
  			grpAdapter = new ItemAdapter();
  			grpAdapter.setAllGroups(lifeGrp.getCleanGroupData());
  			groupList.setAdapter(grpAdapter);
		  break;
  		case 2:
  			if(googleCircled != null){
  				gplusAdapter = new ItemAdapter(googleCircled);
  				gplusList.setAdapter(gplusAdapter);
  			}
  			INGRP = false;
		  break;
  		case 3:
  			
  			//dataAdapter = new ItemAdapter();
  		//	dataAdapter.setAllInvites(lifeInv.getCleanInviteData(this, "sent"));
		  break;
	  	}
  }
  
  
@Override
  public boolean onTouch(View arg0, MotionEvent arg1) {

      // Get the action that was done on this touch event
      switch (arg1.getAction())
      {
          case MotionEvent.ACTION_DOWN:
          {
              // store the X value when the user's finger was pressed down
              downXValue = arg1.getX();
              break;
          }

          case MotionEvent.ACTION_UP:
          {
              // Get the X value when the user released his/her finger
              float currentX = arg1.getX();            

              // going backwards: pushing stuff to the right
              if (downXValue < currentX)
              {
                    flipper.showPrevious();
              }

              // going forwards: pushing stuff to the left
              if (downXValue > currentX)
              {
                   flipper.showNext();
              }
              break;
          }
      }

      // if you return false, these actions will not be recorded
      return true;
  } 

class ItemAdapter extends BaseAdapter {

	/*
	 * LIST TYPES 0 = phone, 1 = GROUPS, 2= CONTACTS IN GROUPS, 3 = GPLUS
	 */
	
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	public ArrayList<Contact> life;
	public LifeGroup lifeGroup;
	Group grp;
	int listType = 0;
	String imageUrls[];
	
	private class ViewHolder {
		public TextView text;
		public ImageView image;
		public CheckBox cb;
		public LinearLayout item;
	}
	
	public ItemAdapter(){
		this.imageUrls = new String[1];
		this.lifeGroup = new LifeGroup(ctx);
		this.life = new ArrayList<Contact>();
		this.grp = new Group(ctx);
	}

	public ListAdapter getInstance() {
		return this;
	}

	public ItemAdapter(ArrayList<Contact> arr){
		this.life = new ArrayList<Contact>();
		this.life = arr;
		this.imageUrls = new String[life.size()];
		this.listType = 0;
	}
	
	public void setContpositionact(ArrayList<Contact> arr){
		this.life = new ArrayList<Contact>();
		this.life = arr;
		this.imageUrls = new String[life.size()];
		this.listType = 0;
	}
	
	public ItemAdapter(Group arr){
		this.grp = new Group(ctx);
		this.grp = arr;
		this.imageUrls = new String[grp.getAll().size()];
		this.imageUrls = grp.fillURIList();
		this.listType = 3;
	}
	
	public void setAllGroups(ArrayList<Group> arrayList){
		this.lifeGroup = new LifeGroup(ctx);
		this.lifeGroup.groups = arrayList;
		this.imageUrls = new String[lifeGroup.groups.size()];
		///imageUrls = grp.fillURIList();
		this.listType = 1;
	}
	
	public void setGroup(Group arr){
		this.grp = new Group(ctx);
		this.grp = arr;
		this.imageUrls = new String[grp.getSize()];
		this.imageUrls = grp.fillURIList();
		this.listType = 2;
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

	@SuppressWarnings("null")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		boolean contactBox = false;
		
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
		/// reminder set to Image Name
		
		switch(listType){
		case 0:
			Contact cont = this.life.get(position);

			holder.text.setText(cont.getName());
			holder.cb.setChecked(cont.isSelected());
			holder.cb.setTag(cont);
			contactBox = true;
			break;
		case 1:
			
			Group lfGrp = this.lifeGroup.groups.get(position);
			holder.text.setText(lfGrp.getName());
			holder.cb.setChecked(lfGrp.isSelected());
			holder.cb.setTag(lfGrp);
			holder.cb.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					Group vwGrp = (Group) cb.getTag();
					vwGrp.setSelected(cb.isChecked());
				}});  
			contactBox = false;
			break;
		case 2:
			Contact contGrp = this.grp.getContact(position);

			holder.text.setText(contGrp.getName());
			holder.cb.setChecked(contGrp.isSelected());
			holder.cb.setTag(contGrp);
			contactBox = true;
			break;
		case 3: 
			Contact contGrp2 = this.grp.getContact(position);

			holder.text.setText(contGrp2.getName());
			holder.cb.setChecked(contGrp2.isSelected());
			holder.cb.setTag(contGrp2);
			contactBox = true;
			break;
		}
		
		if(EDITMODE){
			holder.cb.setVisibility(View.VISIBLE);
		}else{
			holder.cb.setVisibility(View.GONE);
		}
		
		if(contactBox){
		holder.cb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				Contact contact = (Contact) cb.getTag();
				contact.setSelected(cb.isChecked());
				
			}}); 
		}

		
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

@Override
public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	System.out.println("an item was selected");
	switch(getFlipPos()){
	case 0:
			Intent i = new Intent();
		i.setClass(Communicate.this, ContactDetails.class);
			System.out.println("selected contact name "+phoneAdapter.life.get(position).getName());
			i.putExtra("selContactID", phoneAdapter.life.get(position).getID());
			i.putExtra("selContactGrpID", "0");
			i.putExtra("selContactName", phoneAdapter.life.get(position).getName());
			i.putExtra("selContactPhone", phoneAdapter.life.get(position).getPhone());
			i.putExtra("selContactEmail", phoneAdapter.life.get(position).getEmail());
		startActivity(i);
	break;
	case 1:
		if(!INGRP){
			Group grp = new Group();
			grp = lifeGrp.getCleanGroupData().get(position);
			grpAdapter = new ItemAdapter();
			grpAdapter.setGroup(grp);
			groupList.setAdapter(grpAdapter);
			INGRP = true;
		}else{
			INGRP = false;
 			Intent i2 = new Intent();
			i2.setClass(Communicate.this, ContactDetails.class);
    			System.out.println("selected contact name "+grpAdapter.grp.getContact(position).getName());
    			i2.putExtra("selContactID", grpAdapter.grp.getContact(position).getID());
    			i2.putExtra("selContactGrpID", "0");
    			i2.putExtra("selContactName", grpAdapter.grp.getContact(position).getName());
    			i2.putExtra("selContactPhone", grpAdapter.grp.getContact(position).getPhone());
    			i2.putExtra("selContactEmail", grpAdapter.grp.getContact(position).getEmail());
			startActivity(i2);
			showDialog(DIALOG_GETMODE, position, grpAdapter.grp, true);
		}
	break;
	case 2:
	break;
	}
	
}

@Override
public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
		long arg3) {
	System.out.println("an item was long click");

	switch(getFlipPos()){
	case 0:
		 showDialog(DIALOG_GETMODE, position, phoneGrp.groups.get(0), true);
	break;
	case 1:
		if(INGRP){
		showDialog(DIALOG_GETMODE, position, lifeGrp.getCleanGroupData().get(position), true);
		GRP_POS = position;
		}else{
			showDialog(DIALOG_GETMODE, position, lifeGrp.getCleanGroupData().get(position), false);
		}
	break;
	case 2:
	break;
	}
	return false;
}
public void displayMyList(){
	 
	imageUrls = new String[googleCircled.getSize()];
	imageUrls = googleCircled.fillURIList();
	gplusList.setAdapter(new ItemAdapter(googleCircled.getAll()));
}
 
@Override
public void loadGList(Group receivedCircles){
	googleCircled= new Group(this);

	googleCircled = receivedCircles;
//	displayMyList();
//	redraw();position
}

public void redraw(){
	lifeGrp.getGroupData();
	grpAdapter = new ItemAdapter();
	grpAdapter.setAllGroups(lifeGrp.getCleanGroupData());
	groupList.setAdapter(grpAdapter);
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.main_list, menu);
	return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
	if (itemId == R.id.menu_add) {
		addAllChecked();
	} else if (itemId == R.id.menu_edit) {
		setEditMode();
		checkPos();
	} else if (itemId == R.id.menu_remove) {
		removeAllChecked();
	} else if (itemId == R.id.menu_invite) {
		lifeInv = new LifeInvite();
		switch(getFlipPos()){
		
		case 0: 
			lifeInv.convertContactsToInvite(ctx, phoneGrp.getCleanGroupData().get(0).getAll());
			break;
		case 1:
			if(INGRP){
				lifeInv.convertContactsToInvite(ctx, lifeGrp.getCleanGroupData().get(GRP_POS).getAll());
			}
			break;
		case 2:
			lifeInv.convertContactsToInvite(ctx, googleCircled.getAll());
		
			break;
		}

		

		
		//flipSpecific(4);
	}
    return true;
    
}
private void addAllChecked() {
	switch(getFlipPos()){
	case 0:
		if(!EDITGROUP){
			phoneGrp.createGroup(phoneGrp.getCleanGroupData().get(0).getAll());
		}else{
			//lifeGrp.addToGroup(dataAdapter.grp, selGroupID);
			//finish();
		}
	break;
		
	case 2: 
		if(!EDITGROUP){
			phoneGrp.createGroup(gplusAdapter.grp.getAll());
		}else{
			//phoneGrp.addToGroup(gplusAdapter.grp.getAll(), selGroupID);
			//finish();
		}
	break;
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

private void removeAllChecked() {
	switch(getFlipPos()){
		case 1:		
			//removeContacts();
		grpAdapter.lifeGroup.setRemoveAllGroups();
		break;
	}
}

private void setEditMode() {
	if(EDITMODE){
		EDITMODE=false;
	}else{
		EDITMODE=true;
	}
}


public void showDialog(int i, final int position, final Group group, final boolean ContactSelected) {
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
	    			i.setClass(Communicate.this, smsMessage.class);
	    			if(ContactSelected){
		    			i.putExtra("selContactID", group.getContact(position).getID());
		    			i.putExtra("selGrpID", "0");
		    			i.putExtra("selContactName", group.getContact(position).getName());
		    			i.putExtra("selContactImg", group.getContact(position).getName());
	    			}else{
	    				System.out.println("selected group name "+group.getName());
		    			i.putExtra("selContactID", "0");
		    			i.putExtra("selContactGrpID", group.getID());
		    			i.putExtra("selContactName", group.getName());
	    			}
	    			startActivity(i);
	     			dialog.dismiss();
             }
         });
         Button buttonPhone = (Button)dialog.findViewById(R.id.btn_phone);
         buttonPhone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ContactSelected){
				String number = "tel:" + group.getContact(position).getPhone();
		        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number)); 
		        startActivity(callIntent);
     			dialog.dismiss();
				}
			}
		});
		dialog.show();
		break;
	}
}

@Override
public void onDestroy(){
	super.onDestroy();
	setInGroup();
}



}
