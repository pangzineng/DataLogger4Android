package com.hsr.datalogger.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
		return db.getAllMatchValue(Database.FEED_INDEX, Database.colUsername, username, Database.colFeedID, false);
	}
	
	public List<String> getCurrentDataList(String currentFeed) {
		return db.getAllMatchValue(Database.DATASTREAM_INDEX, Database.colFeedID, currentFeed, Database.colDataName, false);
	}

	
	String[] sensors;
	
	public void storeSensorStatus(boolean[] available){
		sensors = context.getResources().getStringArray(com.hsr.datalogger.R.array.sensor_list);
		for(int i=0; i<sensors.length; i++){
			db.addSensor(sensors[i], String.valueOf(i), available[i]?"1":"0");
		}
	}
	
	public String[] getSensorForDevice(){
		List<String> temp  = db.getAllMatchValue(Database.SENSOR_INDEX, Database.colAvailable, "1", Database.colSensorName, false);
		return temp.toArray(new String[temp.size()]);
	}
	
	public void addFeedToList(String user, String feedID, String ownership, String permission, String permissionLevel, String feedTitle, String feedType, String location){
		db.addFeed(user, feedID, ownership, permission, permissionLevel, feedTitle, feedType, location);
	}

	// FeedTitle, DataCount, Ownership, PermissionLevel
	public String[] getOneFeedInfo(String currentUser, String feedID) {
		String name = db.getValue(Database.FEED_INDEX, currentUser, feedID, Database.colFeedTitle);
		String count = ""+db.getRowNum(Database.DATASTREAM_INDEX, Database.colFeedID, feedID);
		String ownership = db.getValue(Database.FEED_INDEX, currentUser, feedID, Database.colOwnership);
		String permissionLevel = db.getValue(Database.FEED_INDEX, currentUser, feedID, Database.colPermissionLevel);
		return new String[]{name, count, ownership, permissionLevel};
	}
	
	// DataTags, checked
	public String[] getOneDataInfo(String feedID, String dataName) {
		String tag = db.getValue(Database.DATASTREAM_INDEX, feedID, dataName, Database.colDataTag);
		String checked = db.getValue(Database.DATASTREAM_INDEX, feedID, dataName, Database.colChecked);
		return new String[]{tag, checked};
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

	public void editFeedLocation(String user, String id, String location) {
		db.edit(Database.FEED_INDEX, user, id, Database.colLocation, location);
	}

	public void addDataToFeed(String feedID, String dataName, String value, String tag, int sensorID) {
		db.addData(feedID, dataName, value, tag, String.valueOf(sensorID));
	}
	

	public String getPermissionFor(String user, String feedID) {
		return db.getValue(Database.FEED_INDEX, user, feedID, Database.colPermission);
	}

	public void deleteData(String feedID, String dataID) {
		db.deleteRow(Database.DATASTREAM_INDEX, feedID, dataID);
	}

	public void editDataTitle(String feedID, String dataName, String nTags) {
		db.edit(Database.DATASTREAM_INDEX, feedID, dataName, Database.colDataTag, nTags);
	}

	public List<String> getOfflineTime(String dataName) {
		return db.getAllMatchValue(Database.DATAPOINT_INDEX, Database.colDataName, dataName, Database.colDPTimestamp, false);
	}

	public List<String> getOfflineValue(String dataName) {
		return db.getAllMatchValue(Database.DATAPOINT_INDEX, Database.colDataName, dataName, Database.colDPValue, false);
	}
	
	public List<String> getOfflineData(){
		return db.getAllMatchValue(Database.DATAPOINT_INDEX, null, "DISTINCT", Database.colDataName, false);
	}

	// offline v2
	public List<String> getOfflineFeedID(){
		return db.getAllMatchValue(Database.DATAPOINT_INDEX, null, null, Database.colFeedID, true);
	}
	
	public List<String> getOfflinePermission(List<String> feedIDs){
		List<String> allKeys = new ArrayList<String>();
		for(int i=0; i<feedIDs.size(); i++){
			// should at least get one value, because all datapoints has permission associate with in database or in cache (masterkey when feed belong to himself)
			allKeys.add(db.getAllMatchValue(Database.DATAPOINT_INDEX, Database.colFeedID, feedIDs.get(i), Database.colPermission, false).get(0));
		}
		return allKeys;
	}
	
	public List<String> getOfflineData(String feedID) {
		return db.getAllMatchValue(Database.DATAPOINT_INDEX, Database.colFeedID, feedID, Database.colDataName, true);
	}
	
	public List<String> getOfflineDatapointTime(String feedID, String dataName) {
		return db.getAllMatchValue(Database.DATAPOINT_INDEX, Database.colFeedID, feedID, Database.colDataName, dataName, Database.colDPTimestamp, false);
	}
	
	public List<String> getOfflineDatapointValue(String feedID, String dataName) {
		return db.getAllMatchValue(Database.DATAPOINT_INDEX, Database.colFeedID, feedID, Database.colDataName, dataName, Database.colDPValue, false);
	}

	public void checkData(String feedID, String dataName, boolean isChecked) {
		db.edit(Database.DATASTREAM_INDEX, feedID, dataName, Database.colChecked, isChecked?"1":"0");
	}

	public int getDataCheckNum(String feedID) {
		return db.getAllMatchValue(Database.DATASTREAM_INDEX, Database.colFeedID, feedID, Database.colChecked, false).size();
	}
	
	public void cleanDatapoint() {
		db.deleteOfflineData();
	}

	public List<String> getUpdateDataNames(String feedID) {
		return db.getAllMatchValue(Database.DATASTREAM_INDEX, Database.colFeedID, feedID, Database.colChecked, "1", Database.colDataName, false);
	}
	
	public List<String> getUpdateDataSensors(String feedID){
		return db.getAllMatchValue(Database.DATASTREAM_INDEX, Database.colFeedID, feedID, Database.colChecked, "1", Database.colSensorID, false);
	}

	public void editDataValue(String feedID, String dataName, String newValue) {
		db.edit(Database.DATASTREAM_INDEX, feedID, dataName, Database.colDataValue, newValue);
	}

	public void putOfflineData(String feedID, String permission, Date date, List<String> dataNames, float[] dataValues) {
		String time = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(date);
		for(int i=0; i< dataNames.size(); i++){
			db.addDatapoint(feedID, permission, dataNames.get(i), time, String.valueOf(dataValues[i]));
		}
	}

	public String getFeedTitle(String[] info) {
		return db.getValue(Database.FEED_INDEX, info[0], info[1], Database.colFeedTitle);
	}

	// title, id, owned(Public, Private, None), access(View, Full), location("lon, lat, alt"), datacount
	public String[] getFeedInfo(String[] feed) {
		String title = db.getValue(Database.FEED_INDEX, feed[0], feed[1], Database.colFeedTitle);
		String owned = db.getValue(Database.FEED_INDEX, feed[0], feed[1], Database.colOwnership);
		String access = db.getValue(Database.FEED_INDEX, feed[0], feed[1], Database.colPermissionLevel);
		String location = db.getValue(Database.FEED_INDEX, feed[0], feed[1], Database.colLocation);
		int dataCount = db.getRowNum(Database.DATASTREAM_INDEX, Database.colFeedID, feed[1]);
		
		return new String[]{title, feed[1], owned, access, location, String.valueOf(dataCount)};
	}



}
