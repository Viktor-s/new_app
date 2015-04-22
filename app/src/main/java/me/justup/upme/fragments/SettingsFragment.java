package me.justup.upme.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.justup.upme.R;
import me.justup.upme.utils.AppPreferences;


public class SettingsFragment extends Fragment implements View.OnClickListener {


    private static final String SAVE_FRAGMENT_TAG = "save_settings_fragment_tag";

    private TextView mWiFiMenu;
    private TextView mScreenSoundMenu;
    private TextView mSocialMenu;
    private TextView mMonitoringMenu;
    private TextView mWebRTCMenu;
    private TextView mCurrentMenu;
    private ArrayList<TextView> mButtonList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        mWiFiMenu = (TextView) view.findViewById(R.id.settings_wifi_menu_textView);
        mScreenSoundMenu = (TextView) view.findViewById(R.id.settings_screen_menu_textView);
        mSocialMenu = (TextView) view.findViewById(R.id.settings_social_menu_textView);
        mMonitoringMenu = (TextView) view.findViewById(R.id.settings_monitoring_textView);
        mWebRTCMenu = (TextView) view.findViewById(R.id.settings_webrtc_menu_textView);
        ImageView mCloseSettings = (ImageView) view.findViewById(R.id.close_settings_activity_imageView);

        mWiFiMenu.setOnClickListener(this);
        mScreenSoundMenu.setOnClickListener(this);
        mSocialMenu.setOnClickListener(this);
        mMonitoringMenu.setOnClickListener(this);
        mWebRTCMenu.setOnClickListener(this);
        mCloseSettings.setOnClickListener(this);

        mButtonList.add(mWiFiMenu);
        mButtonList.add(mScreenSoundMenu);
        mButtonList.add(mWebRTCMenu);
        mButtonList.add(mSocialMenu);
        mButtonList.add(mMonitoringMenu);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.settings_fragment_container, new SettingsWifiFragment()).commit();
        } else {
            int currentMenu = savedInstanceState.getInt(SAVE_FRAGMENT_TAG);
            changeButtonState(mButtonList.get(currentMenu));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;

        switch (v.getId()) {
            case R.id.settings_wifi_menu_textView:
                changeButtonState(mWiFiMenu);
                fragment = new SettingsWifiFragment();
                break;

            case R.id.settings_screen_menu_textView:
                changeButtonState(mScreenSoundMenu);
                fragment = new SettingsScreenFragment();
                break;

            case R.id.settings_social_menu_textView:
                changeButtonState(mSocialMenu);
                fragment = new SettingsSocialFragment();
                break;

            case R.id.settings_monitoring_textView:
                changeButtonState(mMonitoringMenu);
                fragment = new SettingsMonitoringFragment();
                break;

            case R.id.settings_webrtc_menu_textView:
                changeButtonState(mWebRTCMenu);
                fragment = new SettingsWebRtcFragment();
                break;

            case R.id.close_settings_activity_imageView:
//                finish();
                break;

            default:
                break;
        }

        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.settings_fragment_container, fragment).commit();
        }
    }

    private void changeButtonState(TextView activeButton) {
        for (TextView button : mButtonList) {
            button.setBackgroundResource(R.drawable.settings_back_left_trans);
        }

        activeButton.setBackgroundResource(R.drawable.settings_back_left);
        mCurrentMenu = activeButton;
    }

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        int currentMenu = 0;
//
//        if (mCurrentMenu != null) {
//            for (int i = 0; i < mButtonList.size(); i++) {
//                if (mButtonList.get(i).equals(mCurrentMenu)) {
//                    currentMenu = i;
//                }
//            }
//        }
//
//        outState.putInt(SAVE_FRAGMENT_TAG, currentMenu);
//    }


}
