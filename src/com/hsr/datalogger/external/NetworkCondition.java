package com.hsr.datalogger.external;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCondition {

	private Context mContext;
	private ConnectivityManager cm;

	public NetworkCondition(Context context) {
		mContext = context;
		cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE); 
	}

	public boolean getNetworkInfo(){
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info==null?false:info.isConnected();
	}
}
