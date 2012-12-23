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

import java.util.List;

import sinks.Sink;
import sinks.SinkCallbackListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.stdev293.batterywasterdemo.R;
import com.stdev293.batterywasterdemo.controllers.BatteryLevelDisplayController;
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
    private List<Sink> mSinks;
    
    private boolean mWasting;


    private OnCheckedChangeListener mOnCheckedChangedListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        	switch (buttonView.getId()) {
        	case R.id.switch_on_off:
	            if (isChecked) {
	            	buttonView.setEnabled(false); // disable while starting
	            	buttonView.post(new Runnable() {
						@Override
						public void run() {
			                startWasting();
						}
	            	});
	            } else {
	            	buttonView.setEnabled(false); // disable while stopping
	            	buttonView.post(new Runnable() {
						@Override
						public void run() {
							stopWasting();
						}
	            	});
	            }
	            break;
        	case R.id.switch_light:
        		// TODO
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

        mOnOffSwitch = (Switch) findViewById(R.id.switch_on_off);
        mUseLightSwitch = (Switch) findViewById(R.id.switch_light);
        mConsole = (CustomTextView) findViewById(R.id.console);
        mConsole.setText("--"+getString(R.string.app_name)+" v"+getString(R.string.app_version_name)+"--\n");
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
	            
	            // TODO instantiate and start all sinks
	            
	            
	        }
        	mOnOffSwitch.setEnabled(true); // enable back now that it has started
    	}
    }

    private void stopWasting() {
    	synchronized(this) {
	        if (mWasting) {
	            mWasting = false;
	            mConsole.log(getString(R.string.battery_waster_stop));

	            // TODO stop all sinks
	            
	        }
        	mOnOffSwitch.setEnabled(true); // enable back now that it has stopped
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
