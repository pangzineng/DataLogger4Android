package com.hsr.datalogger.external;

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
	
	// lon, lat, alt
	public double[] getLocation(){
		turnOnLocation();
		if(nl.getLocation()==null) return new double[]{0,0,0};
		return new double[]{nl.getLon(),nl.getLat(),nl.getAlt()};
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
