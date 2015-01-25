package me.justup.upme.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class AppPreferences {
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private static final String PREFS_FILE = "UPMEPrefsFile";
    private static final String TAG_TOKEN = "token";
    private static final String TAG_PIN_CODE = "pin_code";


    public AppPreferences(final Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

    public void setToken(String token) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(TAG_TOKEN, token);
        mEditor.apply();
    }

    public String getToken() {
        return mSharedPreferences.getString(TAG_TOKEN, "token");
    }

    public void setPinCode(String pinCode) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(TAG_PIN_CODE, pinCode);
        mEditor.apply();
    }

    public String getPinCode() {
        return mSharedPreferences.getString(TAG_PIN_CODE, "pin");
    }

    public void clearPreferences() {
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.apply();
    }

}
