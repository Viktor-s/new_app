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
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileNotFoundException;
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
    private static final int REQUEST_TAKE_FILE = 2;

    private Button mMailMessageCloseButton;
    private Button mStaplebutton;
    private RelativeLayout mAddFileContainer;
    private Button mAddPhotoButton;
    private Button mAddAudioButton;
    private Button mAddDocumentButton;
    private String mCurrentPhotoPath;
    private ImageButton mImageAttachedImageView;
    private TextView mAudioPreviewCurrentPosTextView;
    private SeekBar mAudioPreviewSeekBar;
    private Handler mAudioPreviewHandler;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String audioOutputFile = null;
    private ToggleButton mAudioRecordButton;

    private AttachFileType mAttachFileType;
    private Bitmap mAttachImageBitmap;

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
        mAttachFileType = AttachFileType.NOTHING;
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
        mAddDocumentButton = (Button) view.findViewById(R.id.mail_messages_add_document_button);
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
        mAddDocumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooserDialog();
            }
        });
        mImageAttachedImageView = (ImageButton) view.findViewById(R.id.mail_messages_image_attach_imageView);
        mImageAttachedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mAttachFileType) {
                    case NOTHING:
                        break;
                    case IMAGE:
                        showImagePreviewDialog();
                        break;
                    case AUDIO:
                        //startPlaying();
                        showAudioPreviewDialog();
                        break;
                    case DOC:
                        break;
                }
            }
        });
        mImageAttachedImageView.setVisibility(View.INVISIBLE);
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
                mAttachImageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                mImageAttachedImageView.setImageBitmap(mAttachImageBitmap);
                mAttachFileType = AttachFileType.IMAGE;
                mImageAttachedImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));
            } else if (requestCode == REQUEST_TAKE_IMAGE_FILE) {
                Uri selectedImageUri = data.getData();
                try {
                    mAttachImageBitmap = decodeUri(selectedImageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                mAttachFileType = AttachFileType.IMAGE;
                mImageAttachedImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));
            } else if (requestCode == REQUEST_TAKE_FILE) {
                Uri uriFile = data.getData();
                String path = getPath(uriFile, MailMessagesFragment.this.getActivity());
                // File file = new File(path);
                mAttachFileType = AttachFileType.DOC;
                mImageAttachedImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_get));
            }
            mImageAttachedImageView.setVisibility(View.VISIBLE);
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o);
        final int REQUIRED_SIZE = 200;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o2);
    }

    public String getPath(Uri uri, Activity activity) {
        if (uri == null) {
            // show user feedback
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
        mAudioRecordButton = (ToggleButton) dialog.findViewById(R.id.dialog_audio_record_button);
        mAudioRecordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    startRecording();
                } else {
                    stopRecording();
                    dialog.dismiss();
                    mAttachFileType = AttachFileType.AUDIO;
                    mImageAttachedImageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_mic_green));
                }
            }
        });
        dialog.show();
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
        mRecorder.setMaxDuration(10000);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
        }

        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    mAudioRecordButton.performClick();
                }
            }
        });
    }

    private void stopRecording() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAudioPreviewDialog() {
        mAudioPreviewHandler = new Handler();
        mPlayer = new MediaPlayer();
        final Dialog dialog = new Dialog(MailMessagesFragment.this.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_attach_audio_preview);
        dialog.setCancelable(false);
        mAudioPreviewSeekBar = (SeekBar) dialog.findViewById(R.id.dialog_audio_preview_seekBar);
        mAudioPreviewCurrentPosTextView = (TextView) dialog.findViewById(R.id.dialog_audio_preview_current_position_textView);
        TextView mAudioPreviewTotalDurationTextView = (TextView) dialog.findViewById(R.id.dialog_audio_preview_total_duration_textView);
        try {
            mPlayer.setDataSource(audioOutputFile);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int totalAudioDuration = mPlayer.getDuration() / 1000;
        mAudioPreviewSeekBar.setMax(totalAudioDuration);
        mAudioPreviewTotalDurationTextView.setText("" + totalAudioDuration);
        mPlayer.start();
        updateProgressBar();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mAudioPreviewHandler.removeCallbacks(mUpdateTimeTask);
                stopPlaying();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showImagePreviewDialog() {
        final Dialog dialog = new Dialog(MailMessagesFragment.this.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_attach_image_preview);
        dialog.setCancelable(false);
        ImageView mImagePreviewImageView = (ImageView) dialog.findViewById(R.id.dialog_image_preview_imageView);
        Button mImagePreviewCloseButton = (Button) dialog.findViewById(R.id.dialog_image_preview_close_button);
        mImagePreviewImageView.setImageBitmap(mAttachImageBitmap);

        mImagePreviewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void updateProgressBar() {
        mAudioPreviewHandler.postDelayed(mUpdateTimeTask, 500);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            int currentDuration = mPlayer.getCurrentPosition() / 1000;
            mAudioPreviewCurrentPosTextView.setText("" + currentDuration);
            mAudioPreviewSeekBar.setProgress(currentDuration);
            mAudioPreviewHandler.postDelayed(this, 500);
        }
    };

    private void showFileChooserDialog() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File"),
                    REQUEST_TAKE_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }

    private enum AttachFileType {
        NOTHING, IMAGE, AUDIO, DOC
    }
}
