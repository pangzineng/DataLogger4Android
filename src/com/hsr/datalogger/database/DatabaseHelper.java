package com.hsr.datalogger.database;

import java.util.List;

import android.content.Context;

public class DatabaseHelper {
	static String temp1 = "a"; static String temp11 = "sensor"; static String temp12 = "private";
	static String temp2 = "b"; static String temp21 = "sensor"; static String temp22 = "public";
	
	Database db;
	Context context;
	public DatabaseHelper(Context context) {
		this.context = context;
		db = new Database(context);
	}


	public static String getFeedName(String username, int feedIndex) {
		return (feedIndex==1)?temp1:temp2;
	}


	public static String getFeedType(String username, int feedIndex) {
		return (feedIndex==1)?temp11:temp21;
	}


	public static String getFeedStatus(String username, int feedIndex) {
		return (feedIndex==1)?temp12:temp22;
	}
	
	String[] sensors;
	
	public void storeSensorStatus(boolean[] available){
		sensors = context.getResources().getStringArray(com.hsr.datalogger.R.array.sensor_list);
		for(int i=0; i<sensors.length; i++){
			db.Add(sensors[i], i, available[i]);
		}
	}
	
	public String[] getSensorForDevice(){
		List<String> temp  = db.getAllMatchValue(Database.SENSOR_INDEX, Database.colAvailable, "1", Database.colSensorName);
		return temp.toArray(new String[temp.size()]);
	}
	
	public void addFeedToList(String[] info, String feedType, String feedStatus,String key){
		
	}
}
