package com.hutchgrant.imagesend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.networks.gplus.HttpUtils;

import android.os.AsyncTask;
import android.util.Log;

public class getUploadUrl extends AsyncTask<String, Void, String>{

	public String UStoken = "";
	public Photo mPhoto;
	private String TAG;
	
	public getUploadUrl(Photo photo) {
		this.mPhoto = photo;
	}

	@Override
	protected String doInBackground(String... params) {
		for (String UStoken : params) {
	       HttpURLConnection urlConnection = null;
	        OutputStream outStream = null;
	        String response = null;
	        int statusCode = 0;
	        try {
	            URL url = new URL(Endpoints.PHOTO_UPLOAD);
	            
	            Gson gson = new Gson();
	            String jsonPhoto = gson.toJson(this.mPhoto);
	            byte[] postBody = String.format(jsonPhoto).getBytes();
	            
	            urlConnection = (HttpURLConnection) url.openConnection();
	            urlConnection.setRequestMethod("POST");
	            urlConnection.setAllowUserInteraction(false);
	            urlConnection.setDoOutput(true);
	            urlConnection.setRequestProperty("User-Agent", Endpoints.USER_AGENT);
	            urlConnection.setRequestProperty("Content-Type", "application/json");
	            urlConnection.setRequestProperty("UStoken", UStoken);
	            outStream = urlConnection.getOutputStream();
	            outStream.write(postBody);

	            statusCode = urlConnection.getResponseCode();
	            Log.v("RESPONSECODE", "code = "+statusCode);
	            if (statusCode == 200) {
	            	InputStream responseStream = urlConnection.getInputStream();
	                byte[] responseBytes = HttpUtils.getContent(responseStream).toByteArray();
	                response = new String(responseBytes, "UTF-8");
	                Log.v(TAG, "Upload Url: " + response);
	                System.out.println(response);
	                return response;
	            } else { 
	                response = HttpUtils.getErrorResponse(urlConnection);
	                return null;
	            } 
	        } catch (MalformedURLException e) {
	            Log.e(TAG, e.getMessage(), e);
	        } catch (IOException e) {
	            Log.e(TAG, e.getMessage(), e);
	        } finally {
	            if (urlConnection != null) {
	                urlConnection.disconnect();
	            }
	            
	            if (outStream != null) {
	                try {
	                    outStream.close();
	                } catch (IOException e) {
	                    Log.e(TAG, e.getMessage(), e);
	                }
	            }
	        }
		
		}
		return null;
	}

}
