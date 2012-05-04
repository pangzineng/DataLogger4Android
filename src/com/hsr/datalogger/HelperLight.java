package com.hsr.datalogger;

import java.util.ArrayList;
import java.util.List;

import com.hsr.datalogger.database.DatabaseHelper;
import com.hsr.datalogger.hardware.HardwareHelper;
import com.hsr.datalogger.pachube.PachubeHelper;

import android.content.Context;

public class HelperLight {

	DatabaseHelper dbH;
	HardwareHelper hwH;
	PachubeHelper paH;
	
	public HelperLight(Context context, String masterKey) {
		dbH = new DatabaseHelper(context);
		hwH = new HardwareHelper(context);
		paH = new PachubeHelper(masterKey);
	}
	
	/* 4. Feed Page Tab function & 6. Background function
	 * (1) update called by background service
	 * */
	public boolean update(String feedID, String permission) {
		// FIXME 
		
		// first get from dbH the selected datastream (name & sensorID)
		List<String> dataNames = dbH.getUpdateDataNames(feedID);
		List<String> sensors = dbH.getUpdateDataSensors(feedID);

		// second get from hwH the value matching the data list
		float[] dataValues = hwH.getSensorValue(sensors);

		// [Main Work] then check internet condition
		if(hwH.getNetworkCondition()){ // if internet is good
		// update & clean datapoints table if any
			startOfflineUpdate();
		// call paH and pass the value
			if(paH.update(feedID, permission, dataNames, dataValues)) { // if paH return true, update dbH with new value
				for(int i=0; i<dataNames.size(); i++){
					dbH.editDataValue(feedID, dataNames.get(i), String.valueOf(dataValues[i]));
				}
			  } else { // else return false, which is different from no-network/offline kind of failed update
			 		return false;
			  }	
			} else { // else call dbH and pass the value to offline table
				dbH.putOfflineData(feedID, permission, hwH.getSystemTime(), dataNames, dataValues);
			}
		
		return true;
	}

	public void closeBackground() {
		hwH.stopListenToSensor();
	}
	
	/* 4. Feed Page Tab function
	 * (3) Clean Offline Data (when network is on)
	 * */
	List<String> allFeedID;
	List<String> allPermi;
	List<List<String>> allDatanames;
	List<List<List<String[]>>> allList;

	public boolean startOfflineUpdate(){
		
		if(!getOfflineData()){
			return false;
		}
		
		for(int i=0; i<allFeedID.size(); i++){
			if(!paH.updateOffline(allFeedID.get(i), allPermi.get(i), allDatanames.get(i), allList.get(i))){
				return false;
			}
		}

		dbH.cleanDatapoint();
		return true;
	}

	
	// FIXME big problem here,the update needs permission
	// if there are more than one feed in the offline storage, it will be big error
	// need to change the database table, paH passing method, and helper
	public boolean getOfflineData(){
		
		// FYI
//		List<List<List<String[]>>> allFeed = new ArrayList<List<List<String[]>>>();
//		List<List<String[]>> allDataOfOneFeed = new ArrayList<List<String[]>>();
//		List<String[]> allDatapointsOfOneData = new ArrayList<String[]>();
//		String[] oneDatapoint = new String[]{"time", "value"};
		
		allFeedID = dbH.getOfflineFeedID();
		if(allFeedID==null) return false;
		allPermi = dbH.getOfflinePermission(allFeedID);
		allList = new ArrayList<List<List<String[]>>>();
		allDatanames = new ArrayList<List<String>>();
		
		// each loop fill in the data streams with this one feed
		for(int j=0; j<allFeedID.size(); j++){
			
			List<String> allDatanameOfOneFeed = dbH.getOfflineData(allFeedID.get(j));
			allDatanames.add(allDatanameOfOneFeed);
			
			List<List<String[]>> listOfDatas = new ArrayList<List<String[]>>();
			// each loop fill in the data points with this one data stream
			for(int k=0; k<allDatanameOfOneFeed.size(); k++){
				List<String> allDatapointsTimeOfOneData = dbH.getOfflineDatapointTime(allFeedID.get(j), allDatanameOfOneFeed.get(k));
				List<String> allDatapointsValueOfOneData = dbH.getOfflineDatapointValue(allFeedID.get(j), allDatanameOfOneFeed.get(k));
				List<String[]> allDatapointsOfOneData = new ArrayList<String[]>();

				// each loop merge the time and value of one datapoint into String[] and add to the this one datapoint
				for(int m=0; m<allDatapointsTimeOfOneData.size(); m++){
					allDatapointsOfOneData.add(new String[]{allDatapointsTimeOfOneData.get(m), allDatapointsValueOfOneData.get(m)});
				}

				// put all datapoints in one data
				listOfDatas.add(allDatapointsOfOneData);
			}
			
			// put all datas in one feed
			allList.add(listOfDatas);
		}
		
		return true;
	}

}
