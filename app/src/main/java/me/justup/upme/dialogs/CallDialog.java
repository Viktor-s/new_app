package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import me.justup.upme.R;
import me.justup.upme.entity.Push;
import me.justup.upme.interfaces.OnLoadMailFragment;


public class CallDialog extends DialogFragment {
    public static final String CALL_DIALOG = "call_dialog";
    private static final String CALL_PUSH = "call_dialog_push";

    private OnLoadMailFragment mListener;


    public static CallDialog newInstance(final Push push) {
        Bundle args = new Bundle();
        args.putSerializable(CALL_PUSH, push);

        CallDialog fragment = new CallDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnLoadMailFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnLoadMailFragment");
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Push push = (Push) getArguments().getSerializable(CALL_PUSH);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_push_call, null);

        TextView mUserName = (TextView) dialogView.findViewById(R.id.call_user_name_textView);
        mUserName.setText(push.getUserName());

        builder.setView(dialogView).setTitle(R.string.dialog_video_call)
                .setPositiveButton(R.string.dialog_answer_call, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onLoadMailFragment(push);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel_call, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // out push

                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

}
