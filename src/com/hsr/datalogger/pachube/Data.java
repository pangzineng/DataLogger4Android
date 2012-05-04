package com.hsr.datalogger.pachube;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Data{

	private String id;

	private ArrayList<String> tag;
	
	private String symbol;

	private Double value;
	
	private String unit;

	private Double minValue;

	private Double maxValue;
	
	private ArrayList<DataPoint> points;
	
	public Data(String id, String tag, String unit, String symbol) {
		this.id = id;
		this.tag = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(tag, ",");
		while (st.hasMoreTokens())
			this.tag.add(st.nextToken().trim());
		this.unit = unit;
		this.points = new ArrayList<DataPoint>();
		this.value = null;
		this.minValue = null;
		this.maxValue = null;
		this.symbol = symbol;
	}

	public Data() {
		this.id = null;
		this.tag = new ArrayList<String>();
		this.points = new ArrayList<DataPoint>();
		this.unit = null;
		this.value = null;
		this.minValue = null;
		this.maxValue = null;
		this.symbol = null;
	}
	
	public boolean hasSymbol(){
		if (this.symbol != null){
			return true;
		} else return false;
	}
	
	public boolean hasValue(){
		if (this.value != null) 
			return true;
		else return false;
	}
	
	public boolean hasMax(){
		if (this.maxValue != null) 
			return true;
		else return false;
	}
	
	public boolean hasMin(){
		if (this.minValue != null) 
			return true;
		else return false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId(int id) {
		this.id = Integer.toString(id);
	}

	public ArrayList<String> getTag() {
		return tag;
	}

	public void setTag(ArrayList<String> tags) {
		this.tag = tags;
	}

	public double getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = Double.parseDouble(value);
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		if (minValue != null) {
			this.minValue = minValue;
		}
	}

	public void setMinValue(String minValue) {
		if (minValue != null) {
			this.minValue = Double.parseDouble(minValue);
		}
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		if (maxValue != null) {
			this.maxValue = maxValue;
		}
	}

	public void setMaxValue(String maxValue) {
		if (maxValue != null) {
			this.maxValue = Double.parseDouble(maxValue);
		}
	}
	
	public void setSymbol(String symbol){
		if (symbol != null) {
			this.symbol = symbol;
		}
	}
	
	public String getSymbol(){
		return this.symbol;
	}
	
	public String getUnit(){
		return this.unit;
	}
	
	public void setUnit(String unit){
		this.unit = unit;
	}
	
	public void addDataPoint(DataPoint dp){
		this.points.add(dp);
	}

	@Override
	public String toString() {
		return "Data [id=" + id + ", maxValue=" + maxValue + ", minValue="
				+ minValue + ", tag=" + tag + ", value=" + value + "]";
	}

}