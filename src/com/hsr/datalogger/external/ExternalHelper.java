package com.hsr.datalogger.external;

import java.util.Date;

import android.content.Context;
import android.widget.ImageView;

public class ExternalHelper {

	LocationReport nl;
	Email email;
	DeviceInfo deviceInfo;
	ImageDownload image;
	NetworkCondition network;
	
	Context context;
	
	public ExternalHelper(Context context) {
		this.context = context;
		deviceInfo = new DeviceInfo(context);
		image = new ImageDownload();
		network = new NetworkCondition(context);
	}

	public void turnOnLocation(){
		nl = new LocationReport(context);
	}
	
	// send permission email
	public boolean sendEmail(String address, String permission, String level, String[] info, Context dialog){
		email = new Email(dialog);
		return email.send(address, permission, level, info);
	}

	// send diagram email
	public boolean sendDiagramEmail(String address, String[] info, String description, Context dialog, ImageView diagram) {
		image.startDownload(diagram, info[0]+" - "+info[3]);
		email = new Email(dialog);
		return email.sendDiagram(address, info, description);
	}
	
	public int[] getDeviceInfo(){
		int[] size = deviceInfo.getScreenSize();
		int zone = deviceInfo.getTimezone();
		return new int[]{size[0], size[1], zone};
	}
	
	public boolean getNetworkCondition(){
		return network.getNetworkInfo();
	}
	
	public Date getSystemTime(){
		return deviceInfo.getSystemTime();
	}
}
