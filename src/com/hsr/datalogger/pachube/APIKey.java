package com.hsr.datalogger.pachube;

public class APIKey {
	private int resource=-1;
	private String label;
	private boolean privateAccess = false;
	private boolean[] methods = {false,false};
	
	public APIKey(int feedID, boolean fullPermission, boolean hasPrivateAccess){
		this.label=PachubeProperty.sharingKeyName + "_" + feedID;
		this.resource = feedID;
		this.methods[0]=true;
		if (fullPermission){
			methods[1]=true;
		}
		this.privateAccess=hasPrivateAccess;
	}
	
	public int getResource(){
		return this.resource;
	}
	
	public boolean hasFullPermission(){
		return this.methods[1];
	}
	
	public boolean hasPrivateAccess(){
		return this.privateAccess;
	}
	
	public String getLabel(){
		return this.label;
	}
}
