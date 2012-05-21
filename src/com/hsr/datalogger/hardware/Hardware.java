package com.hsr.datalogger.hardware;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Hardware implements SensorEventListener{
	
	// Hardware objects
	private SoundMeter mSensor;
	private SensorManager sm;
	private List<Sensor> sensorList;
	
	// Sensor Value
	private float[] sensorValue;
	
	
	public Hardware(Context context) {
        sensorValue = new float[context.getResources().getStringArray(com.hsr.datalogger.R.array.sensor_list).length];
		mSensor = new SoundMeter();
		sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		
		sensorList = sm.getSensorList(Sensor.TYPE_ALL);
		
		for(int i=0; i<sensorList.size(); i++){
			sm.registerListener(this, sensorList.get(i), SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void close(){
		sm.unregisterListener(this);
		mSensor.destroy();
	}
	
	public boolean[] getAvailable(){
		boolean[] ava = new boolean[sensorValue.length];
		ava[0] = true;
		for(int i=0; i<sensorList.size(); i++){
			ava[sensorList.get(i).getType()] = true;
		}
		return ava;
	}
	
	public double getSoundValue(){
		mSensor.start();
		double sound = mSensor.getdB();
		mSensor.stop();
		return sound<0?0:sound;
	}

	public float getSensorValue(int SensorType){
		return sensorValue[SensorType];
	}
	
	public float[] getSensorValue(){
		return sensorValue;
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	public void onSensorChanged(SensorEvent event) {
		sensorValue[event.sensor.getType()] = event.values[0];
	}
}
