package com.hsr.datalogger.external;

import android.content.Context;

public class ExternalHelper {

	NetworkLocation nl;
	Email email;
	
	public ExternalHelper(Context context) {
		nl = new NetworkLocation(context);
		email = new Email(context);
	}
	
	public boolean sendEmail(String address, String permission, String level, String[] info){
		return email.send(address, permission, level, info);
	}
}
