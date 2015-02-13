package me.justup.upme.fragments;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class SettingsScreenFragment extends Fragment {
    private static final String TAG = makeLogTag(SettingsScreenFragment.class);

    private int brightness;
    private ContentResolver mContentResolver;
    private Window mWindow;
    private TextView mPercentage;
    private AudioManager mAudioManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        getActivity().setVolumeControlStream(AudioManager.STREAM_RING);
        getActivity().setVolumeControlStream(AudioManager.STREAM_ALARM);
        getActivity().setVolumeControlStream(AudioManager.STREAM_NOTIFICATION);

        View v = inflater.inflate(R.layout.fragment_settings_screen, container, false);

        mContentResolver = getActivity().getContentResolver();
        mWindow = getActivity().getWindow();
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        brightness = getSystemBrightness();

        SeekBar mBrightBar = (SeekBar) v.findViewById(R.id.settings_bright_bar);
        mBrightBar.setMax(255);
        mBrightBar.setKeyProgressIncrement(1);
        mBrightBar.setProgress(brightness);
        mBrightBar.setOnSeekBarChangeListener(new OnBrightnessChangeListener());

        mPercentage = (TextView) v.findViewById(R.id.settings_bright_bar_percentage);
        setPercentage(brightness);

        SeekBar mPlayerSeekBar = (SeekBar) v.findViewById(R.id.settings_player_seekBar);
        mPlayerSeekBar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mPlayerSeekBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        mPlayerSeekBar.setOnSeekBarChangeListener(new OnPlayerChangeListener());

        SeekBar mRingerSeekBar = (SeekBar) v.findViewById(R.id.settings_ringer_seekBar);
        mRingerSeekBar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        mRingerSeekBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_RING));
        mRingerSeekBar.setOnSeekBarChangeListener(new OnRingerChangeListener());

        SeekBar mAlarmSeekBar = (SeekBar) v.findViewById(R.id.settings_alarm_seekBar);
        mAlarmSeekBar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        mAlarmSeekBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM));
        mAlarmSeekBar.setOnSeekBarChangeListener(new OnAlarmChangeListener());

        SeekBar mNotificationSeekBar = (SeekBar) v.findViewById(R.id.settings_notification_seekBar);
        mNotificationSeekBar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
        mNotificationSeekBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
        mNotificationSeekBar.setOnSeekBarChangeListener(new OnNotificationChangeListener());

        return v;
    }

    private void setPercentage(int bright) {
        float percentage = (bright / (float) 255) * 100;
        mPercentage.setText((int) percentage + " %");
    }

    private int getSystemBrightness() {
        int bright = 0;

        try {
            bright = Settings.System.getInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            LOGE(TAG, "getSystemBrightness()", e);
        }

        return bright;
    }

    private class OnBrightnessChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            brightness = (progress <= 20) ? 20 : progress;
            setPercentage(brightness);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
            WindowManager.LayoutParams mLayoutParams = mWindow.getAttributes();
            mLayoutParams.screenBrightness = brightness / (float) 255;
            mWindow.setAttributes(mLayoutParams);
        }
    }

    private class OnPlayerChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    private class OnRingerChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    private class OnAlarmChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    private class OnNotificationChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, progress, 0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

}
