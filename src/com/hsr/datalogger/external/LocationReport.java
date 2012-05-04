package com.hsr.datalogger.external;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class LocationReport {
	
	private boolean enable = true;
	
	private double lon = 0.0;
	private double lat = 0.0;
	private double alt = 0.0;

	LocationManager lm; 
	LocationListener ll;
	
	public LocationReport(Context context) {
		
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		ll = new LocationListener() {
			
			public void onStatusChanged(String provider, int status, Bundle extras) {
				switch(status){
					case LocationProvider.AVAILABLE:
						enable = true;
						break;
					case LocationProvider.OUT_OF_SERVICE:
					case LocationProvider.TEMPORARILY_UNAVAILABLE:
						enable = false;
						break;
				}
			}
			
			public void onProviderEnabled(String provider) {
			}
			
			public void onProviderDisabled(String provider) {
			}
			
			public void onLocationChanged(Location location) {
				lon = location.getLongitude();
				lat = location.getLatitude();
				alt = location.getAltitude();
				lm.removeUpdates(this);
			}
		};
		
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
		
		// set the location to last know first
		if(enable){
			Location lastKnown = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			lon = lastKnown.getLongitude();
			lat = lastKnown.getLatitude();
			alt = lastKnown.getAltitude();
		}
	}
	
	public double getLon(){
		return lon;
	}
	
	public double getLat(){
		return lat;
	}
	
	public double getAlt(){
		return alt;
	}
	
	public Location getLocation(){
		if(!enable) return null;
		
		Location l = new Location(LocationManager.NETWORK_PROVIDER);
		l.setAltitude(alt);
		l.setLatitude(lat);
		l.setLongitude(lon);
		return l;
	}
}
