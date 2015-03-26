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
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class SettingsScreenFragment extends Fragment {
    private static final String TAG = makeLogTag(SettingsScreenFragment.class);
    private static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
    private static final int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
    private static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;

    private int brightness;
    private ContentResolver mContentResolver;
    private Window mWindow;
    private TextView mPercentage;
    private AudioManager mAudioManager;
    private ToggleButton mAutoBrightnessButton;


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

        final SeekBar mBrightBar = (SeekBar) v.findViewById(R.id.settings_bright_bar);
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
        mAutoBrightnessButton = (ToggleButton) v.findViewById(R.id.settings_brightness_auto_button);
        mAutoBrightnessButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBrightBar.setEnabled(false);
                    setAutoBrightness(true);
                } else {
                    mBrightBar.setEnabled(true);
                    setAutoBrightness(false);
                }

            }
        });
        int mode = 0;
        try {
            mode = Settings.System.getInt(mContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (mode == SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            mAutoBrightnessButton.setChecked(true);
        } else {
            mAutoBrightnessButton.setChecked(false);
        }


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

    private void setAutoBrightness(boolean value) {
        if (value) {
            Settings.System.putInt(mContentResolver, SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        } else {
            Settings.System.putInt(mContentResolver, SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL);
        }

        // After brightness change we need to "refresh" current app brightness
        if (value) {
            refreshBrightness(-1);
        } else {
            refreshBrightness(brightness);
        }
    }

    private void refreshBrightness(float brightness) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        if (brightness < 0) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = brightness;
        }
        getActivity().getWindow().setAttributes(lp);
    }

    private int getBrightnessLevel() {
        try {
            int value = Settings.System.getInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS);
            // convert brightness level to range 0..1
            value = value / 255;
            return value;
        } catch (Settings.SettingNotFoundException e) {
            return 0;
        }
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
