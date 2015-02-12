package me.justup.upme.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class DocumentsFragment extends Fragment {
    private static final String TAG = makeLogTag(DocumentsFragment.class);

    private LinearLayout mFileExplorer;
    private LayoutInflater mLayoutInflater;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_documents, container, false);

        mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFileExplorer = (LinearLayout) view.findViewById(R.id.files_panel);

        File mStorageDirectory = Environment.getExternalStorageDirectory();
        File[] mDirList = mStorageDirectory.listFiles();

        for (File file : mDirList) {
            if (!file.isDirectory()) {
                setFileItem(file.getName());
            }
        }

        return view;
    }

    @SuppressLint("InflateParams")
    private void setFileItem(String fileName) {
        final View item = mLayoutInflater.inflate(R.layout.item_documents_file, null);
        ((TextView) item.findViewById(R.id.file_name_textView)).setText(fileName);
        mFileExplorer.addView(item);
    }

}
