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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.stdev293.batterywasterdemo.R;

public class Gps extends Sink {
    private LocationManager mLocationManager;
    private LocationCallbackHandler mLocationListener;
    

	// --------------------------------------------------------------------------------------------
    // inner classes    
	// --------------------------------------------------------------------------------------------
	private class LocationCallbackHandler implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			// void			
		}

		@Override
		public void onProviderDisabled(String provider) {
			notifyStatusChange(getContext().getString(R.string.please_turn_gps_on));
		}

		@Override
		public void onProviderEnabled(String provider) {
			notifyStatusChange(getContext().getString(R.string.thank_you_gps));
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// void
		}		
	}
	
	// --------------------------------------------------------------------------------------------
    // instance methods   
	// --------------------------------------------------------------------------------------------	
	public Gps(Context context) {
		super(context);
        
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationCallbackHandler();
	}

	@Override
	public void startImpl() {
        // start GPS
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        	notifyStatusChange(getContext().getString(R.string.please_turn_gps_on));
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		
	}

	@Override
	public void stopImpl() {
        // stop GPS
        mLocationManager.removeUpdates(mLocationListener);
	}

}
