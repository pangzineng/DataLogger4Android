package com.hsr.datalogger;

import android.content.Context;

import com.hsr.datalogger.cache.CacheHelper;
import com.hsr.datalogger.database.DatabaseHelper;
import com.hsr.datalogger.external.ExternalHelper;
import com.hsr.datalogger.hardware.HardwareHelper;
import com.hsr.datalogger.service.ServiceHelper;

public class Helper {

	CacheHelper caH;
	DatabaseHelper dbH;
	HardwareHelper hwH;
	ServiceHelper srH;
	ExternalHelper exH;
  //PachubeHelper paH;
	
	public Helper(Context context) {
		caH = new CacheHelper(context);
		dbH = new DatabaseHelper(context);
		hwH = new HardwareHelper(context);
		srH = new ServiceHelper(context);
		exH = new ExternalHelper(context);
	  //paH = new PachubeHelper(context);
	}
	
	// run for every launch when the sensor list is needed for the display of the "Add Datastream" spinner
	public String[] setSensorForDevice(){
		// will only run once for the first launch of the application to settle the sensor info of this device
		if(!caH.detectSensor()){
			dbH.storeSensorStatus(hwH.getAvailableSensor());
		}
		
		return dbH.getSensorForDevice();
	}

	// [NOTICE] This one should be remove later when the PachubeHelper is done, just duplicate method here 
	public String createPermission(String selectedLevel){
		String key = null;
		//String key = paH.createPermission(caH.getEmailInfo, selectedLevel);
		return key;
	}
	
	public boolean sendEmail(String address, String selected){
		return exH.sendEmail(address, createPermission(selected), selected, caH.getInfoForEmail());
	}
}
