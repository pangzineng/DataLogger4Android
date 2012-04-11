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
	
	public boolean sendEmail(String address, String permission, String level, String[] info, Context dialog){
		email = new Email(dialog);
		return email.send(address, permission, level, info);
	}
}
