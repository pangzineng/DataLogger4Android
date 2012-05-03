package com.hsr.datalogger.pachube;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.graphics.drawable.Drawable;

public class PachubeHelper {
	//private String defaultViewKey;
	private String defaultCreateKey;
	private String currentMasterKey; //MasterKey for current User (can achieve only by login)
	
	public PachubeHelper(){
		//this.defaultViewKey 	= PachubeProperty.defaultViewKey;
		this.defaultCreateKey 	= PachubeProperty.defaultCreateKey;
	}
	
	public PachubeHelper(String masterKey){
		this.currentMasterKey = masterKey;
	}
	
	public void setKey(String key){
		this.currentMasterKey = key;
	}
	
	public String getKey(){
		return this.currentMasterKey;
	}
	
	public String login(String[] account){
		String username = account[0];
		String password = account[1];
		try {
			return Pachube.login(username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean createUser(String username, String password, String fullName){
		User u = new User(username,password);
		u.setName(fullName);
		
		try {
			return Pachube.createUser(u, defaultCreateKey);
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public String[] getFeed(String idString, String key){
		String[] result = new String[2];
		int id = Integer.parseInt(idString);
		String currentKey;
		if (key != null) 
			currentKey = key;
		else 
			return null;
		
		Feed f;
		try {
			f = Pachube.getFeed(id, currentKey);
			if (f != null) {
				result[0] = f.getTitle();
				if (!Pachube.checkKey(currentKey, id)) 
					result[1] = "View";
				else 
					result[1] = "Full";
				return result;
			}
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String createFeed(String title, String isPrivate){
		String currentKey;
		if (this.currentMasterKey != null)
			currentKey = this.currentMasterKey;
		else 
			return null;
		
		try {
			Feed f = new Feed();
			f.setTitle(title);
			if (isPrivate.equalsIgnoreCase("private"))
				f.setPrivacy(true);
			else
				f.setPrivacy(false);
			Feed n = Pachube.createFeed(f, currentKey);
			if (n != null) return Integer.toString(n.getId());
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean deleteFeed(String feedID, String key){
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (this.currentMasterKey != null)
			currentKey = this.currentMasterKey;
		else 
			return false;
		
		int id = Integer.parseInt(feedID);
		try {
			return Pachube.deleteFeed(id, currentKey);
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean editTitle(String feedID, String newTitle, String key){
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (this.currentMasterKey != null)
			currentKey = this.currentMasterKey;
		else 
			return false;
		
		int id = Integer.parseInt(feedID);
		try{
			Feed f = Pachube.getFeed(id, currentKey);
			f.setTitle(newTitle);
			return Pachube.updateFeed(id, PachubeFactory.toFeedXMLWithoutData(f), currentKey);
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean editStatus(String feedID, String newStatus, String key){
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (this.currentMasterKey != null)
			currentKey = this.currentMasterKey;
		else 
			return false;
		
		int id = Integer.parseInt(feedID);
		try{
			Feed f = Pachube.getFeed(id, currentKey);
			f.setPrivacy(newStatus.equalsIgnoreCase("Public")?false:true);
			return Pachube.updateFeed(id, PachubeFactory.toFeedXMLWithoutData(f), currentKey);
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public Drawable getDiagram(String[] info1, int[] info2){
		int feedID = Integer.parseInt(info1[0]);
		String streamID = info1[1];
		String duration = info1[2];
		int w = info2[0];
		int h = info2[1];
		int timezone = info2[2];
		try{
			Drawable d = Drawable.createFromStream(Pachube.showGraph(feedID, streamID, w, h, duration, timezone), "srcName");
			return d;
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public String createKey(String feedID, String permission){
		String currentKey;
		if (this.currentMasterKey != null)
			currentKey = this.currentMasterKey;
		else
			return null;
		int id = Integer.parseInt(feedID);
		boolean fullPermission = permission.equalsIgnoreCase("Full")?true:false;
		boolean privateAccess = false;
		
		APIKey key = new APIKey(id, fullPermission, privateAccess);
		try {
			if (Pachube.createSharingKey(currentKey, PachubeFactory.toKeyXML(key))){
				return Pachube.getKey(currentKey, key.getLabel());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean createData(String feedID, String dataID, String key, String tag, String unit){
		int id = Integer.parseInt(feedID);
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (this.currentMasterKey != null)
			currentKey = this.currentMasterKey;
		else 
			return false;
		Data data = new Data(dataID,tag,unit);
		
		try {
			return Pachube.createDatastream(id, PachubeFactory.toDataXMLWithWrapper(data), currentKey);
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean editData(String feedID, String dataID, String newTag, String key){
		int id = Integer.parseInt(feedID);
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (this.currentMasterKey != null)
			currentKey = this.currentMasterKey;
		else 
			return false;
		try {
			Data data = Pachube.getDatastream(id, dataID, currentKey);
			StringTokenizer st = new StringTokenizer(newTag, ",");
			ArrayList<String> tags = new ArrayList<String>();
			while (st.hasMoreTokens())
				tags.add(st.nextToken().trim());
			data.setTag(tags);
			return Pachube.updateDatastream(id, dataID, PachubeFactory.toDataXMLWithWrapper(data), currentKey);
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean deleteData(String feedID, String dataID, String key){
		int id = Integer.parseInt(feedID);
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (this.currentMasterKey != null)
			currentKey = this.currentMasterKey;
		else 
			return false;
		try {
			return Pachube.deleteDatastream(id, dataID, currentKey);
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean update(String feedID, String key, List<String> dataIDs, float[] values){
		int id = Integer.parseInt(feedID);
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (this.currentMasterKey != null)
			currentKey = this.currentMasterKey;
		else 
			return false;
		
		try {
			Feed f = Pachube.getFeed(id, currentKey);
			if (f != null) {
				for (int i=0; i<dataIDs.size(); i++){
					Data d = f.getDatastream(dataIDs.get(i));
					if (d != null) 
						d.setValue((double) values[i]);
				}
				return Pachube.updateFeed(id, PachubeFactory.toFeedXML(f), currentKey);
			}
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean updateOffline(String feedID, String key, String[] dataIDs, List<List<String[]>> data){
		int id = Integer.parseInt(feedID);
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (this.currentMasterKey != null)
			currentKey = this.currentMasterKey;
		else 
			return false;
		
		try {
			boolean updated = true;
			for (int i=0; i<dataIDs.length; i++){
				List<String[]> dataPointsString = data.get(i);
				ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
				for (int j=0; j<dataPointsString.size(); j++){
					String[] point = dataPointsString.get(j);
					DataPoint dp = new DataPoint(point[0],point[1]);
					dataPoints.add(dp);
				}
				if (!Pachube.updateDataPoints(id, dataIDs[i], PachubeFactory.toDataPointXMLWithWrapper(dataPoints), currentKey))
					updated = false;
			}
			return updated;
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	public String[] getDataStat(String feedID, String dataName, String key){
		int id = Integer.parseInt(feedID);
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (this.currentMasterKey != null)
			currentKey = this.currentMasterKey;
		else 
			return null;
		String[] result = new String[4];
		
		try{
			Feed f = Pachube.getFeed(id, currentKey);
			if (f != null){
				Data d = f.getDatastream(dataName);
				if (d != null){
					if (d.hasValue()) 
						result[0] = String.valueOf(d.getValue());
					else 
						result[0] = null;
					
					if (d.getUnit() != null) 
						result[1] = d.getUnit();
					else
						result[1] = null;
					
					if (d.hasMax()) 
						result[2] = String.valueOf(d.getMaxValue());
					else
						result[2] = null;
					
					if (d.hasMin()) 
						result[3] = String.valueOf(d.getMinValue());
					else
						result[3] = null;
					
					return result;
				}
			}
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
}
