package com.hsr.datalogger.service;

import com.hsr.datalogger.cache.CacheHelper;
import com.hsr.datalogger.hardware.HardwareHelper;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class BackgroundUpdate extends Service {
	
	HardwareHelper hwH;
	CacheHelper caH;
  //PachubeHelper paH;
	
	int[] selected;
	
	private int interval;
	private int runningTime;
	
	private Handler mHandler = new Handler();
	private Runnable mTask = new Runnable(){

		public void run() {
			
			// pass the value from Hardware component to Pachube component
			// paH.update(hwH.getSensorValue(selected));
			
			runningTime = runningTime - (int)(interval/60000);
			if(runningTime <= 0) stopSelf();
			mHandler.postDelayed(mTask, interval);
		}		
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent.hasExtra("STOP")) stopSelf();
	
		interval = intent.getIntExtra("Interval", 1) * 60000;
		runningTime = intent.getIntExtra("Running Time", 0);
		
		hwH = new HardwareHelper(getApplicationContext());
		caH = new CacheHelper(getApplicationContext());
	  //paH = new PachubeHelper();
		
		selected = caH.getSelectedSensor();
		
		mHandler.removeCallbacks(mTask);
		mHandler.postDelayed(mTask, 100);
	
		return 1;
	}
	
	@Override
	public void onDestroy() {
		mHandler.removeCallbacks(mTask);
		hwH.stopListenToSensor();
	}

	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
