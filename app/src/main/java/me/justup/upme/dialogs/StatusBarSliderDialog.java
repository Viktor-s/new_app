package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import me.justup.upme.interfaces.OnDownloadCloudFile;
import me.justup.upme.interfaces.OnLoadMailFragment;
import me.justup.upme.utils.AppLocale;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class StatusBarSliderDialog extends DialogFragment {
    private static final String TAG = makeLogTag(StatusBarSliderDialog.class);
    public static final String STATUS_BAR_DIALOG = "status_bar_dialog";

    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATE_FORMAT = "E dd MMMM yyyy";

    private static final String LEFT_BRACERS = "[ ";
    private static final String RIGHT_BRACERS = " ]";

    private LinearLayout mPushContainer;
    private StringBuilder mStringBuilder = new StringBuilder();
    private OnLoadMailFragment mOnLoadMailFragment;
    private OnDownloadCloudFile mOnDownloadCloudFile;


    public static StatusBarSliderDialog newInstance() {
        return new StatusBarSliderDialog();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnLoadMailFragment = (OnLoadMailFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnLoadMailFragment");
        }

        try {
            mOnDownloadCloudFile = (OnDownloadCloudFile) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDownloadCloudFile");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBAdapter.getInstance().openDatabase();

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

        Button mClearAllMessages = (Button) dialogView.findViewById(R.id.clear_all_messages_button);
        mClearAllMessages.setOnClickListener(new OnClearAllPush());

        @SuppressWarnings("unchecked")
        ArrayList<Push> mPushArray = DBAdapter.getInstance().loadPushArray();
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

        DBAdapter.getInstance().closeDatabase();
    }

    @SuppressLint("InflateParams")
    private void addPushToList(final Push push) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.item_push, null);

        TextView mPushTitle = (TextView) item.findViewById(R.id.push_title_TextView);
        TextView mPushDate = (TextView) item.findViewById(R.id.push_date_TextView);

        mStringBuilder.setLength(0);
        mStringBuilder.append(push.getUserName());
        switch (push.getType()) {
            case MailFragment.JABBER:
                mStringBuilder.append(getString(R.string.invites_you)).append(getString(R.string.in_chat));
                break;

            case MailFragment.WEBRTC:
                mStringBuilder.append(getString(R.string.invites_you)).append(getString(R.string.on_video));
                break;

            case MailFragment.FILE:
                mStringBuilder.append(getString(R.string.submit_file)).append(LEFT_BRACERS).append(push.getFileName()).append(RIGHT_BRACERS);
                break;

            case MailFragment.ORDER_FORM:
                mStringBuilder.append(LEFT_BRACERS).append(push.getPushDescription()).append(RIGHT_BRACERS);
                break;

            case MailFragment.ORDER_INFO:
                mStringBuilder.append(LEFT_BRACERS).append(push.getPushDescription()).append(RIGHT_BRACERS);
                break;

            default:
                break;
        }

        mPushTitle.setText(mStringBuilder.toString());
        mPushDate.setText(push.getDate());

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBAdapter.getInstance().deletePush(push.getId());

                if (push.getType() != MailFragment.FILE) {
                    if (push.getType() == MailFragment.WEBRTC) {
                        mOnLoadMailFragment.onLoadMailFragment(null);
                    } else {
                        if (push.getType() != MailFragment.ORDER_INFO) {
                            mOnLoadMailFragment.onLoadMailFragment(push);
                        }
                    }
                } else {
                    mOnDownloadCloudFile.onDownloadCloudFile(push.getLink(), push.getFileName());
                }

                dismiss();
            }
        });

        mPushContainer.addView(item);
    }

    private class OnClearAllPush implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            DBAdapter.getInstance().deleteAllPush();
            dismiss();
        }
    }

}
