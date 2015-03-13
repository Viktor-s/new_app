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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import me.justup.upme.R;
import me.justup.upme.entity.GetMailContactQuery;
import me.justup.upme.entity.GetMailContactResponse;
import me.justup.upme.http.ApiWrapper;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class FileShareDialog extends DialogFragment {
    private static final String TAG = makeLogTag(FileShareDialog.class);

    public static final String FILE_SHARE_DIALOG = "file_share_dialog";

    private LayoutInflater mLayoutInflater;
    private LinearLayout mUserShareLayout;
    private Button mShareForAll;


    public static FileShareDialog newInstance() {
        return new FileShareDialog();
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_file_share, null);

        mShareForAll = (Button) dialogView.findViewById(R.id.share_for_all_button);
        mUserShareLayout = (LinearLayout) dialogView.findViewById(R.id.user_share_items_layout);

        ApiWrapper.query(new GetMailContactQuery(), new GetContactList());

        builder.setView(dialogView).setTitle(R.string.you_contacts)
                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // to FileExplorerService for query
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

            GetMailContactResponse response = null;
            try {
                response = ApiWrapper.gson.fromJson(content, GetMailContactResponse.class);
            } catch (JsonSyntaxException e) {
                LOGE(TAG, "gson.fromJson:\n" + content);
            }

            if (response != null && response.result != null) {
                for (GetMailContactResponse.Result user : response.result) {
                    setUserItem(user);
                }
            } else if (response != null && response.error != null) {
                setError(response.error.data);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure(): " + content);

            if (error != null) {
                setError(error.getMessage());
            } else {
                setError(content);
            }
        }
    }

    @SuppressLint("InflateParams")
    private void setUserItem(final GetMailContactResponse.Result user) {
        final View item = mLayoutInflater.inflate(R.layout.item_file_share, null);

        TextView mUserName = (TextView) item.findViewById(R.id.share_user_name_TextView);
        mUserName.setText(user.name);

        mUserShareLayout.addView(item);
    }

    @SuppressLint("InflateParams")
    private void setError(final String error) {
        final View item = mLayoutInflater.inflate(R.layout.item_file_share, null);

        mShareForAll.setVisibility(View.GONE);
        TextView mUserName = (TextView) item.findViewById(R.id.share_user_name_TextView);
        mUserName.setText(error);

        mUserShareLayout.addView(item);
    }

}
