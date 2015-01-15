package me.justup.upme.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class AppPreferences {
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private static final String PREFS_FILE = "UPMEPrefsFile";
    private static final String TAG_TOKEN = "token";
    private static final String TAG_ACCOUNT = "account";


    public AppPreferences(final Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

    public void setToken(String token) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(TAG_TOKEN, token);
        mEditor.apply();
    }

    public String getToken() {
        return mSharedPreferences.getString(TAG_TOKEN, "error");
    }

    public void setCredentials(String account, String login, String password) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(TAG_ACCOUNT, account);
        mEditor.apply();
    }

    public void clearPreferences() {
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.apply();
    }

}
