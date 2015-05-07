package me.justup.upme.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.justup.upme.R;
import me.justup.upme.dialogs.WarningDialog;
import me.justup.upme.entity.SetAvatarQuery;
import me.justup.upme.entity.SetAvatarResponse;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.services.FileExplorerService;

import static me.justup.upme.services.FileExplorerService.AVATARS;
import static me.justup.upme.services.FileExplorerService.BROADCAST_EXTRA_ACTION_TYPE;
import static me.justup.upme.services.FileExplorerService.BROADCAST_EXTRA_ERROR;
import static me.justup.upme.services.FileExplorerService.ERROR;
import static me.justup.upme.services.FileExplorerService.EXPLORER_SERVICE_ACTION_TYPE;
import static me.justup.upme.services.FileExplorerService.EXPLORER_SERVICE_FILE_PATH;
import static me.justup.upme.services.FileExplorerService.FILE_ACTION_DONE_BROADCAST;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class AccountSettingsFragment extends Fragment {
    private static final String TAG = makeLogTag(AccountSettingsFragment.class);

    private static final int REQUEST_TAKE_PHOTO = 100;
    private static final int REQUEST_TAKE_IMAGE_FILE = 101;

    private static final String TAKE_PHOTO = "Сделать фото";
    private static final String CHOOSE_FROM_GALLERY = "Выбрать из галереи";
    private static final String DIALOG_CANCEL = "Отмена";

    private ImageView mUserAvatar;
    private Button mUploadImageButton;
    private String mFilePath;
    private FrameLayout mProgressBar;

    private BroadcastReceiver mFileActionDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int actionType = intent.getIntExtra(BROADCAST_EXTRA_ACTION_TYPE, 0);
            String error = intent.getStringExtra(BROADCAST_EXTRA_ERROR);

            if (actionType == AVATARS) {
                SetAvatarQuery query = new SetAvatarQuery();
                ApiWrapper.query(query, new OnSetAvatarResponse());
            }

            if (actionType == ERROR && error != null) {
                showWarningDialog(error);
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(mFileActionDoneReceiver, new IntentFilter(FILE_ACTION_DONE_BROADCAST));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        Button mNewImageButton = (Button) view.findViewById(R.id.avatar_new_button);
        mNewImageButton.setOnClickListener(new LoadNewImage());

        mUploadImageButton = (Button) view.findViewById(R.id.avatar_save_button);
        mUploadImageButton.setOnClickListener(new UploadImage());

        mUserAvatar = (ImageView) view.findViewById(R.id.avatar_image);
        mProgressBar = (FrameLayout) view.findViewById(R.id.base_progressBar);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unregisterReceiver(mFileActionDoneReceiver);
    }

    private class LoadNewImage implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            createTakePictureDialog();
        }
    }

    private class UploadImage implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mFilePath != null) {
                startProgressBar();

                Bundle bundle = new Bundle();
                bundle.putString(EXPLORER_SERVICE_FILE_PATH, mFilePath);
                bundle.putInt(EXPLORER_SERVICE_ACTION_TYPE, AVATARS);

                Intent intent = new Intent(getActivity(), FileExplorerService.class);
                getActivity().startService(intent.putExtras(bundle));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bitmap image = null;

            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    image = BitmapFactory.decodeFile(mFilePath);

                    break;

                case REQUEST_TAKE_IMAGE_FILE:
                    Uri selectedImageUri = data.getData();
                    mFilePath = getPath(selectedImageUri, getActivity().getApplicationContext());

                    try {
                        image = decodeUri(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        LOGE(TAG, "REQUEST_TAKE_IMAGE_FILE", e);
                    }

                    break;

                default:
                    break;
            }

            if (image != null) {
                mUserAvatar.setImageBitmap(image);
                mUploadImageButton.setEnabled(true);

                LOGD(TAG, "image path:" + mFilePath);
            }
        }
    }

    private void createTakePictureDialog() {
        final CharSequence[] items = {TAKE_PHOTO, CHOOSE_FROM_GALLERY, DIALOG_CANCEL};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_option));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(TAKE_PHOTO)) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getApplicationContext().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ignored) {
                        }
                        if (photoFile != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    }

                } else if (items[item].equals(CHOOSE_FROM_GALLERY)) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_TAKE_IMAGE_FILE);

                } else if (items[item].equals(DIALOG_CANCEL)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("avatar", ".jpg", storageDir);

        mFilePath = image.getAbsolutePath();

        return image;
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

    public void startProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    private class OnSetAvatarResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "onSuccess() : " + content);

            if (AccountSettingsFragment.this.isAdded()) {
                stopProgressBar();

                SetAvatarResponse response = null;
                try {
                    response = ApiWrapper.gson.fromJson(content, SetAvatarResponse.class);
                } catch (JsonSyntaxException e) {
                    LOGE(TAG, "gson.fromJson:\n" + content);
                }

                if (response != null && response.error != null) {
                    showWarningDialog(response.error.message);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.avatar_is_update), Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure(): " + content);

            if (AccountSettingsFragment.this.isAdded()) {
                stopProgressBar();
                showWarningDialog(content);
            }
        }
    }

    private void showWarningDialog(String message) {
        WarningDialog dialog = WarningDialog.newInstance(getString(R.string.network_error), message);
        dialog.show(getFragmentManager(), WarningDialog.WARNING_DIALOG);
    }

}
