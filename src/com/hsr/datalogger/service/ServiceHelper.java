package com.hsr.datalogger.service;

import android.content.Context;
import android.content.Intent;

public class ServiceHelper {

	Context context;
	NotificationBar noti;
	
	public ServiceHelper(Context context) {
		this.context = context;
	}
	
	public void startBackgroundUpdate(String FeedName, int interval, int runningTime, String[] info, String masterKey){
		
		createNotification(FeedName, runningTime, info[0]);
		
		Intent startBackground = new Intent(context, BackgroundUpdate.class);
		startBackground.putExtra("Interval", interval);
		startBackground.putExtra("Running Time", runningTime);
		startBackground.putExtra("info", info);
		startBackground.putExtra("master", masterKey);
		context.startService(startBackground);
	}
	
	public void stopBackgroundUpdate(){
		
		closeNoti();

		Intent stopBackground = new Intent(context, BackgroundUpdate.class);
		stopBackground.putExtra("STOP", true);
		context.startService(stopBackground);
	}
	
	public void createNotification(String FeedName, int runningTime, String user){
		noti = new NotificationBar(context, FeedName, runningTime, user);
	}
	
	private void closeNoti(){
		noti.closeNoti();
	}
}
