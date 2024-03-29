package me.justup.upme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;

import me.justup.upme.JustUpApplication;
import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.dialogs.ViewPDFDialog;
import me.justup.upme.dialogs.WarningDialog;
import me.justup.upme.entity.EducationMaterialEntity;
import me.justup.upme.entity.EducationModuleEntity;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.AsyncSaveFile;
import me.justup.upme.utils.CommonUtils;

import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_CONTENT_TYPE;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_CREATED_AT;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_DESCRIPTION;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_EXTRA_LINK;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_EXTRA_SOURCE;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_MODULE_ID;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_NAME;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_PRIORITY_TYPE;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_SERVER_ID;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_SORT_WEIGHT;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_UPDATED_AT;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_CREATED_AT;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_DESCRIPTION;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_NAME;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_PROGRAM_ID;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_SERVER_ID;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_UPDATED_AT;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class EducationModuleFragment extends Fragment {
    private static final String TAG = makeLogTag(EducationModuleFragment.class);

    private static final String YOU_TUBE_VIDEO = "video";
    private static final String PRIORITY_TYPE_PRIMARY = "primary";
    private static final String PRIORITY_TYPE_SECONDARY = "secondary";

    private static final String ARG_PRODUCT_MODULE_NAME = "product_name";
    private static final String ARG_PRODUCT_MODULE_ID = "product_id";

    private static final int GRID_WIDTH = 460;
    private static final int GRID_ITEM_MARGIN = 5;

    private String mProductName = null;
    private int mProgramId;
    private LayoutInflater mLayoutInflater = null;
    private BroadcastReceiver mEducationModuleReceiver = null;
    private ProgressBar mProgressBar = null;
    private FrameLayout mLargeProgressBar = null;
    private boolean isProgressBarShown = true;
    private EducationModuleEntity mEducationModuleEntity = null;
    private TextView mModuleDescriptionTitle = null;
    private GridLayout mMainMaterialContainer = null, mSecondaryMaterialContainer = null;
    private int mColumn = 3;
    private int mScreenWidth;
    private ArrayList<EducationMaterialEntity> mPrimaryMaterialsList = null;
    private ArrayList<EducationMaterialEntity> mSecondaryMaterialsList = null;
    private TextView mModuleMainTextView = null, mModuleSecondaryTextView = null;
    private LinearLayout mPassTestLayout = null;
    private Button mCloseYoutubeFragmentButton = null;

    private int mArrayCounter;
    private String mDocFilePath = null;
    private String mDocFileName = null;

    private View mContentView = null;

    public static EducationModuleFragment newInstance(String productName, int productId) {
        EducationModuleFragment fragment = new EducationModuleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_MODULE_NAME, productName);
        args.putInt(ARG_PRODUCT_MODULE_ID, productId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        mScreenWidth = size.x - CommonUtils.convertDpToPixels(getActivity(), GRID_WIDTH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_education_module, container, false);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mProductName = bundle.getString(ARG_PRODUCT_MODULE_NAME);
            mProgramId = bundle.getInt(ARG_PRODUCT_MODULE_ID);
        }

        // Init UI
        if (getActivity() != null) {
            initUI();
        }
    }

    private void initUI(){
        mLayoutInflater = LayoutInflater.from(getActivity());
        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.education_module_progressbar);
        mLargeProgressBar = (FrameLayout) mContentView.findViewById(R.id.base_progressBar);

        mMainMaterialContainer = (GridLayout) mContentView.findViewById(R.id.educ_module_main_material_container);
        mSecondaryMaterialContainer = (GridLayout) mContentView.findViewById(R.id.educ_module_secondary_material_container);
        mPassTestLayout = (LinearLayout) mContentView.findViewById(R.id.education_module_pass_test);
        mPassTestLayout.setVisibility(View.INVISIBLE);
        mPassTestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEducationModuleEntity.getId() > 0) {
                    getChildFragmentManager().beginTransaction().replace(R.id.fragment_module_youtube_container, EducationTestFragment.newInstance(mEducationModuleEntity.getId())).addToBackStack(null).commit();
                    mPassTestLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        mModuleDescriptionTitle = (TextView) mContentView.findViewById(R.id.fragment_education_module_description);
        if (isProgressBarShown) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }

        TextView tvTitleMain = (TextView) mContentView.findViewById(R.id.fragment_education_module_ab_title);
        tvTitleMain.setText(mProductName);

        mModuleMainTextView = (TextView) mContentView.findViewById(R.id.education_module_main_textView);
        mModuleSecondaryTextView = (TextView) mContentView.findViewById(R.id.education_module_secondary_textView);
        mModuleMainTextView.setVisibility(View.INVISIBLE);
        mModuleSecondaryTextView.setVisibility(View.INVISIBLE);

        mCloseYoutubeFragmentButton = (Button) mContentView.findViewById(R.id.education_module_close_button);
        mCloseYoutubeFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().popBackStack();
                mCloseYoutubeFragmentButton.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGI(TAG, "RegisterRecNewsFeed");
        mEducationModuleReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (DBAdapter.EDUCATION_GET_PRODUCT_MODULES_SQL_BROADCAST_INTENT.equals(intent.getAction())) {
                    Cursor cursor = JustUpApplication.getApplication().getTransferActionEducationProductModule().getCursorOfEducationProductModuleFromProgramId(getActivity().getApplicationContext(), mProgramId);
                    mEducationModuleEntity = fillModuleFromCursor(cursor);
                    fillViewsWithData();
                    if (cursor != null) {
                        cursor.close();
                    }

                    mProgressBar.setVisibility(View.GONE);
                    isProgressBarShown = false;
                    mPrimaryMaterialsList = getMainMaterialsList(mEducationModuleEntity.getMaterials());
                    mSecondaryMaterialsList = getSecondaryMaterialsList(mEducationModuleEntity.getMaterials());
                    int rowMain = mPrimaryMaterialsList.size() / mColumn;
                    mMainMaterialContainer.setColumnCount(mColumn);
                    mMainMaterialContainer.setRowCount(rowMain + 1);
                    updateView(mPrimaryMaterialsList, mMainMaterialContainer);
                    int rowSecondary = mSecondaryMaterialsList.size() / mColumn;
                    mSecondaryMaterialContainer.setColumnCount(mColumn);
                    mSecondaryMaterialContainer.setRowCount(rowSecondary + 1);
                    updateView(mSecondaryMaterialsList, mSecondaryMaterialContainer);
                    if (mPrimaryMaterialsList.size() > 0) {
                        mModuleMainTextView.setVisibility(View.VISIBLE);
                    }

                    if (mSecondaryMaterialsList.size() > 0) {
                        mModuleSecondaryTextView.setVisibility(View.VISIBLE);
                    }

                    mPassTestLayout.setVisibility(View.VISIBLE);
                }

                if (HttpIntentService.BROADCAST_INTENT_EDUCATION_MODULE_SERVER_ERROR.equals(intent.getAction())) {
                    LOGI(TAG, "OnReceive, error");
                    mProgressBar.setVisibility(View.GONE);
                    isProgressBarShown = false;
                }

            }
        };

        IntentFilter filter = new IntentFilter(DBAdapter.EDUCATION_GET_PRODUCT_MODULES_SQL_BROADCAST_INTENT);
        filter.addAction(HttpIntentService.BROADCAST_INTENT_EDUCATION_MODULE_SERVER_ERROR);
        LocalBroadcastManager.getInstance(EducationModuleFragment.this.getActivity()).registerReceiver(mEducationModuleReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(EducationModuleFragment.this.getActivity()).unregisterReceiver(mEducationModuleReceiver);
        LOGI(TAG, "UnregisterRecNewsFeed");
    }

    private EducationModuleEntity fillModuleFromCursor(Cursor cursor) {
        EducationModuleEntity educationModuleEntity = new EducationModuleEntity();

        if (cursor != null && cursor.moveToFirst()) {
            educationModuleEntity.setId(cursor.getInt(cursor.getColumnIndex(EDUCATION_PRODUCT_MODULE_SERVER_ID)));
            int productId = cursor.getInt(cursor.getColumnIndex(EDUCATION_PRODUCT_MODULE_PROGRAM_ID));
            educationModuleEntity.setProgram_id(productId);
            educationModuleEntity.setName(cursor.getString(cursor.getColumnIndex(EDUCATION_PRODUCT_MODULE_NAME)));
            educationModuleEntity.setDescription(cursor.getString(cursor.getColumnIndex(EDUCATION_PRODUCT_MODULE_DESCRIPTION)));
            educationModuleEntity.setCreated_at(cursor.getString(cursor.getColumnIndex(EDUCATION_PRODUCT_MODULE_CREATED_AT)));
            educationModuleEntity.setUpdated_at(cursor.getString(cursor.getColumnIndex(EDUCATION_PRODUCT_MODULE_UPDATED_AT)));

            Cursor cursorMaterials = JustUpApplication.getApplication().getTransferActionEducationProductModule().getCursorOfEducationProductModuleFromModuleId(getActivity().getApplicationContext(), productId);

            ArrayList<EducationMaterialEntity> materialEntities = new ArrayList<>();

            if (cursorMaterials != null) {
                for (cursorMaterials.moveToFirst(); !cursorMaterials.isAfterLast(); cursorMaterials.moveToNext()) {
                    EducationMaterialEntity educationMaterialEntity = new EducationMaterialEntity();
                    educationMaterialEntity.setId(cursorMaterials.getInt(cursorMaterials.getColumnIndex(EDUCATION_MODULES_MATERIAL_SERVER_ID)));
                    educationMaterialEntity.setModule_id(cursorMaterials.getInt(cursorMaterials.getColumnIndex(EDUCATION_MODULES_MATERIAL_MODULE_ID)));
                    educationMaterialEntity.setContent_type(cursorMaterials.getString(cursorMaterials.getColumnIndex(EDUCATION_MODULES_MATERIAL_CONTENT_TYPE)));
                    educationMaterialEntity.setPriority_type(cursorMaterials.getString(cursorMaterials.getColumnIndex(EDUCATION_MODULES_MATERIAL_PRIORITY_TYPE)));
                    educationMaterialEntity.setSort_weight(cursorMaterials.getInt(cursorMaterials.getColumnIndex(EDUCATION_MODULES_MATERIAL_SORT_WEIGHT)));
                    educationMaterialEntity.setCreated_at(cursorMaterials.getString(cursorMaterials.getColumnIndex(EDUCATION_MODULES_MATERIAL_CREATED_AT)));
                    educationMaterialEntity.setUpdated_at(cursorMaterials.getString(cursorMaterials.getColumnIndex(EDUCATION_MODULES_MATERIAL_UPDATED_AT)));
                    educationMaterialEntity.setExtraSource(cursorMaterials.getString(cursorMaterials.getColumnIndex(EDUCATION_MODULES_MATERIAL_EXTRA_SOURCE)));
                    educationMaterialEntity.setExtraLink(cursorMaterials.getString(cursorMaterials.getColumnIndex(EDUCATION_MODULES_MATERIAL_EXTRA_LINK)));
                    educationMaterialEntity.setName(cursorMaterials.getString(cursorMaterials.getColumnIndex(EDUCATION_MODULES_MATERIAL_NAME)));
                    educationMaterialEntity.setDescription(cursorMaterials.getString(cursorMaterials.getColumnIndex(EDUCATION_MODULES_MATERIAL_DESCRIPTION)));
                    materialEntities.add(educationMaterialEntity);
                }

                educationModuleEntity.setMaterials(materialEntities);
            }
        }

        return educationModuleEntity;
    }

    private void fillViewsWithData() {
        mModuleDescriptionTitle.setText(mEducationModuleEntity.getDescription());
    }

    private ArrayList<EducationMaterialEntity> getMainMaterialsList(ArrayList<EducationMaterialEntity> materialEntities) {
        ArrayList<EducationMaterialEntity> entities = new ArrayList<>();
        for (int i = 0; i < materialEntities.size(); i++) {
            if (materialEntities.get(i).getPriority_type().toLowerCase().equals(PRIORITY_TYPE_PRIMARY)) {
                entities.add(materialEntities.get(i));
            }
        }

        return entities;
    }

    private ArrayList<EducationMaterialEntity> getSecondaryMaterialsList(ArrayList<EducationMaterialEntity> materialEntities) {
        ArrayList<EducationMaterialEntity> entities = new ArrayList<>();
        for (int i = 0; i < materialEntities.size(); i++) {
            if (materialEntities.get(i).getPriority_type().toLowerCase().equals(PRIORITY_TYPE_SECONDARY)) {
                entities.add(materialEntities.get(i));
            }
        }

        return entities;
    }

    private void updateView(final ArrayList<EducationMaterialEntity> entities, GridLayout gridLayout) {
        for (int i = 0, c = 0, r = 0; i < entities.size(); i++, c++) {
            if (c == mColumn) {
                c = 0;
                r++;
            }

            mArrayCounter = i;

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = (mScreenWidth / 3);
            param.rightMargin = CommonUtils.convertDpToPixels(getActivity(), GRID_ITEM_MARGIN);
            // param.topMargin = 10;
            // param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(c);
            param.rowSpec = GridLayout.spec(r);

            RelativeLayout categoryProductLayout = (RelativeLayout) mLayoutInflater.inflate(R.layout.education_module_item, null, false);

            TextView moduleId = (TextView) categoryProductLayout.findViewById(R.id.education_module_item_id);
            moduleId.setText(Integer.toString(entities.get(i).getId()));

            TextView moduleType = (TextView) categoryProductLayout.findViewById(R.id.education_module_item_content_type);
            moduleType.setText(entities.get(i).getContent_type());

            TextView moduleLink = (TextView) categoryProductLayout.findViewById(R.id.education_module_item_link);
            moduleLink.setText(entities.get(i).getExtraLink());

            ImageView contentTypePhoto = (ImageView) categoryProductLayout.findViewById(R.id.education_module_item_image_view);
            if (entities.get(i).getContent_type().toLowerCase().equals(YOU_TUBE_VIDEO)) {
                contentTypePhoto.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.video_play));
            } else {
                contentTypePhoto.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.education_book));
            }

            // Set Type
            // ((TextView) categoryProductLayout.findViewById(R.id.education_module_item_main_type)).setText(entities.get(i).getContent_type());
            // Set Text
            TextView mainTxtView = (TextView) categoryProductLayout.findViewById(R.id.education_module_item_main_text);
            mainTxtView.setText(entities.get(i).getName());

            // Set Text
            ((TextView) categoryProductLayout.findViewById(R.id.education_module_item_description_text)).setText(entities.get(i).getDescription().replace("Описание", "Описание : "));

            categoryProductLayout.setLayoutParams(param);
            categoryProductLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String contentType = ((TextView) v.findViewById(R.id.education_module_item_content_type)).getText().toString();
                    String link = ((TextView) v.findViewById(R.id.education_module_item_link)).getText().toString();
                    if (contentType.toLowerCase().equals(YOU_TUBE_VIDEO)) {
                        getChildFragmentManager().beginTransaction().replace(R.id.fragment_module_youtube_container, YoutubeDefaultFragment.newInstance(link)).addToBackStack(null).commit();
                        mCloseYoutubeFragmentButton.setVisibility(View.VISIBLE);
                    } else {
                        mDocFileName = entities.get(mArrayCounter).getName();

                        if (isFileFind(mDocFileName)) {
                            LOGI(TAG, "File found !");

                            showViewPDFDialog(mDocFileName, mDocFilePath);
                        } else {
                            LOGD(TAG, "File NOT found !");

                            mLargeProgressBar.setVisibility(View.VISIBLE);
                            ApiWrapper.downloadFileFromUrl(link, new OnDownloadFileResponse(getActivity()));
                        }
                    }
                }
            });

            gridLayout.addView(categoryProductLayout);
        }
    }

    public void closeTest() {
        getChildFragmentManager().popBackStack();
        WarningDialog dialog = WarningDialog.newInstance("Отправлено", "Ваш тест отправлен на сервер");
        dialog.show(getChildFragmentManager(), WarningDialog.WARNING_DIALOG);
        mPassTestLayout.setVisibility(View.VISIBLE);
    }

    private void showViewPDFDialog(String mFileName, String mFilePath) {
        ViewPDFDialog dialog = ViewPDFDialog.newInstance(mFileName, mFilePath);
        dialog.show(getChildFragmentManager(), ViewPDFDialog.VIEW_PDF_DIALOG);
    }

    private boolean isFileFind(String fileName) {
        File mStorageDirectory = Environment.getExternalStorageDirectory();
        File[] mDirList = mStorageDirectory.listFiles();

        for (File file : mDirList) {
            if (!file.isDirectory()) {
                if (fileName.equals(file.getName())) {
                    mDocFilePath = file.getAbsolutePath();
                    return true;
                }
            }
        }

        return false;
    }

    private class OnDownloadFileResponse extends FileAsyncHttpResponseHandler {

        public OnDownloadFileResponse(Context context) {
            super(context);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, File file) {
            LOGD(TAG, "OnDownloadFileResponse onSuccess()");

            new AsyncSaveFile(mDocFileName).execute(file);

            if (EducationModuleFragment.this.isAdded()) {
                mLargeProgressBar.setVisibility(View.GONE);
                showViewPDFDialog("PDF", file.getAbsolutePath());
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
            LOGE(TAG, "OnDownloadFileResponse onFailure()");
            if (throwable != null) {
                LOGE(TAG, "Throwable:", throwable);
            }

            if (EducationModuleFragment.this.isAdded()) {
                mLargeProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Server error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
