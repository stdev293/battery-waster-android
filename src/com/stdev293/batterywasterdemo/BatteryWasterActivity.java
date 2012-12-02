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
 * 
 * Derived from com.android.batterywaster.BatteryWaster - the Android Open Source Project
 */
package com.stdev293.batterywasterdemo;

import java.util.ArrayList;
import java.util.Date;

import com.stdev293.batterywasterdemo.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Uses battery through CPU and GPS
 *
 */
public class BatteryWasterActivity extends Activity {
	private static final int NUMBER_OF_THREADS = 8;
	
	// --------------------------------------------------------------------------------------------
    // members
	// --------------------------------------------------------------------------------------------
    private TextView mConsole;
    private Switch mOnOffSwitch,mScreenWakeLockSwitch;
    private CustomGLView mGLView;
    
    private IntentFilter mFilter;
    private ArrayList<SpinThread> mThreads;
    private LocationManager mLocationManager;
    private LocationCallbackHandler mLocationListener;
    private boolean mWasting;

	private java.text.DateFormat mDateFormat;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String title = action;
            int index = title.lastIndexOf('.');
            if (index >= 0) {
                title = title.substring(index + 1);
            }
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                log(title + ": "+getString(R.string.level)+"=" + level);
            } else {
                log(title);
            }
        }
    };

    private OnCheckedChangeListener mOnClickListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        	switch (buttonView.getId()) {
        	case R.id.switch1:
	            if (isChecked) {
	                startWasting();
	            } else {
	                stopWasting();
	            }
	            break;
        	case R.id.switch2:
        		updateWakeLock(isChecked);
            	break;
            default:
            	break;
        	}
		}
    };
    
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
			if (mWasting) {
                log(getString(R.string.please_turn_gps_on));
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			if (mWasting) {
                log(getString(R.string.thank_you_gps));
			}		
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// void
		}		
	}
	
    /** Endless loop, no sleep for you */
	private class SpinThread extends Thread {
        private boolean mStop;

        public void quit() {
            synchronized (this) {mStop = true;}
        }

        public void run() {
        	this.setPriority(MIN_PRIORITY);
            while (true) {
                synchronized (this) {
                    if (mStop) {
                        return;
                    }
                }
            }
        }
    }

    

	// --------------------------------------------------------------------------------------------
    // methods   
	// --------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.battery_waster);

        mOnOffSwitch = (Switch) findViewById(R.id.switch1);
        mScreenWakeLockSwitch = (Switch) findViewById(R.id.switch2);
        mConsole = (TextView) findViewById(R.id.console);
        mConsole.setText("--"+getString(R.string.app_name)+" v"+getString(R.string.app_version_name)+"--\n");
        mGLView = (CustomGLView) findViewById(R.id.glview);
        
        mDateFormat = DateFormat.getTimeFormat(this);
        
        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mFilter.addAction(Intent.ACTION_BATTERY_LOW);
        mFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        mFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationCallbackHandler();
        mThreads = new ArrayList<SpinThread>();
        mWasting = false;

        mOnOffSwitch.setOnCheckedChangeListener(mOnClickListener);
        mScreenWakeLockSwitch.setOnCheckedChangeListener(mOnClickListener);
        
        log(getString(R.string.number_of_threads,NUMBER_OF_THREADS));
        
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOnOffSwitch.isChecked()) {
            startWasting();
        }
        updateWakeLock(mScreenWakeLockSwitch.isChecked());
    }

    @Override
    public void onPause() {
        super.onPause();
        stopWasting();
        updateWakeLock(false);
    }

    private void startWasting() {
        if (!mWasting) {
            log(getString(R.string.battery_waster_start));
            registerReceiver(mReceiver, mFilter);
            mWasting = true;

            // start threads
            for (int k=0;k<NUMBER_OF_THREADS;k++) {
            	SpinThread sp = new SpinThread();
            	sp.start();
            	mThreads.add(sp);
            }
            
            // start GPS
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                log(getString(R.string.please_turn_gps_on));
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            
            // start open GL view
            mGLView.resumeAnimation();
        }
    }

    private void stopWasting() {
        if (mWasting) {
            log(getString(R.string.battery_waster_stop));
            unregisterReceiver(mReceiver);
            mWasting = false;
            for (SpinThread sp:mThreads) {
            	sp.quit();
            }
            mThreads.clear();
            mLocationManager.removeUpdates(mLocationListener);
            // stop open GL view
            mGLView.pauseAnimation();
        }
    }

    private void updateWakeLock(boolean bKeepScreenOn) {
		getWindow().getDecorView().setKeepScreenOn(bKeepScreenOn);
    }

    private void log(String s) {
        mConsole.append(mDateFormat.format(new Date()) + ": " + s+"\n");
    }
}
