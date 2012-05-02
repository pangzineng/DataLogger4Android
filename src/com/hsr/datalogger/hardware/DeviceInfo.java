package com.hsr.datalogger.hardware;

import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class DeviceInfo {

	Context mContext;
	private Display display;
	private TimeZone timezone;
	private Date current;
	
	public DeviceInfo(Context context) {
		mContext = context;
		display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		timezone = TimeZone.getDefault();
	}
	
	public int[] getScreenSize(){
		Point point = new Point();
		display.getSize(point);
		return new int[]{point.x, point.y};
	}
	
	public int getTimezone(){
		return (int)(timezone.getRawOffset()/3600000);
	}
	
	public Date getSystemTime(){
		current = new Date();
		return current;
	}
	
}
