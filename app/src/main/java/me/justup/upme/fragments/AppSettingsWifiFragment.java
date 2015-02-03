package me.justup.upme.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import me.justup.upme.R;


public class AppSettingsWifiFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_wifi);

        ListPreference listPref = (ListPreference) findPreference("list1");
        listPref.setEntryValues(new String[]{"0", "1", "2"});
        listPref.setEntries(new String[]{"one", "two", "next"});

    }

}
