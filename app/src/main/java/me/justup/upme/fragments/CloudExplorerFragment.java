package me.justup.upme.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import me.justup.upme.R;
import me.justup.upme.entity.FileGetAllQuery;
import me.justup.upme.entity.FileGetAllResponse;
import me.justup.upme.http.ApiWrapper;

import static me.justup.upme.fragments.DocumentsFragment.DOC;
import static me.justup.upme.fragments.DocumentsFragment.IMAGE;
import static me.justup.upme.fragments.DocumentsFragment.KB;
import static me.justup.upme.fragments.DocumentsFragment.SIZE_VALUE;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class CloudExplorerFragment extends Fragment {
    private static final String TAG = makeLogTag(CloudExplorerFragment.class);

    private static final String TAB_1 = "tag1";
    private static final String TAB_2 = "tag2";

    private TableLayout mMyFileExplorer;
    private TableLayout mShareFileExplorer;
    private LayoutInflater mLayoutInflater;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloud_explorer, container, false);
        mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TabHost tabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec(TAB_1);
        tabSpec.setIndicator(getString(R.string.my_files));
        tabSpec.setContent(R.id.explorer_Tab_my);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(TAB_2);
        tabSpec.setIndicator(getString(R.string.share_with_me));
        tabSpec.setContent(R.id.explorer_Tab_share);
        tabHost.addTab(tabSpec);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                if (tabId.equals(TAB_1)) {
                    fileQuery(ApiWrapper.FILE_GET_MY_FILES, mMyFileExplorer);
                } else {
                    fileQuery(ApiWrapper.FILE_GET_ALL_SHARED_WITH_ME, mShareFileExplorer);
                }
            }
        });

        mMyFileExplorer = (TableLayout) view.findViewById(R.id.files_panel_my);
        mShareFileExplorer = (TableLayout) view.findViewById(R.id.files_panel_share);

        fileQuery(ApiWrapper.FILE_GET_MY_FILES, mMyFileExplorer);

        return view;
    }

    private void fileQuery(String apiMethod, TableLayout parentLayout) {
        FileGetAllQuery query = new FileGetAllQuery();
        query.method = apiMethod;

        ApiWrapper.query(query, new GetAllFilesResponse(parentLayout));
    }

    @SuppressLint("InflateParams")
    private void setFileItem(TableLayout parentLayout, final String fileHash, final String fileName, final int fileLength) {
        final View item = mLayoutInflater.inflate(R.layout.item_documents_file, null);

        boolean isImage = false;
        if (fileName != null) {
            isImage = fileName.contains(".jpg") || fileName.contains(".jpeg") || fileName.contains(".png");
        }

        int type;
        if (isImage) {
            type = IMAGE;
        } else {
            type = DOC;
        }

        ImageView mFileImage = (ImageView) item.findViewById(R.id.file_image_imageView);
        TextView mFileName = (TextView) item.findViewById(R.id.file_name_textView);
        TextView mFileSize = (TextView) item.findViewById(R.id.file_size_textView);

        if (type == IMAGE) {
            mFileImage.setImageResource(R.drawable.ic_file_image);
        }
        mFileName.setText(fileName);
        mFileSize.setText((fileLength / SIZE_VALUE) + KB);

        parentLayout.addView(item);
    }

    private class GetAllFilesResponse extends AsyncHttpResponseHandler {
        private TableLayout parentLayout;

        public GetAllFilesResponse(TableLayout parentLayout) {
            this.parentLayout = parentLayout;
        }

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

            parentLayout.removeAllViews();

            if (response != null && response.result != null) {
                for (FileGetAllResponse.Result file : response.result) {
                    setFileItem(parentLayout, file.hash_name, file.origin_name, file.size);
                }
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure(): " + content);
        }
    }

}
