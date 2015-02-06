package com.hutchgrant.imagesend;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.hutchgrant.Elements.Photo;
import com.hutchgrant.app.ImageAccess;
import com.hutchgrant.coconut.Endpoints;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
 
//Uploader class
   public class HttpUploader extends AsyncTask<String, Void, Photo> {
	   public Photo unsentPhoto;
	   public String UStoken = "";
		ImageAccess imgAccess;
		DataListener listener;
		
        public HttpUploader(Photo unsentImg, String ustoken, Context context) {
		this.unsentPhoto = new Photo();
        this.unsentPhoto = unsentImg;
		this.UStoken = ustoken;
		ImageAccess.initAccess(context);
		imgAccess = ImageAccess.getInstance();
	}
		@Override
		protected Photo doInBackground(String ... path) {
             
            String outPut = "";
             
            for (String sdPath : path) {
            	BitmapFactory.Options options = new BitmapFactory.Options();
            	options.inSampleSize = 5;   ///  note to self, adjust for better quality(bigger) images,  lower the better
                Bitmap bitmapOrg = BitmapFactory.decodeFile(sdPath,options);
                ByteArrayOutputStream bao = new ByteArrayOutputStream(bitmapOrg.getWidth() * bitmapOrg.getHeight());
                bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                byte[] ba = bao.toByteArray();
                
                String ba1 = Base64.encodeToString(ba, 0);
                 
                System.out.println("uploading image now ---"); 
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("image", ba1));
                 
                String newUploadUrl;
                newUploadUrl = Endpoints.PHOTO_UPLOAD;                
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(newUploadUrl);
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    httppost.setHeader("UStoken", this.UStoken);
                    httppost.setHeader("uploadID", this.unsentPhoto.UploadToken);
                    HttpResponse response = httpclient.execute(httppost);
                   HttpEntity entity = response.getEntity();                
               
                   outPut = EntityUtils.toString(entity);
               	String im_path = "";
                	
    				im_path = Environment.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_PICTURES)+"/Tuna/";
    				
    				File from = new File(sdPath);
    			    File to = new File(im_path,outPut);
    			     if(from.exists()){
    			        from.renameTo(to);
    			        this.unsentPhoto.Name = outPut;
    			        this.unsentPhoto.Synced = true;
    			     }
                     bitmapOrg.recycle();
                    Log.e("log_tag ******", "good connection");
                     
                } catch (Exception e) {
                    Log.e("log_tag ******", "Error in http connection " + e.toString());
                }
            }
            return this.unsentPhoto;
        }    
    
		@Override
		protected void onPostExecute(Photo unsavedImg){
			
			listener.saveImage(unsavedImg);
	    //    imgAccess.saveLocalImage(unsavedImg, true);
			System.out.println("POST EXECUTE");
			System.out.println(unsavedImg.toString());


		}
		
		public void setListener(DataListener data){
			this.listener = data;
		}
		
		public static interface DataListener{
			public void saveImage(Photo unsentPhoto);
		}
   }
