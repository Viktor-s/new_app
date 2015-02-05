package me.justup.upme.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.Timer;
import java.util.TimerTask;

import me.justup.upme.entity.SendGPSQuery;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class GPSTracker extends Service implements LocationListener {
    private static final String TAG = makeLogTag(GPSTracker.class);

    private Context mContext;
    private Timer mTimer = null;

    // flag for GPS status
    private boolean canGetLocation = false;

    private Location location;
    private double latitude;
    private double longitude;
    private static final long TIMER_INTERVAL = 1000 * 60 * 15; // 15 min

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100; // 100 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 10; // 10 min

    // Declaring a Location Manager
    protected LocationManager locationManager;


    @Override
    public void onCreate() {
        super.onCreate();
        LOGI(TAG, "Start GPSTracker");

        mContext = AppContext.getAppContext();
        getLocation();

        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new SendGPS(), 0, TIMER_INTERVAL);
        }
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;

                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    private class SendGPS extends TimerTask {
        @Override
        public void run() {
            if (ApiWrapper.isOnline()) {
                double latitude = getLatitude();
                double longitude = getLongitude();

                LOGD(TAG, "Send to server - latitude: " + latitude + " longitude: " + longitude);

                SendGPSQuery query = new SendGPSQuery();
                query.params.latitude = latitude;
                query.params.longitude = longitude;

                ApiWrapper.syncQuery(query, new OnSendGPSResponse());
            }
        }

        private class OnSendGPSResponse extends AsyncHttpResponseHandler {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGD(TAG, "onSuccess(): " + content);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGE(TAG, "onSuccess(): " + content);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LOGI(TAG, "Destroy GPSTracker");

        stopUsingGPS();
        mTimer.cancel();
        mTimer = null;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
