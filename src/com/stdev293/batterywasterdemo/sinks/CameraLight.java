/*
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

package com.stdev293.batterywasterdemo.sinks;

import java.io.IOException;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceView;

import com.stdev293.batterywasterdemo.R;

/**
 * 
 * torch feature, uses the camera flash LED.
 * Tested on Galaxy S2 with Android 4.0.3, works fine.
 * Would need tests on other devices.
 */
public class CameraLight extends Sink {
	private boolean isFeatureSupported;
	private boolean isFeatureStarted;
	private Camera mCamera = null;
    private SurfaceView mSurfaceView;
	
	public CameraLight(Activity activityContext) {
		super(activityContext);
		
        mSurfaceView = (SurfaceView) activityContext.findViewById(R.id.surfaceView);
		
		// check that the device supports this feature
		isFeatureSupported = 
				activityContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
				activityContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	}
	
	
	@Override
	public void startImpl() {
		// try to open the default (back-facing) camera
		if (isFeatureSupported) {
			mCamera = null;
			try {
				mCamera = Camera.open();
				mCamera.setPreviewDisplay(mSurfaceView.getHolder());
			} catch (RuntimeException e) {
				// this has happened on device "OTHER" (!!)
				Log.e(this.getClass().getName(),"Cannot open Camera device: "+e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(this.getClass().getName(),"Cannot set preview display: "+e.getMessage());
				e.printStackTrace();
			}
			if (mCamera==null) {
				// can't open camera: do not try anymore (can cause crash)
				isFeatureSupported = false;
			}
		}
		
		// check that this flash mode is supported
		Parameters cameraParams = null;
		if (isFeatureSupported) {
			cameraParams = mCamera.getParameters();
			
			if (cameraParams==null) {
				Log.w(this.getClass().getName(),"Cannot access Camera parameters");
				isFeatureSupported = false;				
			} else if (!cameraParams.getSupportedFlashModes().contains(Parameters.FLASH_MODE_TORCH)) {
				Log.w(this.getClass().getName(),"This device does not seem to support FLASH_MODE_TORCH");
				isFeatureSupported = false;				
			}
		}
		
		// all good, camera object is ready at this stage if the boolean is still true
		if (isFeatureSupported) {
			// turn on the light
			cameraParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(cameraParams);
			try {
				// native crash reported from accessing mCamera.startPreview() on some devices
				mCamera.startPreview();
				isFeatureStarted = true;
			} catch (RuntimeException e) {
				Log.e(this.getClass().getName(),"Failed to turn on the LED flash light: "+e.getMessage());
				e.printStackTrace();
				isFeatureSupported = false;					
			}
		}

		// report status to the user
		if (isFeatureSupported) {
			notifyStatusChange(getContext().getString(R.string.torch_on));			
		} else {			
			notifyStatusChange(getContext().getString(R.string.torch_feature_not_supported));
		}
	}

	@Override
	public void stopImpl() {
		// turn off		
		if (isFeatureStarted && mCamera!=null) {
			mCamera.stopPreview();
			try {
				Parameters p = mCamera.getParameters();
				p.setFlashMode(Parameters.FLASH_MODE_OFF);
			} catch (RuntimeException e) {
				// native crash reported from accessing mCamera.getParameters() on some devices -- just ignore.
				Log.e(this.getClass().getName(),"Failed to set FLASH_MODE_OFF in Camera parameters: "+e.getMessage());
				e.printStackTrace();
			}
			mCamera.release();
		}
		isFeatureStarted = false;
		mCamera = null;
	}

}
