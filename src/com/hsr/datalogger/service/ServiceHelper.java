package com.hsr.datalogger.service;

import android.content.Context;
import android.content.Intent;

public class ServiceHelper {

	Context context;
	NotificationBar noti;
	
	public ServiceHelper(Context context) {
		this.context = context;
	}
	
	public void startBackgroundUpdate(String FeedName, int interval, int runningTime){
		
		createNotification(FeedName, runningTime);
		
		Intent startBackground = new Intent(context, BackgroundUpdate.class);
		startBackground.putExtra("Interval", interval);
		startBackground.putExtra("Running Time", runningTime);
		context.startService(startBackground);
	}
	
	public void stopBackgroundUpdate(){
		
		closeNoti();

		Intent stopBackground = new Intent(context, BackgroundUpdate.class);
		stopBackground.putExtra("STOP", true);
		context.startService(stopBackground);
	}
	
	public void createNotification(String FeedName, int runningTime){
		noti = new NotificationBar(context, FeedName, runningTime);
	}
	
	private void closeNoti(){
		noti.closeNoti();
	}
}
