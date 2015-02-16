package me.justup.upme.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;


public class WarningDialog extends DialogFragment {
    public static final String WARNING_DIALOG = "warning_dialog";
    private static final String TITLE = "warning_dialog_title";
    private static final String MESSAGE = "warning_dialog_message";


    public static WarningDialog newInstance(String title, String message) {
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);

        WarningDialog fragment = new WarningDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String mTitle = (String) getArguments().getSerializable(TITLE);
        String mMessage = (String) getArguments().getSerializable(MESSAGE);

        return new AlertDialog.Builder(getActivity()).setTitle(mTitle).setMessage(mMessage)
                .setPositiveButton(android.R.string.ok, null).create();
    }

}
