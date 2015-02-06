package com.hutchgrant.contacts;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.hutchgrant.Elements.Invite;
import com.hutchgrant.Elements.LifeInvite;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.coconut.R;
import com.hutchgrant.tasks.GroupTask;
import com.hutchgrant.tasks.InviteTask;
import com.hutchgrant.tasks.InviteTask.InvListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class InviteDisplay extends Activity implements OnTabChangeListener, OnItemClickListener, OnClickListener{
	  protected ImageLoader imageLoader = ImageLoader.getInstance();
	  
		public boolean EDITMODE = false;

	LifeInvite life;
	
	ItemAdapter dataAdapter;
	DisplayImageOptions options;
	public static final String tab1 = "tab_receive";
	public static final String tab2 = "tab_sent";

	private static final int INVITE_REC_SEL = 10;
	private static final int INVITE_SENT_SEL = 20;
	Dialog dialog;
	
	private int LIST = 0;
	private int SELECT_POS = 0;
	TabHost tabHost;
	
	Button btnConfirm, btnIgnore, btnBlock;
	ListView receiveList, sentList;
	Context ctx;
	TunaApp app;
	GroupTask gt;
	InviteTask it;
	SyncObj localSync;
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    ctx = this;
	    setContentView(R.layout.communicate_tabs);
		app = (TunaApp) ((Activity) this).getApplication();

	    tabHost=(TabHost)findViewById(R.id.tab_com_host);
	    tabHost.setup();
	 
	    TabSpec spec1=tabHost.newTabSpec("Tab1");
	    spec1.setContent(R.id.InviteReceive);
	    spec1.setIndicator("InviteReceived",null);
	 
	    TabSpec spec2=tabHost.newTabSpec("Tab2");
	    spec2.setContent(R.id.InviteSent);
	    spec2.setIndicator("InviteSent",null);	 
	 
	    tabHost.addTab(spec1);
	    tabHost.addTab(spec2);
	 
	    tabHost.setOnTabChangedListener(this);
	    
	    receiveList = (ListView)this.findViewById(R.id.receiveView);
	    sentList = (ListView)this.findViewById(R.id.sentView);

	options = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.ic_launcher)
	.showImageForEmptyUri(R.drawable.ic_launcher)
	.showImageOnFail(R.drawable.ic_launcher)
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.displayer(new RoundedBitmapDisplayer(20))
	.build();
	
	fillList();
}

@Override
public void onTabChanged(String tabId) {
/*System.out.println(tabId);
	for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) {
		//if()
	} */
	if(LIST == 0){
		LIST = 1;
	}else{
		LIST = 0;
	}
	fillList();
}

public void getInvites(boolean clean, String status){
	TunaApp app = (TunaApp) ((Activity) this).getApplication();
	life = new LifeInvite();
	life = app.getInvites(clean, status);
}

public void fillList(){
	if(LIST == 0){
		getInvites(true, "received");
		dataAdapter = new ItemAdapter(this, life);
		receiveList.setAdapter(dataAdapter);
		receiveList.setOnItemClickListener(this);
	}else{
		getInvites(true, "sent");
		dataAdapter = new ItemAdapter(this, life);
		sentList.setAdapter(dataAdapter);
		sentList.setOnItemClickListener(this);
	}
	
}

@Override
public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
	switch(LIST){
	case 0:
		showDialog(INVITE_REC_SEL,dataAdapter.life.invites, pos);	
		break;
	case 1:
		showDialog(INVITE_SENT_SEL,dataAdapter.life.invites, pos);	
		break;
	}
	SELECT_POS = 0;
	
	
}

private void showDialog(int inviteSentSel, ArrayList<Invite> invites, int pos) {
	dialog = new Dialog(this);
	
	switch(inviteSentSel){
	case INVITE_REC_SEL:
		SELECT_POS = pos;
		dialog.setContentView(R.layout.invite_rec_sel);
		dialog.setTitle("Invite Received");
		dialog.setCanceledOnTouchOutside(true);
		
		Button btnInvConfirm = (Button)dialog.findViewById(R.id.btn_inv_confirm);
		btnInvConfirm.setOnClickListener(this);
		
		Button btnInvRemove = (Button)dialog.findViewById(R.id.btn_inv_remove);
		btnInvRemove.setOnClickListener(this);
		
		Button btnInvBlock = (Button)dialog.findViewById(R.id.btn_inv_block);
		btnInvBlock.setOnClickListener(this);
		dialog.show();

		break;
	case INVITE_SENT_SEL:
		
		dialog.setContentView(R.layout.invite_sent_sel);
		dialog.setTitle("Invite Received");
		dialog.setCanceledOnTouchOutside(true);
		
		Button btnInvConfirm2 = (Button)dialog.findViewById(R.id.btn_inv_confirm2);
		btnInvConfirm2.setOnClickListener(this);
		
		Button btnInvRemove2 = (Button)dialog.findViewById(R.id.btn_inv_remove2);
		btnInvRemove2.setOnClickListener(this);
		
		Button btnInvBlock2 = (Button)dialog.findViewById(R.id.btn_inv_block2);
		btnInvBlock2.setOnClickListener(this);
		
		dialog.show();
		break;
	}

	
	
}

class ItemAdapter extends BaseAdapter {
	
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	public LifeInvite life;
	String imageUrls[];
	Context ctx;
	
	private class ViewHolder {
		public TextView text;
		public ImageView image;
		public CheckBox cb;
	}
	
	public ItemAdapter(){
		this.imageUrls = new String[1];
		this.life = new LifeInvite();
	}
	
	public ListAdapter getInstance() {
		return this;
	}

	public ItemAdapter(Context context, LifeInvite arr){
		this.life = new LifeInvite();
		this.life = arr;
		this.imageUrls = new String[arr.invites.size()];
		this.ctx = context;
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
		
		if(EDITMODE){
			holder.cb.setVisibility(View.VISIBLE);
		}else{
			holder.cb.setVisibility(View.GONE);
		}
		
		Invite inv = this.life.invites.get(position);

			if(LIST == 0){
				holder.text.setText(inv.InviteUserName);
			}else{
				holder.text.setText(inv.RecipientName);
			}
			
			holder.cb.setChecked(inv.Selected);
			holder.cb.setTag(inv);
			
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
public void onClick(View v) {
	int id = v.getId();
	System.out.println("TESTING dialog click");

	if (id == R.id.btn_inv_confirm) {
		System.out.println("TESTING CONFIRM");
	//	LifeInvite selInvCon = new LifeInvite();
	//	selInvCon = life.getSelected(dataAdapter.life.invites);
	//	life.setConfirmed(this, selInvCon.invites);

		Invite inv = new Invite();
		inv = dataAdapter.life.invites.get(SELECT_POS);
		inv.InviteStatus = "confirmed";
		inv.InviteDate = "updated";
		app.writeInvite(inv, true);
		app.setPendingData(true, true);

		localSync = new SyncObj();
		localSync = app.getSyncTally();
		
		it = new InviteTask(this);
		it.setSyncType(3);
		it.setListener(new InvListener(){
			
			@Override
			public void getGroups(){
				gt = new GroupTask(ctx);
				gt.setSyncType(0);
				gt.execute(localSync);
				fillList();
			}
		});
		it.execute(new SyncObj());
		
	} else if (id == R.id.btn_inv_remove) {
		LifeInvite selInvRem = new LifeInvite();
		selInvRem = life.getSelected(dataAdapter.life.invites);
		life.setRemoved(this, selInvRem.invites);
	} else if (id == R.id.btn_inv_block) {
	} else if (id == R.id.btn_inv_confirm2) {
		System.out.println("TESTING CONFIRM");

		LifeInvite selInvCon2 = new LifeInvite();
		selInvCon2 = life.getSelected(dataAdapter.life.invites);
		life.setConfirmed(this, selInvCon2.invites);
	} else if (id == R.id.btn_inv_remove2) {
		LifeInvite selInvRem2 = new LifeInvite();
		selInvRem2 = life.getSelected(dataAdapter.life.invites);
		life.setRemoved(this, selInvRem2.invites);
	} else if (id == R.id.btn_inv_block2) {
	}
	dialog.dismiss();

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
		//addAllChecked();
	} else if (itemId == R.id.menu_edit) {
		setEditMode();
		fillList();
	//	checkPos();
	} else if (itemId == R.id.menu_remove) {
	//	removeAllChecked();
	} else if (itemId == R.id.menu_invite) {
	//	lifeInv = new LifeInvite();
	//	lifeInv.convertContactsToInvite(ctx, phoneGrp.getCleanGroupData().get(0).getAll());
	//	flipSpecific(4);
	}
    return true;
    
}

private void setEditMode() {
	if(EDITMODE){
		EDITMODE=false;
	}else{
		EDITMODE=true;
	}
}
}