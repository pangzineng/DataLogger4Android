package com.hsr.datalogger.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

	static final String dbName = "PachubeDB";
	
	static final String accountTable = "Accounts";
	static final String colUsername = "Username";  // key
	static final String colPassword = "Password";
	
	static final String feedTable = "Feeds";
	static final String colFeedID = "FeedID";	  // Key
	static final String colFeedTitle = "FeedName";
	static final String colFeedType = "FeedType";
	static final String colFeedStatus = "FeedStatus";
	static final String colPermission = "FeedPermissionKey";
	
	static final String datastreamTable = "FeedDataStreams";
	static final String colDataName = "FeedDataStreamName";	// Key
	static final String colDataValue = "FeedDataStreamCurrentValue";
	static final String colDataTag = "FeedDataTag";			// This will match the sensor type in integer
	
	static final String datapointTable = "DataPoints";
  //static final String colDataIndex = "Feed Data Index";	// Key
	static final String colDPTimestamp = "DatapointTime";	// Key
	static final String colDPValue = "DatapointValue";
	
	/*
	 * 	This list right here is easier to access than the string array from resource
		Before a better way to store it in strings.xml is found or Android System upgrade and provide a better feature
		This list will remain here
	 * 
	 * */

	static final String sensorTable = "API14Sensors";
	static final String colSensorName = "SensorTypeName";
	static final String colSensorID = "SensorIDinAndroid"; // KEY
	static final String colAvailable = "SensorAvailable";
	
	
	public static final int ACCOUNT_INDEX = 0;
	public static final int FEED_INDEX = 1;
	public static final int DATASTREAM_INDEX = 2;
	public static final int DATAPOINT_INDEX = 3;
	public static final int SENSOR_INDEX = 4;
	
	public Database(Context context) {
		super(context, dbName, null, 1);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if(!db.isReadOnly()){
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		// CREATE the account table
		db.execSQL(	"CREATE TABLE " + accountTable +
					" (" + colUsername + " TEXT PRIMARY KEY, " +
						   colPassword + " TEXT NOT NULL);"
					);

		
		// CREATE the feed table
		db.execSQL(	"CREATE TABLE " + feedTable +
					" (" + colUsername + " TEXT NOT NULL, " +
				           colFeedID + " TEXT NOT NULL, " +
				           colPermission + " TEXT, "  + 
				           colFeedTitle + " TEXT NOT NULL, " +
				           colFeedType + " TEXT NOT NULL, " +
				           colFeedStatus + " TEXT NOT NULL, " +
				           "PRIMARY KEY (" + colUsername + ", " + colFeedID +"));"
					);
	

		// CREATE the data table
		db.execSQL(	"CREATE TABLE " + datastreamTable +
					" (" + colFeedID + " TEXT, " +
						   colDataName + " TEXT, " +
						   colDataValue + " FLOAT NOT NULL, " +
						   colDataTag + " TEXT NOT NULL, " +
						   "PRIMARY KEY (" + colFeedID + ", " + colDataName +"));"
				   );
		
		// CREATE the datapoint table
		db.execSQL(	"CREATE TABLE " + datapointTable +
					" (" + colDataName + " TEXT, " +
					   	   colDPTimestamp + " TEXT, " +
					       colDPValue + " FLOAT NOT NULL, " + 
			               "PRIMARY KEY (" + colDataName + ", " + colDPTimestamp +"));"
			   );
		
		// CREATE the sensor table
		db.execSQL("CREATE TABLE " + sensorTable +
				   " (" + colSensorID + " INTEGER PRIMARY KEY, " +
				   		  colSensorName + " TEXT NOT NULL, " +
				   		  colAvailable + " BLOB);"
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS " + accountTable);
		db.execSQL("DROP TABLE IF EXISTS " + feedTable);
		db.execSQL("DROP TABLE IF EXISTS " + datastreamTable);
		db.execSQL("DROP TABLE IF EXISTS " + datapointTable);
		
		onCreate(db);
	}
	
	/* Database writing. Add & Delete accounts and feeds.
	 * Update the default feed of certain account from null.
	 * */
	
	// Add account
	public int Add(String name, String password){
		ContentValues cv = new ContentValues();
		cv.put(colUsername, name);
		cv.put(colPassword, password);
		
		SQLiteDatabase db = this.getWritableDatabase();
		long rowID = db.insert(accountTable, colUsername, cv);
		db.close();
		
		return (int)rowID;
	}
	
	// Add feed
	public int Add(String name, String feedID, String feedTitle, String feedType, String feedStatus, String Permission){
		ContentValues cv = new ContentValues();
		cv.put(colUsername, name);
		cv.put(colFeedID, feedID);
		cv.put(colFeedTitle, feedTitle);
		cv.put(colFeedType, feedType);
		cv.put(colFeedStatus, feedStatus);
		cv.put(colPermission, Permission);
		
		SQLiteDatabase db = this.getWritableDatabase();
		long rowID = db.insert(feedTable, colFeedID, cv);
		db.close();
		
		return (int)rowID;
	}
	
	// Add data
	public int Add(String feedID, String dataName, String currentValue, String dataTag){
		ContentValues cv = new ContentValues();
		cv.put(colFeedID, feedID);
		cv.put(colDataName, dataName);
		cv.put(colDataValue, currentValue);
		cv.put(colDataTag, dataTag);
		
		SQLiteDatabase db = this.getWritableDatabase();
		long rowID = db.insert(datastreamTable, colDataName, cv);
		db.close();
		
		return (int)rowID;
	}
	
	// Add datapoint
	public int Add(String dataName, String timestamp, String value){
		ContentValues cv = new ContentValues();
		cv.put(colDataName, dataName);
		cv.put(colDataValue, value);
		cv.put(colDPTimestamp, timestamp);
		
		SQLiteDatabase db = this.getWritableDatabase();
		long rowID = db.insert(datapointTable, colDPTimestamp, cv);
		db.close();
		
		return (int)rowID;
	}
	
	// Add sensor
	public void Add(String sensorName, int sensorID, boolean available){
		ContentValues cv = new ContentValues();
		cv.put(colSensorName, sensorName);
		cv.put(colSensorID, sensorID);
		cv.put(colAvailable, available?"1":"0");
		
		SQLiteDatabase db = this.getWritableDatabase();
		long log = db.insert(sensorTable, colSensorID, cv);
		Log.d("pang", "SensorAdd: " + log);
		db.close();
	}
	
	// Delete 
	public int DeleteRow(int tableIndex, String key1, String key2){
		
		String tableName = "";
		String key1Name = "";
		String key2Name = "";

		switch(tableIndex){
			case ACCOUNT_INDEX:
				tableName = accountTable;
				key1Name = colUsername;
				key2Name = colPassword;
				break;
			case FEED_INDEX:
				tableName = feedTable;
				key1Name = colUsername;
				key2Name = colFeedID;
				break;
			case DATASTREAM_INDEX:
				tableName = datastreamTable;
				key1Name = colFeedID;
				key2Name = colDataName;
				break;
			case DATAPOINT_INDEX:
				tableName = datapointTable;
				key1Name = colDataName;
				key2Name = colDPTimestamp;
				break;
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		int result = db.delete(tableName, key1Name + "=? AND " + key2Name + "=?", new String[]{key1, key2});
		db.close();
		return result;
	}
	
	public int DeleteOfflineData(){
		SQLiteDatabase db = this.getWritableDatabase();
		int result = db.delete(datapointTable, null, null);
		db.close();
		return result;
	}
	
	// Update
	public int Edit(int tableIndex, String key1, String key2, String colName, String newValue){

		String tableName = "";
		String key1Name = "";
		String key2Name = "";

		switch(tableIndex){
			case ACCOUNT_INDEX:
				tableName = accountTable;
				key1Name = colUsername;
				key2Name = colPassword;
				break;
			case FEED_INDEX:
				tableName = feedTable;
				key1Name = colUsername;
				key2Name = colFeedID;
				break;
			case DATASTREAM_INDEX:
				tableName = datastreamTable;
				key1Name = colFeedID;
				key2Name = colDataName;
				break;
			case DATAPOINT_INDEX:
				tableName = datapointTable;
				key1Name = colDataName;
				key2Name = colDPTimestamp;
				break;
		}
		
		ContentValues cv = new ContentValues();
		cv.put(key1Name, key1);
		cv.put(key2Name, key2);
		cv.put(colName, newValue);

		SQLiteDatabase db = this.getWritableDatabase();
		int result = db.update(tableName, cv, key1Name + "=? AND " + key2Name + "=?", new String[]{key1, key2});
		db.close();
		return result;
	}
	
		
	
	/* Database reading. Get certain account info & feed info.
	 * Direct result will be return instead of cursor.
	 * */
	
	public String getValue(int tableIndex, String key1, String key2, String colName){
		
		String tableName = "";
		String key1Name = "";
		String key2Name = "";

		switch(tableIndex){
			case ACCOUNT_INDEX:
				tableName = accountTable;
				key1Name = colUsername;
				key2Name = colPassword;
				break;
			case FEED_INDEX:
				tableName = feedTable;
				key1Name = colUsername;
				key2Name = colFeedID;
				break;
			case DATASTREAM_INDEX:
				tableName = datastreamTable;
				key1Name = colFeedID;
				key2Name = colDataName;
				break;
			case DATAPOINT_INDEX:
				tableName = datapointTable;
				key1Name = colDataName;
				key2Name = colDPTimestamp;
				break;
			case SENSOR_INDEX:
				tableName = sensorTable;
				key1Name = colSensorID;
				key2Name = colSensorName; // not primary key, just to match format
				break;
		}

		String query = "SELECT " + colName +  
				   " FROM " + tableName +
				   " WHERE " + key1Name + " =? AND " + key2Name + " =?";
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery(query, new String[]{key1, key2});
		if(cur == null || cur.moveToFirst() == false) return null;
		String result = cur.getString(cur.getColumnIndex(colName));
		db.close();

		return result;
	}
	
	public int getRowNum(int tableIndex, String keyName, String keyValue){
		
		String tableName = "";
		switch(tableIndex){
			case ACCOUNT_INDEX:
				tableName = accountTable;
				break;
			case FEED_INDEX:
				tableName = feedTable;
				break;
			case DATASTREAM_INDEX:
				tableName = datastreamTable;
				break;
			case DATAPOINT_INDEX:
				tableName = datapointTable;
				break;
			case SENSOR_INDEX:
				tableName = sensorTable;
				break;
		}

		SQLiteDatabase db = this.getReadableDatabase();
		String query = "";
		Cursor cur = null;
		
		if(keyName == null){
			query = "SELECT * FROM " + tableName;
			cur = db.rawQuery(query, null);
		} else {
			query = "SELECT * FROM " + tableName + " WHERE " + keyName + " =?";
			cur = db.rawQuery(query, new String[]{keyValue});
		}
		
		if(cur == null || cur.moveToFirst() != true) return -1;
		db.close();
		return cur.getCount();
	}
	
	public List<String> getAllMatchValue(int tableIndex, String keyName, String keyValue, String toGetCol){
		
		List<String> allValues = new ArrayList<String>();
		
		String tableName = "";
		switch(tableIndex){
			case ACCOUNT_INDEX:
				tableName = accountTable;
				break;
			case FEED_INDEX:
				tableName = feedTable;
				break;
			case DATASTREAM_INDEX:
				tableName = datastreamTable;
				break;
			case DATAPOINT_INDEX:
				tableName = datapointTable;
				break;
			case SENSOR_INDEX:
				tableName = sensorTable;
				break;
		}
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = null;
		String query = "";

		if(keyName == null){
			query = "SELECT * FROM " + tableName;
			cur = db.rawQuery(query, null);
		} else {
			query = "SELECT * FROM " + tableName + " WHERE " + keyName + " =?";
			cur = db.rawQuery(query, new String[]{keyValue});
		}
		
		Log.d("pang", "getAllMatch still runing A");

		
		if(cur == null || cur.moveToFirst() == false) return null;
		
		Log.d("pang", "getAllMatch still runing B");
		Log.d("pang", "Rows: " + cur.getCount());
		int colIndex = cur.getColumnIndex(toGetCol);
		
		while(cur.isAfterLast() != true){
			allValues.add(cur.getString(colIndex));
			cur.moveToNext();
		}
		
		db.close();
		return allValues;
	}
}