package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import me.justup.upme.R;


public class StatusBarSliderDialog extends DialogFragment {
    public static final String STATUS_BAR_DIALOG = "status_bar_dialog";
    private static final String STATUS_BAR_DATA = "status_bar_data";


    public static StatusBarSliderDialog newInstance(String testString) {
        Bundle args = new Bundle();
        args.putString(STATUS_BAR_DATA, testString);

        StatusBarSliderDialog fragment = new StatusBarSliderDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // final String test = (String) getArguments().getSerializable(STATUS_BAR_DATA);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.StatusBarDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_status_bar, null);

        builder.setView(dialogView);

        return builder.create();
    }

}
