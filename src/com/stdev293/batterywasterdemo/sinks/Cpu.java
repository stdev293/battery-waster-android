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

import java.util.ArrayList;

import com.stdev293.batterywasterdemo.R;

import android.content.Context;

public class Cpu extends Sink {
	public static final int NUMBER_OF_THREADS = 8;

    private ArrayList<SpinThread> mThreads;
    
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
	
	public Cpu(Context context) {
		super(context);
        mThreads = new ArrayList<SpinThread>();
	}
	
	@Override
	public void startImpl() {
        notifyStatusChange(getContext().getString(R.string.number_of_threads,Cpu.NUMBER_OF_THREADS));
		
        // start threads
        for (int k=0;k<NUMBER_OF_THREADS;k++) {
        	SpinThread sp = new SpinThread();
        	sp.start();
        	mThreads.add(sp);
        }      
	}

	@Override
	public void stopImpl() {
        for (SpinThread sp:mThreads) {
        	sp.quit();
        }
        mThreads.clear();
        

	}

}
