package me.justup.upme.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import me.justup.upme.JustUpApplication;
import me.justup.upme.SplashActivity;
import me.justup.upme.utils.ThreadPolicyManager;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;

public class WakefulGCMBroadcastReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = WakefulGCMBroadcastReceiver.class.getSimpleName();

    private GoogleCloudMessaging mGoogleCloudMessaging = null;
    private String mRegId = null;

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (checkConnectingToInternet(context)) {
            if (checkPlayServices(context)) {
                mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(context);

                ThreadPolicyManager.getInstance().executePermissiveUnit(new ThreadPolicyManager.PermissiveUnit(){
                    @Override
                    public void executeUnitOfWork() {
                        mRegId = getRegistrationId(context);
                    }
                });

                LOGI(TAG, "Object GCM : " + mGoogleCloudMessaging + ", RegId : " + mRegId);
                if (!mRegId.isEmpty()) {
                    // Explicitly specify that GcmIntentService will handle the intent.
                    ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());

                    // Start the service, keeping the device awake while it is launching.
                    if(!JustUpApplication.getApplication().isMyServiceRunning(GcmIntentService.class)) {
                        startWakefulService(context, (intent.setComponent(comp)));
                    }
                    setResultCode(Activity.RESULT_OK);
                }
            } else {
                LOGE(TAG, "No Play Services on device");
            }
        } else {
            LOGE(TAG, "No Internet connection");
        }
    }

    /**
     * true - on / false - off
     */
    public boolean checkConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        return resultCode == ConnectionResult.SUCCESS;
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        LOGI(TAG, "GCM Pref : " + prefs);

        String registrationId = prefs.getString(SplashActivity.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            LOGI(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new app version.
        int registeredVersion = prefs.getInt(SplashActivity.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = SplashActivity.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            LOGI(TAG, "App version changed.");
            return "";
        }

        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        return context.getSharedPreferences(SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

}
