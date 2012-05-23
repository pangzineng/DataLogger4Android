package com.hsr.datalogger.pachube;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.graphics.drawable.Drawable;

public class PachubeHelper {
	//private String defaultViewKey;
	private String defaultCreateKey;
	private static String currentMasterKey; //MasterKey for current User (can achieve only by login)
	
	public PachubeHelper(){
		//this.defaultViewKey 	= PachubeProperty.defaultViewKey;
		this.defaultCreateKey 	= PachubeProperty.defaultCreateKey;
	}
	
	public PachubeHelper(String masterKey){
		currentMasterKey = masterKey;
	}
	
	public void setKey(String key){
		currentMasterKey = key;
	}
	
	public String getKey(){
		return currentMasterKey;
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
	
	public boolean createUser(String username, String password){
		User u = new User(username,password);
		u.addRole("signup_plan");
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
		String[] result = new String[5];
		int id = Integer.parseInt(idString);
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (currentMasterKey != null)
			currentKey = currentMasterKey;
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
				PachubeLocation loc = f.getLocation();					
				System.err.println("made it here");
				if (loc != null){
					result[2]=String.valueOf(loc.getLon());
					result[3]=String.valueOf(loc.getLat());
					result[4]=String.valueOf(loc.getElevation());
				}
				return result;
			}
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String createFeed(String title, String isPrivate, double[] location){
		String currentKey;
		if (currentMasterKey != null)
			currentKey = currentMasterKey;
		else 
			return null;
		
		try {
			Feed f = new Feed();
			
			f.setTitle(title);
			
			if (isPrivate.equalsIgnoreCase("private"))
				f.setPrivacy(true);
			else
				f.setPrivacy(false);
			
			PachubeLocation loc = new PachubeLocation(location);
			f.setLocation(loc);
			
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
		else if (currentMasterKey != null)
			currentKey = currentMasterKey;
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
		else if (currentMasterKey != null)
			currentKey = currentMasterKey;
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
		else if (currentMasterKey != null)
			currentKey = currentMasterKey;
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
	
	public boolean editLocation(String feedID, double[] location, String key){
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (currentMasterKey != null)
			currentKey = currentMasterKey;
		else 
			return false;
		
		int id = Integer.parseInt(feedID);
		
		try{
			Feed f = Pachube.getFeed(id, currentKey);
			PachubeLocation loc = new PachubeLocation(location);
			f.setLocation(loc);
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
		if (currentMasterKey != null)
			currentKey = currentMasterKey;
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
	
	public boolean createData(String feedID, String dataID, String key, String tag, String unit, String symbol){
		int id = Integer.parseInt(feedID);
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (currentMasterKey != null)
			currentKey = currentMasterKey;
		else 
			return false;
		Data data = new Data(dataID,tag,unit,symbol);
		
		try {
			return Pachube.createDatastream(id, PachubeFactory.toDataXMLWithWrapper(data), currentKey);
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public ArrayList<String[]> getAllData(String feedID, String key){
		int id = Integer.parseInt(feedID);
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (currentMasterKey != null)
			currentKey = currentMasterKey;
		else 
			return null;
		
		try {
			Feed f = Pachube.getFeed(id, currentKey);
			if (f != null){
				ArrayList<String[]> datastreams = new ArrayList<String[]>();
				for (int i=0; i<f.getData().size(); i++){
					String[] datastream = new String[4];
					Data d = f.getData().get(i);
					datastream[0] = d.getId();
					if (d.hasValue()) 
						datastream[1] = String.valueOf(d.getValue());
					else datastream[1] = null;
					if (d.getTag().size() > 0){
						datastream[2] = "";
						for (int j=0; j<d.getTag().size()-1; j++)
							datastream[2] += d.getTag().get(j) + ",";
						datastream[2] += d.getTag().get(d.getTag().size()-1);
					}
					else datastream[2] = null;
					if (d.hasSymbol())
						datastream[3] = d.getSymbol();
					else datastream[3] = null;
					
					datastreams.add(datastream);
				}
				
				return datastreams;
			}
		} catch (PachubeException e){
			System.err.println(e.errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean editData(String feedID, String dataID, String newTag, String key){
		int id = Integer.parseInt(feedID);
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (currentMasterKey != null)
			currentKey = currentMasterKey;
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
		else if (currentMasterKey != null)
			currentKey = currentMasterKey;
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
		else if (currentMasterKey != null)
			currentKey = currentMasterKey;
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
	
	public boolean updateOffline(String feedID, String key, List<String> dataIDs, List<List<String[]>> data){
		int id = Integer.parseInt(feedID);
		String currentKey;
		if (key != null) 
			currentKey = key;
		else if (currentMasterKey != null)
			currentKey = currentMasterKey;
		else 
			return false;
		
		try {
			boolean updated = true;
			for (int i=0; i<dataIDs.size(); i++){
				List<String[]> dataPointsString = data.get(i);
				ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
				for (int j=0; j<dataPointsString.size(); j++){
					String[] point = dataPointsString.get(j);
					DataPoint dp = new DataPoint(point[0],point[1]);
					dataPoints.add(dp);
				}
				if (!Pachube.updateDataPoints(id, dataIDs.get(i), PachubeFactory.toDataPointXMLWithWrapper(dataPoints), currentKey))
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
		else if (currentMasterKey != null)
			currentKey = currentMasterKey;
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
