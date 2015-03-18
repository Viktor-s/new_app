package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class FilePropertiesDialog extends DialogFragment {
    private static final String TAG = makeLogTag(FilePropertiesDialog.class);

    public static final String FILE_PROPERTIES_DIALOG = "file_properties_dialog";
    private static final String FILE_PROPERTIES_FILE_HASH = "file_properties_file_hash";


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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_file_properties, null);

        ImageView closeDialog = (ImageView) dialogView.findViewById(R.id.file_prop_close_imageView);

        builder.setView(dialogView);

        return builder.create();
    }

}
