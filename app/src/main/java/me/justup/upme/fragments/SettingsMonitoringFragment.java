package me.justup.upme.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import me.justup.upme.R;
import me.justup.upme.services.ApplicationSupervisorService;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppPreferences;


public class SettingsMonitoringFragment extends Fragment {
    private AppPreferences mAppPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_monitoring, container, false);

        mAppPreferences = new AppPreferences(getActivity());

        CheckBox mOnOffMonitoring = (CheckBox) v.findViewById(R.id.monitoring_checkBox);
        mOnOffMonitoring.setChecked(mAppPreferences.isMonitoring());
        mOnOffMonitoring.setOnCheckedChangeListener(new MonitoringListener());

        return v;
    }

    private class MonitoringListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mAppPreferences.isMonitoring()) {
                mAppPreferences.setMonitoring(false);
                getActivity().stopService(new Intent(AppContext.getAppContext(), ApplicationSupervisorService.class));
            } else {
                mAppPreferences.setMonitoring(true);
                getActivity().startService(new Intent(AppContext.getAppContext(), ApplicationSupervisorService.class));
            }
        }
    }

}
