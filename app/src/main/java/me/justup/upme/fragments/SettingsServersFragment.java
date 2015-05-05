package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import me.justup.upme.R;
import me.justup.upme.utils.CommonUtils;
import me.justup.upme.utils.ServerSwitcher;


public class SettingsServersFragment extends Fragment {
    public static final String CURRENT_SERVER = "Текущий: ";
    private TextView mCurrentServerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_servers, container, false);

        mCurrentServerView = (TextView) v.findViewById(R.id.current_server_textView);
        updateServerInfo();

        final EditText mNewUrlString = (EditText) v.findViewById(R.id.test_set_url_editText);
        Button mSetNewUrl = (Button) v.findViewById(R.id.test_set_url_button);
        mSetNewUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUrl = mNewUrlString.getText().toString();

                if (newUrl.length() > 2) {
                    mNewUrlString.setText("");
                    ServerSwitcher.getInstance().setEasyUrl(newUrl);

                    updateServerInfo();
                    CommonUtils.clearAllAppData();
                }
            }
        });

        RadioGroup radiogroup = (RadioGroup) v.findViewById(R.id.server_radioGroup);
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.server1_radioButton:
                        ServerSwitcher.getInstance().setUrl("http://test.justup.me/uptabinterface/jsonrpc/");
                        ServerSwitcher.getInstance().setCloudStorageUrl("http://test.justup.me/CloudStorage");

                        updateServerInfo();
                        CommonUtils.clearAllAppData();
                        break;

                    case R.id.server2_radioButton:
                        ServerSwitcher.getInstance().setEasyUrl("pre-prod.justup.me");

                        updateServerInfo();
                        CommonUtils.clearAllAppData();
                        break;

                    default:
                        break;
                }
            }
        });

        return v;
    }

    private void updateServerInfo() {
        mCurrentServerView.setText(CURRENT_SERVER + ServerSwitcher.getInstance().getUrl());
    }

}
