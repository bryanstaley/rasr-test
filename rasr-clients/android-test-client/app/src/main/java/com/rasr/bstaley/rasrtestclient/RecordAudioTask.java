package com.rasr.bstaley.rasrtestclient;

import android.annotation.TargetApi;
import android.media.AudioRecord;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;

/**
 * Created by bstaley on 8/26/2016.
 */
public class RecordAudioTask implements Callable<Integer> {
    private static final String TAG = "StreamAudioTask";
    private AudioRecord mRecorder;
    private FileOutputStream mFos;
    boolean notDone = true;


    public RecordAudioTask(AudioRecord recorder, File filestream) throws FileNotFoundException {
        this.mRecorder = recorder;
        this.mFos = new FileOutputStream(filestream);
    }

    public void stop() {
        this.notDone = false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public Integer call() throws Exception {
        mRecorder.startRecording();
        int totalBytesRead = 0;

        try {
            byte[] buffer = new byte[1024];
            while (mRecorder.getState() == AudioRecord.STATE_INITIALIZED && !Thread.currentThread().isInterrupted() && notDone) {
                int read = 0;
                read = mRecorder.read(buffer, 0, 1024, AudioRecord.READ_NON_BLOCKING);
                mFos.write(buffer, 0, read);
                mFos.flush();
                totalBytesRead += read;
            }
            this.mFos.close();

        } catch (Exception e) {
            Log.e(TAG, "Unable to record audio!", e);
        }
        mRecorder.stop();
        return totalBytesRead;
    }
}
