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

import com.stdev293.batterywasterdemo.R;
import com.stdev293.batterywasterdemo.views.CustomGLView;

import android.app.Activity;

public class Gpu extends Sink {
    private CustomGLView mGLView;

	public Gpu(Activity activityContext) {
		super(activityContext);
        mGLView = (CustomGLView) activityContext.findViewById(R.id.glview);
	}

	@Override
	protected void startImpl() {
        // start open GL view
        mGLView.resumeAnimation();

	}

	@Override
	protected void stopImpl() {
        // stop open GL view
        mGLView.pauseAnimation();
	}

}
