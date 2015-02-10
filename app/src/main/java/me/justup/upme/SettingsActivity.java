package me.justup.upme;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.justup.upme.fragments.SettingsLangFragment;
import me.justup.upme.fragments.SettingsScreenFragment;
import me.justup.upme.fragments.SettingsWifiFragment;


public class SettingsActivity extends Activity implements View.OnClickListener {
    private TextView mWiFiMenu;
    private TextView mScreenSoundMenu;
    private TextView mLangMenu;
    private ArrayList<TextView> mButtonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mWiFiMenu = (TextView) findViewById(R.id.settings_wifi_menu_textView);
        mScreenSoundMenu = (TextView) findViewById(R.id.settings_screen_menu_textView);
        mLangMenu = (TextView) findViewById(R.id.settings_lang_menu_textView);
        ImageView mCloseSettings = (ImageView) findViewById(R.id.close_settings_activity_imageView);

        mWiFiMenu.setOnClickListener(this);
        mScreenSoundMenu.setOnClickListener(this);
        mLangMenu.setOnClickListener(this);
        mCloseSettings.setOnClickListener(this);

        mButtonList.add(mWiFiMenu);
        mButtonList.add(mScreenSoundMenu);
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

}
