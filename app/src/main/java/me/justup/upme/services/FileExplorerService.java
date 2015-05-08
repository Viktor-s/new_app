package me.justup.upme.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import me.justup.upme.entity.FileCopySharedQuery;
import me.justup.upme.entity.FileDeleteQuery;
import me.justup.upme.entity.FileUnlinkSharedQuery;
import me.justup.upme.entity.SetAvatarFileHashResponse;
import me.justup.upme.http.ApiWrapper;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class FileExplorerService extends IntentService {
    private static final String TAG = makeLogTag(FileExplorerService.class);

    public static final String FILE_ACTION_DONE_BROADCAST = "me.justup.upme.broadcast.explorer.file_action_done";
    public static final String BROADCAST_EXTRA_ACTION_TYPE = "broadcast_extra_action_type";
    public static final String BROADCAST_EXTRA_ERROR = "broadcast_explorer_service_error";
    public static final String BROADCAST_EXTRA_FILE_HASH = "broadcast_explorer_service_file_hash";

    public static final String EXPLORER_SERVICE_FILE_NAME = "explorer_service_file_name";
    public static final String EXPLORER_SERVICE_FILE_HASH = "explorer_service_file_hash";
    public static final String EXPLORER_SERVICE_FILE_PATH = "explorer_service_file_path";
    public static final String EXPLORER_SERVICE_ACTION_TYPE = "explorer_service_type";

    public static final int DOWNLOAD = 1;
    public static final int UPLOAD = 2;
    public static final int DELETE = 3;
    public static final int COPY = 4;
    public static final int ERROR = 5;
    public static final int UNSUBSCRIBE = 6;
    public static final int AVATARS = 7;


    public FileExplorerService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int actionType = intent.getIntExtra(EXPLORER_SERVICE_ACTION_TYPE, 0);

        String fileHash = intent.getStringExtra(EXPLORER_SERVICE_FILE_HASH);
        String fileName = intent.getStringExtra(EXPLORER_SERVICE_FILE_NAME);
        String filePath = intent.getStringExtra(EXPLORER_SERVICE_FILE_PATH);

        switch (actionType) {
            case DOWNLOAD:
                downloadFileQuery(fileHash, fileName);
                break;

            case UPLOAD:
                uploadFileQuery(filePath);
                break;

            case DELETE:
                deleteFileQuery(fileHash);
                break;

            case COPY:
                copyQuery(fileHash);
                break;

            case UNSUBSCRIBE:
                unsubscribeQuery(fileHash);
                break;

            case AVATARS:
                uploadAvatar(filePath);
                break;

            default:
                break;
        }

    }

    private void downloadFileQuery(final String fileHash, final String fileName) {
        ApiWrapper.syncDownloadFileFromCloud(fileHash, new FileAsyncHttpResponseHandler(FileExplorerService.this) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                LOGE(TAG, "downloadFileQuery(): onFailure", throwable);

                if (throwable != null) {
                    sendExplorerBroadcast(ERROR, throwable.getMessage());
                } else {
                    sendExplorerBroadcast(ERROR, "Error downloading file!");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                LOGI(TAG, "downloadFileQuery(): onSuccess");

                if (saveFile(file, fileName)) {
                    sendExplorerBroadcast(DOWNLOAD);
                }
            }
        });
    }

    private void uploadFileQuery(final String path) {
        final File file = new File(path);

        ApiWrapper.syncSendFileToCloud(file, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGD(TAG, "syncSendFileToCloud onSuccess(): " + content);

                sendExplorerBroadcast(UPLOAD);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGE(TAG, "syncSendFileToCloud onFailure(): " + content);

                if (error != null) {
                    sendExplorerBroadcast(ERROR, error.getMessage());
                } else {
                    sendExplorerBroadcast(ERROR, content);
                }
            }
        });
    }

    private void deleteFileQuery(final String fileHash) {
        FileDeleteQuery query = new FileDeleteQuery();
        query.params.file_hash = fileHash;

        ApiWrapper.syncQuery(query, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGD(TAG, "deleteFileQuery onSuccess(): " + content);

                sendExplorerBroadcast(DELETE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGE(TAG, "deleteFileQuery onFailure(): " + content);

                if (error != null) {
                    sendExplorerBroadcast(ERROR, error.getMessage());
                } else {
                    sendExplorerBroadcast(ERROR, content);
                }
            }
        });
    }

    private void copyQuery(final String fileHash) {
        FileCopySharedQuery query = new FileCopySharedQuery();
        query.params.file_hash = fileHash;

        ApiWrapper.syncQuery(query, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGD(TAG, "copyQuery onSuccess(): " + content);

                sendExplorerBroadcast(0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGE(TAG, "copyQuery onFailure(): " + content);

                if (error != null) {
                    sendExplorerBroadcast(ERROR, error.getMessage());
                } else {
                    sendExplorerBroadcast(ERROR, content);
                }
            }
        });
    }

    private void unsubscribeQuery(final String fileHash) {
        FileUnlinkSharedQuery query = new FileUnlinkSharedQuery();
        query.params.file_hash = fileHash;

        ApiWrapper.syncQuery(query, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGD(TAG, "unsubscribeQuery onSuccess(): " + content);

                sendExplorerBroadcast(UNSUBSCRIBE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGE(TAG, "unsubscribeQuery onFailure(): " + content);

                if (error != null) {
                    sendExplorerBroadcast(ERROR, error.getMessage());
                } else {
                    sendExplorerBroadcast(ERROR, content);
                }
            }
        });
    }

    private void uploadAvatar(String filePath) {
        File file = saveBitmap(filePath);

        if (file != null)
            ApiWrapper.syncSendFileToCloud(file, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String content = ApiWrapper.responseBodyToString(responseBody);
                    LOGD(TAG, "uploadAvatar onSuccess(): " + content);

                    SetAvatarFileHashResponse response = null;

                    try {
                        response = ApiWrapper.gson.fromJson(content, SetAvatarFileHashResponse.class);
                    } catch (JsonSyntaxException e) {
                        LOGE(TAG, "gson.fromJson:\n" + content);
                    }

                    if (response != null && response.status.equals("ok")) {
                        Intent intent = new Intent(FILE_ACTION_DONE_BROADCAST);
                        intent.putExtra(BROADCAST_EXTRA_ACTION_TYPE, AVATARS);
                        intent.putExtra(BROADCAST_EXTRA_FILE_HASH, response.file_hash);
                        sendBroadcast(intent);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    String content = ApiWrapper.responseBodyToString(responseBody);
                    LOGE(TAG, "uploadAvatar onFailure(): " + content);

                    if (error != null) {
                        sendExplorerBroadcast(ERROR, error.getMessage());
                    } else {
                        sendExplorerBroadcast(ERROR, content);
                    }
                }
            });
    }

    private Bitmap getResizedBitmap(Bitmap bm) {
        final int REQUIRED_SIZE = 200;

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) REQUIRED_SIZE) / width;
        float scaleHeight = ((float) REQUIRED_SIZE) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    private File saveBitmap(String filePath) {
        Bitmap pictureBitmap = getResizedBitmap(BitmapFactory.decodeFile(filePath));

        File file = new File(filePath);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (FileNotFoundException e) {
            LOGE(TAG, "saveBitmap FileNotFoundException" + e);
        }
        try {
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            LOGE(TAG, "saveBitmap IOException" + e);
        }

        return file;
    }

    private void sendExplorerBroadcast(final int actionType) {
        Intent intent = new Intent(FILE_ACTION_DONE_BROADCAST);
        intent.putExtra(BROADCAST_EXTRA_ACTION_TYPE, actionType);
        sendBroadcast(intent);
    }

    private void sendExplorerBroadcast(final int actionType, final String error) {
        Intent intent = new Intent(FILE_ACTION_DONE_BROADCAST);
        intent.putExtra(BROADCAST_EXTRA_ACTION_TYPE, actionType);
        intent.putExtra(BROADCAST_EXTRA_ERROR, error);
        sendBroadcast(intent);
    }

    private Boolean saveFile(final File mFile, final String mFileName) {
        if (mFile == null) {
            LOGE(TAG, "mFile == null");
            return false;
        }

        if (isExternalStorageWritable()) {
            File mainDirectory = Environment.getExternalStorageDirectory();

            FileOutputStream fos;
            File file = new File(mainDirectory, mFileName);
            byte[] serverData = fileToByteArray(mFile);

            if (serverData != null) {
                try {
                    fos = new FileOutputStream(file);
                    fos.write(serverData);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    LOGE(TAG, "FileOutputStream", e);

                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private byte[] fileToByteArray(File file) {
        byte[] buffer = new byte[(int) file.length()];
        InputStream ios = null;

        try {
            ios = new FileInputStream(file);
            if (ios.read(buffer) == -1) {
                LOGE(TAG, "fileToByteArray(): EOF reached while trying to read the whole file");
            }
        } catch (Exception e) {
            LOGE(TAG, "fileToByteArray() catch", e);
        } finally {
            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
                LOGE(TAG, "fileToByteArray() finally", e);
            }
        }

        return buffer;
    }

}
