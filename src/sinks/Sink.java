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
package sinks;

import android.content.Context;

public abstract class Sink {
	private SinkCallbackListener callbackListener;
	private Context context;
	
	public Sink(Context c) {
		context = c;
	}
	
	public void start(SinkCallbackListener listener) {
		callbackListener = listener;
		startImpl();
	}
	abstract protected void startImpl();

	public void stop() {
		callbackListener = null;
		stopImpl();
	}
	abstract protected void stopImpl();
	
	protected void notifyStatusChange(String statusInfo) {
		if (callbackListener!=null) {
			callbackListener.onStatusChange(statusInfo);
		}
	}
	
	protected Context getContext() {
		return context;
	}
}
