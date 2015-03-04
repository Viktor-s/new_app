package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import me.justup.upme.R;


public class BreakCallDialog extends DialogFragment {
    public static final String BREAK_CALL_DIALOG = "break_call_dialog";
    private static final String BREAK_CALL_USER = "break_call_user";


    public static BreakCallDialog newInstance(String userName) {
        Bundle args = new Bundle();
        args.putString(BREAK_CALL_USER, userName);

        BreakCallDialog fragment = new BreakCallDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String userName = getArguments().getString(BREAK_CALL_USER);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_push_break_call, null);

        setCancelable(false);

        TextView mUserName = (TextView) dialogView.findViewById(R.id.break_call_user_name);
        mUserName.setText(userName);

        builder.setView(dialogView).setTitle(R.string.dialog_video_call)
                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Fragment videoFragment = getFragmentManager().findFragmentById(R.id.container_video_chat);
                        if (videoFragment != null)
                            getFragmentManager().beginTransaction().remove(videoFragment).commit();
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

}
