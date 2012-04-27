package com.hsr.datalogger.database;

import java.text.SimpleDateFormat;
import java.util.Date;
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
	
	public void addFeedToList(String user, String feedID, String ownership, String permission, String permissionLevel, String feedTitle, String feedType){
		db.Add(user, feedID, ownership, permission, permissionLevel, feedTitle, feedType);
	}

	// FeedTitle, DataCount, Ownership, PremissionLevel
	public String[] getOneFeedInfo(String currentUser, String feedID) {
		String name = db.getValue(Database.FEED_INDEX, currentUser, feedID, Database.colFeedTitle);
		String count = ""+db.getRowNum(Database.DATASTREAM_INDEX, Database.colFeedID, feedID);
		String ownership = db.getValue(Database.FEED_INDEX, currentUser, feedID, Database.colOwnership);
		String premissionLevel = db.getValue(Database.FEED_INDEX, currentUser, feedID, Database.colPermissionLevel);
		return new String[]{name, count, ownership, premissionLevel};
	}

	public void deleteFeed(String username, String id) {
		db.DeleteRow(Database.FEED_INDEX, username, id);
	}

	public void editFeedTitle(String user, String id, String nTitle) {
		db.Edit(Database.FEED_INDEX, user, id, Database.colFeedTitle, nTitle);
	}

	public void editFeedOwn(String user, String id, String nOwn) {
		db.Edit(Database.FEED_INDEX, user, id, Database.colOwnership, nOwn);
	}

	public void addDataToFeed(String feedID, String dataName, String tag) {
		db.Add(feedID, dataName, 0, tag);
	}

	public String getPremissionFor(String user, String feedID) {
		return db.getValue(Database.FEED_INDEX, user, feedID, Database.colPermission);
	}

	public void deleteData(String feedID, String dataID) {
		db.DeleteRow(Database.DATASTREAM_INDEX, feedID, dataID);
	}

	public void editDataTitle(String feedID, String dataName, String nTags) {
		db.Edit(Database.DATASTREAM_INDEX, feedID, dataName, Database.colDataTag, nTags);
	}

	public List<String> getOfflineTime(String dataName) {
		return db.getAllMatchValue(Database.DATAPOINT_INDEX, Database.colDataName, dataName, Database.colDPTimestamp);
	}

	public List<String> getOfflineValue(String dataName) {
		return db.getAllMatchValue(Database.DATAPOINT_INDEX, Database.colDataName, dataName, Database.colDPValue);
	}
	
	public List<String> getOfflineData(){
		return db.getAllMatchValue(Database.DATAPOINT_INDEX, null, "DISTINCT", Database.colDataName);
	}

	public void checkData(String feedID, String dataName, boolean isChecked) {
		db.Edit(Database.DATASTREAM_INDEX, feedID, dataName, Database.colChecked, isChecked?"1":"0");
	}

	public int getDataCheckNum(String feedID) {
		return db.getAllMatchValue(Database.DATASTREAM_INDEX, Database.colFeedID, feedID, Database.colChecked).size();
	}
	
	// FIXME for offline data
	public void storeDatapoint(Date date){
		String time = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(date);
	}
}
