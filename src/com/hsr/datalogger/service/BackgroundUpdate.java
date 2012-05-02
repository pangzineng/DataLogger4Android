package com.hsr.datalogger.service;

import com.hsr.datalogger.Helper;
import com.hsr.datalogger.HelperLight;
import com.hsr.datalogger.hardware.HardwareHelper;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BackgroundUpdate extends Service {
	
	HelperLight helper;
	
	private int interval;
	private int runningTime;
	
	private String username;
	private String feedID;
	private String premission;
	
	private Handler mHandler = new Handler();
	private Runnable mTask = new Runnable(){

		public void run() {
			
			helper.update(feedID, premission);

			runningTime = runningTime - (int)(interval/60000);
			if(runningTime <= 0) stopSelf();
			mHandler.postDelayed(mTask, interval);
		}		
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent.hasExtra("STOP")) stopSelf();
	
		helper = new HelperLight(getApplicationContext());
		
		interval = intent.getIntExtra("Interval", 1) * 60000;
		runningTime = intent.getIntExtra("Running Time", 0);
		
		String[] info = intent.getStringArrayExtra("info");
		username = info[0];
		feedID = info[1];
		premission = info[2];
		
		mHandler.removeCallbacks(mTask);
		mHandler.postDelayed(mTask, 100);
	
		return 1;
	}
	
	@Override
	public void onDestroy() {
		mHandler.removeCallbacks(mTask);
		helper.closeBackground();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
