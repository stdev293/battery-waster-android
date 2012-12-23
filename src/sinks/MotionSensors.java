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

import java.util.List;

import com.stdev293.batterywasterdemo.R;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MotionSensors extends Sink implements SensorEventListener {
    private SensorManager mSensorManager;
    private List<Sensor> mSensors;

	public MotionSensors(Context c) {
		super(c);
        
        mSensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        mSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
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
    	String stype;
    	switch (sensorType) {
    	case Sensor.TYPE_ACCELEROMETER:
    		stype="ACCELEROMETER";
    		break;
    	case Sensor.TYPE_AMBIENT_TEMPERATURE:
    		stype="AMBIENT_TEMPERATURE";
    		break;
    	case Sensor.TYPE_GRAVITY:
    		stype="GRAVITY";
    		break;
    	case Sensor.TYPE_GYROSCOPE:
    		stype="GYROSCOPE";
    		break;
    	case Sensor.TYPE_LIGHT:
    		stype="LIGHT";
    		break;
    	case Sensor.TYPE_LINEAR_ACCELERATION:
    		stype="LINEAR_ACCELERATION";
    		break;
    	case Sensor.TYPE_MAGNETIC_FIELD:
    		stype="MAGNETIC_FIELD";
    		break;
    	case Sensor.TYPE_PRESSURE:
    		stype="PRESSURE";
    		break;
    	case Sensor.TYPE_PROXIMITY:
    		stype="PROXIMITY";
    		break;
    	case Sensor.TYPE_RELATIVE_HUMIDITY:
    		stype="RELATIVE_HUMIDITY";
    		break;
    	case Sensor.TYPE_ROTATION_VECTOR:
    		stype="ROTATION_VECTOR";
    		break;
    	default:
    	case Sensor.TYPE_ALL:
    		stype = getContext().getString(R.string.sensor_unknown_type,sensorType);
    		break;
    	}
    	return stype;
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
