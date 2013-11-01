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

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.stdev293.batterywasterdemo.R;

/**
 * 
 * torch feature, uses the camera flash LED.
 * Tested on Galaxy S2 with Android 4.0.3, works fine.
 * Would need tests on other devices.
 */
public class CameraLight extends Sink {
	private boolean isFeatureSupported;
	private Camera mCamera = null;
	
	public CameraLight(Context context) {
		super(context);
		
		// check that the device supports this feature
		isFeatureSupported = 
				context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
				context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	}
	
	
	@Override
	public void startImpl() {
		// try to open the default (back-facing) camera
		if (isFeatureSupported) {
			mCamera = null;
			try {
				mCamera = Camera.open();
			} catch (RuntimeException e) {
				// this has happened on device "OTHER" (!!)
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
			if (!cameraParams.getSupportedFlashModes().contains(Parameters.FLASH_MODE_TORCH)) {
				isFeatureSupported = false;				
			}
		}
		
		// all good, camera object is ready at this stage if the boolean is still true
		if (isFeatureSupported && (cameraParams!= null)) {
			// turn on the light
			notifyStatusChange(getContext().getString(R.string.torch_on));
			cameraParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(cameraParams);
		} else {
			// just tell the user
			notifyStatusChange(getContext().getString(R.string.torch_feature_not_supported));
		}
	}

	@Override
	public void stopImpl() {
		// turn off		
		if (mCamera!=null) {
			Parameters p = mCamera.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCamera.release();
		}
		mCamera = null;
	}

}
