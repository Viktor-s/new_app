package me.justup.upme;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;

import java.util.List;

import me.justup.upme.fragments.AppSettingsWifiFragment;


public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (hasHeaders()) {
            Button button = new Button(this);
            button.setText(getString(R.string.settings_exit));
            setListFooter(button);

            button.setOnClickListener(new OnExitListener());
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        /*
        return MyPreferenceFragmentA.class.getName().equals(fragmentName)
                || MyPreferenceFragmentB.class.getName().equals(fragmentName)
                || // ... Finish with your last fragment.
        */
        return AppSettingsWifiFragment.class.getName().equals(fragmentName);
    }

    private class OnExitListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

}
