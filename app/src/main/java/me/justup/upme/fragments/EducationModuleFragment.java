package me.justup.upme.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.ArticleFullEntity;
import me.justup.upme.entity.ArticleShortCommentEntity;
import me.justup.upme.entity.ProductsCategoryBrandEntity;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.CommonUtils;

import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_DESCRIPTION;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_ID;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_NAME;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_PROGRAM_ID;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_SERVER_ID;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_TABLE_NAME;
import static me.justup.upme.db.DBHelper.FULL_NEWS_FULL_DESCR;
import static me.justup.upme.db.DBHelper.FULL_NEWS_SERVER_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_IMAGE;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_CONTENT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_POSTED_AT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_SERVER_ID;
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
        mModuleDescriptionTitle = (TextView) view.findViewById(R.id.fragment_education_module_description);
        if (isProgressBarShown) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
        TextView tvTitleMain = (TextView) view.findViewById(R.id.fragment_education_module_ab_title);
        tvTitleMain.setText(productName);

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
            String selectQueryMaterials = QUERY_MATERIALS + productId;


            Cursor cursorMaterials = database.rawQuery(selectQueryMaterials, null);

//            ArrayList<EducationMaterialEntity> materialEntities = new ArrayList<>();
//            if (cursorMaterials != null) {
//                for (cursorMaterials.moveToFirst(); !cursorMaterials.isAfterLast(); cursorMaterials.moveToNext()) {
//                    EducationMaterialEntity educationMaterialEntity = new EducationMaterialEntity();
//                    educationMaterialEntity.setId(cursorMaterials.getInt(cursorMaterials.getColumnIndex(SHORT_NEWS_COMMENTS_SERVER_ID)));
//                    educationMaterialEntity.setContent(cursorMaterials.getString(cursorMaterials.getColumnIndex(SHORT_NEWS_COMMENTS_CONTENT)));
//                    educationMaterialEntity.setAuthor_id(cursorMaterials.getInt(cursorMaterials.getColumnIndex(SHORT_NEWS_COMMENTS_AUTHOR_ID)));
//                    educationMaterialEntity.setAuthor_name(cursorMaterials.getString(cursorMaterials.getColumnIndex(SHORT_NEWS_COMMENTS_AUTHOR_NAME)));
//                    educationMaterialEntity.setAuthor_img(cursorMaterials.getString(cursorMaterials.getColumnIndex(SHORT_NEWS_COMMENTS_AUTHOR_IMAGE)));
//                    educationMaterialEntity.setPosted_at(cursorMaterials.getString(cursorMaterials.getColumnIndex(SHORT_NEWS_COMMENTS_POSTED_AT)));
//                    materialEntities.add(educationMaterialEntity);
//                }
//                educationModuleEntity.setComments(materialEntities);
//                if (cursorMaterials != null) {
//                    cursorMaterials.close();
//                }
//            }
        }
        return educationModuleEntity;
    }

    private void fillViewsWithData() {
        mModuleDescriptionTitle.setText(educationModuleEntity.getDescription());

    }

}