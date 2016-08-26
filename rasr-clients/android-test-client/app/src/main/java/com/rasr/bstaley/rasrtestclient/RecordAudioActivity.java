package com.rasr.bstaley.rasrtestclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RecordAudioActivity extends AppCompatActivity {

    private static final String TAG = "RecordAudioActivity";

    //private static int[] mSampleRates = new int[] { 8000, 11025,16000, 22050, 44100,48000 };
    private static int[] mSampleRates = new int[]{16000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button streamButton = (Button) findViewById(R.id.streamButton);
        Button recordButton = (Button) findViewById(R.id.recordButton);
        final TextView outputText = (TextView) findViewById(R.id.output);
        outputText.setMovementMethod(new ScrollingMovementMethod());
        final TextView htmlTV = (TextView) findViewById(R.id.html_output);
        htmlTV.setMovementMethod(new ScrollingMovementMethod());
        final TextView htmlResponseTV = (TextView) findViewById(R.id.html_response_code);
        htmlResponseTV.setMovementMethod(new ScrollingMovementMethod());
        assert streamButton != null;
        assert recordButton != null;

        final TextView startTV = (TextView) findViewById(R.id.start_val_tv);
        final TextView stopTV = (TextView) findViewById(R.id.stop_val_tv);
        final TextView elapsedTV = (TextView) findViewById(R.id.elapsed_val_tv);
        final TextView audioElapsedTV = (TextView) findViewById(R.id.audio_elapsed_val_tv);
        final TextView bytesTV = (TextView) findViewById(R.id.bytes_processed_val_tv);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        final Context context = this.getApplicationContext();

        final AudioRecord recorder = this.findAudioRecord(mSampleRates);

        if (recorder == null)
        {
            outputText.append("Unable to attach to device mic using sample rates: " + mSampleRates + "\n");
        }
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/DD HH:mm:ss z");
            File tmpAudioFile = null;
            File outputDir = context.getCacheDir();
            RecordAudioTask recordTask;
            FutureTask<Integer> recordTaskFutureTask;
            Integer postTimeout = Integer.parseInt(sharedPref.getString(getString(R.string.rasr_post_timeout_pref), "20"));
            Date startD = null;

            ExecutorService exService = Executors.newSingleThreadExecutor();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Date endD;
                try {

                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            startD = new Date();
                            clearScreen();
                            tmpAudioFile = File.createTempFile("rasr-audio", "raw", outputDir);
                            this.recordTask = new RecordAudioTask(recorder, tmpAudioFile);
                            recordTaskFutureTask = new FutureTask<Integer>(this.recordTask);
                            exService.execute(recordTaskFutureTask);
                            outputText.append("Record Starting\n");
                            break;
                        case MotionEvent.ACTION_UP:
                            recordTask.stop();

                            try {
                                Date now = new Date();
                                long elapsedMillis = now.getTime() - startD.getTime();
                                Double elapsedSecs = (double) elapsedMillis / 1000.0;
                                audioElapsedTV.setText(elapsedSecs.toString());
                                outputText.append("Record Stopped\n");
                                Integer response = recordTaskFutureTask.get(postTimeout, TimeUnit.SECONDS);
                                FutureTask<RecognizeResults> postTask = new FutureTask<RecognizeResults>(new PostAudioTask(tmpAudioFile, buildURL()));
                                exService.execute(postTask); //TODO don't fire and forget!
                                RecognizeResults results = postTask.get(postTimeout, TimeUnit.SECONDS);

                                htmlTV.append(results.response + "\n\n");
                                outputText.append("Stream Stopped\n");
                                htmlResponseTV.setText(results.htmlCode.toString());
                                endD = new Date();
                                stopTV.setText(format.format(endD));

                                elapsedMillis = endD.getTime() - startD.getTime();
                                elapsedSecs = (double) elapsedMillis / 1000.0;
                                elapsedTV.setText(elapsedSecs.toString());
                                bytesTV.setText(results.bytesProcessed.toString());


                            } catch (Exception e) {
                                Log.e(RecordAudioActivity.TAG, "Audio POST failure", e);
                                outputText.append(e.toString() + "\n");
                                recorder.stop();
                            }

                            break;
                    }

                } catch (IOException e) {
                    Log.e(RecordAudioActivity.TAG, "Audio POST failure", e);
                    outputText.append(e.toString() + "\n");
                }

                return true;
            }
        });

        streamButton.setOnTouchListener(new View.OnTouchListener() {


            StreamAudioTask streamTask;
            FutureTask<RecognizeResults> streamTaskFutureTask;
            ExecutorService exService = Executors.newSingleThreadExecutor();
            Date startD = null;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/DD HH:mm:ss z");

                Date endD;

                Integer postTimeout = Integer.parseInt(sharedPref.getString(getString(R.string.rasr_post_timeout_pref), "20"));
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        try {
                            startD = new Date();
                            clearScreen();
                            this.streamTask = new StreamAudioTask(recorder, buildURL(), getChunkSize());
                            streamTaskFutureTask = new FutureTask<RecognizeResults>(this.streamTask);
                            exService.execute(streamTaskFutureTask);
                            outputText.append("Record Starting\n");
                            outputText.append(String.format("Posting to: %s%s", buildURL(), "\n"));

                            startTV.setText(format.format(startD));
                        }catch(IOException e)
                        {
                            outputText.append(e.toString() + "\n");
                            Log.e(RecordAudioActivity.TAG, "Audio Capture failure", e);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        streamTask.stop();
                        try {
                            Date now = new Date();
                            long elapsedMillis = now.getTime() - startD.getTime();
                            Double elapsedSecs = (double) elapsedMillis / 1000.0;
                            audioElapsedTV.setText(elapsedSecs.toString());
                            RecognizeResults response = streamTaskFutureTask.get(postTimeout, TimeUnit.SECONDS);
                            htmlTV.append(response.response + "\n\n");
                            outputText.append("Stream Stopped\n");
                            htmlResponseTV.setText(response.htmlCode.toString());
                            endD = new Date();
                            stopTV.setText(format.format(endD));

                            elapsedMillis = endD.getTime() - startD.getTime();
                            elapsedSecs = (double) elapsedMillis / 1000.0;
                            elapsedTV.setText(elapsedSecs.toString());
                            bytesTV.setText(response.bytesProcessed.toString());

                            break;
                        } catch (Exception e) {
                            outputText.append(e.toString() + "\n");
                            Log.e(RecordAudioActivity.TAG, "Audio POST failure", e);
                            recorder.stop();
                        }

                }
                return true;
            }
        });
    }


    private String buildURL() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String rasrHost = sharedPref.getString(getString(R.string.rasr_host_pref), "localhost");
        Integer rasrPort = Integer.parseInt(sharedPref.getString(getString(R.string.rasr_port_pref), "8080"));
        Integer rasrNodeId = Integer.parseInt(sharedPref.getString(getString(R.string.rasr_node_pref), "42"));
        String resultUrl = String.format("http://%s:%s/rasr-ws/rasr/control/%s", rasrHost, rasrPort, rasrNodeId);
        Log.i(RecordAudioActivity.TAG, "Recognize URL: " + resultUrl);
        return resultUrl;
    }

    private Integer getChunkSize() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.parseInt(sharedPref.getString(getString(R.string.rasr_chunk_upload_size_pref), "1024"));
    }


    public AudioRecord findAudioRecord(int[] sampleRates) {
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        for (int rate : sampleRates) {
            //for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT,AudioFormat.ENCODING_DEFAULT }) {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_16BIT}) {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO}) {
                    try {
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, rate, channelConfig, audioFormat, bufferSize + 2048);
                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        Log.e(RecordAudioActivity.TAG, "Unable to find audio recorder!", e);

                    }
                }
            }
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record_audio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchSettings(MenuItem item) {
        Intent intent = new Intent(this, AppSettingsActivity.class);
        startActivity(intent);
    }

    public void clearScreen() {
        TextView outputText = (TextView) findViewById(R.id.output);
        outputText.setText("");
        TextView htmlTV = (TextView) findViewById(R.id.html_output);
        htmlTV.setText("");
        TextView bytesText = (TextView) findViewById(R.id.bytes_processed_val_tv);
        bytesText.setText("---");
        final TextView htmlResponseTV = (TextView) findViewById(R.id.html_response_code);
        htmlResponseTV.setText("---");
        TextView startTV = (TextView) findViewById(R.id.start_val_tv);
        startTV.setText("---");
        TextView stopTV = (TextView) findViewById(R.id.stop_val_tv);
        stopTV.setText("---");
        TextView elapsedTV = (TextView) findViewById(R.id.elapsed_val_tv);
        elapsedTV.setText("---");
        TextView audioElapsedTV = (TextView) findViewById(R.id.audio_elapsed_val_tv);
        audioElapsedTV.setText("---");
    }

}

