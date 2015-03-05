package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import me.justup.upme.R;


public class ViewImageDialog extends DialogFragment {
    public static final String VIEW_IMAGE_DIALOG = "view_image_dialog";
    private static final String VIEW_IMAGE_FILE_NAME = "view_image_file_name";
    private static final String VIEW_IMAGE_FILE_PATH = "view_image_file_path";


    public static ViewImageDialog newInstance(final String fileName, final String filePath) {
        Bundle args = new Bundle();
        args.putString(VIEW_IMAGE_FILE_NAME, fileName);
        args.putString(VIEW_IMAGE_FILE_PATH, filePath);

        ViewImageDialog fragment = new ViewImageDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String fileName = (String) getArguments().getSerializable(VIEW_IMAGE_FILE_NAME);
        String filePath = (String) getArguments().getSerializable(VIEW_IMAGE_FILE_PATH);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view_image, null);

        ImageView mImagePanel = (ImageView) dialogView.findViewById(R.id.dialog_image_panel);
        Bitmap image = BitmapFactory.decodeFile(filePath);
        mImagePanel.setImageBitmap(image);

        builder.setView(dialogView).setTitle(fileName).setPositiveButton(R.string.button_close, null);

        return builder.create();
    }

}
