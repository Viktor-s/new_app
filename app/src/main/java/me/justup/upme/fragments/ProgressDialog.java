package me.justup.upme.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.justup.upme.R;

public class ProgressDialog extends DialogFragment {
    private static final String TAG = ProgressDialog.class.getSimpleName();

    public static final String TAG_WAIT_DIALOG = "dialog.wait";

    private static final String KEY_TEXT = "key.text";

    private TextView mTextView = null;

    public static ProgressDialog newInstance(String text) {
        ProgressDialog dlg = new ProgressDialog();
        Bundle args = new Bundle();
        args.putString(KEY_TEXT, text);
        dlg.setArguments(args);

        return dlg;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(width, height);

        return dialog;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.progress_dialog, null);

        mTextView = (TextView) v.findViewById(R.id.text);
        mTextView.setText(getArguments().getString(KEY_TEXT));

        return v;
    }

    public void setText(int text){
        mTextView.setText(getString(text));
        mTextView.invalidate();
    }

    public void setText(String text){
        mTextView.setText(text);
        mTextView.invalidate();
    }
}
