package com.hsr.datalogger.hardware;

import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class SoundMeter {
    private MediaRecorder mRecorder = null;
    private boolean isStarted = false;
    
    public void start(){
    		if(isStarted == false){
    			isStarted = true;
        		mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DataLogger/sound.3gp"); 
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                try {
    				mRecorder.prepare();
                    mRecorder.start();
    			} catch (IllegalStateException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    }
    
    public void stop() {
            if (mRecorder != null) {
            	try{
                    mRecorder.stop();       
                    mRecorder.release();
                    mRecorder = null;
                    isStarted = false;
            	} catch(Exception e){
            		Log.d("beta", "stop exception: " + e.getMessage());
            	}
            }
    }
        
    public double getdB() {
            if (mRecorder != null){
            	if (mRecorder.getMaxAmplitude()==0) {
            		Log.d("beta", "run for the first time, value=0");
            		return 0;
            	}
            	int max = mRecorder.getMaxAmplitude();
            	Log.d("beta", "run not the first time, value=" + max);
                return  (Math.log10(max) * 20);
            } else {
                return 0;
            }
    }
}
