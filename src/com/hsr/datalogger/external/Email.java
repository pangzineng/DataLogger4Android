package com.hsr.datalogger.external;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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
	
	public boolean sendDiagram(String address, String[] info, String description){
		
		// SOS remember to toggle this for testing
		String imageName = info[0] + " - " + info[3];
		
		email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/DataLogger/" + imageName + ".png"));
		
		email.setType("text/plain"); 
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{address}); 
		email.putExtra(Intent.EXTRA_SUBJECT, context.getString(com.hsr.datalogger.R.string.email_diagram_title)); 
		email.putExtra(Intent.EXTRA_TEXT, context.getString(com.hsr.datalogger.R.string.email_diagram_opening) + 
										  context.getString(com.hsr.datalogger.R.string.email_diagram_feed_name) + info[0] + 
										  context.getString(com.hsr.datalogger.R.string.email_diagram_feed_ID) + info[1] + 
										  context.getString(com.hsr.datalogger.R.string.email_diagram_data_name) + info[3] + 
										  context.getString(com.hsr.datalogger.R.string.email_diagram_description) + description + 
										  context.getString(com.hsr.datalogger.R.string.email_diagram_sender) + info[2] +
										  context.getString(com.hsr.datalogger.R.string.email_diagram_ending)); 
		try{
			context.startActivity(Intent.createChooser(email, "Send mail using"));
		} catch (android.content.ActivityNotFoundException ex) {
			return false;
		}

		
		return true;
	}
	
}
