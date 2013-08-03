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
package com.stdev293.batterywasterdemo.sinks;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.stdev293.batterywasterdemo.activities.BatteryWasterActivity;

public class SinksControlThread extends Thread {
	// --------------------------------------------------------------------------------------------
    // Constants
	// --------------------------------------------------------------------------------------------
	public static final String THREAD_NAME="SinksControl";
	public static final int ACTION_START_ALL = 0;
	public static final int ACTION_STOP_ALL = 1;
	public static final int ACTION_LIGHT_ON = 2;
	public static final int ACTION_LIGHT_OFF = 3;

	// --------------------------------------------------------------------------------------------
    // Members
	// --------------------------------------------------------------------------------------------
	private static SinksControlActionHandler handler = null;
	
	private BatteryWasterActivity mActivity;

	// --------------------------------------------------------------------------------------------
    // static Handler class to carry out actions in the thread
	// --------------------------------------------------------------------------------------------
	private static class SinksControlActionHandler extends Handler {
		private WeakReference<BatteryWasterActivity> mActivityRef;
		
		public SinksControlActionHandler(BatteryWasterActivity activity) {
			super();
			setActivityReference(activity);
		}
		
		public void setActivityReference(BatteryWasterActivity activity) {
			mActivityRef = new WeakReference<BatteryWasterActivity>(activity);			
		}
		
		public void handleMessage(Message msg) {
			BatteryWasterActivity activity = mActivityRef.get();
			if (activity==null) {
				return;
			}
			
    		// process incoming messages
    		switch (msg.what) {
			case ACTION_START_ALL:
				activity.startWasting();
				break;
			case ACTION_LIGHT_ON:
				activity.startLight();
				break;
			case ACTION_LIGHT_OFF:
				activity.stopLight();
				break;
			case ACTION_STOP_ALL:
			default:
				activity.stopWasting();
				break;	    		
    		}
    		
    		if (msg.arg1 != View.NO_ID)  {
    			enableViewInUIThread(msg.arg1);
    		}
    	}
		
		public void enableViewInUIThread(int viewId) {
			BatteryWasterActivity activity = mActivityRef.get();
			if (activity != null) {
				final View v = activity.findViewById(viewId);
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (v!=null) {
							v.setEnabled(true);
						}
					}					
				});
			}
		}
		
	}

	// --------------------------------------------------------------------------------------------
    // Methods
	// --------------------------------------------------------------------------------------------
	/**
	 * Constructor
	 * @param activity
	 */
	public SinksControlThread(BatteryWasterActivity activity) {
		super(THREAD_NAME);
		mActivity = activity;
	}
	
	@Override
	public void run() {
		Looper.prepare();		
		handler = new SinksControlActionHandler(mActivity);		
		Looper.loop();
	}
	
	public void executeAction(int actionCode, View viewToReactivateAfterActionCompleted) {
		Message msg = new Message();
		msg.what = actionCode;
		if (viewToReactivateAfterActionCompleted!=null) {
			msg.arg1 = viewToReactivateAfterActionCompleted.getId();
		} else {
			msg.arg1 = View.NO_ID;
		}
		
		if (handler!=null) {
			handler.sendMessage(msg);
		} else if (viewToReactivateAfterActionCompleted != null) {
			// error - handler not initialized yet
			// just reactivate the view
			viewToReactivateAfterActionCompleted.setEnabled(true);
		}
	}

}
