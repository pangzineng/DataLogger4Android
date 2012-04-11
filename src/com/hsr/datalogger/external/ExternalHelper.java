package com.hsr.datalogger.external;

import android.content.Context;

public class ExternalHelper {

	NetworkLocation nl;
	Email email;
	Context context;
	
	public ExternalHelper(Context context) {
		this.context = context;
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
	public boolean sendEmail(String address, String[] info, String description, Context dialog) {
		email = new Email(dialog);
		return email.sendDiagram(address, info, description);
	}
}
