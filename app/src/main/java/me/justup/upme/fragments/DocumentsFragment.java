package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class DocumentsFragment extends Fragment {
    private static final String TAG = makeLogTag(DocumentsFragment.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_documents, container, false);

        TextView mFileExplorer = (TextView) view.findViewById(R.id.files_dirs_textView);

        File mStorageDirectory = Environment.getExternalStorageDirectory();
        File[] mDirList = mStorageDirectory.listFiles();

        StringBuilder mList = new StringBuilder();

        for (File file : mDirList) {
            mList.append(file.getPath()).append("\t\t[").append(file.getName()).append("]\n");
        }

        mFileExplorer.setText(mList.toString());

        return view;
    }

}
