package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.List;

import me.justup.upme.JustUpApplication;
import me.justup.upme.R;
import me.justup.upme.entity.BaseMethodEmptyQuery;
import me.justup.upme.entity.FileAddShareWithQuery;
import me.justup.upme.entity.GetAllContactsResponse;
import me.justup.upme.http.ApiWrapper;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class FileShareDialog extends DialogFragment {
    private static final String TAG = makeLogTag(FileShareDialog.class);

    public static final String FILE_SHARE_DIALOG = "file_share_dialog";
    private static final String FILE_HASH = "file_hash";

    private LayoutInflater mLayoutInflater;
    private LinearLayout mUserShareLayout;
    private String mFileHash;


    public static FileShareDialog newInstance(final String fileHash) {
        Bundle args = new Bundle();
        args.putString(FILE_HASH, fileHash);

        FileShareDialog fragment = new FileShareDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mFileHash = (String) getArguments().getSerializable(FILE_HASH);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_file_share, null);

        mUserShareLayout = (LinearLayout) dialogView.findViewById(R.id.user_share_items_layout);

        BaseMethodEmptyQuery query = new BaseMethodEmptyQuery();
        query.method = ApiWrapper.ACCOUNT_GET_ALL_CONTACTS;
        ApiWrapper.query(query, new GetContactList());

        builder.setView(dialogView).setTitle(R.string.file_share_for)
                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    private class GetContactList extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "onSuccess(): " + content);

            GetAllContactsResponse response = null;
            try {
                response = ApiWrapper.gson.fromJson(content, GetAllContactsResponse.class);
            } catch (JsonSyntaxException e) {
                LOGE(TAG, "gson.fromJson:\n" + content);
            }

            if (response != null && response.result != null) {
                final List<GetAllContactsResponse.Result.Parents> allUsers = response.result.getAllUsers();
                if (allUsers != null) {
                    for (GetAllContactsResponse.Result.Parents user : allUsers) {
                        setUserItem(user.id, user.name, false);
                    }
                }

            } else if (response != null && response.error != null) {
                setUserItem(0, response.error.data, true);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure(): " + content);

            if (error != null) {
                setUserItem(0, error.getMessage(), true);
            } else {
                setUserItem(0, content, true);
            }
        }
    }

    @SuppressLint("InflateParams")
    private void setUserItem(final int userId, final String userName, boolean isErrorMessage) {
        final View item = mLayoutInflater.inflate(R.layout.item_file_share, null);

        TextView mUserName = (TextView) item.findViewById(R.id.share_user_name_TextView);
        mUserName.setText(userName);

        CheckBox setShareCheckBox = (CheckBox) item.findViewById(R.id.file_share_checkBox);
        setShareCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    addFileShareWith(mFileHash, userId);
            }
        });

        if (isErrorMessage) {
            setShareCheckBox.setVisibility(View.GONE);
        }

        mUserShareLayout.addView(item);
    }

    private void addFileShareWith(final String fileHash, final int friendId) {
        FileAddShareWithQuery query = new FileAddShareWithQuery();
        query.params.file_hash = fileHash;
        query.params.member_ids = friendId;

        ApiWrapper.query(query, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGD(TAG, "addFileShareWith onSuccess(): " + content);

                if (FileShareDialog.this.isAdded()) {
                    Toast.makeText(JustUpApplication.getApplication().getApplicationContext(), getString(R.string.share_access_grant), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGE(TAG, "addFileShareWith onFailure(): " + content);
            }
        });
    }

}
