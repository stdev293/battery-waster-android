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

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

/**
 * This class does not really control a widget in a view, it directly controls
 * the screen brightness.
 * 
 */
public class ScreenBrightness extends Sink {
	private Window mCurrentWindow;

	public ScreenBrightness(Activity activity) {
		super(activity);
		mCurrentWindow = activity.getWindow();
	}

	/**
	 * Sets the screen brightness to max value
	 */
	@Override
	protected void startImpl() {
		WindowManager.LayoutParams layoutParams = mCurrentWindow.getAttributes();
		layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
		mCurrentWindow.setAttributes(layoutParams);
		
	}

	@Override
	protected void stopImpl() {
		WindowManager.LayoutParams layoutParams = mCurrentWindow.getAttributes();
		layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
		mCurrentWindow.setAttributes(layoutParams);		
	}

}
