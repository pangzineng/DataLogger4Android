package com.hsr.datalogger.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;

public class DatabaseHelper {
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
			db.addSensor(sensors[i], String.valueOf(i), available[i]?"1":"0");
		}
	}
	
	public String[] getSensorForDevice(){
		List<String> temp  = db.getAllMatchValue(Database.SENSOR_INDEX, Database.colAvailable, "1", Database.colSensorName);
		return temp.toArray(new String[temp.size()]);
	}
	
	public void addFeedToList(String user, String feedID, String ownership, String permission, String permissionLevel, String feedTitle, String feedType){
		db.addFeed(user, feedID, ownership, permission, permissionLevel, feedTitle, feedType);
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
		db.deleteRow(Database.FEED_INDEX, username, id);
	}

	public void editFeedTitle(String user, String id, String nTitle) {
		db.edit(Database.FEED_INDEX, user, id, Database.colFeedTitle, nTitle);
	}

	public void editFeedOwn(String user, String id, String nOwn) {
		db.edit(Database.FEED_INDEX, user, id, Database.colOwnership, nOwn);
	}

	public void addDataToFeed(String feedID, String dataName, String tag, int sensorID) {
		db.addData(feedID, dataName, "0", tag, String.valueOf(sensorID));
	}

	public String getPremissionFor(String user, String feedID) {
		return db.getValue(Database.FEED_INDEX, user, feedID, Database.colPermission);
	}

	public void deleteData(String feedID, String dataID) {
		db.deleteRow(Database.DATASTREAM_INDEX, feedID, dataID);
	}

	public void editDataTitle(String feedID, String dataName, String nTags) {
		db.edit(Database.DATASTREAM_INDEX, feedID, dataName, Database.colDataTag, nTags);
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
		db.edit(Database.DATASTREAM_INDEX, feedID, dataName, Database.colChecked, isChecked?"1":"0");
	}

	public int getDataCheckNum(String feedID) {
		return db.getAllMatchValue(Database.DATASTREAM_INDEX, Database.colFeedID, feedID, Database.colChecked).size();
	}
	
	public void cleanDatapoint() {
		db.deleteOfflineData();
	}

	public List<String> getUpdateDataNames() {
		return db.getAllMatchValue(Database.DATASTREAM_INDEX, Database.colChecked, "1", Database.colDataName);
	}
	
	public List<String> getUpdateDataSensors(){
		return db.getAllMatchValue(Database.DATASTREAM_INDEX, Database.colChecked, "1", Database.colSensorID);
	}

	public void editDataValue(String feedID, String dataName, String newValue) {
		db.edit(Database.DATASTREAM_INDEX, feedID, dataName, Database.colDataValue, newValue);
	}

	public void putOfflineData(String feedID, String premission, Date date, List<String> dataNames, float[] dataValues) {
		String time = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(date);
		for(int i=0; i< dataNames.size(); i++){
			db.addDatapoint(feedID, premission, dataNames.get(i), time, String.valueOf(dataValues[i]));
		}
	}

	public String getFeedTitle(String[] info) {
		return db.getValue(Database.FEED_INDEX, info[0], info[1], Database.colFeedTitle);
	}
}
