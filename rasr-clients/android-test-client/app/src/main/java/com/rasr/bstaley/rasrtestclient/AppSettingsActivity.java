package com.rasr.bstaley.rasrtestclient;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by bstaley on 5/6/2016.
 */
public class AppSettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AppSettingsFragment())
                .commit();
    }
}