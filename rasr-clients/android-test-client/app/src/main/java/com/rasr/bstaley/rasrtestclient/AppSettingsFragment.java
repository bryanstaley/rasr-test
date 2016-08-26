package com.rasr.bstaley.rasrtestclient;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by bstaley on 5/6/2016.
 */
public class AppSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
