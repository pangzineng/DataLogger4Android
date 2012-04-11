package com.hsr.datalogger.cache;

import android.content.Context;

public class CacheHelper {

	Cache cache;
	
	public CacheHelper(Context context) {
		cache = new Cache(context);
	}

	public void setSelectedSensor(int[] selected){
		cache.setSelectedSensor(selected);
	}
	
	public int[] getSelectedSensor(){
		return cache.getSelectedSensor();
	}
	
	public String[] getInfoForEmail(){
		return new String[]{cache.getFeedName(), cache.getFeedID(), cache.getUsername()};
	}
	
	public String[] getCurrentFeed(){
		return new String[]{cache.getFeedID(), cache.getFeedName()};
	}
	
	public boolean detectSensor(){
		if(cache.getSensorDetect()){
			return true;
		} else {
			cache.setSensorDetectTrigger();
			return false;
		}
	}
}
