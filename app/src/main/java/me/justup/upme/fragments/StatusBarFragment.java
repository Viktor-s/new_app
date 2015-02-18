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
import android.widget.TextView;

import me.justup.upme.R;


public class StatusBarFragment extends Fragment {
    private static final String DOTS = ":";
    private static final String PERCENT = "%";

    private TextView mAccumulator;
    private TextView mClock;

    private BroadcastReceiver mStatusBarInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

            // int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            // boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

            long millis = System.currentTimeMillis();
            int minutes = (int) ((millis / (1000 * 60)) % 60);
            int hours = (int) ((millis / (1000 * 60 * 60)) % 24);

            mAccumulator.setText(String.valueOf(level) + PERCENT);
            mClock.setText(hours + DOTS + minutes);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status_bar, container, false);

        mAccumulator = (TextView) v.findViewById(R.id.status_bar_accum_textView);
        mClock = (TextView) v.findViewById(R.id.status_bar_clock_textView);

        getActivity().registerReceiver(this.mStatusBarInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unregisterReceiver(this.mStatusBarInfoReceiver);
    }

}
