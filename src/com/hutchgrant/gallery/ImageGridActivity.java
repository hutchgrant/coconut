/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.hutchgrant.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.LifeMessage;
import com.hutchgrant.Elements.Message;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.coconut.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageGridActivity extends AbsListViewBaseActivity {

	String[] imageUrls;

	Album defAlbum;
	DisplayImageOptions options;
	String selAlbumID = "", selGrpID ="", selContactID = "", selContactName = "";
	TunaApp app;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_grid);

		Bundle extras = getIntent().getExtras();
		selAlbumID = extras.getString("selAlbumID");
		selGrpID = extras.getString("selGrpID");
		selContactID = extras.getString("selContactID");
		selContactName = extras.getString("selContactName");
		
		app = (TunaApp) this.getApplication();
		defAlbum = new Album(this);
		defAlbum.getPhotos(selAlbumID, true);
		defAlbum.fillURIList();
		
		options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.ic_launcher)
			.showImageForEmptyUri(R.drawable.ic_launcher)
			.showImageOnFail(R.drawable.ic_launcher)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();

		listView = (GridView) findViewById(R.id.btn1);
		((GridView) listView).setAdapter(new ImageAdapter());
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImagePagerActivity(position);
			}
		});
	}

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra("selected_album_id", defAlbum.getID());
		intent.putExtra("selected_image_pos", position);
		startActivity(intent);
	}

	public class ImageAdapter extends BaseAdapter {
		
	///	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public ImageView image;
			public CheckBox cb;
		}

		@Override
		public int getCount() {
			return defAlbum.getUriList().length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) view.findViewById(R.id.image);
				holder.cb = (CheckBox)view.findViewById(R.id.grid_check);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.cb.setText(defAlbum.getPhoto(position).Title);
			holder.cb.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox check = (CheckBox) v;
					defAlbum.selectPhoto(position, check.isChecked());
				}});
			imageLoader.displayImage(defAlbum.getUriList()[position], holder.image, options);

			return view;
		}
	}
	
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    new MenuInflater(this).inflate(R.menu.img_options, menu);
	    
	    return(super.onCreateOptionsMenu(menu));
	  }

	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    Intent resultIntent;
	    int id = item.getItemId();
	    if(id == R.id.Send){
	    	  Message msg;
	    	  LifeMessage lifeMsg = new LifeMessage();
	    	  Album alb = new Album(this);

	    	  alb = defAlbum.getSelectedPhotos();
	    	  
	    	  for(int i =0; i< alb.photos.size(); i++){
	    		  msg = new Message();
		    	  lifeMsg.writeNew(this, "IMG", alb.photos.get(i).ID, selContactID, selGrpID);
	    	  }
	    	  resultIntent = new Intent();
	    	  resultIntent.putExtra("selContactID", selContactID);
	    	  resultIntent.putExtra("selContactGrpID", selGrpID);
	    	  resultIntent.putExtra("selContactName", selContactName);
	    	  setResult(Activity.RESULT_OK, resultIntent);
	    	  finish();
	        return(true);
	    }
	    
	    return(super.onOptionsItemSelected(item));
	  }
}