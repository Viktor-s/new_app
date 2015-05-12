package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.text.SimpleDateFormat;

import me.justup.upme.R;
import me.justup.upme.entity.FileGetPropertiesQuery;
import me.justup.upme.entity.FileGetPropertiesResponse;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.utils.AppLocale;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class FilePropertiesDialog extends DialogFragment {
    private static final String TAG = makeLogTag(FilePropertiesDialog.class);

    public static final String FILE_PROPERTIES_DIALOG = "file_properties_dialog";
    private static final String FILE_PROPERTIES_FILE_HASH = "file_properties_file_hash";

    private static final String DATE_FORMAT = "E dd MMMM yyyy HH:mm";

    private TextView ownerName;
    private TextView dateAdded;
    private SimpleDateFormat mDateFormat;


    public static FilePropertiesDialog newInstance(final String fileHash) {
        Bundle args = new Bundle();
        args.putString(FILE_PROPERTIES_FILE_HASH, fileHash);

        FilePropertiesDialog fragment = new FilePropertiesDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String fileHash = (String) getArguments().getSerializable(FILE_PROPERTIES_FILE_HASH);

        FileGetPropertiesQuery query = new FileGetPropertiesQuery();
        query.params.file_hash = fileHash;
        ApiWrapper.query(query, new GetFileProperties());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_file_properties, null);

        ImageView closeDialog = (ImageView) dialogView.findViewById(R.id.file_prop_close_imageView);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mDateFormat = new SimpleDateFormat(DATE_FORMAT, AppLocale.getAppLocale());

        ownerName = (TextView) dialogView.findViewById(R.id.file_owner_textView);
        dateAdded = (TextView) dialogView.findViewById(R.id.file_added_textView);

        builder.setView(dialogView);

        return builder.create();
    }

    private class GetFileProperties extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "GetFileProperties onSuccess(): " + content);

            FileGetPropertiesResponse response = null;

            try {
                response = ApiWrapper.gson.fromJson(content, FileGetPropertiesResponse.class);
            } catch (JsonSyntaxException e) {
                LOGE(TAG, "gson.fromJson:\n" + content);
            }

            if (response != null && response.result != null) {
                ownerName.setText(response.result.owner.name);
                dateAdded.setText(formatDate(response.result.create_date));
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "GetFileProperties onFailure(): " + content);
        }
    }

    private String formatDate(long unixTimeStamp) {
        long millis = unixTimeStamp * 1000;

        return mDateFormat.format(millis);
    }

}
