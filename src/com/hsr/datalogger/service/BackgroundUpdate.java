package com.hsr.datalogger.service;

import com.hsr.datalogger.Helper;
import com.hsr.datalogger.hardware.HardwareHelper;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BackgroundUpdate extends Service {
	
	Helper helper;
	HardwareHelper hwH;
  //PachubeHelper paH;
	
	private int[] selected;
	private int interval;
	private int runningTime;
	
	private Handler mHandler = new Handler();
	private Runnable mTask = new Runnable(){

		public void run() {
			// FIXME everything about update
			
			
			// pass the value from Hardware component to Pachube component
			// paH.update(hwH.getSensorValue(selected));
			Log.d("pang", "Background is running");
			
			runningTime = runningTime - (int)(interval/60000);
			if(runningTime <= 0) stopSelf();
			mHandler.postDelayed(mTask, interval);
		}		
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent.hasExtra("STOP")) stopSelf();
	
		helper = new Helper(getApplicationContext());
		
		interval = intent.getIntExtra("Interval", 1) * 60000;
		runningTime = intent.getIntExtra("Running Time", 0);
		selected = intent.getIntArrayExtra("selected");
				
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

	private void putToOfflineStore(){
		
	}
}
