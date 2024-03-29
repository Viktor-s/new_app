package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.fragments.SettingsWifiFragment;
import me.justup.upme.utils.BackAwareEditText;
import me.justup.upme.utils.CommonUtils;


public class SetWiFiDialog extends DialogFragment {
    public static final String SET_WIFI_DIALOG = "set_wifi_dialog";
    private static final String NETWORK_SSID = "wifi_dialog_network_ssid";
    private static final String SECURITY_TYPE = "wifi_dialog_security_type";


    public static SetWiFiDialog newInstance(String networkSSID, String securityType) {
        Bundle args = new Bundle();
        args.putString(NETWORK_SSID, networkSSID);
        args.putString(SECURITY_TYPE, securityType);

        SetWiFiDialog fragment = new SetWiFiDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String mNetworkSSID = (String) getArguments().getSerializable(NETWORK_SSID);
        final String mSecurityType = (String) getArguments().getSerializable(SECURITY_TYPE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_wifi, null);

        final BackAwareEditText mNetworkPass = (BackAwareEditText) dialogView.findViewById(R.id.wifi_pass);
        mNetworkPass.setBackPressedListener(new BackAwareEditText.BackPressedListener() {
            @Override
            public void onImeBack(BackAwareEditText editText) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideNavBar();
                }
            }
        });

        builder.setView(dialogView).setTitle(mNetworkSSID)
                .setPositiveButton(R.string.settings_wifi_dialog_connect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String password = mNetworkPass.getText().toString();
                        ((SettingsWifiFragment) getTargetFragment()).connect(mNetworkSSID, password != null ? password : "", mSecurityType);
                        CommonUtils.hideKeyboard(getActivity(), mNetworkPass);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.settings_wifi_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        CommonUtils.hideKeyboard(getActivity(), mNetworkPass);
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

}
