/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hutchgrant.networks.gplus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hutchgrant.Elements.Group;
import com.hutchgrant.Elements.User;
import com.hutchgrant.Elements.Sync.gPlusSyncData;
import com.hutchgrant.coconut.Endpoints;
/**
 * Provides static utility methods to help make authenticated requests.
 */
public class AuthUtil {

    private static final String TAG = AuthUtil.class.getSimpleName();

    public static final String[] SCOPES = {
            Scopes.PLUS_LOGIN, Scopes.PLUS_PROFILE
    };

    public static final String[] VISIBLE_ACTIVITIES = {
            "http://schemas.google.com/AddActivity", "http://schemas.google.com/ReviewActivity"
    };

    static final String SCOPE_STRING = "oauth2:" + TextUtils.join(" ", SCOPES);

    private static final String ACCESS_TOKEN_JSON = "{ \"access_token\":\"%s\"}";

	private static final int AUTH_REQUEST_CODE = 0;

    private static String sAccessToken = null;

    private static String sCookies = null;

    public static void setAuthHeaders(HttpURLConnection connection) {
        Log.d(TAG, "Authorization: OAuth " + sAccessToken);
        connection.setRequestProperty("Authorization", "OAuth " + sAccessToken);
        connection.setRequestProperty("Cookie", sCookies);
    }
    static class AObj {
    	public String access_token = "";
    	public  String Number = "";
    	public  String Email = "";
    	

    }
    public static User authenticate(Context ctx, String account, String token) {
        HttpURLConnection urlConnection = null;
        OutputStream outStream = null;
        String response = null;
        int statusCode = 0;
        String number = "";

        System.out.println("authenticating this account "+account);
        try {
            URL url = new URL(Endpoints.API_CONNECT);

            Log.v(TAG, "Authenticating at [" + url + "] with: " + token);
            
            AObj ao = new AObj();
            ao.access_token = token;
        	ao.Number = getNumber(ctx);
            ao.Email = account;
			Gson gson2 = new Gson();
            
            byte[] postBody = String.format(gson2.toJson(ao)).getBytes();

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("User-Agent", Endpoints.USER_AGENT);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            
            outStream = urlConnection.getOutputStream();
            outStream.write(postBody);

            statusCode = urlConnection.getResponseCode();
            Log.v("RESPONSECODE", "code = "+statusCode);
            Gson gson = new GsonBuilder().create(); 
            if (statusCode == 200) {
            	InputStream responseStream = urlConnection.getInputStream();
                byte[] responseBytes = HttpUtils.getContent(responseStream).toByteArray();
                response = new String(responseBytes, "UTF-8");
                Log.v(TAG, "Authenticated: " + response);
                User result = gson.fromJson(response, User.class);
                return result;
            /*
                User result = null;
                String[] cookies = urlConnection.getHeaderField("set-cookie").split(";");
                for (String cookie : cookies) {
                    if (cookie.trim().startsWith("JSESSIONID")) {
                        InputStream responseStream = urlConnection.getInputStream();
                        byte[] responseBytes = HttpUtils.getContent(responseStream).toByteArray();
                        response = new String(responseBytes, "UTF-8");
                        Log.v("RESPONSE", "response = "+response);
                        if (!TextUtils.isEmpty(response)) {
                 //           result = new Gson().fromJson(response, User.class);
                        }
                        
                        sCookies = cookie;
                        break;
                    }
                } */
                

              //  return result;
            } else { 
                response = HttpUtils.getErrorResponse(urlConnection);
                
                Log.w(TAG, "HTTP Status (" + statusCode + ") while authenticating: " + response);
                GoogleAuthUtil.invalidateToken(ctx, sAccessToken);
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

        return null;
    }
    
    
    private static String getNumber(Context ctx) {
    	TelephonyManager tMgr =(TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
		return tMgr.getLine1Number();
	}

	public static void send(Context ctx, String account) {
        HttpURLConnection urlConnection = null;
        OutputStream outStream = null;
        String response = null;
        int statusCode = 0;
        System.out.println("authenticating this account "+account);
        try {
            URL url = new URL(Endpoints.API_CONNECT);

            sAccessToken = GoogleAuthUtil.getToken(ctx, account, AuthUtil.SCOPE_STRING);
            
         //   Log.v(TAG, "Authenticating at [" + url + "] with: " + sAccessToken);
            
            byte[] postBody = String.format(ACCESS_TOKEN_JSON, sAccessToken).getBytes();

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("User-Agent", Endpoints.USER_AGENT);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            outStream = urlConnection.getOutputStream();
            outStream.write(postBody);

            statusCode = urlConnection.getResponseCode();
       //     Log.v("RESPONSECODE", "code = "+statusCode);
            if (statusCode == 200) {
            	InputStream responseStream = urlConnection.getInputStream();
                byte[] responseBytes = HttpUtils.getContent(responseStream).toByteArray();
                response = new String(responseBytes, "UTF-8");
        //        Log.v(TAG, "Authenticated: " + response);
            } else { 
                response = HttpUtils.getErrorResponse(urlConnection);
                
                Log.w(TAG, "HTTP Status (" + statusCode + ") while authenticating: " + response);
                GoogleAuthUtil.invalidateToken(ctx, sAccessToken);
            } 
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (GoogleAuthException e) {
            GoogleAuthUtil.invalidateToken(ctx, sAccessToken);
            Log.e(TAG, "Response was: " + response);
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
    
    public static Group receiveGFriends(Context ctx, String account){
		HttpURLConnection urlConnection = null;
	    OutputStream outStream = null;
	    String response = null;
	    int statusCode = 0;
		 
	    try {
	    	URL url = new URL(Endpoints.GPLUS_FRIENDS);
	    	
			
			 String GToken = GoogleAuthUtil.getToken(ctx, account, AuthUtil.SCOPE_STRING);
	    
	     //   System.out.println(GToken);
	        byte[] postBody = String.format(ACCESS_TOKEN_JSON, GToken).getBytes();
	        
	   	urlConnection = Endpoints.setHeader(url, ctx);
	   	outStream = urlConnection.getOutputStream();
	        outStream.write(postBody);
	
	        statusCode = urlConnection.getResponseCode();
	        Log.v("RESPONSECODE", "code = "+statusCode);
	        if (statusCode == 200) {
	        	InputStream responseStream = urlConnection.getInputStream();
	            byte[] responseBytes = HttpUtils.getContent(responseStream).toByteArray();
	            response = new String(responseBytes, "UTF-8");
	      //      Log.v(TAG, "Tuna Server Response: " + response);
	           System.out.println(response);
	            if(response != null){
	            	/// convert google circle members to contacts, place in group
	         gPlusSyncData gcontacts = new Gson().fromJson(response, gPlusSyncData.class);
	                System.out.println(" the first gplus contact is named "+gcontacts.items.get(0).displayName);
	                Group googleCircle = new Group(ctx);
	                googleCircle = gcontacts.convertToGroup(ctx);
	            	/// return groups
	                return googleCircle;
	            }	           
	            
	        } else { 
	          //  response = HttpUtils.getErrorResponse(urlConnection);
	        } 
	    } catch (MalformedURLException e) {
	        Log.e(TAG, e.getMessage(), e);
	    } catch (IOException e) {
	        System.out.println("WIFI DISCONNECTED?");
	        Log.e(TAG, e.getMessage(), e);
	    } catch (GoogleAuthException e) {
            GoogleAuthUtil.invalidateToken(ctx, sAccessToken);
            Log.e(TAG, "Response was: " + response);
	    }finally {
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
		return null;
	 }
    
    public static void invalidateSession() {
        sAccessToken = null;
        sCookies = null;
    }
    
    
}
