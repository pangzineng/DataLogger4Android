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

	public List<String> getCurrentFeedList(String username){
		List<String> feedIDlist = null;
		feedIDlist = db.getAllMatchValue(Database.FEED_INDEX, Database.colUsername, username, Database.colFeedID);
		return feedIDlist;
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

	public String[] getOneFeedInfo(String currentUser, String feedID) {
		// SOS need to return feed name, feed data count and feed premission
		String name = db.getValue(Database.FEED_INDEX, currentUser, feedID, Database.colFeedTitle);
		String count = ""+db.getRowNum(Database.DATASTREAM_INDEX, Database.colFeedID, feedID);
		String premission = db.getValue(Database.FEED_INDEX, currentUser, feedID, Database.colFeedStatus);
		return new String[]{name, count, premission};
	}
}
