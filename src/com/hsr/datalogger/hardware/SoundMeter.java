package com.hsr.datalogger.hardware;

import java.io.IOException;

import android.media.MediaRecorder;

public class SoundMeter {
    private MediaRecorder mRecorder = new MediaRecorder();

    public void start(){
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null"); 
            try {
				mRecorder.prepare();
                mRecorder.start();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
    
    public void stop() {
            if (mRecorder != null) {
                    mRecorder.stop();       
                    mRecorder.reset();
            }
    }
    
    public void destroy(){
        mRecorder.release();
        mRecorder = null;
    }
    
    public double getdB() {
            if (mRecorder != null)
                    return  (Math.log10(mRecorder.getMaxAmplitude()) * 20);
            else
                    return 0;

    }
}
