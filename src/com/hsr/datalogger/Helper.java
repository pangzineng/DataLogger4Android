package com.hsr.datalogger;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.hsr.datalogger.cache.CacheHelper;
import com.hsr.datalogger.database.DatabaseHelper;
import com.hsr.datalogger.external.ExternalHelper;
import com.hsr.datalogger.hardware.HardwareHelper;
import com.hsr.datalogger.service.ServiceHelper;

public class Helper {

	Context context;
	
	CacheHelper caH;
	DatabaseHelper dbH;
	HardwareHelper hwH;
	ServiceHelper srH;
	ExternalHelper exH;
  //PachubeHelper paH;
	
	public Helper(Context context) {
		caH = new CacheHelper(context);
		dbH = new DatabaseHelper(context);
		hwH = new HardwareHelper(context);
		srH = new ServiceHelper(context);
		exH = new ExternalHelper(context);
	  //paH = new PachubeHelper(context);
		
		this.context = context;
	}
	
	
	/* handle different cases for login & logout
	 * (1) login/out dialog
	 * */ 
	
	public void logout(){
		caH.removeAutoLogin();
		caH.setCurrentUser(new String[]{"guest"});
	}
	
	public boolean login(String[] account, boolean checked){
		// FIXME if(paH.login(account)){  // to checked whether user change name&pw somewhere else 
					if(checked){
						caH.setAutoLogin(true, account);
					} else {
						caH.removeAutoLogin();
					}
					caH.setCurrentUser(account);
					return true;
		//		  }	else {
		// 			return false;
		//		  {
	}
	
	/* handle different cases for login & logout
	 * (2) action bar title
	 * */
	
	public String AutoLoginAccount(){
		String[] autoAccount = caH.getAutoLogin();
		if(autoAccount == null){
			caH.setCurrentUser(new String[]{"guest"});
			return "guest";
		} else {
			// FIXME if(paH.login(autoAccount)){  // to checked whether user change name&pw somewhere else 
						caH.setCurrentUser(autoAccount);	
			//		 } else {	// this means the name&pw in database for autologin aren't correct, need to remove
			//			caH.removeAutoLogin();
			//			caH.setCurrentUser(new String[]{"guest"});
			//			return null;			
			//		 }
		}
		return autoAccount[0];
	}
	
	
	// run for every launch when the sensor list is needed for the display of the "Add Datastream" spinner
	public String[] setSensorForDevice(){
		// will only run once for the first launch of the application to settle the sensor info of this device
		if(!caH.detectSensor()){
			Log.d("pang", "should run for the first time");
			dbH.storeSensorStatus(hwH.getAvailableSensor());
		}
		
		return dbH.getSensorForDevice();
	}

	// [NOTICE] This one should be remove later when the PachubeHelper is done, just duplicate method here 
	public String createPermission(String selectedLevel){
		String key = null;
		//String key = paH.createPermission(caH.getEmailInfo, selectedLevel);
		return key;
	}
	
	public boolean sendEmail(String address, String selected, Context dialog){
		return exH.sendEmail(address, createPermission(selected), selected, caH.getInfoForEmail(), dialog);
	}

	public boolean sendDiagramEmail(String address, String description, Context dialog, ImageView diagram){
		
		return exH.sendDiagramEmail(address, new String[]{"Office", "250250", "Peter Pang", "noise level"}, description, dialog, diagram);

		//return exH.sendDiagramEmail(address, caH.getInfoForEmail(), description, dialog, diagram);
	}
	
	public String getDiagramDuration(){
		return caH.getDataInfoForDiagram()[2];
	}
	
	public void setDiagramDuration(String duration){
		caH.setDiagramDuration(duration);
	}

	/* SOS there should be a better way to communicate between fragment
	 * in this case, I can only use Helper to get ImageView from main fragment, and called by dialog.
	 * Think twice, now believe this is a pretty good way. Because the ImageView need to communicate 
	 * with pachube & cache components all the time. Now I just bring one ImageView to Helper layer,
	 * it's better to have no external element in this layer, but it's the best I can get now
	 * */
	ImageView v;
	
	public void tempStore(ImageView view) {
		v = view;
	}

	public void reDraw(){
		Drawable diagram = getDiagram();
		
		// only for testing
		diagram = context.getResources().getDrawable(R.drawable.icon);
		
		v.setImageDrawable(diagram);
	}
	
	public Drawable getDiagram(){
		
		// include feed id, data stream name and duration
		String[] para1 = caH.getDataInfoForDiagram();
		// include screen size and time zone
		int[] para2 = exH.getDeviceInfo();
		
		// this method will set the duration to 1hour for the first launch
		if(para1[2]==null){
			caH.setDiagramDuration("1hour");
			para1 = caH.getDataInfoForDiagram();
		}
		
		// this method will adjust the screen size number (the height actually) to match the pachube requirement <300,000px
		while(para2[0] * para2[1] > 300000){
			para2[1] -= 100;
		}
		
		Drawable diagram = null;
		
		// diagram = paH.getDiagram(para1, para2);
		return diagram;
	}
	
	public void startUpdateData(int interval, int runningTime) {
		String FeedName = caH.getCurrentFeedInfo()[1];
		
		// SOS this code need change, it should be the selected datastream. we will allow multiple data binded with one sensor
		// if user selected two datastream that connect with the same sensor, we should alert the case but still allow to continue
		// this means one update should have the unit of "1 datastream -- the sensor allocated". code should went through the datastream to get the sensor type
		int[] selected = caH.getSelectedSensor();
		
		srH.startBackgroundUpdate(FeedName, interval, runningTime, selected);
		
	}


	/* For the feed list in the first tab
	 * */
	
	public String[] getFeedListItem(String feedID) {
		String currentUser = caH.getCurrentUser()[0];
		return dbH.getOneFeedInfo(currentUser, feedID);
	}


	public List<String> getFeedList() {
		String currentUser = caH.getCurrentUser()[0];
		return dbH.getCurrentFeedList(currentUser);
	}


	public boolean feedCreate(String title, String type, String ownership) {
		String[] user = caH.getCurrentUser();
		String feedID = null; // FIXME  = paH.create(user, title, status); // should return the feed ID or null if fail
		if(feedID != null){   
			dbH.addFeedToList(user[0], feedID, ownership, null, "Full", title, type);
		} else {	
			return false;			
		}
		return true;
	}


	public boolean feedImport(String feedID, String permission) {
		String[] feed = null; // FIXME  = paH.getFeed(feedID, permission); // should include feedName and premission level(View, Full), return null if fail
		String[] user = caH.getCurrentUser();
		if(feed != null){
			dbH.addFeedToList(user[0], feedID, "None", permission, feed[1], feed[0], "Sensor");
		} else {
			return false;
		}
		return true;
	}
}
