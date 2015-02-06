package com.hutchgrant.contacts;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.hutchgrant.Elements.Invite;
import com.hutchgrant.Elements.LifeInvite;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.coconut.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LifeInviteList extends Activity implements OnItemClickListener{
	  protected ImageLoader imageLoader = ImageLoader.getInstance();

	LifeInvite life;
	
	ListView listView;
	ItemAdapter dataAdapter;
	DisplayImageOptions options;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_contact_list);
		listView = (ListView) this.findViewById(R.id.list_contacts);
		
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
	public void getInvites(){
		TunaApp app = (TunaApp) ((Activity) this).getApplication();
		life = new LifeInvite();
		life = app.getInvites(true, "sent");
	}
	
	public void fillList(){
		getInvites();
		dataAdapter = new ItemAdapter(this, life);
		listView.setAdapter(dataAdapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
	
	class ItemAdapter extends BaseAdapter {
		
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		LifeInvite life;
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
				Invite inv = this.life.invites.get(position);
				holder.text.setText(inv.RecipientName);
			//	holder.cb.setChecked(inv.);
				holder.cb.setTag(inv);
				holder.cb.setVisibility(View.VISIBLE);
			
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
