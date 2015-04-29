package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;

import me.justup.upme.R;


public class ViewVideoDialog extends DialogFragment {
    public static final String VIEW_VIDEO_DIALOG = "view_video_dialog";
    private static final String VIEW_VIDEO_FILE_NAME = "view_video_file_name";
    private static final String VIEW_VIDEO_FILE_PATH = "view_video_file_path";

    private Button mPlayButton;
    private SeekBar mVideoSeekBar;
    private VideoView mVideoView;

    private boolean isPlaying = true;

    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run() {
            if (mVideoSeekBar != null) {
                mVideoSeekBar.setProgress(mVideoView.getCurrentPosition());
            }

            if (mVideoView != null && mVideoView.isPlaying() && mVideoSeekBar != null) {
                mVideoSeekBar.postDelayed(onEverySecond, 1000);
            }
        }
    };


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

        mPlayButton = (Button) dialogView.findViewById(R.id.video_dialog_play_button);
        mPlayButton.setOnClickListener(new OnPlayPauseListener());

        mVideoSeekBar = (SeekBar) dialogView.findViewById(R.id.video_dialog_seekBar);

        mVideoView = (VideoView) dialogView.findViewById(R.id.dialog_videoView);

        mVideoView.setZOrderOnTop(true);
        mVideoView.setVideoPath(filePath);

        mVideoView.requestFocus();
        mVideoView.start();

        builder.setView(dialogView).setTitle(fileName).setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoSeekBar.setMax(mVideoView.getDuration());
                mVideoSeekBar.postDelayed(onEverySecond, 1000);
            }
        });

        mVideoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mVideoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return builder.create();
    }

    private class OnPlayPauseListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isPlaying) {
                mVideoView.pause();

                mPlayButton.setText("Play");
                isPlaying = false;
            } else {
                mVideoView.start();

                mPlayButton.setText("Pause");
                isPlaying = true;
            }
        }
    }

}
