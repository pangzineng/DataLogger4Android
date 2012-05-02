package com.hsr.datalogger.pachube;

public class DataPoint {
	
	private String time;
	private Double value;
	
	public DataPoint(){
		time = null;
		value = null;
	}
	
	public DataPoint(String t, Double v){
		time = t;
		value = v;
	}
	
	public DataPoint(String t, String v){
		time = t;
		value = Double.valueOf(v);
	}
	
	public void setTime(String t){
		time = t;		
	}
	
	public String getTime(){
		return time;
	}
	
	public void setValue(Double v){
		value = v;
	}
	
	public void setValue(String v){
		value = Double.valueOf(v);
	}
	
	public Double getValue(){
		return value;
	}
	
}
