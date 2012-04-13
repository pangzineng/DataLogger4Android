package com.hsr.datalogger.cache;

import android.content.Context;

public class CacheHelper {

	Cache cache;
	
	public CacheHelper(Context context) {
		cache = new Cache(context);
	}

	public void setAutoLogin(boolean autoL, String[] account){
		cache.setAutoLogin(autoL, account[0], account[1]);
	}
	
	public void removeAutoLogin(){
		cache.removeAutoLogin();
	}
	
	public String[] getAutoLogin(){
		return cache.getAutoLogin();
	}

	public void setCurrentUser(String[] account){
		cache.setUsername(account[0]);
		if(account.length==2){
			cache.setPW(account[1]);
		}
	}
	
	
	public void setSelectedSensor(int[] selected){
		cache.setSelectedSensor(selected);
	}
	
	public void setDiagramDuration(String duration){
		cache.setDiagramDuration(duration);
	}
	
	public int[] getSelectedSensor(){
		return cache.getSelectedSensor();
	}
	
	public String[] getInfoForEmail(){
		return new String[]{cache.getFeedName(), cache.getFeedID(), cache.getUsername(), cache.getDatastream()};
	}
	
	public String[] getCurrentFeedInfo(){
		return new String[]{cache.getFeedID(), cache.getFeedName()};
	}
	
	public String[] getDataInfoForDiagram(){
		return new String[]{cache.getFeedID(), cache.getDatastream(), cache.getDiagramDuration()};
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
