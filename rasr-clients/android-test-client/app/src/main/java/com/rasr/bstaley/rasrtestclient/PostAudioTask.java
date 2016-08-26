package com.rasr.bstaley.rasrtestclient;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
public class PostAudioTask implements Callable<RecognizeResults> {
    private static final String TAG = "PostAudioTask";
    private URL url;
    private File audioFile;

    public PostAudioTask(File audioFile, String url) throws IOException {
        this.audioFile = audioFile;
        this.url = new URL(url);;
    }


    @Override
    public RecognizeResults call() throws Exception {

        String results = new String();
        HttpURLConnection rasrConnection = (HttpURLConnection) url.openConnection();
        rasrConnection.setRequestMethod("POST");
        rasrConnection.setUseCaches(false);
        rasrConnection.setDoInput(true);
        rasrConnection.setDoOutput(true);
        rasrConnection.setRequestProperty("Content-Type", "audio/x-pcm");
        rasrConnection.setInstanceFollowRedirects(false);
        OutputStream output = rasrConnection.getOutputStream();

        FileInputStream fos = new FileInputStream(audioFile);
        byte[] buffer = new byte[1024];
        int read = fos.read(buffer);
        int totalBytesProcessed = 0;

        while (read > 0) {
            output.write(buffer, 0, read);
            read = fos.read(buffer);
            totalBytesProcessed += read;
        }

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
        return new RecognizeResults(results, rasrConnection.getResponseCode(), totalBytesProcessed);
    }

}