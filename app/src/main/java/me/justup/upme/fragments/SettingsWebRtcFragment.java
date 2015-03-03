package me.justup.upme.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import me.justup.upme.R;

public class SettingsWebRtcFragment  extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}