package me.justup.upme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.justup.upme.fragments.SettingsLangFragment;
import me.justup.upme.fragments.SettingsScreenFragment;
import me.justup.upme.fragments.SettingsSocialFragment;
import me.justup.upme.fragments.SettingsWifiFragment;


public class SettingsActivity extends Activity implements View.OnClickListener {
    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";

    private TextView mWiFiMenu;
    private TextView mScreenSoundMenu;
    private TextView mSocialMenu;
    private TextView mLangMenu;
    private ArrayList<TextView> mButtonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mWiFiMenu = (TextView) findViewById(R.id.settings_wifi_menu_textView);
        mScreenSoundMenu = (TextView) findViewById(R.id.settings_screen_menu_textView);
        mSocialMenu = (TextView) findViewById(R.id.settings_social_menu_textView);
        mLangMenu = (TextView) findViewById(R.id.settings_lang_menu_textView);
        ImageView mCloseSettings = (ImageView) findViewById(R.id.close_settings_activity_imageView);

        mWiFiMenu.setOnClickListener(this);
        mScreenSoundMenu.setOnClickListener(this);
        mSocialMenu.setOnClickListener(this);
        mLangMenu.setOnClickListener(this);
        mCloseSettings.setOnClickListener(this);

        mButtonList.add(mWiFiMenu);
        mButtonList.add(mScreenSoundMenu);
        mButtonList.add(mSocialMenu);
        mButtonList.add(mLangMenu);

        getFragmentManager().beginTransaction().add(R.id.settings_fragment_container, new SettingsWifiFragment()).commit();
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

            case R.id.settings_lang_menu_textView:
                changeButtonState(mLangMenu);
                fragment = new SettingsLangFragment();
                break;

            default:
                finish();
                break;
        }

        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.settings_fragment_container, fragment).commit();
        }
    }

    private void changeButtonState(TextView activeButton) {
        for (TextView button : mButtonList) {
            button.setBackground(null);
        }

        activeButton.setBackgroundResource(R.drawable.settings_back_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
