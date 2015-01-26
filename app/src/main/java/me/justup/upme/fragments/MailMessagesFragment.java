package me.justup.upme.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.justup.upme.R;
import me.justup.upme.utils.AnimateButtonClose;
import me.justup.upme.utils.AppContext;

public class MailMessagesFragment extends Fragment {

    private static final String ARG_MAIL_MESSAGES = "mail_messages";
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_TAKE_IMAGE_FILE = 1;
    private Button mMailMessageCloseButton;
    private Button mStaplebutton;
    private RelativeLayout mAddFileContainer;
    private Button mAddPhotoButton;
    private Button mAddAudioButton;
    private String mCurrentPhotoPath;
    private ImageView mImageAttached;

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String audioOutputFile = null;
    private Button mAudioRecordButton;
    private Button mAudioPlayButton;
    private Button mAudioConfirmButton;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;

    public static MailMessagesFragment newInstance() {
        MailMessagesFragment fragment = new MailMessagesFragment();
        Bundle args = new Bundle();
        //args.put(ARG_MAIL_MESSAGES,);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail_messages, container, false);
        mAddFileContainer = (RelativeLayout) view.findViewById(R.id.mail_messages_add_file_container);
        mMailMessageCloseButton = (Button) view.findViewById(R.id.mail_messages_close_button);
        mMailMessageCloseButton.setVisibility(View.INVISIBLE);
        mMailMessageCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragment().getChildFragmentManager().beginTransaction().remove(MailMessagesFragment.this).commit();
            }
        });
        mStaplebutton = (Button) view.findViewById(R.id.mail_messages_staple_button);
        mStaplebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAddFileContainer.getVisibility() == View.GONE) {
                    mAddFileContainer.setVisibility(View.VISIBLE);
                } else {
                    mAddFileContainer.setVisibility(View.GONE);
                }
            }
        });
        AnimateButtonClose.animateButtonClose(mMailMessageCloseButton);
        mAddPhotoButton = (Button) view.findViewById(R.id.mail_messages_add_photo_button);
        mAddAudioButton = (Button) view.findViewById(R.id.mail_messages_add_audio_button);
        mAddPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        mAddAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecordAudioDialog();
            }
        });
        mImageAttached = (ImageView) view.findViewById(R.id.mail_messages_image_attach_imageView);
        return view;
    }

    private void selectImage() {
        createTakePictureDialog();
    }

    private void createTakePictureDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MailMessagesFragment.this.getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(AppContext.getAppContext().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            //Creating file error
                        }
                        if (photoFile != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile));
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    }
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_TAKE_IMAGE_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                mImageAttached.setImageBitmap(bitmap);
            } else if (requestCode == REQUEST_TAKE_IMAGE_FILE) {
                Uri selectedImageUri = data.getData();
                String tempPath = getPath(selectedImageUri, MailMessagesFragment.this.getActivity());
                BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(tempPath, mBitmapOptions);
                mImageAttached.setImageBitmap(bitmap);
            }
        }
    }

    public String getPath(Uri uri, Activity activity) {
        if (uri == null) {
            // TODO show user feedback
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void showRecordAudioDialog() {
        audioOutputFile = Environment.getExternalStorageDirectory().
                getAbsolutePath() + "/test_record.3gpp";
        final Dialog dialog = new Dialog(MailMessagesFragment.this.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_record_audio);
        mAudioRecordButton = (Button) dialog.findViewById(R.id.dialog_audio_record_button);
        mAudioRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    mAudioRecordButton.setText("Stop recording");
                } else {
                    mAudioRecordButton.setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        });

        mAudioPlayButton = (Button) dialog.findViewById(R.id.dialog_audio_play_button);
        mAudioPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    mAudioPlayButton.setText("Stop playing");
                } else {
                    mAudioPlayButton.setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        });
        mAudioConfirmButton = (Button) dialog.findViewById(R.id.dialog_audio_confirm_button);
        mAudioConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(audioOutputFile);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //   mAudioPlayButton.setText("start");
                    //  stopPlaying();
                }
            });
        } catch (IOException e) {

        }
    }

    private void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(audioOutputFile);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
        }
    }

    private void stopRecording() {
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

}
