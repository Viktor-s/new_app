package me.justup.upme;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import me.justup.upme.entity.ErrorResponse;
import me.justup.upme.entity.SetGooglePushIdQuery;
import me.justup.upme.gcm.GCMIntentService;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.utils.CommonUtils;
import me.justup.upme.utils.ThreadPolicyManager;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;


public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    // GCM
    private GoogleCloudMessaging mGCM = null;
    private String mRegId = null;
    private Boolean isDeviceRegister = false;
    private Timer mTimer = null;

    private static final String SENDER_ID = "896253211448";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            public void run() {

                if (checkConnectingToInternet()) {
                    if (checkAndRegGCM()) {
                        mTimer = new Timer();
                        mTimer.schedule(new CheckRegisterGCM(), 100, 1000);
                    }
                } else {
                    Toast.makeText(SplashActivity.this, "No Internet connection", Toast.LENGTH_LONG).show();
                    SplashActivity.this.finish();
                }

            }
        }, 500);
    }

    private boolean checkAndRegGCM() {
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            mGCM = GoogleCloudMessaging.getInstance(this);

            ThreadPolicyManager.getInstance().executePermissiveUnit(new ThreadPolicyManager.PermissiveUnit(){
                @Override
                public void executeUnitOfWork() {
                    mRegId = getRegistrationId(getApplicationContext());
                }
            });

            LOGI(TAG, "Object GCM : " + mGCM + ", RegId : " + mRegId);
            if (mRegId.isEmpty()) {
                registerInBackground();
            } else {
                sendAsyncRegistrationIdToBackend(mRegId);
            }

            return true;

        } else {

            LOGE(TAG, "Warning! This device is not supported GCM now.");
            return false;
        }
    }

    private void goToDashboardActivity() {
        if(isDeviceRegister!=null && isDeviceRegister) {
            if(!JustUpApplication.getApplication().isMyServiceRunning(GCMIntentService.class)) {
                startService(new Intent(this, GCMIntentService.class));
            }

            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            // Maybe add some param
            startActivity(intent);
        }else if(isDeviceRegister==null){
            runOnUiThread(new Runnable() {
                public void run() {
                    final Toast toast = Toast.makeText(getApplicationContext(), "Problem width registration device.", Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 2000);
                }
            });
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        SplashActivity.this.finish();
                    }
                });

                dialog.show();
            } else {
                LOGI(TAG, "This device is not supported.");
                SplashActivity.this.finish();
            }

            return false;
        }

        return true;
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
        final SharedPreferences prefs = getGcmPreferences();
        LOGI(TAG, "GCM Pref : " + prefs);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            LOGI(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            LOGI(TAG, "App version changed.");
            return "";
        }

        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences() {
        return getSharedPreferences(SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name : " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg;

                try {
                    if (mGCM == null) {
                        mGCM = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }

                    mRegId = mGCM.register(SENDER_ID);

                    LOGD(TAG, "GCM registration ID = " + mRegId);
                    msg = "Device registered, registration ID = " + mRegId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendSyncRegistrationIdToBackend(mRegId);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(getApplicationContext(), mRegId);
                } catch (IOException ex) {
                    LOGE(TAG, ex.getMessage());
                    msg = "Error : " + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(), msg + "\n", Toast.LENGTH_SHORT).show();
            }

        }.execute(null, null, null);
    }

    /**
     * For works threads
     * <p/>
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */

    private void sendSyncRegistrationIdToBackend(final String pushId) {
        LOGI(TAG, "Send new request to Server from regGCM method.");
        ApiWrapper.syncQuery(createPushIdEntity(pushId), new OnPushRegisterResponse());
    }

    // For Main thread
    private void sendAsyncRegistrationIdToBackend(final String pushId) {
        LOGI(TAG, "Send old request to Server from regGCM method.");
        ApiWrapper.query(createPushIdEntity(pushId), new OnPushRegisterResponse());
    }

    private SetGooglePushIdQuery createPushIdEntity(final String pushId) {
        SetGooglePushIdQuery query = new SetGooglePushIdQuery();
        query.params.google_push_id = pushId;

        return query;
    }

    private class OnPushRegisterResponse extends AsyncHttpResponseHandler {
        private final String TAG = OnPushRegisterResponse.class.getSimpleName();

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGI(TAG, "OnSuccess : " + content);

            ErrorResponse response = null;
            try {
                response = ApiWrapper.gson.fromJson(content, ErrorResponse.class);
            } catch (JsonSyntaxException e) {
                LOGE(TAG, "OnPushRegisterResponse gson.fromJson:\n" + content);
            }

            if (response != null && response.error != null) {
                CommonUtils.showWarningToast(SplashActivity.this, response.error.data);
            }

            // Notify device is registered
            isDeviceRegister = true;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGI(TAG, "OnFailure : " + content);

            // Notify device is unregistered
            isDeviceRegister = null;
        }
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences();
        int appVersion = getAppVersion(context);

        LOGI(TAG, "Saving regId on app version : " + appVersion);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    /**
     * true - on / false - off
     */
    public boolean checkConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    class CheckRegisterGCM extends TimerTask {

        @Override
        public void run() {
            goToDashboardActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
