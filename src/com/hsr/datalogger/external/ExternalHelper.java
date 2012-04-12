package com.hsr.datalogger.external;

import android.content.Context;
import android.widget.ImageView;

public class ExternalHelper {

	NetworkLocation nl;
	Email email;
	DeviceInfo deviceInfo;
	ImageDownload image;
	
	Context context;
	
	public ExternalHelper(Context context) {
		this.context = context;
		deviceInfo = new DeviceInfo(context);
		image = new ImageDownload();
	}

	public void turnOnLocation(){
		nl = new NetworkLocation(context);
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
	
	// SOS this will 99% crash the diagram, because pachube only allow 300,000px, which means < 730 * 411
	public int[] getDeviceInfo(){
		int[] size = deviceInfo.getScreenSize();
		int zone = deviceInfo.getTimezone();
		return new int[]{size[0], size[1], zone};
	}
}
