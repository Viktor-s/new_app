package me.justup.upme.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import static me.justup.upme.fragments.StatusBarFragment.*;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class StatusBarService extends Service {
    private static final String TAG = makeLogTag(StatusBarService.class);

    private Timer mTimer = null;
    private ConnectivityManager mConnectivityManager;
    private static final long TIMER_INTERVAL = 5000; // 5 sec


    @Override
    public void onCreate() {
        super.onCreate();
        LOGI(TAG, "Start StatusBarService");

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new getAllParams(), 0, TIMER_INTERVAL);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LOGI(TAG, "Destroy StatusBarService");

        mTimer.cancel();
        mTimer = null;
    }

    private class getAllParams extends TimerTask {
        @Override
        public void run() {
            long millis = System.currentTimeMillis();
            int minutes = (int) ((millis / (1000 * 60)) % 60);
            int hours = (int) ((millis / (1000 * 60 * 60)) % 24);

            NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra(BROADCAST_EXTRA_HOURS, hours);
            intent.putExtra(BROADCAST_EXTRA_MINUTES, minutes);
            intent.putExtra(BROADCAST_EXTRA_IS_CONNECTED, isConnected);
            sendBroadcast(intent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
