package com.hutchgrant.phone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.Contact;
import com.hutchgrant.Elements.Group;
import com.hutchgrant.Elements.Invite;
import com.hutchgrant.Elements.LifeAlbum;
import com.hutchgrant.Elements.LifeGroup;
import com.hutchgrant.Elements.LifeInvite;
import com.hutchgrant.Elements.LifeMessage;
import com.hutchgrant.Elements.Message;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.Elements.User;
import com.hutchgrant.Elements.Sync.SyncMsgObj;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.app.MessageAccess;
import com.hutchgrant.app.ProfileAccess;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.coconut.Communicate;
import com.hutchgrant.coconut.R;
import com.hutchgrant.contacts.ContactDetails;
import com.hutchgrant.gallery.ImageGridActivity;
import com.hutchgrant.imagesend.ImageSend.SelectAlbumListener;
import com.hutchgrant.tasks.AlbumTask;
import com.hutchgrant.tasks.AlbumTask.AlbListener;
import com.hutchgrant.tasks.ImageTask;
import com.hutchgrant.tasks.ImageTask.DataListener;
import com.hutchgrant.tasks.MessageTask;
import com.hutchgrant.tasks.MessageTask.MTDataListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class smsMessage extends Activity implements OnClickListener,
		OnScrollListener {

	private static final int DIALOG_IMGMODE = 70;
	private static final int DIALOG_ALBMODE = 80;
	TextView name, phone;
	EditText msg;
	Button send;
	ListView msgList;
	ItemAdapter msgAdapter;
	String selContactName = "", selContactID = "", selGrpID = "",
			selContactImg = "", selectedAlbumID = "";

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions options;
	MessageTask mt;
	LifeMessage life;
	LifeMessage sentImgLife; //
	Album msgAlb; // / alb of sent/received images
	LifeAlbum msgLfAlb; // / alb list of sent/received albums
	LifeAlbum lifeAlb; // alb list for spinner
	MessageAccess msgAccess;
	SyncMsgObj msgTally;
	Dialog dialog;
	TunaApp app;

	Spinner album_spin;
	String[] ImageUrls, sentImgUrls, sentAlbUrls;
	String[] spinnerArray;
	boolean ContactSelected = true;
	int imgCounter = 0,	albCounter = 0;
	private boolean SYNCABLE = false, CACHEABLE = false;
	private int MSGCACHE = 0;
	Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_create);
		app = (TunaApp) getApplication();
		MessageAccess.initAccess(this);
		msgAccess = MessageAccess.getInstance();
		ctx = this;
		Bundle extras = getIntent().getExtras();
		selGrpID = extras.getString("selContactGrpID");
		selContactID = extras.getString("selContactID");
		selContactName = extras.getString("selContactName");
		selContactImg = extras.getString("selContactImg");

		if (selContactID.equals("0")) {
			ContactSelected = false;
		}

		name = (TextView) findViewById(R.id.txt_name);
		// phone = (TextView)findViewById(R.id.txt_phone);
		msg = (EditText) findViewById(R.id.edit_msg);
		msgList = (ListView) findViewById(R.id.msgList);

		name.setText("Message To: " + selContactName);
		if (ContactSelected) {
			// phone.setText(selContactID);
		} else {
			// phone.setText(selGrpID);
		}
		send = (Button) findViewById(R.id.btn_sms);
		send.setOnClickListener(this);

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(20))
				.build();

		fillMsgList();

	}

	private void fillMsgList() {
		life = new LifeMessage();
		msgLfAlb = new LifeAlbum(ctx);
		if (ContactSelected) {
			life = msgAccess.getMessages(true, selContactID, true);
		} else {
			life = msgAccess.getMessages(true, selGrpID, false);
		}
		MSGCACHE = this.life.messages.size();
		ImageUrls = new String[this.life.messages.size()];
		ImageUrls = getSenderImage();

		getLifeImgMsgs();
		msgAlb.fillURIList();

		msgAdapter = new ItemAdapter(life);
		msgAdapter.fillImages(ImageUrls);
		msgAdapter.fillImageMsg(sentImgUrls, this.life.messages.size());
		msgAdapter.fillAlbMsg(sentAlbUrls, this.life.messages.size());
		msgList.setAdapter(msgAdapter);
		msgList.setOnScrollListener(this);

		// setTally();
	}

	private void setTally() {
		msgTally = new SyncMsgObj();
		msgTally = app.getLocalMsgTally();

		if (msgTally.syncMsgCached < (msgTally.syncMsgRecAmount + msgTally.syncMsgSntAmount)) {
			SYNCABLE = true;
		}
		if (MSGCACHE < msgTally.syncMsgCached) {
			CACHEABLE = true;
		}

	}

	private void getLifeImgMsgs() {
		msgAlb = new Album(this);
		msgLfAlb = new LifeAlbum(this);
		albCounter = 0;
		imgCounter = 0;
		Photo photo;
		Album album;
		ImageTask it;
		AlbumTask at;

		sentAlbUrls = new String[this.life.messages.size()];
		sentImgUrls = new String[this.life.messages.size()];

		// / check messages for images, download if needed
		for (int i = 0; i < this.life.messages.size(); i++) {		
			if (this.life.messages.get(i).Type.equals("IMG")) {				/// Get Images
				photo = new Photo();
				photo = app.readImage(this.life.messages.get(i).Content);
				if (!photo.ID.equals("")) {
					addMsgImg(photo, i);
				} else {													/// Download if needed
					it = new ImageTask(this);
					it.setImgPos(i);
					it.setListener(new DataListener() {
						@Override
						public void setAlb(Photo photo, int pos) {
							addMsgImg(photo, pos);							/// Add to adapter
						}
					});
					it.execute(this.life.messages.get(i).Content);
				}
			} else if (this.life.messages.get(i).Type.equals("ALB")) { 	/// Get Albums
				album = new Album(this);
				album = app.readAlbum(this.life.messages.get(i).Content);
				if (!album.ID.equals("")) {
					System.out.println("reading album images");
					album.addPhotos(app.readImages(album.ID, true));
					System.out.println("adding album to message list");
					addMsgAlb(album, i);
				}else {														/// Download if needed
					at = new AlbumTask(this);
					at.setSyncType(2);
					at.setSyncID(this.life.messages.get(i).Content, i);
					at.setListener(new AlbListener(){

						@Override
						public void setAlb(Album alb, int pos) {			
							addMsgAlb(alb, pos);							/// Add to adapter
						}
						
					});
					at.execute(new SyncObj());
				}
			}
		}
	}

	private void addMsgAlb(Album album, int i) {
		msgLfAlb.addAlbum(album);
		sentAlbUrls[i] = "file:///mnt/sdcard/Pictures/Tuna/" + album.photos.get(0).Name;
		albCounter++;
	}

	public void addMsgImg(Photo photo, int position) {
		msgAlb.addPhoto(photo);
		sentImgUrls[position] = "file:///mnt/sdcard/Pictures/Tuna/"
				+ msgAlb.photos.get(imgCounter).Name;
		imgCounter++;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_sms) {
			life.writeNew(this, "MSG", msg.getText().toString(), selContactID,
					selGrpID);
			mt = new MessageTask(this);
			mt.setSyncType(1, true);
			mt.setListener(new MTDataListener() {
				@Override
				public void refresh() {
					// fillMsgList();

				}
			});
			mt.execute(new SyncObj());
			// fillMsgList();

		}
	}

	private String[] getSenderImage() {
		String AuthorID = "", Author = "";
		String RecipID = "", Recip = "";
		ImageUrls = new String[this.life.messages.size()];

		if (life.messages.size() > 0) {
			AuthorID = this.life.messages.get(0).AuthorID;
			RecipID = this.life.messages.get(0).ReceiverID;

			// / get Profile ID
			TunaApp app = (TunaApp) ((Activity) this).getApplication();
			User profile = app.getProfile();

			if (profile.userGID.equals(AuthorID)) {
				Author = profile.userGID;
			} else if (profile.userGID.equals(RecipID)) {
				Author = RecipID;
			}

			// / search Friends for recipient
			LifeGroup lifeGrp = new LifeGroup(this);
			lifeGrp.getGroupData();
			Group group = new Group(this);
			group = lifeGrp.getSpecifcGroupByName("Friends");

			if (group.ContainsGoogleID(RecipID)) {
				Recip = RecipID;
			} else if (group.ContainsGoogleID(AuthorID)) {
				Recip = AuthorID;
			}

			Contact cRecip = new Contact();
			cRecip = group.getContactByGoogleID(Recip);

			for (int i = 0; i < this.life.messages.size(); i++) {
				if (this.life.messages.get(i).AuthorID.equals(profile.userGID)) {
					ImageUrls[i] = profile.userImgUrl;
				} else {
					ImageUrls[i] = cRecip.getProfileImg();
				}
			}
		}
		return ImageUrls;
	}

	public void showImageDialog(int i) {
		switch (i) {
		case DIALOG_IMGMODE:
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.image_message_dialog);
			dialog.setTitle("Send Image from Album");
			dialog.setCanceledOnTouchOutside(true);

			album_spin = (Spinner) dialog.findViewById(R.id.album_spin_msg);
			lifeAlb = new LifeAlbum(this);

			setAlbumSpinner();

			Button buttonSubmit = (Button) dialog
					.findViewById(R.id.btn_submit_img);
			buttonSubmit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.setClass(smsMessage.this, ImageGridActivity.class);
					i.putExtra("selAlbumID", selectedAlbumID);
					i.putExtra("selGrpID", selGrpID);
					i.putExtra("selContactID", selContactID);
					i.putExtra("selContactName", selContactName);
					startActivityForResult(i, 99);
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		case DIALOG_ALBMODE:
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.image_message_dialog);
			dialog.setTitle("Send Image from Album");
			dialog.setCanceledOnTouchOutside(true);

			album_spin = (Spinner) dialog.findViewById(R.id.album_spin_msg);
			lifeAlb = new LifeAlbum(this);
			setAlbumSpinner();

			Button buttonSubmit2 = (Button) dialog
					.findViewById(R.id.btn_submit_img);
			buttonSubmit2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					LifeMessage lifeM = new LifeMessage();
					lifeM.writeNew(ctx, "ALB", selectedAlbumID, selContactID,
							selGrpID);
					fillMsgList();

					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (99):
			if (resultCode == Activity.RESULT_OK) {
				fillMsgList();
			}
			break;
		case (101):
			if (resultCode == Activity.RESULT_OK) {
				fillMsgList();
			}
		}
	}

	private void setAlbumSpinner() {
		lifeAlb.fillAlbums();
		spinnerArray = new String[lifeAlb.albAmount + 2];
		spinnerArray = lifeAlb.fillSpinner();
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, spinnerArray);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		album_spin.setAdapter(spinnerArrayAdapter);
		album_spin.setOnItemSelectedListener(new SelectAlbumListener());
	}

	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		private ImageLoadingListener animateSecondListener = new AnimateFirstDisplayListener();

		public LifeMessage life = new LifeMessage();
		Group grp;
		int listType = 0;
		String imageUrls[];
		String[] msgUrls;
		String[] msgAlb;
		String[] msgImgs;
		int imgCounter = 0;
		int albCounter = 0;
		int msgImgMax = 0;
		int msgAlbMax = 0;

		private class ViewHolder {
			public TextView text;
			public ImageView image;
			public ImageView sentImg;
		}

		public ItemAdapter() {
			this.imageUrls = new String[1];
			this.life = new LifeMessage();
		}

		public void fillImageMsg(String[] sentImgUrls, int size) {
			this.msgUrls = new String[size];
			this.msgUrls = sentImgUrls;
			msgImgMax = size;
			imgCounter = 0;
		}

		public void fillAlbMsg(String[] sentAlbUrls, int size) {
			this.msgAlb = new String[size];
			this.msgAlb = sentAlbUrls;
			msgAlbMax = size;
			albCounter = 0;
		}

		public void fillImages(String[] imageUrls2) {
			this.imageUrls = new String[life.messages.size()];
			this.imageUrls = imageUrls2;
		}

		public ListAdapter getInstance() {
			return this;
		}

		public ItemAdapter(LifeMessage arr) {
			this.life = new LifeMessage();
			this.life = arr;
			this.msgImgs = new String[this.life.messages.size()];
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			boolean contactBox = false;

			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_list_image,
						parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.image = (ImageView) view.findViewById(R.id.image);
				holder.sentImg = (ImageView) view.findViewById(R.id.sentImg);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			view.setBackgroundResource(R.drawable.list_selector);
			if (position % 2 == 0) {
				// view.setBackgroundResource(R.drawable.listview_selector_even);
			} else {
				// view.setBackgroundResource(R.drawable.listview_selector_odd);
			}

			Message msg = this.life.getMsgByPos(position);

			if (msg.Type.equals("IMG")) {
				holder.sentImg.setVisibility(View.VISIBLE);
				holder.text.setText("");
				imageLoader.displayImage(msgUrls[position], holder.sentImg,
						options);

			} else if (msg.Type.equals("ALB")) {
				holder.sentImg.setVisibility(View.VISIBLE);
				holder.text.setText("");
				imageLoader.displayImage(msgAlb[position], holder.sentImg,
						options);
			}

			else {
				holder.text.setText(msg.Content);
				holder.sentImg.setVisibility(View.GONE);
			}

			imageLoader.displayImage(imageUrls[position], holder.image,
					options, animateSecondListener);

			return view;
		}
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
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
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.option, menu);

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.add_image) {
			showImageDialog(DIALOG_IMGMODE);
			return (true);
		} else if (id == R.id.add_album) {
			showImageDialog(DIALOG_ALBMODE);
			return (true);
		}

		return (super.onOptionsItemSelected(item));
	}

	public class SelectAlbumListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (pos > 0) {
				selectedAlbumID = lifeAlb.getAlbum(pos - 2).getID();
			}
		}

		@Override
		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem == 0) {
			if (CACHEABLE) {

			} else {
				if (SYNCABLE) {
					mt = new MessageTask(this);
					mt.setSyncType(2, true);
					mt.setListener(new MTDataListener() {

						@Override
						public void refresh() {
							fillMsgList();
						}

					});
				}
			}
			System.out.println("WE'VE REACHED THE TOP OF THE LIST");
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}
