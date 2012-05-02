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
	
	public HelperLight(Context context) {
		dbH = new DatabaseHelper(context);
		hwH = new HardwareHelper(context);
	}
	
	/* 4. Feed Page Tab function & 6. Background function
	 * (1) update called by background service
	 * */
	public boolean update(String feedID, String premission) {
		// FIXME 
		
		// first get from dbH the selected datastream (name & sensorID)
		List<String> dataNames = dbH.getUpdateDataNames();
		List<String> sensors = dbH.getUpdateDataSensors();

		// second get from hwH the value matching the data list
		float[] dataValues = hwH.getSensorValue(sensors);
		
//		// [Main Work] then check internet condition
//		if(hwH.getNetworkCondition()){ // if internet is good
//		// update & clean datapoints table if any
//			startOfflineUpdate();
//		// call paH and pass the value
//			if(paH.update(feedID, premission, dataNames, dataValues)) { // if paH return true, update dbH with new value
//					for(int i=0; i<dataNames.size(); i++){
//						dbH.editDataValue(feedID, dataNames.get(i), String.valueOf(dataValues[i]));
//					}
//			  } else { // else return false, which is different from no-network/offline kind of failed update
//			 		return false;
//			  }	
//			} else { // else call dbH and pass the value to offline table
//				dbH.putOfflineData(feedID, premission, hwH.getSystemTime(), dataNames, dataValues);
//			}
		
		return true;
	}

	public void closeBackground() {
		hwH.stopListenToSensor();
	}
	
	/* 4. Feed Page Tab function
	 * (3) Clean Offline Data (when network is on)
	 * */
	
	public boolean startOfflineUpdate(){
		List<List<String[]>> datapoints = getOfflineData();
		
		if(datapoints==null) return true;
		
		// FIXME
		String[] dataNames = null;
		String feedID = null;
		String premission = null;
		
		if(paH.updateOffline(feedID, premission, dataNames, datapoints)){
			dbH.cleanDatapoint();
			return true;
		} else {
			return false;
		}
	}

	// FIXME big problem here,the update needs premission
	// if there are more than one feed in the offline storage, it will be big error
	// need to change the database table, paH passing method, and helper
	public List<List<String[]>> getOfflineData(){
		List<String> datas = dbH.getOfflineData();
		
		if(datas==null) return null;
		
		List<List<String[]>> mList = new ArrayList<List<String[]>>();
		
		for(int i=0; i<datas.size(); i++){
			List<String[]> temp = new ArrayList<String[]>();
			List<String> times = dbH.getOfflineTime(datas.get(i));
			List<String> values = dbH.getOfflineValue(datas.get(i));
			for(int j=0; j<times.size(); j++){
				temp.add(new String[]{times.get(j), values.get(j)});
			}
			mList.add(temp);
		}
		return mList;
	}

}
