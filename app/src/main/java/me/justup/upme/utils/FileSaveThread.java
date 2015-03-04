package me.justup.upme.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class FileSaveThread extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = makeLogTag(FileSaveThread.class);

    private Context mContext;
    private File mFile;
    private String mFileName;


    public FileSaveThread(Context context, File file, String fileName) {
        mContext = context;
        mFile = file;
        mFileName = fileName;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
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

    @Override
    protected void onPostExecute(Boolean isSave) {
        if (isSave)
            Toast.makeText(mContext, R.string.file_from_cloud_ok, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mContext, R.string.file_from_cloud_error, Toast.LENGTH_LONG).show();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public byte[] fileToByteArray(File file) {
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
