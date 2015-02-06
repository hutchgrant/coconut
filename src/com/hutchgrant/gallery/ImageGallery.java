package com.hutchgrant.gallery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.coconut.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ImageGallery extends BaseActivity implements OnClickListener{
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	ContentResolver cr;
	Cursor cur2;
	ArrayList<Photo> ListPhotos;
	int photoListCount = 0;
	ListView viewList;
	DisplayImageOptions options;
	String[] imageUrls;
	String URILOCATE = "com.hutchgrant.gallery.ImageGallery.uriList";
	Button b_sync, b_edit, b_remove;
	LinearLayout editLayout;
	boolean EDITMODE = false;
	TunaApp app;
	Album defAlbum;
	String selAlbumID = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.img_gallery);
		Bundle extras = getIntent().getExtras();
		
		selAlbumID = extras.getString("selAlbumID");
		
		app = (TunaApp) this.getApplication();
		defAlbum = new Album(this);
		defAlbum.getPhotos(selAlbumID, true);
		defAlbum.fillURIList();
		
		editLayout = (LinearLayout)this.findViewById(R.id.edit_layout);
		b_sync = (Button)this.findViewById(R.id.sync_receive);
		b_edit = (Button)this.findViewById(R.id.btn_edit);
		b_remove = (Button)this.findViewById(R.id.btn_remove);
		b_edit.setOnClickListener(this);
		b_sync.setOnClickListener(this);
		b_remove.setOnClickListener(this);
		
		Bundle bundle = getIntent().getExtras();
		imageUrls = bundle.getStringArray(URILOCATE);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
		viewList = (ListView) this.findViewById(R.id.imgListView);
		viewList.setAdapter(new ItemAdapter());
		viewList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImagePagerActivity(position);
			}
		});
	}
	@Override
	public void onBackPressed() {
		AnimateFirstDisplayListener.displayedImages.clear();
		super.onBackPressed();
	}

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra("selected_album_id", defAlbum.getID());
		intent.putExtra("selected_image_pos", position);
		startActivity(intent);
	}
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_edit){
		//	fillMyList();
			if(EDITMODE){
				EDITMODE = false;
				editLayout.setVisibility(View.GONE);
			}else {
				EDITMODE = true;
				editLayout.setVisibility(View.VISIBLE);
				
			}
			redraw();
		//	displayMyList();
		}else if(v.getId() == R.id.sync_receive){
		}else if(v.getId() == R.id.btn_remove){
			defAlbum.removeSelected();
		//	displayMyList();
		}
		
	}
	
	public void redraw(){
		defAlbum = new Album(this);
		defAlbum.getPhotos(selAlbumID, true);
		defAlbum.fillURIList();
		viewList.setAdapter(new ItemAdapter());
	}
	
	public void displayMyList(){
		defAlbum.getDefaultPhotos();
		defAlbum.fillURIList();
		viewList.setAdapter(new ItemAdapter());
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
			return defAlbum.getUriList().length;
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
				holder.cb.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CheckBox check = (CheckBox) v;
						defAlbum.selectPhoto(position, check.isChecked());
					}});
			}else{
				holder.cb.setVisibility(View.GONE);
			}
			/// reminder set to Image Name
			holder.text.setText(defAlbum.getPhoto(position).Title);

			imageLoader.displayImage(defAlbum.getUriList()[position], holder.image, options, animateFirstListener);

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
