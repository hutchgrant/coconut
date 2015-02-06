package com.hutchgrant.camera;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CamPreview extends SurfaceView implements SurfaceHolder.Callback{
	   private SurfaceHolder mSurfaceHolder;
	    private Camera mCamera;
	 
	    public CamPreview(Context context){
	    	super(context);
	    }
	    
	    //Constructor that obtains context and camera
	 ///   @SuppressWarnings("deprecation")
		public CamPreview(Context context, Camera camera) {
	        super(context);
	        this.mCamera = camera;
	         
	        this.mSurfaceHolder = this.getHolder();
	        this.mSurfaceHolder.addCallback(this); // we get notified when underlying surface is created and destroyed
	        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //this is a deprecated method, is not requierd after 3.0
	    }
	 
	    @Override
	    public void surfaceCreated(SurfaceHolder surfaceHolder) {
	        try {
	            mCamera.setPreviewDisplay(surfaceHolder);
	            
	         // get Camera parameters
	            Camera.Parameters params = mCamera.getParameters();
	            List<String> focusModes = params.getSupportedFocusModes();
	            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
	                // set the focus mode
		            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		            // set Camera parameters
		            mCamera.setParameters(params);
	            }
	        //    mCamera.setDisplayOrientation(0);
	            mCamera.startPreview();
	        } catch (IOException e) {
	          // left blank for now
	        }
	 
	    }
	     
	    @Override
	    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
	    	surfaceHolder.removeCallback(this);
	        mCamera.stopPreview();
	        mCamera.release();
	    }
	 
	    @Override
	    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
	            int width, int height) {
	        // start preview with new settings
	        try {
	            mCamera.setPreviewDisplay(surfaceHolder);
		         // get Camera parameters
	            Camera.Parameters params = mCamera.getParameters();
	            List<String> focusModes = params.getSupportedFocusModes();
	            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
	                // set the focus mode
		            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		            // set Camera parameters
		            mCamera.setParameters(params);
	            }
	            mCamera.setDisplayOrientation(0);
	            mCamera.startPreview();
	        } catch (Exception e) {
	            // intentionally left blank for a test
	        }
	    }
	     
	}