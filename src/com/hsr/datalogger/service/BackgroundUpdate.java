package com.hsr.datalogger.service;

import com.hsr.datalogger.Helper;
import com.hsr.datalogger.HelperLight;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class BackgroundUpdate extends Service {
	
	HelperLight helper;
	
	private int interval;
	private int runningTime;
	
	//private String masterKey;
	private String[] info;
	
	private Handler mHandler = new Handler();
	private Runnable mTask = new Runnable(){

		public void run() {
			
			helper.update(info);

			runningTime = runningTime - (int)(interval/60000);
			if(runningTime < 0) {
				mHandler.removeCallbacks(mTask);
				helper.closeBackground(info);
				stopSelf();
			} else {
				mHandler.postDelayed(mTask, interval);
			}
		}		
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent.hasExtra("STOP")) {
			helper.closeBackground(info);
			helper.closeNoti();
			stopSelf();
		} else {
			helper = Helper.helperL;
			
			interval = intent.getIntExtra("Interval", 1) * 60000;
			runningTime = intent.getIntExtra("Running Time", 0);
			info = intent.getStringArrayExtra("info");
			
			mHandler.removeCallbacks(mTask);
			mHandler.postDelayed(mTask, 10);
		}
		return 1;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
