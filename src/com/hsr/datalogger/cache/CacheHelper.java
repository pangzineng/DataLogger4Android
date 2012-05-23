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
		cache.setFeedAccess(false);
		cache.setDiagramAccess(false);
		cache.setUsername(account[0]);
		if(account.length>1){
			cache.setPW(account[1]);
			cache.setMasterKey(master);
		} else {
			cache.setPW("");
			cache.setMasterKey("EppoRYwcGi-QRG0ieqk-XOlgAv2SAKxNRmM4cGRWMkNEST0g");
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

	public void setCurrentFeed(String feedID, String title) {
		cache.setFeedAccess(true);
		cache.setDiagramAccess(false);
		cache.setFeedID(feedID);
		cache.setFeedName(title);
	}
	
	public void setCurrentTab(int index) {
		cache.setCurrentTab(index);
	}

	public void setCurrentData(String dataName) {
		cache.setDiagramAccess(true);
		cache.setDataStream(dataName);
	}

	public void closeListLoader() {
		cache.setInitFL(false);
		cache.setInitFP(false);
	}

	public void setInitFL(boolean b) {
		cache.setInitFL(b);
	}

	public void setInitFP(boolean b) {
		cache.setInitFP(b);
	}

	public boolean getInitFL() {
		return cache.getInitFL();
	}

	public boolean getIniFP() {
		return cache.getInitFP();
	}
	
	
	public void checkoffCurrentDiagramState(String[] info, String dataID) {
		if(info[0].equalsIgnoreCase(cache.getUsername())&&
			info[1].equalsIgnoreCase(cache.getFeedID())&&
			dataID.equalsIgnoreCase(cache.getDatastream())){
			cache.setDiagramAccess(false);
		}
	}
	
	public void setDiagramAccess(boolean now){
		cache.setDiagramAccess(now);
	}

	public boolean getDiagramAccess() {
		return cache.getDiagramAccess();
	}
	
	public void checkoffCurrentFeedState(String[] info){
		if(info[0].equalsIgnoreCase(cache.getUsername())&&info[1].equalsIgnoreCase(cache.getFeedID())){
			cache.setFeedAccess(false);
			cache.setDiagramAccess(false);
		}
	}
	
	public void setFeedAccess(boolean now){
		cache.setFeedAccess(now);
	}
	
	public boolean getFeedAccess(){
		return cache.getFeedAccess();
	}
}
