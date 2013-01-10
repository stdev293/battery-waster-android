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
import android.util.Log;

public class CameraLight extends Sink {
	public CameraLight(Context context) {
		super(context);
		// TODO
		Log.d(this.getClass().getName(), "debug >> CameraLight() ");
	}
	
	
	@Override
	public void startImpl() {
		// TODO Auto-generated method stub
		Log.d(this.getClass().getName(), "debug >> startImpl() ");
		
	}

	@Override
	public void stopImpl() {
		// TODO Auto-generated method stub
		Log.d(this.getClass().getName(), "debug >> stopImpl() ");
		
	}

}
