package me.justup.upme.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.File;

import me.justup.upme.R;
import me.justup.upme.dialogs.ViewImageDialog;
import me.justup.upme.services.FileExplorerService;

import static me.justup.upme.services.FileExplorerService.EXPLORER_SERVICE_ACTION_TYPE;
import static me.justup.upme.services.FileExplorerService.EXPLORER_SERVICE_FILE_PATH;
import static me.justup.upme.services.FileExplorerService.UPLOAD;


public class DocumentsFragment extends Fragment {
    public static final String KB = "kB";
    public static final int SIZE_VALUE = 1024;

    public static final int IMAGE = 1;
    public static final int DOC = 2;

    private TableLayout mFileExplorer;
    private LayoutInflater mLayoutInflater;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_documents, container, false);

        mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFileExplorer = (TableLayout) view.findViewById(R.id.files_panel);

        getChildFragmentManager().beginTransaction().add(R.id.cloud_explorer_fragment_container, new CloudExplorerFragment()).commit();

        getLocalFileList();

        return view;
    }

    private void getLocalFileList() {
        mFileExplorer.removeAllViews();

        File mStorageDirectory = Environment.getExternalStorageDirectory();
        File[] mDirList = mStorageDirectory.listFiles();

        for (File file : mDirList) {
            if (!file.isDirectory()) {
                setFileItem(file.getName(), file.getAbsolutePath(), file.length());
            }
        }
    }

    @SuppressLint("InflateParams")
    private void setFileItem(final String fileName, final String filePath, final long fileLength) {
        final View item = mLayoutInflater.inflate(R.layout.item_documents_file, null);

        final boolean isImage = fileName.contains(".jpg") || fileName.contains(".jpeg") || fileName.contains(".png");

        int type;
        if (isImage) {
            type = IMAGE;
        } else {
            type = DOC;
        }

        ImageView mFileImage = (ImageView) item.findViewById(R.id.file_image_imageView);
        TextView mFileName = (TextView) item.findViewById(R.id.file_name_textView);
        TextView mFileSize = (TextView) item.findViewById(R.id.file_size_textView);

        ImageButton mFileActionButton = (ImageButton) item.findViewById(R.id.file_action_button);
        mFileActionButton.setOnClickListener(new OnFileActionListener(filePath));

        if (type == IMAGE) {
            mFileImage.setImageResource(R.drawable.ic_file_image);
        }
        mFileName.setText(fileName);
        mFileSize.setText((fileLength / SIZE_VALUE) + KB);

        mFileImage.setOnClickListener(new OnOpenFileListener(fileName, filePath, type));
        mFileName.setOnClickListener(new OnOpenFileListener(fileName, filePath, type));

        mFileExplorer.addView(item);
    }

    private class OnOpenFileListener implements View.OnClickListener {
        private final String mFileName;
        private final String mFilePath;
        private final int fileType;

        public OnOpenFileListener(String fileName, String filePath, int fileType) {
            mFileName = fileName;
            mFilePath = filePath;
            this.fileType = fileType;
        }

        @Override
        public void onClick(View v) {
            switch (fileType) {
                case IMAGE:
                    showViewImageDialog(mFileName, mFilePath);
                    break;

                default:
                    break;
            }
        }
    }

    private void showViewImageDialog(String mFileName, String mFilePath) {
        ViewImageDialog dialog = ViewImageDialog.newInstance(mFileName, mFilePath);
        dialog.show(getChildFragmentManager(), ViewImageDialog.VIEW_IMAGE_DIALOG);
    }

    private class OnFileActionListener implements View.OnClickListener {
        private final String filePath;

        public OnFileActionListener(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(getActivity(), v);
            popup.inflate(R.menu.file_local_popup_menu);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.file_local_upload:
                            startExplorerService(filePath, UPLOAD);
                            return true;

                        case R.id.file_local_delete:
                            File file = new File(filePath);
                            if (file.delete()) {
                                getLocalFileList();
                            }
                            return true;

                        default:
                            return false;
                    }
                }
            });

            popup.show();
        }
    }

    private void startExplorerService(final String filePath, final int actionType) {
        Bundle bundle = new Bundle();
        bundle.putString(EXPLORER_SERVICE_FILE_PATH, filePath);
        bundle.putInt(EXPLORER_SERVICE_ACTION_TYPE, actionType);

        Intent intent = new Intent(getActivity(), FileExplorerService.class);
        getActivity().startService(intent.putExtras(bundle));
    }

}
