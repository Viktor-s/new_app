package me.justup.upme.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import me.justup.upme.utils.AppLocale;

import static me.justup.upme.fragments.StatusBarFragment.BROADCAST_ACTION;
import static me.justup.upme.fragments.StatusBarFragment.BROADCAST_EXTRA_IS_CONNECTED;
import static me.justup.upme.fragments.StatusBarFragment.BROADCAST_EXTRA_TIME;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class StatusBarService extends Service {
    private static final String TAG = makeLogTag(StatusBarService.class);

    private Timer mTimer = null;
    private ConnectivityManager mConnectivityManager;
    private static final long TIMER_INTERVAL = 5000; // 5 sec
    private static final String TIME_FORMAT = "HH:mm";
    private SimpleDateFormat mTimeFormat;


    @Override
    public void onCreate() {
        super.onCreate();
        LOGI(TAG, "Start StatusBarService");

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mTimeFormat = new SimpleDateFormat(TIME_FORMAT, AppLocale.getAppLocale());

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
            String currentTime = mTimeFormat.format(new Date());

            NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra(BROADCAST_EXTRA_TIME, currentTime);
            intent.putExtra(BROADCAST_EXTRA_IS_CONNECTED, isConnected);
            sendBroadcast(intent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
