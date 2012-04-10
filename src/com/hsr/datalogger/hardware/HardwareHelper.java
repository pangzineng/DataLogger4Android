package com.hsr.datalogger.hardware;

import android.content.Context;

public class HardwareHelper {

	Hardware hw;
	
	public HardwareHelper(Context context) {
		hw = new Hardware(context);
	}
	
	public float[] getSensorValue(int[] selected){
		float[] values = new float[selected.length];
		
		for(int i=0; i<selected.length; i++){
			if(selected[i]==0) {
				values[i] = (float) hw.getSoundValue();
			} else {
				values[i] = hw.getSensorValue(selected[i]);
			}
		}
		
		return values;
	}
	
	public boolean[] getAvailableSensor(){
		return hw.getAvailable();
	}
	
	public void stopListenToSensor(){
		hw.close();
	}
	
}
