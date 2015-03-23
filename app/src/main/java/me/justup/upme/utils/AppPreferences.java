package me.justup.upme.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class AppPreferences {
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private static final String PREFS_FILE = "UPMEPrefsFile";
    private static final String TAG_TOKEN = "token";
    private static final String TAG_PIN_CODE = "pin_code";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_USER_ID = "user_id";
    private static final String TAG_PHONE = "user_phone";
    private static final String TAG_JABBER_ID = "jabber_id";
    private static final String TAG_MONITORING = "is_monitoring";


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


    public void setUserName(String userName) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(TAG_USER_NAME, userName);
        mEditor.apply();
    }

    public String getUserName() {
        return mSharedPreferences.getString(TAG_USER_NAME, "vault boy");
    }

    public void setUserId(int userId) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(TAG_USER_ID, userId);
        mEditor.apply();
    }

    public int getUserId() {
        return mSharedPreferences.getInt(TAG_USER_ID, 0);
    }

    /*
    public void clearPreferences() {
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.apply();
    }
    */

    public void setPhoneNumber(String phoneNumber) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(TAG_PHONE, phoneNumber);
        mEditor.apply();
    }

    public String getPhoneNumber() {
        return mSharedPreferences.getString(TAG_PHONE, null);
    }

    public void setJabberId(String jabberId) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(TAG_JABBER_ID, jabberId);
        mEditor.apply();
    }

    public String getJabberId() {
        return mSharedPreferences.getString(TAG_JABBER_ID, "jabber");
    }

    public void setMonitoring(boolean isMonitoring) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(TAG_MONITORING, isMonitoring);
        mEditor.apply();
    }

    public boolean isMonitoring() {
        return mSharedPreferences.getBoolean(TAG_MONITORING, true);
    }

}
