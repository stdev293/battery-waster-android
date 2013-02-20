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
 */
package com.stdev293.batterywasterdemo.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.stdev293.batterywasterdemo.R;
import com.stdev293.batterywasterdemo.controllers.BatteryLevelDisplayController;
import com.stdev293.batterywasterdemo.sinks.CameraLight;
import com.stdev293.batterywasterdemo.sinks.Cpu;
import com.stdev293.batterywasterdemo.sinks.Gps;
import com.stdev293.batterywasterdemo.sinks.Gpu;
import com.stdev293.batterywasterdemo.sinks.MotionSensors;
import com.stdev293.batterywasterdemo.sinks.ScreenBrightness;
import com.stdev293.batterywasterdemo.sinks.Sink;
import com.stdev293.batterywasterdemo.sinks.SinkCallbackListener;
import com.stdev293.batterywasterdemo.views.CustomTextView;

/**
 * Main (and only) activity of this application.
 * Uses battery through CPU (multi-threaded), GPU, sensors and GPS
 */
public class BatteryWasterActivity extends Activity implements SinkCallbackListener {
	
	// --------------------------------------------------------------------------------------------
    // members
	// --------------------------------------------------------------------------------------------
    private CustomTextView mConsole;
    private Switch mOnOffSwitch,mUseLightSwitch;
    private BatteryLevelDisplayController mBatteryLevelDisplayController;
    private List<Sink> mSinks; // all sinks except the flashlight
    private Sink mLightSink;
    
    private boolean mWasting;


    private OnCheckedChangeListener mOnCheckedChangedListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        	switch (buttonView.getId()) {
        	case R.id.switch_on_off:
	            if (isChecked) {
	            	mOnOffSwitch.setEnabled(false); // disable while starting
	            	mOnOffSwitch.post(new Runnable() {
						@Override
						public void run() {
			                startWasting();
			            	mOnOffSwitch.setEnabled(true); // enable back now that it has stopped
						}
	            	});
	            } else {
	            	mOnOffSwitch.setEnabled(false); // disable while stopping
	            	mOnOffSwitch.post(new Runnable() {
						@Override
						public void run() {
							stopWasting();
				        	mOnOffSwitch.setEnabled(true); // enable back now that it has stopped
						}
	            	});
	            }
	            break;
        	case R.id.switch_light:
        		mUseLightSwitch.setEnabled(false); // disable while switching
            	final boolean fIsChecked = isChecked;
            	mUseLightSwitch.post(new Runnable() {
					@Override
					public void run() {
		        		setLightSink(fIsChecked);
		        		mUseLightSwitch.setEnabled(true);
					}
            	});
            	break;
            default:
            	break;
        	}
		}
    };
    
    
    // --------------------------------------------------------------------------------------------
    // methods   
	// --------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.battery_waster);
        
        mSinks = new ArrayList<Sink>();

        mOnOffSwitch = (Switch) findViewById(R.id.switch_on_off);
        mUseLightSwitch = (Switch) findViewById(R.id.switch_light);
        mConsole = (CustomTextView) findViewById(R.id.console);

        String appStr;
        try {
        	String ver = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        	appStr = "--"+getString(R.string.app_name)+" v"+ver+"--\n";
        } catch (Exception e) {
        	appStr = "--"+getString(R.string.app_name)+"--\n";
        }
        mConsole.setText(appStr);
        
        mBatteryLevelDisplayController = new BatteryLevelDisplayController(this);
        mWasting = false;

        mOnOffSwitch.setOnCheckedChangeListener(mOnCheckedChangedListener);
        mUseLightSwitch.setOnCheckedChangeListener(mOnCheckedChangedListener);
        
    }

    @Override
    public void onResume() {
        super.onResume();
    	mBatteryLevelDisplayController.startMonitoring(mConsole);
        if (mOnOffSwitch.isChecked()) {
            startWasting();
        }
		getWindow().getDecorView().setKeepScreenOn(true);
    }

    @Override
    public void onPause() {
		getWindow().getDecorView().setKeepScreenOn(false);
        stopWasting();
    	mBatteryLevelDisplayController.stopMonitoring();
        super.onPause();
    }

    private void startWasting() {
    	synchronized(this) {
	        if (!mWasting) {
	        	mConsole.log(getString(R.string.battery_waster_start));
	            mWasting = true;
	            
	            // instantiate sinks
	            mSinks.add(new ScreenBrightness(this));
	            mSinks.add(new Gps(this));
	            mSinks.add(new Cpu(this));
	            mSinks.add(new Gpu(this));
	            mSinks.add(new MotionSensors(this));
	            mLightSink = new CameraLight(this);
	            
	            // start them
	            for (Sink s:mSinks) {
	            	s.start(this);
	            }
	            if (mUseLightSwitch.isChecked()) {
	            	mLightSink.start(this);
	            }
	        }
    	}
    }

    private void stopWasting() {
    	synchronized(this) {
	        if (mWasting) {
	            mWasting = false;
	            mConsole.log(getString(R.string.battery_waster_stop));

	            // stop and destroy all sinks
	            mLightSink.stop();
	            mLightSink = null;
	            for (Sink s:mSinks) {
	            	s.stop();
	            }
	            mSinks.clear();
	        }
    	}
    }

	private void setLightSink(boolean isChecked) {
    	synchronized(this) {
			if (mLightSink!=null) {
				if (isChecked) {
					mLightSink.start(this);
				} else {
					mLightSink.stop();				
				}
			}
    	}		
	}

	// --------------------------------------------------------------------------------------------
    // Sink callback interface
	// --------------------------------------------------------------------------------------------
	@Override
	public void onStatusChange(String statusInfo) {
		if (mConsole!=null) {
			mConsole.log(statusInfo);	
		}
	}
}
