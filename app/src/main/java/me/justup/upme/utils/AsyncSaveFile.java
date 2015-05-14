package me.justup.upme.utils;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public final class AsyncSaveFile extends AsyncTask<File, Void, Void> {
    private static final String TAG = makeLogTag(AsyncSaveFile.class);
    private String fileName;


    public AsyncSaveFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected Void doInBackground(File... params) {
        LOGI(TAG, "doInBackground()");

        File file = params[0];
        saveFile(file, fileName);

        return null;
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

            LOGI(TAG, "save DONE");
            return true;

        } else {
            LOGE(TAG, "!isExternalStorageWritable");
            return false;
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private byte[] fileToByteArray(final File file) {
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
