package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.Push;
import me.justup.upme.fragments.MailFragment;
import me.justup.upme.fragments.StatusBarFragment;
import me.justup.upme.utils.AppLocale;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class StatusBarSliderDialog extends DialogFragment {
    private static final String TAG = makeLogTag(StatusBarSliderDialog.class);

    public static final String STATUS_BAR_DIALOG = "status_bar_dialog";

    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATE_FORMAT = "E dd MMMM yyyy";

    private DBAdapter mDBAdapter;
    private LinearLayout mPushContainer;
    private StringBuilder mStringBuilder = new StringBuilder();


    public static StatusBarSliderDialog newInstance() {
        return new StatusBarSliderDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDBAdapter = new DBAdapter(getActivity());
        mDBAdapter.open();

        Intent i = new Intent(StatusBarFragment.BROADCAST_ACTION_PUSH);
        i.putExtra(StatusBarFragment.BROADCAST_EXTRA_IS_NEW_MESSAGE, false);
        getActivity().sendBroadcast(i);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.StatusBarDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_status_bar, null);

        mPushContainer = (LinearLayout) dialogView.findViewById(R.id.push_container);

        Date date = new Date();
        SimpleDateFormat mTimeFormat = new SimpleDateFormat(TIME_FORMAT, AppLocale.getAppLocale());
        SimpleDateFormat mDateFormat = new SimpleDateFormat(DATE_FORMAT, AppLocale.getAppLocale());
        String currentTime = mTimeFormat.format(date);
        String currentDate = mDateFormat.format(date);

        TextView mTimeTextView = (TextView) dialogView.findViewById(R.id.status_bar_time);
        TextView mDateTextView = (TextView) dialogView.findViewById(R.id.status_bar_date);
        mTimeTextView.setText(currentTime);
        mDateTextView.setText(currentDate);

        @SuppressWarnings("unchecked")
        ArrayList<Push> mPushArray = mDBAdapter.loadPushArray();
        LOGD(TAG, "mPushArray: " + mPushArray.toString());

        if (mPushArray.size() > 0) {
            for (Push push : mPushArray) {
                addPushToList(push);
            }
        }

        builder.setView(dialogView);

        return builder.create();
    }

    @Override
    public void onStop() {
        super.onStop();

        mDBAdapter.close();
    }

    @SuppressLint("InflateParams")
    private void addPushToList(final Push push) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.item_push, null);

        mStringBuilder.setLength(0);
        mStringBuilder.append(push.getUserName()).append(getString(R.string.invites_you));
        switch (push.getType()) {
            case MailFragment.JABBER:
                mStringBuilder.append(getString(R.string.in_chat));
                break;

            case MailFragment.WEBRTC:
                mStringBuilder.append(getString(R.string.on_video));
                break;

            default:
                break;
        }

        TextView mPushTitle = (TextView) item.findViewById(R.id.push_title_TextView);
        mPushTitle.setText(mStringBuilder.toString());

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDBAdapter.deletePush(push.getId());

                // do som

                dismiss();
            }
        });

        mPushContainer.addView(item);
    }

}
