package com.hsr.datalogger.external;

import java.util.Date;

import com.hsr.datalogger.hardware.DeviceInfo;
import com.hsr.datalogger.hardware.NetworkCondition;

import android.content.Context;
import android.widget.ImageView;

public class ExternalHelper {

	LocationReport nl;
	Email email;
	ImageDownload image;
	
	Context context;
	
	public ExternalHelper(Context context) {
		this.context = context;
		image = new ImageDownload();
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
	
}
