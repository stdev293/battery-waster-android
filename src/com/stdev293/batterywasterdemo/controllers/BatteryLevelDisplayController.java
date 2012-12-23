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
package com.stdev293.batterywasterdemo.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.stdev293.batterywasterdemo.R;
import com.stdev293.batterywasterdemo.views.CustomTextView;

/**
 * This class watches the battery level and events and displays this information.
 */
public class BatteryLevelDisplayController extends BroadcastReceiver {
	private Context mContext;
	private CustomTextView mTextView;
    private IntentFilter mFilter;
    
    public BatteryLevelDisplayController(Context context) {
    	super();
    	mContext = context;
    	mTextView = null;
    	
        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mFilter.addAction(Intent.ACTION_BATTERY_LOW);
        mFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        mFilter.addAction(Intent.ACTION_POWER_CONNECTED);
    }
    
    public void startMonitoring(CustomTextView textView) {
    	mTextView = textView;
        mContext.registerReceiver(this, mFilter);	
    }
    
    public void stopMonitoring() {
    	mContext.unregisterReceiver(this);
    }
	
    // BroadcastReceiver implementation
    public void onReceive(Context context, Intent intent) {
    	if (mTextView==null) {
    		return;
    	}
        String action = intent.getAction();
        String title = action;
        int index = title.lastIndexOf('.');
        if (index >= 0) {
            title = title.substring(index + 1);
        }
        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            mTextView.log(title + ": "+mContext.getString(R.string.level)+"=" + level);
        } else {
        	mTextView.log(title);
        }
    }

}
