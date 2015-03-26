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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import me.justup.upme.R;
import me.justup.upme.entity.FileDropShareWithQuery;
import me.justup.upme.entity.FileGetShareWithQuery;
import me.justup.upme.entity.FileGetShareWithResponse;
import me.justup.upme.http.ApiWrapper;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class FileRemoveShareDialog extends DialogFragment {
    private static final String TAG = makeLogTag(FileRemoveShareDialog.class);

    public static final String FILE_REMOVE_SHARE_DIALOG = "file_remove_share_dialog";
    private static final String FILE_HASH = "file_hash";

    private LayoutInflater mLayoutInflater;
    private LinearLayout mUserShareLayout;
    private String mFileHash;


    public static FileRemoveShareDialog newInstance(final String fileHash) {
        Bundle args = new Bundle();
        args.putString(FILE_HASH, fileHash);

        FileRemoveShareDialog fragment = new FileRemoveShareDialog();
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
        View dialogView = inflater.inflate(R.layout.dialog_remove_file_share, null);

        mUserShareLayout = (LinearLayout) dialogView.findViewById(R.id.user_remove_share_items_layout);

        FileGetShareWithQuery query = new FileGetShareWithQuery();
        query.params.file_hash = mFileHash;
        ApiWrapper.query(query, new GetContactList());

        builder.setView(dialogView).setTitle(R.string.file_remove_share_for)
                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    @SuppressLint("InflateParams")
    private void addUser(final int userId, final String userName) {
        final View item = mLayoutInflater.inflate(R.layout.item_file_remove_share, null);

        TextView mUserName = (TextView) item.findViewById(R.id.user_name_item_textView);
        mUserName.setText(userName);
        ImageView delShare = (ImageView) item.findViewById(R.id.del_share_item);

        delShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileDropShareWithQuery query = new FileDropShareWithQuery();
                query.params.file_hash = mFileHash;
                query.params.member_ids = userId;
                ApiWrapper.query(query, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String content = ApiWrapper.responseBodyToString(responseBody);
                        LOGD(TAG, "FileDropShareWithQuery onSuccess(): " + content);

                        mUserShareLayout.removeView(item);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        String content = ApiWrapper.responseBodyToString(responseBody);
                        LOGE(TAG, "FileDropShareWithQuery onFailure(): " + content);
                    }
                });
            }
        });

        if (userId == 0) {
            delShare.setVisibility(View.GONE);
        }

        mUserShareLayout.addView(item);
    }

    private class GetContactList extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "GetContactList onSuccess(): " + content);

            if (FileRemoveShareDialog.this.isAdded()) {
                FileGetShareWithResponse response = null;
                try {
                    response = ApiWrapper.gson.fromJson(content, FileGetShareWithResponse.class);
                } catch (JsonSyntaxException e) {
                    LOGE(TAG, "gson.fromJson:\n" + content);
                }

                if (response != null && response.result != null) {
                    if (response.result.size() > 0) {
                        for (FileGetShareWithResponse.Result user : response.result) {
                            addUser(user.id, user.name);
                        }
                    } else {
                        addUser(0, getString(R.string.file_share_user_list_empty));
                    }
                } else {
                    if (response != null && response.error != null) {
                        addUser(0, response.error.data);
                    }
                }
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "GetContactList onFailure(): " + content);

            if (FileRemoveShareDialog.this.isAdded()) {
                if (error != null) {
                    addUser(0, error.getMessage());
                } else {
                    addUser(0, content);
                }
            }
        }
    }

}
