package me.justup.upme.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class StatusBarFragment extends Fragment {
    private static final String TAG = makeLogTag(StatusBarFragment.class);

    private static final String PERCENT = "%";

    public static final String BROADCAST_ACTION = "me.justup.upme.broadcast.status_bar.notify";
    public static final String BROADCAST_ACTION_PUSH = "me.justup.upme.broadcast.status_bar.push";

    public static final String BROADCAST_EXTRA_TIME = "broadcast_extra_time";
    public static final String BROADCAST_EXTRA_IS_CONNECTED = "broadcast_extra_is_connected";

    public static final String BROADCAST_EXTRA_IS_NEW_MESSAGE = "broadcast_extra_is_new_message";

    private TextView mAccumulator;
    private TextView mClock;
    private ImageView mWiFi;
    private ImageView mPushIcon;

    private BroadcastReceiver mStatusBarAccumReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            // int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            // boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            mAccumulator.setText(String.valueOf(level) + PERCENT);
        }
    };

    private BroadcastReceiver mStatusBarServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String time = intent.getStringExtra(BROADCAST_EXTRA_TIME);
            boolean isConnected = intent.getBooleanExtra(BROADCAST_EXTRA_IS_CONNECTED, true);

            mClock.setText(time);

            if (isConnected) {
                mWiFi.setImageResource(R.drawable.wifi_3);
            } else {
                mWiFi.setImageResource(R.drawable.ic_no_wifi);
            }
        }
    };

    private BroadcastReceiver mStatusBarPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            boolean isNewPush = intent.getBooleanExtra(BROADCAST_EXTRA_IS_NEW_MESSAGE, false);

            if (isNewPush)
                mPushIcon.setVisibility(View.VISIBLE);
            else
                mPushIcon.setVisibility(View.INVISIBLE);
        }
    };


    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(mStatusBarAccumReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        getActivity().registerReceiver(mStatusBarServiceReceiver, new IntentFilter(BROADCAST_ACTION));
        getActivity().registerReceiver(mStatusBarPushReceiver, new IntentFilter(BROADCAST_ACTION_PUSH));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status_bar, container, false);

        mAccumulator = (TextView) v.findViewById(R.id.status_bar_accum_textView);
        mClock = (TextView) v.findViewById(R.id.status_bar_clock_textView);
        mWiFi = (ImageView) v.findViewById(R.id.status_bar_wifi_imageView);
        mPushIcon = (ImageView) v.findViewById(R.id.status_bar_push_imageView);

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            getActivity().unregisterReceiver(mStatusBarAccumReceiver);
        } catch (Exception e) {
            LOGE(TAG, "unregisterReceiver(mStatusBarAccumReceiver)", e);
            mStatusBarAccumReceiver = null;
        }

        try {
            getActivity().unregisterReceiver(mStatusBarServiceReceiver);
        } catch (Exception e) {
            LOGE(TAG, "unregisterReceiver(mStatusBarServiceReceiver)", e);
            mStatusBarServiceReceiver = null;
        }

        try {
            getActivity().unregisterReceiver(mStatusBarPushReceiver);
        } catch (Exception e) {
            LOGE(TAG, "unregisterReceiver(mStatusBarPushReceiver)", e);
            mStatusBarPushReceiver = null;
        }
    }

}
