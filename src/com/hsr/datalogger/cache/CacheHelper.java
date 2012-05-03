package com.hsr.datalogger.cache;

import android.content.Context;

public class CacheHelper {

	Cache cache;
	
	public CacheHelper(Context context) {
		cache = new Cache(context);
	}
	
	/* 1. Launch of the app
	 * */
	public int getCurrentTab(){
		return cache.getCurrentTab();
	}
	
	/* 2. Handle different cases for login & logout
	 * */
	public String[] getAutoLogin(){
		return cache.getAutoLogin();
	}
	
	public void setCurrentUser(String[] account, String master){
		cache.setUsername(account[0]);
		if(account.length>1){
			cache.setPW(account[1]);
			cache.setMasterKey(master);
		}
	}

	public void removeAutoLogin(){
		cache.removeAutoLogin();
	}
	
	public void setAutoLogin(boolean autoL, String[] account){
		cache.setAutoLogin(autoL, account[0], account[1]);
	}

	public String getCurrentMasterKey(){
		return cache.getMaster();
	}
	
	public String[] getCurrentUser(){
		return new String[]{cache.getUsername(), cache.getPw()};
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

	public void setCurrentFeed(String feedID, String title, String permission) {
		cache.setFeedID(feedID);
		cache.setFeedName(title);
		cache.setKey(permission);
	}

	public void setCurrentTab(int index) {
		cache.setCurrentTab(index);
	}

	public void setCurrentData(String dataName) {
		cache.setDataStream(dataName);
	}
}
