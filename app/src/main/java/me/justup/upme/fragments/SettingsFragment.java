package me.justup.upme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.justup.upme.LoginActivity;
import me.justup.upme.R;
import me.justup.upme.interfaces.OnCloseFragment;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppPreferences;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class SettingsFragment extends Fragment {
    private static final String TAG = makeLogTag(SettingsFragment.class);
    private OnCloseFragment mOnCloseSettingsFragmentCallback;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnCloseSettingsFragmentCallback = (OnCloseFragment) activity;
        } catch (ClassCastException e) {
            LOGE(TAG, "Must implement OnCloseFragment", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button mCloseFragment = (Button) view.findViewById(R.id.settings_close_button);
        mCloseFragment.setOnClickListener(new OnCloseListener());

        Button mExitButton = (Button) view.findViewById(R.id.settings_exit_button);
        mExitButton.setOnClickListener(new OnExitListener());

        return view;
    }

    private class OnCloseListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mOnCloseSettingsFragmentCallback.onCloseFragment();
        }
    }

    private class OnExitListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new AppPreferences(AppContext.getAppContext()).clearPreferences();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }

}
