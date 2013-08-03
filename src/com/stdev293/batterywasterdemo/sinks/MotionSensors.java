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

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.SparseArray;

import com.stdev293.batterywasterdemo.R;

public class MotionSensors extends Sink implements SensorEventListener {
    private SensorManager mSensorManager;
    private List<Sensor> mSensors;
    private static SparseArray<String> SENSORS_NAME = new SparseArray<String>(20);

	public MotionSensors(Context c) {
		super(c);
        
        mSensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        mSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

    	if (SENSORS_NAME.size()==0) {
    		synchronized(SENSORS_NAME) {
    			initializeSensorNames();
    		}
    	}
	}

	@Override
	protected void startImpl() {
        // start sensors
        for (Sensor sensor:mSensors) {
        	notifyStatusChange(getContext().getString(R.string.using_sensor,sensorTypeToString(sensor.getType())));
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }

	}

	@Override
	protected void stopImpl() {
        // stop all sensors
        mSensorManager.unregisterListener(this);
	}
	
	private String sensorTypeToString(int sensorType) {
    	String stype = SENSORS_NAME.get(sensorType);
    	if (stype==null) {
    		stype = getContext().getString(R.string.sensor_unknown_type, sensorType);    		
    	}
    	
    	return stype;
    }

    
    @SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
    private static void initializeSensorNames() {
		SENSORS_NAME.append(Sensor.TYPE_ACCELEROMETER, "ACCELEROMETER");
		SENSORS_NAME.append(Sensor.TYPE_MAGNETIC_FIELD, "MAGNETIC_FIELD");
		SENSORS_NAME.append(Sensor.TYPE_ORIENTATION, "ORIENTATION");
		SENSORS_NAME.append(Sensor.TYPE_GYROSCOPE, "GYROSCOPE");
		SENSORS_NAME.append(Sensor.TYPE_LIGHT, "LIGHT");
		SENSORS_NAME.append(Sensor.TYPE_PRESSURE, "PRESSURE");
		SENSORS_NAME.append(Sensor.TYPE_TEMPERATURE, "TEMPERATURE");
		SENSORS_NAME.append(Sensor.TYPE_PROXIMITY, "PROXIMITY");
		SENSORS_NAME.append(Sensor.TYPE_GRAVITY, "GRAVITY");
		SENSORS_NAME.append(Sensor.TYPE_LINEAR_ACCELERATION, "ACCELEROMETER");
		SENSORS_NAME.append(Sensor.TYPE_ACCELEROMETER, "LINEAR_ACCELERATION");
		SENSORS_NAME.append(Sensor.TYPE_ROTATION_VECTOR, "ROTATION_VECTOR");
		SENSORS_NAME.append(Sensor.TYPE_RELATIVE_HUMIDITY, "RELATIVE_HUMIDITY");
		SENSORS_NAME.append(Sensor.TYPE_AMBIENT_TEMPERATURE, "AMBIENT_TEMPERATURE");
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2) {
			// only in API 18+
			SENSORS_NAME.append(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, "MAGNETIC_FIELD_UNCALIBRATED");
			SENSORS_NAME.append(Sensor.TYPE_GAME_ROTATION_VECTOR, "GAME_ROTATION_VECTOR");
			SENSORS_NAME.append(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, "GYROSCOPE_UNCALIBRATED");
			SENSORS_NAME.append(Sensor.TYPE_SIGNIFICANT_MOTION, "SIGNIFICANT_MOTION");
		}
    }


	// --------------------------------------------------------------------------------------------
    // sensor listener interface
	// --------------------------------------------------------------------------------------------
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// void - dismiss
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// void - dismiss
	}

}
