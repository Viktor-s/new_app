package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import me.justup.upme.R;


public class ChangeAvatarDialog extends DialogFragment {
    public static final String CHANGE_AVATAR_DIALOG = "change_avatar_dialog";


    public static ChangeAvatarDialog newInstance() {
        return new ChangeAvatarDialog();
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_avatar, null);

        builder.setView(dialogView).setTitle("Смена аватарки").setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

}
