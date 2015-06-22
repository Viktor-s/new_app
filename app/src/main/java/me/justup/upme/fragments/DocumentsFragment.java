package me.justup.upme.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.justup.upme.JustUpApplication;
import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.dialogs.FilePropertiesDialog;
import me.justup.upme.dialogs.FileRemoveShareDialog;
import me.justup.upme.dialogs.FileShareDialog;
import me.justup.upme.dialogs.ViewImageDialog;
import me.justup.upme.dialogs.ViewPDFDialog;
import me.justup.upme.dialogs.ViewVideoDialog;
import me.justup.upme.dialogs.WarningDialog;
import me.justup.upme.entity.BaseMethodEmptyQuery;
import me.justup.upme.entity.FileEntity;
import me.justup.upme.entity.FileGetAllResponse;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.services.FileExplorerService;
import me.justup.upme.utils.AppLocale;
import me.justup.upme.utils.ExplorerUtils;

import static me.justup.upme.services.FileExplorerService.BROADCAST_EXTRA_ACTION_TYPE;
import static me.justup.upme.services.FileExplorerService.BROADCAST_EXTRA_ERROR;
import static me.justup.upme.services.FileExplorerService.COPY;
import static me.justup.upme.services.FileExplorerService.DELETE;
import static me.justup.upme.services.FileExplorerService.DOWNLOAD;
import static me.justup.upme.services.FileExplorerService.ERROR;
import static me.justup.upme.services.FileExplorerService.EXPLORER_SERVICE_ACTION_TYPE;
import static me.justup.upme.services.FileExplorerService.EXPLORER_SERVICE_FILE_HASH;
import static me.justup.upme.services.FileExplorerService.EXPLORER_SERVICE_FILE_NAME;
import static me.justup.upme.services.FileExplorerService.EXPLORER_SERVICE_FILE_PATH;
import static me.justup.upme.services.FileExplorerService.FILE_ACTION_DONE_BROADCAST;
import static me.justup.upme.services.FileExplorerService.UNSUBSCRIBE;
import static me.justup.upme.services.FileExplorerService.UPLOAD;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class DocumentsFragment extends Fragment {
    private static final String TAG = makeLogTag(DocumentsFragment.class);

    public static final String KB = " kB";
    public static final String MB = " MB";
    public static final int SIZE_VALUE = 1024;
    private static final String DATE_FORMAT = "dd MMMM yyyy, HH:mm";

    private static final String FILE_ARRAY_MESSAGE = "file_array_thread_message";

    private TableLayout mFileExplorer = null;
    private LayoutInflater mLayoutInflater = null;
    private ProgressBar mProgressBar = null;
    private String mShareFileName = null;

    private BroadcastReceiver mFileActionDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int actionType = intent.getIntExtra(BROADCAST_EXTRA_ACTION_TYPE, 0);
            String error = intent.getStringExtra(BROADCAST_EXTRA_ERROR);
            if (actionType == ERROR && error != null) {
                showWarningDialog(error);
            }

            getTotalFileList();
        }
    };

    private SimpleDateFormat mDateFormat = new SimpleDateFormat(DATE_FORMAT, AppLocale.getAppLocale());
    private FileListHandler mFileListHandler = new FileListHandler();

    private static final int FILE_LOCAL_DELETE = 1;
    private static final int FILE_CLOUD_DELETE = 2;
    private static final int FILE_SHARE_FOR = 3;
    private static final int FILE_REMOVE_SHARE_FOR = 4;
    private static final int FILE_REMOVE_SHARE = 5;
    private static final int FILE_SHARE_PROPERTIES = 6;

    private ArrayList<FileEntity> mFileArray = null;
    private EditText mSearchField;


    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(mFileActionDoneReceiver, new IntentFilter(FILE_ACTION_DONE_BROADCAST));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_documents, container, false);

        mShareFileName = ((MainActivity) getActivity()).getShareFileName();
        if (mShareFileName != null) {
            ((MainActivity) getActivity()).setShareFileName(null);
        }

        mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFileExplorer = (TableLayout) view.findViewById(R.id.files_panel);
        mProgressBar = (ProgressBar) view.findViewById(R.id.explorer_progressBar);

        mSearchField = (EditText) view.findViewById(R.id.doc_search_field);
        mSearchField.addTextChangedListener(new SearchTextWatcher());

        ImageButton mClearSearchField = (ImageButton) view.findViewById(R.id.doc_clear_search_text);
        mClearSearchField.setOnClickListener(new ClearSearchField());

        getChildFragmentManager().beginTransaction().add(R.id.doc_sort_panel_fragment, new DocumentsSortPanelFragment()).commit();

        getTotalFileList();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unregisterReceiver(mFileActionDoneReceiver);
    }

    @SuppressLint("InflateParams")
    private void setFileItem(final FileEntity file) {
        final View item = mLayoutInflater.inflate(R.layout.item_documents_file, null);

        final int type = file.getFileType();

        ImageView mFileFavorite = (ImageView) item.findViewById(R.id.file_star_imageView);
        if (file.isFavorite()) {
            mFileFavorite.setImageResource(R.drawable.ic_file_star);
        }

        ImageView mFileImage = (ImageView) item.findViewById(R.id.file_image_imageView);
        TextView mFileName = (TextView) item.findViewById(R.id.file_name_textView);
        TextView mFileSize = (TextView) item.findViewById(R.id.file_size_textView);
        TextView mFileDate = (TextView) item.findViewById(R.id.file_date_textView);

        ImageView mFileInTablet = (ImageView) item.findViewById(R.id.file_tablet_imageView);
        ImageView mFileInCloud = (ImageView) item.findViewById(R.id.file_cloud_imageView);

        switch (file.getType()) {
            case ExplorerUtils.LOCAL_FILE:
                mFileInCloud.setOnClickListener(new OnUploadFileListener(file.getPath()));
                break;

            case ExplorerUtils.CLOUD_FILE:
                mFileInTablet.setImageResource(R.drawable.ic_file_tab_gray);
                mFileInCloud.setImageResource(R.drawable.ic_file_cloud);

                mFileName.setTextColor(getResources().getColor(R.color.cloud_drive_gray_text));
                mFileSize.setTextColor(getResources().getColor(R.color.cloud_drive_gray_text));
                mFileDate.setTextColor(getResources().getColor(R.color.cloud_drive_gray_text));

                mFileInTablet.setOnClickListener(new OnDownloadFileListener(file.getHash(), file.getName()));
                break;

            case ExplorerUtils.SHARE_FILE:
                mFileInTablet.setImageResource(R.drawable.ic_file_tab_gray);
                mFileInCloud.setImageResource(R.drawable.ic_file_cloud_gray);

                mFileName.setTextColor(getResources().getColor(R.color.cloud_drive_gray_text));
                mFileSize.setTextColor(getResources().getColor(R.color.cloud_drive_gray_text));
                mFileDate.setTextColor(getResources().getColor(R.color.cloud_drive_gray_text));

                mFileInTablet.setOnClickListener(new OnDownloadFileListener(file.getHash(), file.getName()));
                mFileInCloud.setOnClickListener(new OnCloudCopyFileListener(file.getHash()));
                break;

            case ExplorerUtils.LOCAL_AND_CLOUD_FILE:
                mFileInCloud.setImageResource(R.drawable.ic_file_cloud);
                break;

            default:
                break;
        }

        if (type == ExplorerUtils.IMAGE) {
            if (file.getType() == ExplorerUtils.CLOUD_FILE || file.getType() == ExplorerUtils.SHARE_FILE) {
                mFileImage.setImageResource(R.drawable.ic_file_image_gray);
            } else {
                mFileImage.setImageResource(R.drawable.ic_file_image);
            }
        }
        if (type == ExplorerUtils.PDF) {
            if (file.getType() == ExplorerUtils.CLOUD_FILE || file.getType() == ExplorerUtils.SHARE_FILE) {
                mFileImage.setImageResource(R.drawable.ic_file_pdf_gray);
            } else {
                mFileImage.setImageResource(R.drawable.ic_file_pdf);
            }
        }
        if (type == ExplorerUtils.VIDEO) {
            if (file.getType() == ExplorerUtils.CLOUD_FILE || file.getType() == ExplorerUtils.SHARE_FILE) {
                mFileImage.setImageResource(R.drawable.ic_file_video_gray);
            } else {
                mFileImage.setImageResource(R.drawable.ic_file_video);
            }
        }

        mFileName.setText(file.getName());

        String fileSize = (file.getSize() / SIZE_VALUE) < SIZE_VALUE ?
                (file.getSize() / SIZE_VALUE) + KB : (file.getSize() / SIZE_VALUE / SIZE_VALUE) + MB;
        mFileSize.setText(fileSize);

        mFileDate.setText(mDateFormat.format(new Date(file.getDate())));

        if (file.getType() == ExplorerUtils.LOCAL_FILE || file.getType() == ExplorerUtils.LOCAL_AND_CLOUD_FILE) {
            mFileImage.setOnClickListener(new OnOpenFileListener(file.getName(), file.getPath(), type));
            // mFileName.setOnClickListener(new OnOpenFileListener(file.getName(), file.getPath(), type));
        }

        item.setOnLongClickListener(new OnContextMenuListener(file.getHash(), file.getPath(), file.getType()));

        if (mShareFileName != null && mShareFileName.equals(file.getName())) {
            TableRow mFileLayout = (TableRow) item.findViewById(R.id.explorer_file_item_layout);
            mFileLayout.setBackgroundColor(getResources().getColor(R.color.light_gray));
        }

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
                case ExplorerUtils.IMAGE:
                    showViewImageDialog(mFileName, mFilePath);
                    break;

                case ExplorerUtils.PDF:
                    showViewPDFDialog(mFileName, mFilePath);
                    break;

                case ExplorerUtils.VIDEO:
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

    private class OnContextMenuListener implements View.OnLongClickListener {
        private final String filePath;
        private final String fileHash;
        private final int type;

        public OnContextMenuListener(final String fileHash, final String filePath, final int type) {
            this.filePath = filePath;
            this.fileHash = fileHash;
            this.type = type;
        }

        @Override
        public boolean onLongClick(View v) {
            PopupMenu popup = new PopupMenu(getActivity(), v);

            switch (type) {
                case ExplorerUtils.LOCAL_FILE:
                    popup.getMenu().add(Menu.NONE, FILE_LOCAL_DELETE, Menu.NONE, getString(R.string.file_delete));
                    break;

                case ExplorerUtils.CLOUD_FILE:
                    popup.getMenu().add(Menu.NONE, FILE_CLOUD_DELETE, Menu.NONE, getString(R.string.file_cloud_delete));
                    popup.getMenu().add(Menu.NONE, FILE_SHARE_FOR, Menu.NONE, getString(R.string.file_share_for));
                    popup.getMenu().add(Menu.NONE, FILE_REMOVE_SHARE_FOR, Menu.NONE, getString(R.string.file_remove_share_for));
                    popup.getMenu().add(Menu.NONE, FILE_SHARE_PROPERTIES, Menu.NONE, getString(R.string.file_properties));
                    break;

                case ExplorerUtils.LOCAL_AND_CLOUD_FILE:
                    popup.getMenu().add(Menu.NONE, FILE_LOCAL_DELETE, Menu.NONE, getString(R.string.file_delete));
                    popup.getMenu().add(Menu.NONE, FILE_CLOUD_DELETE, Menu.NONE, getString(R.string.file_cloud_delete));
                    popup.getMenu().add(Menu.NONE, FILE_SHARE_FOR, Menu.NONE, getString(R.string.file_share_for));
                    popup.getMenu().add(Menu.NONE, FILE_REMOVE_SHARE_FOR, Menu.NONE, getString(R.string.file_remove_share_for));
                    popup.getMenu().add(Menu.NONE, FILE_SHARE_PROPERTIES, Menu.NONE, getString(R.string.file_properties));
                    break;

                case ExplorerUtils.SHARE_FILE:
                    popup.getMenu().add(Menu.NONE, FILE_REMOVE_SHARE, Menu.NONE, getString(R.string.file_remove_share));
                    popup.getMenu().add(Menu.NONE, FILE_SHARE_PROPERTIES, Menu.NONE, getString(R.string.file_properties));
                    break;

                default:
                    break;
            }

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case FILE_LOCAL_DELETE:
                            if (filePath != null) {
                                File file = new File(filePath);
                                if (file.delete()) {
                                    getTotalFileList();
                                }
                            }
                            return true;

                        case FILE_CLOUD_DELETE:
                            startExplorerService(fileHash, null, null, DELETE);
                            return true;

                        case FILE_SHARE_FOR:
                            FileShareDialog shareDialog = FileShareDialog.newInstance(fileHash);
                            shareDialog.show(getChildFragmentManager(), FileShareDialog.FILE_SHARE_DIALOG);
                            return true;

                        case FILE_REMOVE_SHARE_FOR:
                            FileRemoveShareDialog removeShareDialog = FileRemoveShareDialog.newInstance(fileHash);
                            removeShareDialog.show(getChildFragmentManager(), FileRemoveShareDialog.FILE_REMOVE_SHARE_DIALOG);
                            return true;

                        case FILE_REMOVE_SHARE:
                            startExplorerService(fileHash, null, null, UNSUBSCRIBE);
                            return true;

                        case FILE_SHARE_PROPERTIES:
                            FilePropertiesDialog dialog = FilePropertiesDialog.newInstance(fileHash);
                            dialog.show(getChildFragmentManager(), FilePropertiesDialog.FILE_PROPERTIES_DIALOG);
                            return true;

                        default:
                            return false;
                    }
                }
            });

            popup.show();

            return true;
        }
    }

    private void startExplorerService(final String fileHash, final String filePath, final String fileName, final int actionType) {
        LOGD(TAG, "startExplorerService() - fileHash:" + fileHash + " filePath:" + filePath + " fileName:" + fileName);
        startProgressBar();

        Bundle bundle = new Bundle();
        bundle.putString(EXPLORER_SERVICE_FILE_HASH, fileHash);
        bundle.putString(EXPLORER_SERVICE_FILE_NAME, fileName);
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

    private class OnDownloadFileListener implements View.OnClickListener {
        private final String fileHash;
        private final String fileName;

        public OnDownloadFileListener(final String fileHash, final String fileName) {
            this.fileHash = fileHash;
            this.fileName = fileName;
        }

        @Override
        public void onClick(View v) {
            startExplorerService(fileHash, null, fileName, DOWNLOAD);
        }
    }

    private class OnUploadFileListener implements View.OnClickListener {
        private final String filePath;

        public OnUploadFileListener(final String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void onClick(View v) {
            startExplorerService(null, filePath, null, UPLOAD);
        }
    }

    private class OnCloudCopyFileListener implements View.OnClickListener {
        private final String fileHash;

        public OnCloudCopyFileListener(final String fileHash) {
            this.fileHash = fileHash;
        }

        @Override
        public void onClick(View v) {
            startExplorerService(fileHash, null, null, COPY);
        }
    }

    private void getTotalFileList() {
        startProgressBar();
        new GetTotalFileList().start();
    }

    private class GetTotalFileList extends Thread {
        private ArrayList<FileEntity> mLocalFileList = new ArrayList<>();
        private ArrayList<FileEntity> mCloudFileList = new ArrayList<>();
        private ArrayList<FileEntity> mShareFileList = new ArrayList<>();

        private boolean isListOk = false;
        private int stage = 0;


        @Override
        public void run() {
            if (!isListOk) {
                getLocalFileList();
            }
        }

        private void getLocalFileList() {
            File mStorageDirectory = Environment.getExternalStorageDirectory();
            File[] mDirList = mStorageDirectory.listFiles();

            for (File file : mDirList) {
                if (!file.isDirectory()) {
                    mLocalFileList.add(
                            new FileEntity(false, file.getName(), file.getAbsolutePath(), file.length(), file.lastModified(), null, ExplorerUtils.LOCAL_FILE));
                }
            }

            getCloudFileList();
        }

        private void getCloudFileList() {
            fileQuery(ApiWrapper.FILE_GET_MY_FILES);
        }

        private void getShareFileList() {
            fileQuery(ApiWrapper.FILE_GET_ALL_SHARED_WITH_ME);
        }

        private void fileQuery(String apiMethod) {
            BaseMethodEmptyQuery query = new BaseMethodEmptyQuery();
            query.method = apiMethod;

            ApiWrapper.syncQuery(query, new GetFilesResponse());
        }

        private class GetFilesResponse extends AsyncHttpResponseHandler {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGD(TAG, "onSuccess(): " + content);

                FileGetAllResponse response = null;
                try {
                    response = ApiWrapper.gson.fromJson(content, FileGetAllResponse.class);
                } catch (JsonSyntaxException e) {
                    LOGE(TAG, "gson.fromJson:\n" + content);
                }

                if (response != null && response.result != null) {
                    if (stage == 0) {
                        stage++;

                        fillArray(response, mCloudFileList, ExplorerUtils.CLOUD_FILE);

                        getShareFileList();
                    }
                    if (stage == 1) {
                        stage++;

                        fillArray(response, mShareFileList, ExplorerUtils.SHARE_FILE);

                        sortArrays();

                        isListOk = true;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String content = ApiWrapper.responseBodyToString(responseBody);
                LOGE(TAG, "onFailure(): " + content);

                sendFileArray(mLocalFileList);
                isListOk = true;
            }

            private void fillArray(FileGetAllResponse response, ArrayList<FileEntity> arrayList, int type) {
                for (FileGetAllResponse.Result file : response.result) {
                    if (!file.direct_link)
                        arrayList.add(new FileEntity(false, file.origin_name, null, file.size, (file.create_date * 1000L), file.hash_name, type));
                }
            }

            private void sortArrays() {
                if (mCloudFileList.size() > 0 && mLocalFileList.size() != 0)
                    for (int i = 0; i < mLocalFileList.size(); i++) {
                        for (int j = 0; j < mCloudFileList.size(); j++) {
                            if (mLocalFileList.get(i).getName().equals(mCloudFileList.get(j).getName())) {
                                mLocalFileList.get(i).setType(ExplorerUtils.LOCAL_AND_CLOUD_FILE);
                                mLocalFileList.get(i).setOnCloud(true);
                                mLocalFileList.get(i).setHash(mCloudFileList.get(j).getHash());
                                mCloudFileList.remove(j);
                            }
                        }
                    }

                mLocalFileList.addAll(mCloudFileList);


                if (mShareFileList.size() > 0 && mLocalFileList.size() != 0)
                    for (int i = 0; i < mLocalFileList.size(); i++) {
                        for (int j = 0; j < mShareFileList.size(); j++) {
                            if (mLocalFileList.get(i).getName().equals(mShareFileList.get(j).getName())) {
                                mLocalFileList.get(i).setHash(mShareFileList.get(j).getHash());
                                mShareFileList.remove(j);
                            }
                        }
                    }

                mLocalFileList.addAll(mShareFileList);

                sendFileArray(mLocalFileList);
            }
        }

        private void sendFileArray(ArrayList<FileEntity> array) {
            Message msg = mFileListHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putSerializable(FILE_ARRAY_MESSAGE, array);
            msg.setData(b);
            mFileListHandler.sendMessage(msg);
        }
    }

    private class FileListHandler extends Handler {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            if (DocumentsFragment.this.isAdded()) {
                stopProgressBar();

                ArrayList<FileEntity> array = (ArrayList<FileEntity>) msg.getData().getSerializable(FILE_ARRAY_MESSAGE);

                if ((null != array)) {
                    setFileArray(array);
                    updateFileExplorer();
                }
            }
        }
    }

    private void showWarningDialog(final String message) {
        WarningDialog dialog = WarningDialog.newInstance(getString(R.string.network_error), message);
        dialog.show(getChildFragmentManager(), WarningDialog.WARNING_DIALOG);
    }

    public void setFileArray(ArrayList<FileEntity> array) {
        DocumentsSortPanelFragment.FileSort.sort(array, JustUpApplication.getApplication().getAppPreferences().getFileSortType(), JustUpApplication.getApplication().getAppPreferences().isDescFileSort());
        mFileArray = array;
    }

    public ArrayList<FileEntity> getFileArray() {
        return mFileArray;
    }

    public void updateFileExplorer() {
        mFileExplorer.removeAllViews();

        for (FileEntity file : getFileArray()) {
            setFileItem(file);
        }
    }

    public int initialSortPanelType() {
        return JustUpApplication.getApplication().getAppPreferences().getFileSortType();
    }

    public boolean initialSortPanelIsDesc() {
        return JustUpApplication.getApplication().getAppPreferences().isDescFileSort();
    }

    private class SearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > 0) {
                for (FileEntity file : getFileArray()) {
                    char fileChar = Character.toLowerCase(file.getName().charAt(count - 1));
                    char searchChar = Character.toLowerCase(s.charAt(count - 1));

                    if (fileChar != searchChar) {
                        file.setHiddenForSearch(true);
                    }
                }

                mFileExplorer.removeAllViews();
                for (FileEntity file : getFileArray()) {
                    if (!file.isHiddenForSearch()) {
                        setFileItem(file);
                    }
                }
            } else {
                for (FileEntity file : getFileArray()) {
                    file.setHiddenForSearch(false);
                }

                updateFileExplorer();
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    private class ClearSearchField implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mSearchField.setText("");
        }
    }
}
