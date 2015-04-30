package me.justup.upme.utils;

import android.content.Context;
import android.content.SharedPreferences;

import me.justup.upme.BuildConfig;
import me.justup.upme.fragments.BrowserFragment;


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
    private static final String TAG_BROWSER_URL = "browser_url";
    private static final String TAG_FILE_SORT_TYPE = "file_sort_type";
    private static final String TAG_FILE_SORT_IS_DESC = "file_sort_is_desc";


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

    public void clearPreferences() {
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.apply();
    }

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
        if (BuildConfig.FLAVOR.equals(Constance.APP_FLAVOR_LAUNCHER)) {
            return mSharedPreferences.getBoolean(TAG_MONITORING, false);
        } else {
            return mSharedPreferences.getBoolean(TAG_MONITORING, true);
        }
    }

    public void setBrowserUrl(String url) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(TAG_BROWSER_URL, url);
        mEditor.apply();
    }

    public String getBrowserUrl() {
        return mSharedPreferences.getString(TAG_BROWSER_URL, BrowserFragment.HOME_URL);
    }

    public void setFileSortType(int sortType) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(TAG_FILE_SORT_TYPE, sortType);
        mEditor.apply();
    }

    public int getFileSortType() {
        return mSharedPreferences.getInt(TAG_FILE_SORT_TYPE, 0);
    }

    public void setDescFileSort(boolean isDesc) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(TAG_FILE_SORT_IS_DESC, isDesc);
        mEditor.apply();
    }

    public boolean isDescFileSort() {
        return mSharedPreferences.getBoolean(TAG_FILE_SORT_IS_DESC, false);
    }

}
