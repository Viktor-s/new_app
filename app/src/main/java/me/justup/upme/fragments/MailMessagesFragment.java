package me.justup.upme.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.justup.upme.R;
import me.justup.upme.dialogs.WarningDialog;
import me.justup.upme.entity.SendNotificationQuery;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.services.PushIntentService;
import me.justup.upme.utils.AnimateButtonClose;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppLocale;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class MailMessagesFragment extends Fragment {
    private static final String TAG = makeLogTag(MailMessagesFragment.class);
    private static final String FRIEND_NAME = "mail_messages_friend_name";
    private static final String YOUR_NAME = "mail_messages_your_name";
    private static final String FRIEND_ID = "mail_messages_friend_id";

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_TAKE_IMAGE_FILE = 1;
    private static final int REQUEST_TAKE_FILE = 2;
    private static final int AUDIO_RECORD_MAX_DURATION = 10000;
    private static final String TAKE_PHOTO = "Сделать фото";
    private static final String CHOOSE_FROM_GALLERY = "Выбрать из галереи";
    private static final String DIALOG_CANCEL = "Отмена";

    private RelativeLayout mAddFileContainer;
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

    // Jabber
    private static final String HOST = "95.213.170.164";
    private static final int PORT = 3222;
    private static final String SERVICE = "upme-spb-pbx-dlj01";
    private static final String PASSWORD = "TempuS123#";

    private static final String AT = "@";
    private static final String DOTS = ": ";
    private static final String START_HTML_COMPANION = "<b><font color=magenta>";
    private static final String START_HTML_OWNER = "<b><font color=gray>";
    private static final String END_HTML = "</font></b>";

    private XMPPConnection mXMPPConnection;
    private ArrayList<Spanned> mMessages = new ArrayList<>();
    private Handler mHandler = new Handler();
    private StringBuilder mChatLineBuilder = new StringBuilder();
    private ArrayAdapter<Spanned> mChatAdapter;
    private Button mSendButton;

    private EditText mTextMessage;
    private String mFriendName;
    private int friendId;
    private String mYourName;
    private String mFilePath;
    private String mPushLink;


    public static MailMessagesFragment newInstance(String yourName, String userName, int userId) {
        MailMessagesFragment fragment = new MailMessagesFragment();

        Bundle args = new Bundle();
        args.putString(FRIEND_NAME, userName);
        args.putString(YOUR_NAME, yourName);
        args.putInt(FRIEND_ID, userId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAttachFileType = AttachFileType.NOTHING;

        mYourName = getArguments().getString(YOUR_NAME, "");
        mFriendName = getArguments().getString(FRIEND_NAME, "") + AT + SERVICE;
        friendId = getArguments().getInt(FRIEND_ID, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail_messages, container, false);

        mAddFileContainer = (RelativeLayout) view.findViewById(R.id.mail_messages_add_file_container);
        Button mMailMessageCloseButton = (Button) view.findViewById(R.id.mail_messages_close_button);
        mMailMessageCloseButton.setVisibility(View.INVISIBLE);
        mMailMessageCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragment().getChildFragmentManager().beginTransaction().remove(MailMessagesFragment.this).commit();
            }
        });

        Button mStapleButton = (Button) view.findViewById(R.id.mail_messages_staple_button);
        mStapleButton.setOnClickListener(new View.OnClickListener() {
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
        Button mAddPhotoButton = (Button) view.findViewById(R.id.mail_messages_add_photo_button);
        Button mAddAudioButton = (Button) view.findViewById(R.id.mail_messages_add_audio_button);
        Button mAddDocumentButton = (Button) view.findViewById(R.id.mail_messages_add_document_button);
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
                        showAudioPreviewDialog();
                        break;
                    case DOC:
                        break;
                }
            }
        });

        mImageAttachedImageView.setVisibility(View.INVISIBLE);


        // Jabber
        mTextMessage = (EditText) view.findViewById(R.id.chatET);
        ListView mJabberListView = (ListView) view.findViewById(R.id.jabber_listMessages);

        mChatAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_jabber_chat_item, mMessages);
        mJabberListView.setAdapter(mChatAdapter);

        mSendButton = (Button) view.findViewById(R.id.mail_messages_add_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String to = mFriendName;
                String text = mTextMessage.getText().toString();
                mTextMessage.setText("");

                if (mFilePath != null) {
                    sendFileToCloud(mFilePath);

                    if (text != null)
                        text += getString(R.string.sent_file);
                    else
                        text = getString(R.string.sent_file);

                    mPushLink = mFilePath;
                    mFilePath = null;
                    mImageAttachedImageView.setVisibility(View.GONE);
                }

                LOGI(TAG, "Sending text " + text + " to " + to);
                Message msg = new Message(to, Message.Type.chat);
                msg.setBody(text);
                if (mXMPPConnection != null) {
                    mXMPPConnection.sendPacket(msg);
                    mMessages.add(splitName(mXMPPConnection.getUser(), text));
                    notifyListAdapter();
                }
            }
        });

        connect();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (mXMPPConnection != null)
                mXMPPConnection.disconnect();
        } catch (Exception e) {
            LOGE(TAG, "onDestroy()", e);
        }
    }

    private void notifyListAdapter() {
        mChatAdapter.notifyDataSetChanged();
    }

    public void connect() {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(),
                getString(R.string.jabber_chat_connecting),
                getString(R.string.jabber_chat_wait), false);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
                // connConfig.setTruststoreType("AndroidCAStore");
                // connConfig.setTruststorePassword(null);
                // connConfig.setTruststorePath(null);
                XMPPConnection connection = new XMPPConnection(connConfig);

                try {
                    connection.connect();
                    LOGI(TAG, "Connected to " + connection.getHost());
                } catch (final XMPPException ex) {
                    LOGE(TAG, "Failed to connect to " + connection.getHost(), ex);
                    setConnection(null);

                    dialog.dismiss();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showWarningDialog(ex.getMessage());
                            mSendButton.setEnabled(false);
                        }
                    });

                    return;
                }

                try {
                    // SASLAuthentication.supportSASLMechanism("PLAIN", 0);
                    connection.login(mYourName, PASSWORD);
                    LOGI(TAG, "Logged in as " + connection.getUser());

                    Presence presence = new Presence(Presence.Type.available);
                    connection.sendPacket(presence);
                    setConnection(connection);

                    /*
                    Roster roster = connection.getRoster();
                    Collection<RosterEntry> entries = roster.getEntries();
                    for (RosterEntry entry : entries) {
                        LOGD(TAG, "--------------------------------------");
                        LOGD(TAG, "RosterEntry " + entry);
                        LOGD(TAG, "User: " + entry.getUser());
                        LOGD(TAG, "Name: " + entry.getName());
                        LOGD(TAG, "Status: " + entry.getStatus());
                        LOGD(TAG, "Type: " + entry.getType());
                        Presence entryPresence = roster.getPresence(entry.getUser());

                        LOGD(TAG, "Presence Status: " + entryPresence.getStatus());
                        LOGD(TAG, "Presence Type: " + entryPresence.getType());
                        Presence.Type type = entryPresence.getType();
                        if (type == Presence.Type.available)
                            LOGD(TAG, "Presence AVAILABLE");
                        LOGD(TAG, "Presence : " + entryPresence);
                    }
                    */

                } catch (XMPPException ex) {
                    LOGE(TAG, "Failed to log in as " + mYourName);
                    LOGE(TAG, ex.toString());
                    setConnection(null);
                }

                dialog.dismiss();
            }
        });
        t.start();
        dialog.show();
    }

    public void setConnection(XMPPConnection connection) {
        mXMPPConnection = connection;
        if (connection != null) {
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                @Override
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        LOGI(TAG, "Text Received " + message.getBody() + " from " + fromName);
                        mMessages.add(splitName(fromName, message.getBody()));
                        mHandler.post(new Runnable() {
                            public void run() {
                                notifyListAdapter();
                            }
                        });
                    }
                }
            }, filter);
        }
    }

    private Spanned splitName(String fullName, String text) {
        String[] parts = fullName.split(AT);

        mChatLineBuilder.setLength(0);
        String user = parts[0];

        if (user.equals(mYourName)) {
            mChatLineBuilder.append(START_HTML_OWNER);
        } else {
            mChatLineBuilder.append(START_HTML_COMPANION);
        }

        mChatLineBuilder.append(user).append(DOTS).append(END_HTML).append(text);

        return Html.fromHtml(mChatLineBuilder.toString());
    }

    private void sendFileToCloud(final String path) {
        LOGD(TAG, "file path:" + path);

        final File file = new File(path);

        ApiWrapper.sendFileToCloud(file, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGD(TAG, "onSuccess(): " + content);

                Toast.makeText(getActivity(), getString(R.string.file_in_cloud), Toast.LENGTH_SHORT).show();

                startNotificationIntent(friendId, mYourName, MailFragment.FILE, mPushLink, file.getName());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGE(TAG, "onFailure(): " + content);

                Toast.makeText(getActivity(), getString(R.string.sent_file_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startNotificationIntent(int userId, String ownerName, int connectionType, String link, String fileName) {
        SendNotificationQuery push = new SendNotificationQuery();
        push.params.user_id = userId;
        push.params.data.owner_name = ownerName;
        push.params.data.connection_type = connectionType;
        push.params.data.link = link;
        push.params.data.text = fileName;

        Bundle bundle = new Bundle();
        bundle.putSerializable(PushIntentService.PUSH_INTENT_QUERY_EXTRA, push);

        Intent intent = new Intent(getActivity(), PushIntentService.class);
        getActivity().startService(intent.putExtras(bundle));
    }

    private void selectImage() {
        createTakePictureDialog();
    }

    private void createTakePictureDialog() {
        final CharSequence[] items = {TAKE_PHOTO, CHOOSE_FROM_GALLERY, DIALOG_CANCEL};
        AlertDialog.Builder builder = new AlertDialog.Builder(MailMessagesFragment.this.getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(TAKE_PHOTO)) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(AppContext.getAppContext().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ignored) {
                        }
                        if (photoFile != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile));
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    }
                } else if (items[item].equals(CHOOSE_FROM_GALLERY)) {
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
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    mAttachImageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    mFilePath = mCurrentPhotoPath;
                    mImageAttachedImageView.setImageBitmap(mAttachImageBitmap);
                    mAttachFileType = AttachFileType.IMAGE;
                    mImageAttachedImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));
                    break;

                case REQUEST_TAKE_IMAGE_FILE:
                    Uri selectedImageUri = data.getData();
                    try {
                        mAttachImageBitmap = decodeUri(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    mAttachFileType = AttachFileType.IMAGE;
                    mImageAttachedImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));
                    break;

                case REQUEST_TAKE_FILE:
                    Uri uriFile = data.getData();
                    mFilePath = getPath(uriFile, AppContext.getAppContext());
                    mAttachFileType = AttachFileType.DOC;
                    mImageAttachedImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_get));
                    break;

                default:
                    break;
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

    /*
    public String getPath(Uri uri, Activity activity) {
        if (uri == null) {
            return null;
        }

        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = activity.getContentResolver().query(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    */

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Uri uri, final Context context) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }

            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                switch (type) {
                    case "image":
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "video":
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "audio":
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        break;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }

        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", AppLocale.getAppLocale()).format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                timeStamp,  /* prefix */
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
        mRecorder.setMaxDuration(AUDIO_RECORD_MAX_DURATION);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            LOGE(TAG, "startRecording()", e);
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
            int currentPosition = mPlayer.getCurrentPosition() / 1000;
            mAudioPreviewCurrentPosTextView.setText("" + currentPosition);
            mAudioPreviewSeekBar.setProgress(currentPosition);
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
            LOGE(TAG, "showFileChooserDialog()", ex);
        }
    }

    private enum AttachFileType {
        NOTHING, IMAGE, AUDIO, DOC
    }

    private void showWarningDialog(String message) {
        WarningDialog dialog = WarningDialog.newInstance(getString(R.string.network_error), message);
        dialog.show(getFragmentManager(), WarningDialog.WARNING_DIALOG);
    }

}
