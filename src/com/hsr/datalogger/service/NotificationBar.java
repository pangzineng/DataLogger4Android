package com.hsr.datalogger.service;

import com.hsr.datalogger.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class NotificationBar {

	NotificationManager nm;
	Notification nt;
	PendingIntent pnoti;
	Context context;
	
	String FeedName;
	int runningTime;
	
	int time;
	private Handler mHandler = new Handler();
	private Runnable mTask = new Runnable(){

		public void run() {
			updateTimer();
			mHandler.postDelayed(mTask, 60000);
		}
		
	};

	
	
	public NotificationBar(Context context, String FeedName, int runningTime) {
		
		time = 0;
		this.context = context;
		this.FeedName = FeedName;
		
		nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nt = new Notification(R.drawable.icon, "Datalogger is updating", System.currentTimeMillis());
		nt.flags |= Notification.FLAG_AUTO_CANCEL;
		
		Intent noti = new Intent(context, BackgroundUpdate.class);
		noti.putExtra("STOP", true);
		pnoti = PendingIntent.getService(context, 0, noti, PendingIntent.FLAG_ONE_SHOT);
		
		updateTimer();
		mHandler.removeCallbacks(mTask);
		mHandler.postDelayed(mTask, 60000);

	}
	
	private void updateTimer(){
		time ++;
		nt.setLatestEventInfo(context, "\"" + FeedName + "\" runs for " + time + " mintues.", "Click to stop update or AutoStop in " + (runningTime-time) + " minutes", pnoti);
		nm.notify(0, nt);
	}
	
	public void closeNoti(){
		mHandler.removeCallbacks(mTask);
		nm.cancel(0);
	}
}
