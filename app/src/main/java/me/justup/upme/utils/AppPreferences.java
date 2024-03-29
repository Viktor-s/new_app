package me.justup.upme.utils;

import android.content.Context;
import android.content.SharedPreferences;

import me.justup.upme.BuildConfig;
import me.justup.upme.fragments.BrowserFragment;

import static me.justup.upme.utils.LogUtils.LOGI;

public class AppPreferences {
    private static final String TAG = AppPreferences.class.getSimpleName();

    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;

    private static final long _24h = 86400000;

    private static final String PREFS_FILE = "UPMEPrefsFile";
    private static final String TAG_TOKEN = "token";
    private static final String TAG_PIN_CODE = "pin_code";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_USER_ID = "user_id";
    private static final String TAG_PHONE = "user_phone";
    private static final String TAG_JABBER_ID = "jabber_id";
    private static final String TAG_AVATAR_URL = "avatar_url";
    private static final String TAG_MONITORING = "is_monitoring";
    private static final String TAG_DEMO_MODE = "is_demo_mode";
    private static final String TAG_BROWSER_URL = "browser_url";
    private static final String TAG_FILE_SORT_TYPE = "file_sort_type";
    private static final String TAG_FILE_SORT_IS_DESC = "file_sort_is_desc";
    private static final String TAG_TOKEN_LIFE_TIME = "token_life_time";

    public AppPreferences(final Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

    public void setToken(String token) {
        LOGI(TAG, "SET TOKEN : " + token);

        mEditor = mSharedPreferences.edit();
        mEditor.putString(TAG_TOKEN, token);
        mEditor.apply();
    }

    public String getToken() {
        String token = mSharedPreferences.getString(TAG_TOKEN, "token");
        LOGI(TAG, "GET TOKEN : " + token);

        return token;
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

    public void setUserAvatarUrl(String url) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(TAG_AVATAR_URL, url);
        mEditor.apply();
    }

    public String getUserAvatarUrl() {
        return mSharedPreferences.getString(TAG_AVATAR_URL, null);
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
            // off
            return mSharedPreferences.getBoolean(TAG_MONITORING, false);
        }
    }

    public void setDemoMode(boolean isDemoMode) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(TAG_DEMO_MODE, isDemoMode);
        mEditor.apply();
    }

    public boolean isDemoMode() {
        return mSharedPreferences.getBoolean(TAG_DEMO_MODE, true);
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

    public void setTokenLife(long currentMillis) {
        mEditor = mSharedPreferences.edit();
        mEditor.putLong(TAG_TOKEN_LIFE_TIME, currentMillis);
        mEditor.apply();
    }

    public boolean isTokenLive() {
        boolean isLive;

        long startTime = mSharedPreferences.getLong(TAG_TOKEN_LIFE_TIME, 0);
        long currentTime = System.currentTimeMillis();

        isLive = currentTime < (startTime + _24h);

        return isLive;
    }
}
