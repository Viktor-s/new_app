package me.justup.upme.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.File;

import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.dialogs.ViewImageDialog;
import me.justup.upme.dialogs.ViewPDFDialog;
import me.justup.upme.dialogs.ViewVideoDialog;
import me.justup.upme.services.FileExplorerService;

import static me.justup.upme.services.FileExplorerService.BROADCAST_EXTRA_ACTION_TYPE;
import static me.justup.upme.services.FileExplorerService.DOWNLOAD;
import static me.justup.upme.services.FileExplorerService.EXPLORER_SERVICE_ACTION_TYPE;
import static me.justup.upme.services.FileExplorerService.EXPLORER_SERVICE_FILE_PATH;
import static me.justup.upme.services.FileExplorerService.FILE_ACTION_DONE_BROADCAST;
import static me.justup.upme.services.FileExplorerService.UPLOAD;


public class DocumentsFragment extends Fragment {
    public static final String KB = "kB";
    public static final int SIZE_VALUE = 1024;

    public static final int IMAGE = 1;
    public static final int DOC = 2;
    public static final int PDF = 3;
    public static final int VIDEO = 4;

    private TableLayout mFileExplorer;
    private LayoutInflater mLayoutInflater;
    private ProgressBar mProgressBar;
    private String mShareFileName;

    private BroadcastReceiver mFileActionDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int actionType = intent.getIntExtra(BROADCAST_EXTRA_ACTION_TYPE, 0);
            if (actionType == DOWNLOAD) {
                getLocalFileList();
            }

            stopProgressBar();
        }
    };


    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(mFileActionDoneReceiver, new IntentFilter(FILE_ACTION_DONE_BROADCAST));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_documents, container, false);

        String shareFileName = ((MainActivity) getActivity()).getShareFileName();
        if (shareFileName != null) {
            setShareFileName(shareFileName);
            ((MainActivity) getActivity()).setShareFileName(null);
        }

        mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFileExplorer = (TableLayout) view.findViewById(R.id.files_panel);
        mProgressBar = (ProgressBar) view.findViewById(R.id.explorer_progressBar);

        getLocalFileList();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unregisterReceiver(mFileActionDoneReceiver);
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
        final boolean isPDF = fileName.contains(".pdf");
        final boolean isVideo = fileName.contains(".mp4") || fileName.contains(".avi") || fileName.contains(".3gp");

        int type;
        if (isImage) {
            type = IMAGE;
        } else if (isPDF) {
            type = PDF;
        } else if (isVideo) {
            type = VIDEO;
        } else {
            type = DOC;
        }

        ImageView mFileFavorite = (ImageView) item.findViewById(R.id.file_star_imageView);
        ImageView mFileImage = (ImageView) item.findViewById(R.id.file_image_imageView);
        TextView mFileName = (TextView) item.findViewById(R.id.file_name_textView);
        TextView mFileSize = (TextView) item.findViewById(R.id.file_size_textView);
        TextView mFileDate = (TextView) item.findViewById(R.id.file_date_textView);
        ImageView mFileInTablet = (ImageView) item.findViewById(R.id.file_tablet_imageView);
        ImageView mFileInCloud = (ImageView) item.findViewById(R.id.file_cloud_imageView);

        ImageView mFileActionButton = (ImageView) item.findViewById(R.id.file_action_button);
        mFileActionButton.setOnClickListener(new OnFileActionListener(filePath));

        if (type == IMAGE) {
            mFileImage.setImageResource(R.drawable.ic_file_image);
        }
        if (type == PDF) {
            mFileImage.setImageResource(R.drawable.ic_file_pdf);
        }
        if (type == VIDEO) {
            mFileImage.setImageResource(R.drawable.ic_file_video);
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

                case PDF:
                    showViewPDFDialog(mFileName, mFilePath);
                    break;

                case VIDEO:
                    showViewVideoDialog(mFileName, mFilePath);
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

    private void showViewPDFDialog(String mFileName, String mFilePath) {
        ViewPDFDialog dialog = ViewPDFDialog.newInstance(mFileName, mFilePath);
        dialog.show(getChildFragmentManager(), ViewPDFDialog.VIEW_PDF_DIALOG);
    }

    private void showViewVideoDialog(String mFileName, String mFilePath) {
        ViewVideoDialog dialog = ViewVideoDialog.newInstance(mFileName, mFilePath);
        dialog.show(getChildFragmentManager(), ViewVideoDialog.VIEW_VIDEO_DIALOG);
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
                            startProgressBar();
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

    public void startProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    public String getShareFileName() {
        return mShareFileName;
    }

    public void setShareFileName(String shareFileName) {
        mShareFileName = shareFileName;
    }

}
