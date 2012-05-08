package com.hsr.datalogger;

import java.util.Arrays;
import java.util.List;

import android.app.LoaderManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.hsr.datalogger.FeedList.FLFragment;
import com.hsr.datalogger.FeedPage.FPFragment;
import com.hsr.datalogger.cache.Cache;
import com.hsr.datalogger.cache.CacheHelper;
import com.hsr.datalogger.database.DatabaseHelper;
import com.hsr.datalogger.external.ExternalHelper;
import com.hsr.datalogger.hardware.HardwareHelper;
import com.hsr.datalogger.pachube.PachubeHelper;
import com.hsr.datalogger.service.ServiceHelper;

public class Helper {

	Context context;
	
	CacheHelper caH;
	DatabaseHelper dbH;
	HardwareHelper hwH;
	ServiceHelper srH;
	ExternalHelper exH;
    PachubeHelper paH;

    public Helper(Context context) {
		caH = new CacheHelper(context);
		dbH = new DatabaseHelper(context);
		hwH = new HardwareHelper(context);
		srH = new ServiceHelper(context);
		exH = new ExternalHelper(context);
	    paH = new PachubeHelper(caH.getCurrentMasterKey());
		this.context = context;
	}
	
	/* 1. Launch of the application
	 * (1) initialize tab
	 * */
	public int getCurrentTab(){
		return caH.getCurrentTab();
	}
	
	public void setCurrentTab(int index){
		caH.setCurrentTab(index);
	}
	
	/* 1. Launch of the application
	 * (2) reload the list
	 * */
	private static LoaderManager lmFeed;
	private static FLFragment flf;
	private static LoaderManager lmData;
	private static FPFragment fpf;
	
	public void initFeedListLoader(LoaderManager loaderManager, FLFragment fLFragment) {
		lmFeed = loaderManager;
		flf = fLFragment;
		lmFeed.restartLoader(0, null, flf);
		caH.setInitFL(true);
	}
	
	public void initFeedPageLoader(LoaderManager loaderManager, FPFragment fPFragment){
		lmData = loaderManager;
		fpf = fPFragment;
		lmData.restartLoader(1, null, fpf);
		caH.setInitFP(true);
	}
	
	public void reloadFeedList(){
		if(caH.getInitFL()){
			lmFeed.restartLoader(0, null, flf);
		}
	}
	
	public void reloadDataList(){
		if(caH.getIniFP()){
			lmData.restartLoader(1, null, fpf);
		}
	}
	
	public void closeListLoader() {
		caH.closeListLoader(); 
	}

	/* 2. Handle different cases for login & logout
	 * (1) action bar title
	 * */
	public String AutoLoginAccount(){
		String[] autoAccount = caH.getAutoLogin();
		if(autoAccount == null){
			caH.setCurrentUser(new String[]{"guest"}, null);
			return "guest";
		} else {
			String master = paH.login(autoAccount);
			if(master!=null){  // to checked whether user change name&pw somewhere else 
				caH.setCurrentUser(autoAccount, master);	
				paH.setKey(master);
			 } else {	// this means the name&pw in database for autologin aren't correct, need to remove
				caH.removeAutoLogin();
				caH.setCurrentUser(new String[]{"guest"}, null);
				return null;			
			 }
		}
		return autoAccount[0];
	}

	/* 2. Handle different cases for login & logout
	 * (2) login/out dialog
	 * */ 
	
	public void logout(){
		caH.removeAutoLogin();
		caH.setCurrentUser(new String[]{"guest"}, null);
	}
	
	public boolean login(String[] account, boolean autoL, boolean reg){
		
		if(reg){ // check if need to register before perform login
			if(!paH.createUser(account[0], account[1])){
				return false;
			}
		}
		
		String master = paH.login(account);
		if(master != null){  // check if login success
			
			if(autoL){ // check if saving account info for auto login is needed
				caH.setAutoLogin(true, account);
			} else {
				caH.removeAutoLogin();
			}
			
			caH.setCurrentUser(account, master);
			paH.setKey(master);
			return true;
		}	else {
			return false;
		}
	}
	
	/* 3. Feed List Tab function
	 * (1) Add a feed
	 * */
	public boolean notGuest() {
		String username = caH.getCurrentUser()[0];
		return !username.equals("guest");
	}

	public boolean feedCreate(String title, String type, String ownership) {
		String[] user = caH.getCurrentUser();
		double[] loc = exH.getLocation();
		String location = "(" + loc[0]+", "+loc[1]+", "+loc[2] + ")";
		
		String feedID = paH.createFeed(title, ownership, loc); // should return the feed ID or null if fail
		if(feedID != null){   
			dbH.addFeedToList(user[0], feedID, ownership, caH.getCurrentMasterKey(), "Full", title, type, location);
			caH.setCurrentFeed(feedID, title, caH.getCurrentMasterKey());
			setCurrentTab(Cache.TAB_PAGE);
		} else {	
			return false;			
		}
		return true;
	}

	public boolean feedImport(String feedID, String permission) {
		String[] feed = paH.getFeed(feedID, permission); // should include feedName and permission level(View, Full), return null if fail
		String[] user = caH.getCurrentUser();
		if(feed != null){
			String location = "("+feed[2]+", "+feed[3]+", "+feed[4]+")"; 
			dbH.addFeedToList(user[0], feedID, "None", permission, feed[1], feed[0], "Sensor", location);
			caH.setCurrentFeed(feedID, feed[0], permission);
		} else {
			return false;
		}
		
		// import all datas of this feed
		List<String[]> allData = paH.getAllData(feedID, permission); // {name, value, tag, symbol(sensorID)}
		for(int i=0; i<allData.size(); i++){
			String[] oneData = allData.get(i);
			dbH.addDataToFeed(feedID, oneData[0], oneData[1], oneData[2], Integer.parseInt(oneData[3]));
		}
		
		return true;
	}

	/* 3. Feed List Tab function
	 * (2) Edit or delete a feed
	 * */
	public boolean feedEdit(String id, String nTitle, boolean titleOnly, String nOwn) {
		String user = caH.getCurrentUser()[0];
		String permission = dbH.getPermissionFor(user, id);
		dbH.editFeedTitle(user, id, nTitle);
		if(titleOnly){
			return paH.editTitle(id, nTitle, permission);
		} else {
			dbH.editFeedOwn(user, id, nOwn);
			return paH.editStatus(id, nOwn, permission);
		}
	}

	public boolean updateLocation(String id) {
		String user = caH.getCurrentUser()[0];
		String permission = dbH.getPermissionFor(user, id);
		double[] loc = exH.getLocation();
		 if(paH.editLocation(id, loc, permission)){
			dbH.editFeedLocation(user, id, "(" + loc[0]+", "+loc[1]+", "+loc[2] + ")");
			return true;
		 } else {
		  return false;
		 }
	}

	public boolean feedDelete(String id, boolean local) {
		String username = caH.getCurrentUser()[0];
		String permission = dbH.getPermissionFor(username, id);

		dbH.deleteFeed(username, id);
		if(local){
			return true;
		} else {
			return paH.deleteFeed(id, permission); 
		}
	}

	/* 3. Feed List Tab function
	 * (3) Load the feed list from database
	 * */
	public String[] getFeedListItem(String feedID) {
		String currentUser = caH.getCurrentUser()[0];
		return dbH.getOneFeedInfo(currentUser, feedID);
	}

	public List<String> getFeedList() {
		String currentUser = caH.getCurrentUser()[0];
		return dbH.getCurrentFeedList(currentUser);
	}
	
	public int getFeedListNum(){
		String currentUser = caH.getCurrentUser()[0];
		return dbH.getCurrentFeedListNumber(currentUser);
	}

	/* 3. Feed List Tab function
	 * (4) Click on list item (one feed)
	 * */
	public void clickOneFeed(String feedID, String title) {
		String permission = dbH.getPermissionFor(caH.getCurrentUser()[0], feedID);
		caH.setCurrentFeed(feedID, title, permission);
		setCurrentTab(Cache.TAB_PAGE);
	}

	
	/* 4. Feed Page Tab function
	 * (0) Feed Page Info 
	 * */
	public String[] getFeedPageInfo(){
		// user, feedID, permission
		String user = caH.getCurrentUser()[0];
		String feedID = caH.getCurrentFeedInfo()[0];
		return new String[]{user, feedID, dbH.getPermissionFor(user, feedID)};
	}
	
	
	/* 4. Feed Page Tab function
	 * (1) Share Feed via email
	 * */
	public boolean sendEmail(String address, String selected, Context dialog){
		String key = createPermission(selected);
		if(key==null) return false;
		return exH.sendEmail(address, key, selected, caH.getInfoForEmail(), dialog);
	}
	
	private String createPermission(String selectedLevel){
		String key = paH.createKey(getFeedPageInfo()[1], selectedLevel);
		return key;
	}
	
	/* 4. Feed Page Tab function
	 * (2) Add Datastream to the feed
	 * */
	public String[] setSensorForDevice(){
		// will only run once for the first launch of the app to set the sensor info of this device
		if(!caH.detectSensor()){
			dbH.storeSensorStatus(hwH.getAvailableSensor());
		}
		// later it will get the detected list from the database
		return dbH.getSensorForDevice();
	}
	
	public boolean notFullLevel(){
		String level = dbH.getOneFeedInfo(getFeedPageInfo()[0], getFeedPageInfo()[1])[3];
		if(level.equals("Full")){
			return false;
		}
		return true;
	}
	
	public boolean dataCreate(String dataName, String tag, int sensorID) {		
		String[] sensors = context.getResources().getStringArray(R.array.sensor_list);
		String[] units = context.getResources().getStringArray(R.array.sensor_unit);
		String unit = units[Arrays.asList(sensors).indexOf(tag)];
		
		if(paH.createData(getFeedPageInfo()[1], dataName, getFeedPageInfo()[2], tag, unit, String.valueOf(sensorID))) {
									// permission might be null if it's his own feed
									// tag is the name of sensor from the string list
									// unit is the unit of sensor value from another list
			dbH.addDataToFeed(getFeedPageInfo()[1], dataName, "0", tag, sensorID);
			return true;
		} else {
			return false;
		}
	}
	
	/* 4. Feed Page Tab function
	 * (3) Edit or delete the data
	 * */
	public boolean dataDelete(String dataID) {
		if(paH.deleteData(getFeedPageInfo()[1], dataID, getFeedPageInfo()[2])){
			dbH.deleteData(getFeedPageInfo()[1], dataID);
			return true;
		} else {
			return false;
		}
	}

	public boolean dataEdit(String dataName, String newTags) {
		if(paH.editData(getFeedPageInfo()[1], dataName, newTags, getFeedPageInfo()[2])){
			dbH.editDataTitle(getFeedPageInfo()[1], dataName, newTags);
			return true;
		} else {
			return false;
		}
	}
	
	/* 4. Feed Page Tab function
	 * (4) Turn On Feed Update
	 * */

	public void checkData(String dataName, boolean isChecked) {
		dbH.checkData(getFeedPageInfo()[1], dataName, isChecked);
	}
	
	public int getSelectedDataNum() {
		return dbH.getDataCheckNum(getFeedPageInfo()[1]);
	}
	
	public static HelperLight helperL;
	public void startBackgroundUpdate(int interval, int runningTime) {
		helperL = new HelperLight(context, caH.getCurrentMasterKey(), srH);
		srH.startBackgroundUpdate(dbH.getFeedTitle(getFeedPageInfo()), interval, runningTime, getFeedPageInfo());
	}
	
	/* 4. Feed Page Tab function
	 * (5) Feed Info page
	 * */
	public String[] getFeedInfo() {
		return dbH.getFeedInfo(getFeedPageInfo());
	}

	/* 4. Feed Page Tab function
	 * (6) load the data list from database
	 * */
	public List<String> getDataList() {
		String currentFeed = caH.getCurrentFeedInfo()[0];
		return dbH.getCurrentDataList(currentFeed);
	}

	public int getDataListNum(){
		String currentFeed = caH.getCurrentFeedInfo()[0];
		return dbH.getCurrentDataListNumber(currentFeed);
	}
	
	public String[] getDataListItem(String dataName) {
		String currentFeed = caH.getCurrentFeedInfo()[0];
		return dbH.getOneDataInfo(currentFeed, dataName);
	}

	/* 4. Feed Page Tab function
	 * (7) Click on list item (one data)
	 * */

	public void clickOneData(String dataNames) {
		caH.setCurrentData(dataNames);
		setCurrentTab(Cache.TAB_DATA);		
	}


	/* 5. Feed Data Tab function
	 * (1) display the diagram
	 * */
	/* There should be a better way to communicate between fragment
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
		v.setImageDrawable(diagram);
	}
	
	private Drawable getDiagram(){
		
		// include feed id, data stream name and duration
		String[] para1 = caH.getDataInfoForDiagram();
		// include screen size and time zone
		int[] para2 = hwH.getDeviceInfo();
		
		// set the duration to 1hour as default for the first launch
		if(para1[2]==null){
			caH.setDiagramDuration("1hour");
			para1 = caH.getDataInfoForDiagram();
		}
		
		// adjust the screen size (the height actually) to match the pachube requirement <300,000px
		while(para2[0] * para2[1] > 300000){
			para2[1] -= 100;
		}
		
		Drawable diagram = paH.getDiagram(para1, para2);
		
		return diagram;
	}

	public String[] getDiagramInfo() {
		String dataName = caH.getDataInfoForDiagram()[1];
		String feedName = dbH.getFeedTitle(getFeedPageInfo());
		return new String[]{feedName, dataName};
	}

	public String[] getDiagramStat() {
		String dataName = caH.getDataInfoForDiagram()[1];

		//current, unit, max, min
		return paH.getDataStat(getFeedPageInfo()[1], dataName, getFeedPageInfo()[2]);
	}

	/* 5. Feed Data Tab function
	 * (2) set parameter for diagram
	 * */
	public String getDiagramDuration(){
		return caH.getDataInfoForDiagram()[2];
	}
	
	public void setDiagramDuration(String duration){
		caH.setDiagramDuration(duration);
	}
	
	/* 5. Feed Data Tab function
	 * (3) share via email with diagram attachment
	 * */
	public boolean sendDiagramEmail(String address, String description, Context dialog, ImageView diagram){
		return exH.sendDiagramEmail(address, caH.getInfoForEmail(), description, dialog, diagram);
	}
}
