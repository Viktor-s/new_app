package me.justup.upme.launcher;

import android.os.Handler;

public class Alarm implements Runnable {

    private long mAlarmTriggerTime;

    private boolean mWaitingForCallback;

    private Handler mHandler = null;
    private OnAlarmListener mAlarmListener = null;
    private boolean mAlarmPending = false;

    public Alarm() {
        mHandler = new Handler();
    }

    public void setOnAlarmListener(OnAlarmListener alarmListener) {
        mAlarmListener = alarmListener;
    }

    public void setAlarm(long millisecondsInFuture) {
        long currentTime = System.currentTimeMillis();
        mAlarmPending = true;
        mAlarmTriggerTime = currentTime + millisecondsInFuture;
        if (!mWaitingForCallback) {
            mHandler.postDelayed(this, mAlarmTriggerTime - currentTime);
            mWaitingForCallback = true;
        }
    }

    public void cancelAlarm() {
        mAlarmTriggerTime = 0;
        mAlarmPending = false;
    }

    public void run() {
        mWaitingForCallback = false;
        if (mAlarmTriggerTime != 0) {
            long currentTime = System.currentTimeMillis();
            if (mAlarmTriggerTime > currentTime) {
                mHandler.postDelayed(this, Math.max(0, mAlarmTriggerTime - currentTime));
                mWaitingForCallback = true;
            } else {
                mAlarmPending = false;
                if (mAlarmListener != null) {
                    mAlarmListener.onAlarm(this);
                }
            }
        }
    }

    public boolean alarmPending() {
        return mAlarmPending;
    }
}

interface OnAlarmListener {
    public void onAlarm(Alarm alarm);
}
