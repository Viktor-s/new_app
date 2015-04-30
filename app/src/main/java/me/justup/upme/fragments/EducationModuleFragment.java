package me.justup.upme.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;

import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.dialogs.ViewPDFDialog;
import me.justup.upme.entity.EducationMaterialEntity;
import me.justup.upme.entity.EducationModuleEntity;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.http.HttpIntentService;
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
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class EducationModuleFragment extends Fragment {
    private static final String TAG = makeLogTag(EducationModuleFragment.class);
    private static final String ARG_PRODUCT_MODULE_NAME = "product_name";
    private static final String ARG_PRODUCT_MODULE_ID = "product_id";
    private static final String QUERY_MODULE = "SELECT * FROM education_product_module_table WHERE program_id=";
    private static final String QUERY_MATERIALS = "SELECT * FROM education_modules_material_table WHERE module_id=";

    private String productName;
    private int productId;
    private SQLiteDatabase database;
    private LayoutInflater layoutInflater;
    private BroadcastReceiver mEducationModuleReceiver;
    private ProgressBar mProgressBar;
    private boolean isProgressBarShown = true;
    private String selectQuery;
    private EducationModuleEntity educationModuleEntity;
    private TextView mModuleDescriptionTitle;
    private GridLayout mMainMaterialContainer, mSecondaryMaterialContainer;
    private int column = 3;
    private int screenWidth;
    private ArrayList<EducationMaterialEntity> primaryMaterialsList;
    private ArrayList<EducationMaterialEntity> secondaryMaterialsList;
    private TextView moduleMainTextView, moduleSecondaryTextView;
    private LinearLayout passTestLayout;

    public static EducationModuleFragment newInstance(String productName, int productId) {
        EducationModuleFragment fragment = new EducationModuleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_MODULE_NAME, productName);
        args.putInt(ARG_PRODUCT_MODULE_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            productName = bundle.getString(ARG_PRODUCT_MODULE_NAME);
            productId = bundle.getInt(ARG_PRODUCT_MODULE_ID);
        }
        if (database != null) {
            if (!database.isOpen()) {
                database = DBAdapter.getInstance().openDatabase();
            }
        } else {
            database = DBAdapter.getInstance().openDatabase();
        }
        selectQuery = QUERY_MODULE + productId;
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x - CommonUtils.convertDpToPixels(getActivity(), 440);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(EducationModuleFragment.this.getActivity()).unregisterReceiver(mEducationModuleReceiver);
        LOGI(TAG, "unregisterRecNewsFeed");
    }

    @Override
    public void onDestroy() {
        DBAdapter.getInstance().closeDatabase();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGI(TAG, "RegisterRecNewsFeed");
        mEducationModuleReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (DBAdapter.EDUCATION_GET_PRODUCT_MODULES_SQL_BROADCAST_INTENT.equals(intent.getAction())) {
                    Cursor cursor = database.rawQuery(selectQuery, null);
                    educationModuleEntity = fillModuleFromCursor(cursor);
                    fillViewsWithData();
                    if (cursor != null) {
                        cursor.close();
                    }
                    mProgressBar.setVisibility(View.GONE);
                    isProgressBarShown = false;
                    primaryMaterialsList = getMainMaterialsList(educationModuleEntity.getMaterials());
                    secondaryMaterialsList = getSecondaryMaterialsList(educationModuleEntity.getMaterials());
                    int rowMain = primaryMaterialsList.size() / column;
                    mMainMaterialContainer.setColumnCount(column);
                    mMainMaterialContainer.setRowCount(rowMain + 1);
                    updateView(primaryMaterialsList, mMainMaterialContainer);
                    int rowSecondary = secondaryMaterialsList.size() / column;
                    mSecondaryMaterialContainer.setColumnCount(column);
                    mSecondaryMaterialContainer.setRowCount(rowSecondary + 1);
                    updateView(secondaryMaterialsList, mSecondaryMaterialContainer);
                    if (primaryMaterialsList.size() > 0) {
                        moduleMainTextView.setVisibility(View.VISIBLE);
                    }
                    if (secondaryMaterialsList.size() > 0) {
                        moduleSecondaryTextView.setVisibility(View.VISIBLE);
                    }
                    passTestLayout.setVisibility(View.VISIBLE);

                }
                if (HttpIntentService.BROADCAST_INTENT_EDUCATION_MODULE_SERVER_ERROR.equals(intent.getAction())) {
                    LOGI(TAG, "onReceive, error");
                    mProgressBar.setVisibility(View.GONE);
                    isProgressBarShown = false;
                }

            }
        };
        IntentFilter filter = new IntentFilter(DBAdapter.EDUCATION_GET_PRODUCT_MODULES_SQL_BROADCAST_INTENT);
        filter.addAction(HttpIntentService.BROADCAST_INTENT_EDUCATION_MODULE_SERVER_ERROR);
        LocalBroadcastManager.getInstance(EducationModuleFragment.this.getActivity())
                .registerReceiver(mEducationModuleReceiver, filter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_education_module, container, false);
        layoutInflater = LayoutInflater.from(getActivity());
        mProgressBar = (ProgressBar) view.findViewById(R.id.education_module_progressbar);
        mMainMaterialContainer = (GridLayout) view.findViewById(R.id.educ_module_main_material_container);
        mSecondaryMaterialContainer = (GridLayout) view.findViewById(R.id.educ_module_secondary_material_container);
        passTestLayout = (LinearLayout) view.findViewById(R.id.education_module_pass_test);
        passTestLayout.setVisibility(View.INVISIBLE);
        passTestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (educationModuleEntity.getId() > 0) {
                    getChildFragmentManager().beginTransaction().replace(R.id.fragment_module_youtube_container, EducationTestFragment.newInstance(educationModuleEntity.getId())).addToBackStack(null).commit();
                    passTestLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
        mModuleDescriptionTitle = (TextView) view.findViewById(R.id.fragment_education_module_description);
        if (isProgressBarShown) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
        TextView tvTitleMain = (TextView) view.findViewById(R.id.fragment_education_module_ab_title);
        tvTitleMain.setText(productName);

        moduleMainTextView = (TextView) view.findViewById(R.id.education_module_main_textView);
        moduleSecondaryTextView = (TextView) view.findViewById(R.id.education_module_secondary_textView);
        moduleMainTextView.setVisibility(View.INVISIBLE);
        moduleSecondaryTextView.setVisibility(View.INVISIBLE);

        return view;
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
            String selectQueryMaterials = QUERY_MATERIALS + productId;
            Cursor cursorMaterials = database.rawQuery(selectQueryMaterials, null);

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
                if (cursorMaterials != null) {
                    cursorMaterials.close();
                }
            }
        }
        return educationModuleEntity;
    }

    private void fillViewsWithData() {
        mModuleDescriptionTitle.setText(educationModuleEntity.getDescription());

    }

    private ArrayList<EducationMaterialEntity> getMainMaterialsList(ArrayList<EducationMaterialEntity> materialEntities) {
        ArrayList<EducationMaterialEntity> entities = new ArrayList<>();
        for (int i = 0; i < materialEntities.size(); i++) {
            if (materialEntities.get(i).getPriority_type().equals("Primary")) {
                entities.add(materialEntities.get(i));
            }
        }
        return entities;
    }

    private ArrayList<EducationMaterialEntity> getSecondaryMaterialsList(ArrayList<EducationMaterialEntity> materialEntities) {
        ArrayList<EducationMaterialEntity> entities = new ArrayList<>();
        for (int i = 0; i < materialEntities.size(); i++) {
            if (materialEntities.get(i).getPriority_type().equals("Secondary")) {
                entities.add(materialEntities.get(i));
            }
        }
        return entities;
    }


    private void updateView(ArrayList<EducationMaterialEntity> entities, GridLayout gridLayout) {
        for (int i = 0, c = 0, r = 0; i < entities.size(); i++, c++) {
            if (c == column) {
                c = 0;
                r++;
            }
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = (screenWidth / 3);
            param.rightMargin = CommonUtils.convertDpToPixels(getActivity(), 16);
            // param.topMargin = 10;
            //  param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(c);
            param.rowSpec = GridLayout.spec(r);

            RelativeLayout categoryProductLayout = (RelativeLayout) layoutInflater.inflate(R.layout.education_module_item, null, false);
            TextView moduleId = (TextView) categoryProductLayout.findViewById(R.id.education_module_item_id);
            moduleId.setText(Integer.toString(entities.get(i).getId()));
            TextView moduleType = (TextView) categoryProductLayout.findViewById(R.id.education_module_item_content_type);
            moduleType.setText(entities.get(i).getContent_type());
            TextView moduleLink = (TextView) categoryProductLayout.findViewById(R.id.education_module_item_link);
            moduleLink.setText(entities.get(i).getExtraLink());

            ImageView contentTypePhoto = (ImageView) categoryProductLayout.findViewById(R.id.education_module_item_image_view);
            if (entities.get(i).getContent_type().equals("Video")) {
                contentTypePhoto.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.video_play));
            } else {
                contentTypePhoto.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.education_book));
            }

            TextView mainText = (TextView) categoryProductLayout.findViewById(R.id.education_module_item_main_text);
            mainText.setText(entities.get(i).getContent_type() + " " + entities.get(i).getName());
            TextView description = (TextView) categoryProductLayout.findViewById(R.id.education_module_item_description_text);
            description.setText(entities.get(i).getDescription());
            categoryProductLayout.setLayoutParams(param);
            categoryProductLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String contentType = ((TextView) v.findViewById(R.id.education_module_item_content_type)).getText().toString();
                    if (contentType.equals("Video")) {
                        getChildFragmentManager().beginTransaction().replace(R.id.fragment_module_youtube_container, YoutubeDefaultFragment.newInstance("OMOVFvcNfvE")).addToBackStack(null).commit();
                    } else {
                        String link = ((TextView) v.findViewById(R.id.education_module_item_link)).getText().toString();
                        ApiWrapper.downloadFileFromUrl(link, new OnDownloadFileResponse(getActivity()));
                        mProgressBar.setVisibility(View.VISIBLE);
                        //showViewPDFDialog("The fast forward mba in project management", "file:///android_asset/mba.pdf");
                    }
                }
            });
            gridLayout.addView(categoryProductLayout);
        }
    }


    private void showViewPDFDialog(String mFileName, String mFilePath) {
        ViewPDFDialog dialog = ViewPDFDialog.newInstance(mFileName, mFilePath);
        dialog.show(getChildFragmentManager(), ViewPDFDialog.VIEW_PDF_DIALOG);
    }


    private class OnDownloadFileResponse extends FileAsyncHttpResponseHandler {

        public OnDownloadFileResponse(Context context) {
            super(context);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, File file) {
            mProgressBar.setVisibility(View.GONE);
            showViewPDFDialog("PDF", file.getAbsolutePath());
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
            mProgressBar.setVisibility(View.GONE);

        }
    }
}