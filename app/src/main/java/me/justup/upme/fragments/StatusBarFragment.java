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


public class StatusBarFragment extends Fragment {
    private static final String DOTS = ":";
    private static final String PERCENT = "%";

    public static final String BROADCAST_ACTION = "me.justup.upme.broadcast.status_bar.notify";
    public static final String BROADCAST_EXTRA_HOURS = "broadcast_extra_hours";
    public static final String BROADCAST_EXTRA_MINUTES = "broadcast_extra_minutes";
    public static final String BROADCAST_EXTRA_IS_CONNECTED = "broadcast_extra_is_connected";

    private TextView mAccumulator;
    private TextView mClock;
    private ImageView mWiFi;

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
            int hours = intent.getIntExtra(BROADCAST_EXTRA_HOURS, 0);
            int minutes = intent.getIntExtra(BROADCAST_EXTRA_MINUTES, 0);
            boolean isConnected = intent.getBooleanExtra(BROADCAST_EXTRA_IS_CONNECTED, true);

            mClock.setText(hours + DOTS + String.format("%02d", minutes));

            if (isConnected) {
                mWiFi.setImageResource(R.drawable.wifi_3);
            } else {
                mWiFi.setImageResource(R.drawable.ic_no_wifi);
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(this.mStatusBarAccumReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        getActivity().registerReceiver(mStatusBarServiceReceiver, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status_bar, container, false);

        mAccumulator = (TextView) v.findViewById(R.id.status_bar_accum_textView);
        mClock = (TextView) v.findViewById(R.id.status_bar_clock_textView);
        mWiFi = (ImageView) v.findViewById(R.id.status_bar_wifi_imageView);

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unregisterReceiver(this.mStatusBarAccumReceiver);
        getActivity().unregisterReceiver(mStatusBarServiceReceiver);
    }

}
