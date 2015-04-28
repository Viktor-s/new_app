package me.justup.upme.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.RelativeLayout;

import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.adapter.EducationProductsAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.db.DBHelper;
import me.justup.upme.entity.EducationGetModulesByProgramIdQuery;
import me.justup.upme.http.HttpIntentService;

import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCTS_TABLE_NAME;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class EducationFragment extends Fragment {
    private static final String TAG = makeLogTag(EducationFragment.class);
    private RelativeLayout youtubePlayerButton;
    private BroadcastReceiver receiver;
    private SQLiteDatabase database;
    private String selectQuery;
    private Cursor cursor;
    private int lastChosenPosition = -1;
    private EducationProductsAdapter educationProductsAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (database != null) {
            if (!database.isOpen()) {
                database = DBAdapter.getInstance().openDatabase();
            }
        } else {
            database = DBAdapter.getInstance().openDatabase();
        }
        selectQuery = "SELECT * FROM " + EDUCATION_PRODUCTS_TABLE_NAME;
        cursor = database.rawQuery(selectQuery, null);

        educationProductsAdapter = new EducationProductsAdapter(getActivity().getApplicationContext(), cursor, 0);
        educationProductsAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return fetchProductByName(constraint.toString().toLowerCase());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            LocalBroadcastManager.getInstance(EducationFragment.this.getActivity()).unregisterReceiver(receiver);
        } catch (Exception e) {
            LOGE(TAG, "unregisterReceiver(receiver)", e);
            receiver = null;
        }
    }

    @Override
    public void onDestroy() {
        cursor.close();
        DBAdapter.getInstance().closeDatabase();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LOGE(TAG, "onReceive educationFragment");
                cursor = database.rawQuery(selectQuery, null);
                educationProductsAdapter.changeCursor(cursor);
                educationProductsAdapter.notifyDataSetChanged();
            }
        };
        LocalBroadcastManager.getInstance(EducationFragment.this.getActivity())
                .registerReceiver(receiver, new IntentFilter(DBAdapter.EDUCATION_GET_PRODUCTS_SQL_BROADCAST_INTENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_study, container, false);
        LOGI(TAG, "StudyFragment onCreateView()");
//        youtubePlayerButton = (RelativeLayout) v.findViewById(R.id.youtube_button_container);
//        youtubePlayerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                youtubePlayerButton.setVisibility(View.GONE);
//                //YouTubePlayerFragment youTubePlayerFragment = YouTubePlayerFragment.newInstance("OMOVFvcNfvE");
//                //getChildFragmentManager().beginTransaction().replace(R.id.youtube_player_container, YouTubeCustomPlayerFragment.newInstance("OMOVFvcNfvE")).addToBackStack(null).commit();
//                getChildFragmentManager().beginTransaction().replace(R.id.youtube_player_container, YoutubeDefaultFragment.newInstance("OMOVFvcNfvE")).addToBackStack(null).commit();
//            }
//        });


        ListView contactsListView = (ListView) v.findViewById(R.id.study_ListView);
        contactsListView.setAdapter(educationProductsAdapter);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i = 0; i < adapterView.getChildCount(); i++) {
                    adapterView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                view.setBackground(getResources().getDrawable(R.drawable.educ_let_side_rounded));


                if (lastChosenPosition != position) {
                    int productId = educationProductsAdapter.getCursor().getInt(educationProductsAdapter.getCursor().getColumnIndex(DBHelper.EDUCATION_PRODUCTS_SERVER_ID));
                    EducationGetModulesByProgramIdQuery educationGetModulesByProgramIdQuery = new EducationGetModulesByProgramIdQuery();
                    educationGetModulesByProgramIdQuery.params.program_id = productId;
                    ((MainActivity) EducationFragment.this.getActivity()).startHttpIntent(educationGetModulesByProgramIdQuery, HttpIntentService.EDUCATION_GET_PRODUCT_MODULES);
                    String productName = educationProductsAdapter.getCursor().getString(educationProductsAdapter.getCursor().getColumnIndex(DBHelper.EDUCATION_PRODUCTS_NAME));


                    final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.education_module_container, EducationModuleFragment.newInstance(productName, productId));
                    ft.commit();
                    lastChosenPosition = position;
                }
            }
        });

        EditText mSearchFieldEditText = (EditText) v.findViewById(R.id.study_fragment_search_editText);
        mSearchFieldEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                educationProductsAdapter.getFilter().filter(s.toString());
            }
        });


        return v;
    }

    private Cursor fetchProductByName(String search) {
        Cursor mCursor;
        if (search == null || search.length() == 0) {
            mCursor = database.rawQuery(selectQuery, null);
        } else {
            LOGE(TAG, search);
            mCursor = database.rawQuery("SELECT * FROM "
                    + DBHelper.EDUCATION_PRODUCTS_TABLE_NAME + " where " + "name_lc" + " like '%" + search
                    + "%'", null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void openYoutubeFragment(){

    }
}
