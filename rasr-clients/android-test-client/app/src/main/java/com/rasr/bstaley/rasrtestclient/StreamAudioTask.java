package com.rasr.bstaley.rasrtestclient;

import android.annotation.TargetApi;
import android.media.AudioRecord;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by bstaley on 8/26/2016.
 */
public class StreamAudioTask implements Callable<RecognizeResults>
{

    private static final String TAG = "StreamAudioTask";

    private AudioRecord mRecorder;
    boolean notDone = true;
    private  URL url;
    private Integer chunkSize;

    public StreamAudioTask(AudioRecord recorder,String url,Integer chunkSize)throws IOException
    {
        this.mRecorder = recorder;
        this.url = new URL(url);
        this.chunkSize = chunkSize;
        if ((this.chunkSize & 1) !=0) this.chunkSize +=1; //make it even
        Log.d(TAG,"Resulting chunk size " + this.chunkSize);
    }

    public void stop(){
        this.notDone = false;
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public RecognizeResults call() throws Exception {
        mRecorder.startRecording();
        int totalBytesRead = 0;

        String results = new String();
        HttpURLConnection rasrConnection = (HttpURLConnection) url.openConnection();
        rasrConnection.setRequestMethod("POST");
        rasrConnection.setUseCaches(false);
        rasrConnection.setDoInput(true);
        rasrConnection.setDoOutput(true);
        rasrConnection.setRequestProperty("Content-Type","audio/x-pcm");
        rasrConnection.setInstanceFollowRedirects(false);
        rasrConnection.setChunkedStreamingMode(chunkSize);
        OutputStream output = rasrConnection.getOutputStream();
        byte[] buffer = new byte[this.chunkSize*2];

        int rc = -1;
        try{
            int chunkBytesRead = 0;
            Log.d(TAG,"Recorder " + mRecorder.getState());
            Log.d(TAG,"Thread " +Thread.currentThread().isInterrupted());

            while (mRecorder.getState() == AudioRecord.STATE_INITIALIZED && notDone)
            {
                int read = mRecorder.read(buffer,chunkBytesRead,this.chunkSize-chunkBytesRead, AudioRecord.READ_NON_BLOCKING);
                chunkBytesRead += read;

                if (chunkBytesRead >= this.chunkSize) {
                    output.write(buffer, 0, chunkBytesRead);
                    //Log.d(TAG,"wrote some more bytes");
                    output.flush();
                    chunkBytesRead = 0;
                }
                totalBytesRead += read;
            }
            output.flush();
            output.close();
            rc = rasrConnection.getResponseCode();
            InputStream input = rasrConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isr);

            String nextLine = br.readLine();
            while (nextLine != null) {
                results += nextLine;
                nextLine = br.readLine();
                if (nextLine != null)
                    Log.d(TAG,nextLine);
            }
            return new RecognizeResults(results,rc,totalBytesRead);
        }catch (Exception e)
        {
            Log.e(TAG,"Failure on Audio Stream",e);

            InputStream s = rasrConnection.getErrorStream();
            InputStreamReader isr = new InputStreamReader(s);
            BufferedReader br = new BufferedReader(isr);

            String nextLine = br.readLine();
            while (nextLine != null) {
                results += nextLine;
                nextLine = br.readLine();
                Log.e(TAG,nextLine);
            }

        }
        mRecorder.stop();
        Log.i(TAG,"total bytes " + totalBytesRead);
        return new RecognizeResults(results,rc,totalBytesRead);
    }
}
