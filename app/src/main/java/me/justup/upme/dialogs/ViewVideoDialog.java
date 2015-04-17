package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import me.justup.upme.R;


public class ViewVideoDialog extends DialogFragment {
    public static final String VIEW_VIDEO_DIALOG = "view_video_dialog";
    private static final String VIEW_VIDEO_FILE_NAME = "view_video_file_name";
    private static final String VIEW_VIDEO_FILE_PATH = "view_video_file_path";


    public static ViewVideoDialog newInstance(final String fileName, final String filePath) {
        Bundle args = new Bundle();
        args.putString(VIEW_VIDEO_FILE_NAME, fileName);
        args.putString(VIEW_VIDEO_FILE_PATH, filePath);

        ViewVideoDialog fragment = new ViewVideoDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String fileName = (String) getArguments().getSerializable(VIEW_VIDEO_FILE_NAME);
        String filePath = (String) getArguments().getSerializable(VIEW_VIDEO_FILE_PATH);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view_video, null);

        VideoView mVideoView = (VideoView) dialogView.findViewById(R.id.dialog_videoView);

        mVideoView.setZOrderOnTop(true);
        mVideoView.setVideoPath(filePath);

        MediaController mediaController = new MediaController(getActivity());
        mediaController.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mediaController);

        mVideoView.requestFocus();
        mVideoView.start();

        builder.setView(dialogView).setTitle(fileName).setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

}
