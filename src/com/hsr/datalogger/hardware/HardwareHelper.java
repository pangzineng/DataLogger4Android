package com.hsr.datalogger.hardware;

import java.util.Date;
import java.util.List;

import android.content.Context;

public class HardwareHelper {

	Hardware hw;
	NetworkCondition network;
	DeviceInfo deviceInfo;

	public HardwareHelper(Context context) {
		hw = new Hardware(context);
		network = new NetworkCondition(context);
		deviceInfo = new DeviceInfo(context);
	}

	public boolean[] getAvailableSensor(){
		return hw.getAvailable();
	}
	
	public void stopListenToSensor(){
		hw.close();
	}

	public float[] getSensorValue(List<String> sensors) {
		float[] values = new float[sensors.size()];
		for(int i=0; i<sensors.size(); i++){
			int index = Integer.parseInt(sensors.get(i));
			if(index==0) {
				values[i] = (float)hw.getSoundValue();
			} else {
				values[i] = (float)hw.getSensorValue(index);
			}
		}
		return values;
	}

	public boolean getNetworkCondition(){
		return network.getNetworkInfo();
	}

	public int[] getDeviceInfo(){
		int[] size = deviceInfo.getScreenSize();
		int zone = deviceInfo.getTimezone();
		return new int[]{size[0], size[1], zone};
	}
	
	public Date getSystemTime(){
		return deviceInfo.getSystemTime();
	}

}
