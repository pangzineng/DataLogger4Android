package com.hsr.datalogger.external;

import android.content.Context;
import android.content.Intent;

public class Email {
	
	Intent email;
	Context context;
	
	public Email(Context context) {
		email = new Intent(Intent.ACTION_SEND);
		this.context = context;
	}
	
	public boolean send(String address, String permission, String level, String[] info){

		email.setType("text/plain"); 
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{address}); 
		email.putExtra(Intent.EXTRA_SUBJECT, context.getString(com.hsr.datalogger.R.string.email_title)); 
		email.putExtra(Intent.EXTRA_TEXT, context.getString(com.hsr.datalogger.R.string.email_opening) + 
										  context.getString(com.hsr.datalogger.R.string.email_body_feed_name) + info[0] + 
										  context.getString(com.hsr.datalogger.R.string.email_body_feed_id) + info[1] + 
										  context.getString(com.hsr.datalogger.R.string.email_body_key) + permission + 
										  context.getString(com.hsr.datalogger.R.string.email_body_level) + level + 
										  context.getString(com.hsr.datalogger.R.string.email_body_sender) + info[2] + 
										  context.getString(com.hsr.datalogger.R.string.email_ending)); 

		try{
			context.startActivity(Intent.createChooser(email, "Send mail using"));
		} catch (android.content.ActivityNotFoundException ex) {
			return false;
		}
		return true;
	}
	
}
