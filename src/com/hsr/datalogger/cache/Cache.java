package com.hsr.datalogger.cache;

import java.util.StringTokenizer;

import android.content.Context;
import android.content.SharedPreferences;

public class Cache{
	
	Context context;
	
	SharedPreferences sp;
	SharedPreferences.Editor editor;

	private static final String STORE = "Datalogger for Android Cache";
	
	// to be shown on the view
	private static final String CURRENT_USERNAME = "username";
	private static final String CURRENT_PASSWORD = "password";
	private static final String CURRENT_KEY = "key";
	private static final String CURRENT_FEED_ID = "feed id";
	private static final String CURRENT_FEED_NAME = "feed name";
	private static final String CURRENT_FEED_DATA_ID = "datastream id";
	private static final String CURRENT_ACTION = "next action";
	
	// to be store for the first launch
	private static final String SENSOR_DETECTED = "detect sensor trigger";
	
	// to be transfer thought classes
	private static final String SELECT_SENSORS = "selected update sensor";
	private static final String SELECT_SENSORS_NUM = "selected sensor number";
	
	// to be recognize by activity if needed
	public static final int ACTION_EDIT_PROFILE = 0;
	public static final int ACTION_ADD_SENSOR_FEED = 1;
	public static final int ACTION_ADD_CUSTOM_FEED = 2;
	public static final int ACTION_EDIT_SENSOR_FEED = 3;
	public static final int ACTION_EDIT_CUSTOM_FEED = 4;
	public static final int ACTION_UPDATE_SENSOR_FEED = 5;
	public static final int ACTION_UPDATE_CUSTOM_FEED = 6;
	
	
	public Cache(Context context) {
		this.context = context;
		sp = context.getSharedPreferences(STORE, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	
	/* Put the current data & passing data in the cache
	 * 
	 * */
	
	public void setUsername(String name){
		editor.putString(CURRENT_USERNAME, name);
		editor.commit();
	}
	
	public void setPW(String pw){
		editor.putString(CURRENT_PASSWORD, pw);
		editor.commit();
	}
	
	public void setKey(String key){
		editor.putString(CURRENT_KEY, key);
		editor.commit();
	}
	
	public void setFeedID(String fID){
		editor.putString(CURRENT_FEED_ID, fID);
		editor.commit();
	}
	
	public void setFeedName(String fName){
		editor.putString(CURRENT_FEED_NAME, fName);
		editor.commit();
	}
	
	public void setDataStream(String dataID){
		editor.putString(CURRENT_FEED_DATA_ID, dataID);
		editor.commit();
	}
	
	public void setAction(int choice){
		editor.putInt("Action", choice);
		editor.commit();
	}
	
	public void setSelectedSensor(int[] selected){
		
		setSelectedSensorNum(selected);
		
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<selected.length; i++){
			builder.append(selected[i]).append(",");
		}
		editor.putString(SELECT_SENSORS, builder.toString());
		editor.commit();
	}
	
	private void setSelectedSensorNum(int[] selected){
		editor.putInt(SELECT_SENSORS_NUM, selected.length);
		editor.commit();
	}
	
	public void setSensorDetectTrigger(){
		editor.putBoolean(SENSOR_DETECTED, true);
		editor.commit();
	}
	
	/* Get the current data & passing data from the cache
	 * 
	 * */
	
	public String getUsername(){
		return sp.getString(CURRENT_USERNAME, null);
	}
	
	public String getPw(){
		return sp.getString(CURRENT_PASSWORD, null);
	}
	
	public String getKey(){
		return sp.getString(CURRENT_KEY, null);
	}
	
	public String getFeedID(){
		return sp.getString(CURRENT_FEED_ID, null);
	}
	
	public String getFeedName(){
		return sp.getString(CURRENT_FEED_NAME, null);
	}
	
	public String getDatastream(){
		return sp.getString(CURRENT_FEED_DATA_ID, null);
	}
	
	public int getAction(){
		return sp.getInt(CURRENT_ACTION, -1);
	}
	
	public int[] getSelectedSensor(){
		String saved = sp.getString(SELECT_SENSORS, "");
		StringTokenizer st = new StringTokenizer(saved, ",");
		int[] selected = new int[getSelectedSensorNum()];
		for(int i=0; i<selected.length; i++){
			selected[i] = Integer.parseInt(st.nextToken());
		}
		return selected;
	}
	
	private int getSelectedSensorNum(){
		return sp.getInt(SELECT_SENSORS_NUM, 0);
	}
	
	public boolean getSensorDetect(){
		return sp.getBoolean(SENSOR_DETECTED, false);
	}
}










