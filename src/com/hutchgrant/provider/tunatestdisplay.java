package com.hutchgrant.provider;


import com.hutchgrant.coconut.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class tunatestdisplay extends Activity {
    ListView listView;
    ImageView imageview;
    ContentResolver contentResolver;
    Cursor cur, cursor;
    SimpleCursorAdapter adapter;
	TextView texviw;
    
    @Override
	public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.success);
    	
    	
        listView = (ListView)this.findViewById(R.id.list_profile);
        imageview = (ImageView)this.findViewById(R.id.prof_img);
        texviw = (TextView)this.findViewById(R.id.name_tag);
        contentResolver = this.getContentResolver();
        
    	loadContent();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	if(cur != null) cur.close();
    }
    
	@SuppressWarnings("deprecation")
	private void loadContent() {
        // WARNING: performance flag.  Prod code should use a CursorLoader or do this off the UI thread.
        cur = this.getContentResolver().query(tunadatamodel.TunaProfile.CONTENT_URI, null, null, null, null);
    	
    	// WARNING: deprecation
    	adapter = new SimpleCursorAdapter(
    			this,
    			R.layout.list_item_layout, 
    			cur,
    			new String[]{
    				tunadatamodel.TunaProfile.Cols.USRID,
    				tunadatamodel.TunaProfile.Cols.USRNAME,
    				tunadatamodel.TunaProfile.Cols.USRLOCATE,
    				tunadatamodel.TunaProfile.Cols.USRPROF,
    				tunadatamodel.TunaProfile.Cols.USRIMGURL,
    				tunadatamodel.TunaProfile.Cols.USRTOKEN
    			},
    			new int[]{
    				R.id.list_profile_userid,
    				R.id.list_profile_username,
    				R.id.list_profile_location,
    				R.id.list_profile_profile,
    				R.id.list_profile_imgurl,
    				R.id.list_profile_token
    			});
    	listView.setAdapter(adapter);
    	
    	///set profile image
        if (cur != null ) {
            if  (cur.moveToFirst()) {
            	byte[] bb = cur.getBlob(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.USRIMG));
            	imageview.setImageBitmap(BitmapFactory.decodeByteArray(bb, 0, bb.length));
            	String text = cur.getString(cur.getColumnIndex(tunadatamodel.TunaProfile.Cols.USRNAME));
            	texviw.setText(text);
            
            }
        }
    }
	@Override
	public void onDestroy(){
		super.onDestroy();
		finish();
	}
}
