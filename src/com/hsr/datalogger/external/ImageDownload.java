package com.hsr.datalogger.external;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.widget.ImageView;

public class ImageDownload {

	public ImageDownload(){
		File folder = new File(Environment.getExternalStorageDirectory() + "/DataLogger");
		if(!folder.exists()){
			folder.mkdir();
		}
	}
	
	public boolean startDownload(ImageView image, String name){
		image.setDrawingCacheEnabled(true);
		Bitmap b = image.getDrawingCache();
		try {
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DataLogger/" + name + ".png");
			b.compress(CompressFormat.PNG, 100, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
