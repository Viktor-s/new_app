package me.justup.upme.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.dialogs.SetWiFiDialog;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class SettingsWifiFragment extends Fragment {
    private static final String TAG = makeLogTag(SettingsWifiFragment.class);

    private static final String WPA = "WPA";
    private static final String WEP = "WEP";

    private LinearLayout mWiFiPanel;
    private WifiManager mWifiManager;
    private WifiScanReceiver mWifiScanReceiver;
    private String mCurrentSSID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_wifi, container, false);

        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        mWifiScanReceiver = new WifiScanReceiver();
        mWifiManager.startScan();

        mWiFiPanel = (LinearLayout) v.findViewById(R.id.wifi_items_panel);
        CheckBox mOnOffWiFi = (CheckBox) v.findViewById(R.id.wifi_checkBox);
        mOnOffWiFi.setChecked(mWifiManager.isWifiEnabled());
        mOnOffWiFi.setOnCheckedChangeListener(new OnOffWiFiListener());

        return v;
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mWifiScanReceiver);
        super.onPause();
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = mWifiManager.getScanResults();
            mCurrentSSID = getCurrentSSID(getActivity());

            mWiFiPanel.removeAllViews();

            int listSize = wifiScanList.size();
            for (int i = 0; i < listSize; i++) {
                accessPointsList(wifiScanList.get(i));
            }
        }
    }

    @SuppressLint("InflateParams")
    private void accessPointsList(ScanResult accessPoint) {
        final LinearLayout item = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.list_wifi_item, null);

        TextView mWiFiName = (TextView) item.findViewById(R.id.wifi_name);
        ImageView mCurrentWiFi = (ImageView) item.findViewById(R.id.connected);
        ImageView mWiFiProtected = (ImageView) item.findViewById(R.id.network_protected);
        ImageView mWiFiLevel = (ImageView) item.findViewById(R.id.wifi_strength);
        item.setOnClickListener(new OnWiFiItemClickListener(accessPoint.SSID, accessPoint.capabilities != null ? accessPoint.capabilities : ""));

        mWiFiName.setText(accessPoint.SSID);
        if (accessPoint.capabilities.contains(WEP) || accessPoint.capabilities.contains(WPA)) {
            mWiFiProtected.setBackground(getResources().getDrawable(R.drawable.wifi_lock));
        }

        int level = Math.abs(accessPoint.level);
        if (level > 45 && level < 55) {
            mWiFiLevel.setBackgroundResource(R.drawable.wifi_4);
        } else if (level > 35 && level < 65) {
            mWiFiLevel.setBackgroundResource(R.drawable.wifi_3);
        } else if (level > 20 && level < 80) {
            mWiFiLevel.setBackgroundResource(R.drawable.wifi_2);
        } else if (level > 10 && level < 90) {
            mWiFiLevel.setBackgroundResource(R.drawable.wifi_1);
        } else {
            mWiFiLevel.setBackgroundResource(R.drawable.wifi_0);
        }

        if (mCurrentSSID != null && mCurrentSSID.equals(accessPoint.BSSID)) {
            mCurrentWiFi.setBackgroundResource(R.drawable.wifi_selected);
        }

        mWiFiPanel.addView(item);
    }

    private String getCurrentSSID(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
            if (connectionInfo != null && !connectionInfo.getSSID().isEmpty()) {
                ssid = connectionInfo.getBSSID();
            }
        }
        return ssid;
    }

    private class OnWiFiItemClickListener implements View.OnClickListener {
        private String networkSSID;
        private String securityType;

        public OnWiFiItemClickListener(String networkSSID, String securityType) {
            this.networkSSID = networkSSID;
            this.securityType = securityType;
        }

        @Override
        public void onClick(View v) {
            SetWiFiDialog dialog = SetWiFiDialog.newInstance(networkSSID, securityType);
            dialog.setTargetFragment(SettingsWifiFragment.this, 0);
            dialog.show(getFragmentManager(), SetWiFiDialog.SET_WIFI_DIALOG);
        }
    }

    public void connect(String networkSSID, String networkPass, String securityType) {
        LOGD(TAG, "networkSSID:" + networkSSID + " networkPass:" + networkPass + " securityType:" + securityType);

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";

        if (securityType.contains(WPA)) {
            conf.preSharedKey = "\"" + networkPass + "\"";

        } else if (securityType.contains(WEP)) {
            conf.wepKeys[0] = "\"" + networkPass + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        } else {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        mWifiManager.addNetwork(conf);

        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(i.networkId, true);
                mWifiManager.reconnect();

                break;
            }
        }
    }

    private class OnOffWiFiListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(false);
            } else {
                mWifiManager.setWifiEnabled(true);
            }
        }
    }

}
