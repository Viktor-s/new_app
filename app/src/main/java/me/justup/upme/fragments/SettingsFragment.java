package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import me.justup.upme.R;


public class SettingsFragment extends Fragment implements View.OnClickListener {
    private TextView mWiFiMenu;
    private TextView mScreenSoundMenu;
    private TextView mSocialMenu;
    private TextView mMonitoringMenu;
    private TextView mWebRTCMenu;
    private TextView mServersMenu;
    private ArrayList<TextView> mButtonList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        mWiFiMenu = (TextView) view.findViewById(R.id.settings_wifi_menu_textView);
        mScreenSoundMenu = (TextView) view.findViewById(R.id.settings_screen_menu_textView);
        mSocialMenu = (TextView) view.findViewById(R.id.settings_social_menu_textView);
        mMonitoringMenu = (TextView) view.findViewById(R.id.settings_monitoring_textView);
        mWebRTCMenu = (TextView) view.findViewById(R.id.settings_webrtc_menu_textView);
        mServersMenu = (TextView) view.findViewById(R.id.settings_servers_textView);

        mWiFiMenu.setOnClickListener(this);
        mScreenSoundMenu.setOnClickListener(this);
        mSocialMenu.setOnClickListener(this);
        mMonitoringMenu.setOnClickListener(this);
        mWebRTCMenu.setOnClickListener(this);
        mServersMenu.setOnClickListener(this);

        mButtonList.add(mWiFiMenu);
        mButtonList.add(mScreenSoundMenu);
        mButtonList.add(mWebRTCMenu);
        mButtonList.add(mSocialMenu);
        mButtonList.add(mMonitoringMenu);
        mButtonList.add(mServersMenu);

        getFragmentManager().beginTransaction().add(R.id.settings_fragment_container, new SettingsWifiFragment()).commit();

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

            case R.id.settings_servers_textView:
                changeButtonState(mServersMenu);
                fragment = new SettingsServersFragment();
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
    }

}
