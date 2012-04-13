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
	private static final String AUTO_LOGIN_CHECKED = "auto login";
	private static final String AUTO_LOGIN_USERNAME = "autoLogin username";
	private static final String AUTO_LOGIN_PASSWORD = "autoLogin password";
	private static final String CURRENT_KEY = "key";
	private static final String CURRENT_FEED_ID = "feed id";
	private static final String CURRENT_FEED_NAME = "feed name";
	private static final String CURRENT_FEED_DATA = "datastream name";
	private static final String CURRENT_FEED_DIAGRAM_DURATION = "diagram duration";
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
		editor.apply();
	}
	
	public void setPW(String pw){
		editor.putString(CURRENT_PASSWORD, pw);
		editor.apply();
	}
	
	public void setAutoLogin(boolean autoL, String autoN, String autoP){
		editor.putBoolean(AUTO_LOGIN_CHECKED, autoL);
		editor.putString(AUTO_LOGIN_USERNAME, autoN);
		editor.putString(AUTO_LOGIN_PASSWORD, autoP);
		editor.apply();
	}
	
	public void removeAutoLogin(){
		editor.remove(AUTO_LOGIN_CHECKED);
		editor.remove(AUTO_LOGIN_USERNAME);
		editor.remove(AUTO_LOGIN_PASSWORD);
		editor.apply();
	}
	
	public void setKey(String key){
		editor.putString(CURRENT_KEY, key);
		editor.apply();
	}
	
	public void setFeedID(String fID){
		editor.putString(CURRENT_FEED_ID, fID);
		editor.apply();
	}
	
	public void setFeedName(String fName){
		editor.putString(CURRENT_FEED_NAME, fName);
		editor.apply();
	}
	
	public void setDataStream(String dataName){
		editor.putString(CURRENT_FEED_DATA, dataName);
		editor.apply();
	}
	
	public void setDiagramDuration(String duration){
		editor.putString(CURRENT_FEED_DIAGRAM_DURATION, duration);
		editor.apply();
	}
	
	public void setAction(int choice){
		editor.putInt("Action", choice);
		editor.apply();
	}
	
	public void setSelectedSensor(int[] selected){
		
		setSelectedSensorNum(selected);
		
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<selected.length; i++){
			builder.append(selected[i]).append(",");
		}
		editor.putString(SELECT_SENSORS, builder.toString());
		editor.apply();
	}
	
	private void setSelectedSensorNum(int[] selected){
		editor.putInt(SELECT_SENSORS_NUM, selected.length);
		editor.apply();
	}
	
	public void setSensorDetectTrigger(){
		editor.putBoolean(SENSOR_DETECTED, true);
		editor.apply();
	}
	
	/* Get the current data & passing data from the cache
	 * 
	 * */
	
	public String getUsername(){
		return sp.getString(CURRENT_USERNAME, "guest");
	}
	
	public String getPw(){
		return sp.getString(CURRENT_PASSWORD, null);
	}
	
	public String[] getAutoLogin(){
		if(sp.getBoolean(AUTO_LOGIN_CHECKED, false)){
			return new String[]{sp.getString(AUTO_LOGIN_USERNAME, null), sp.getString(AUTO_LOGIN_PASSWORD, null)};
		} else {
			return null;
		}
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
		return sp.getString(CURRENT_FEED_DATA, null);
	}
	
	public String getDiagramDuration(){
		return sp.getString(CURRENT_FEED_DIAGRAM_DURATION, null);
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











