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
import me.justup.upme.utils.BackAwareEditText;

import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCTS_TABLE_NAME;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class EducationFragment extends Fragment {
    private static final String TAG = makeLogTag(EducationFragment.class);

    private RelativeLayout youtubePlayerButton = null;
    private BroadcastReceiver mReceiver = null;
    private SQLiteDatabase mDatabase = null;
    private String mSelectQuery = null;
    private Cursor mCursor = null;
    private int mLastChosenPosition = -1;
    private EducationProductsAdapter mEducationProductsAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (mDatabase != null) {
            if (!mDatabase.isOpen()) {
                mDatabase = DBAdapter.getInstance().openDatabase();
            }
        } else {
            mDatabase = DBAdapter.getInstance().openDatabase();
        }

        mSelectQuery = "SELECT * FROM " + EDUCATION_PRODUCTS_TABLE_NAME;
        mCursor = mDatabase.rawQuery(mSelectQuery, null);

        mEducationProductsAdapter = new EducationProductsAdapter(getActivity().getApplicationContext(), mCursor, 0);
        mEducationProductsAdapter.setFilterQueryProvider(new FilterQueryProvider() {
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
            LocalBroadcastManager.getInstance(EducationFragment.this.getActivity()).unregisterReceiver(mReceiver);
        } catch (Exception e) {
            LOGE(TAG, "unregisterReceiver(mReceiver)", e);
            mReceiver = null;
        }
    }

    @Override
    public void onDestroy() {
        mCursor.close();
        DBAdapter.getInstance().closeDatabase();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LOGE(TAG, "onReceive educationFragment");
                mCursor = mDatabase.rawQuery(mSelectQuery, null);
                mEducationProductsAdapter.changeCursor(mCursor);
                mEducationProductsAdapter.notifyDataSetChanged();
            }
        };

        LocalBroadcastManager.getInstance(EducationFragment.this.getActivity()).registerReceiver(mReceiver, new IntentFilter(DBAdapter.EDUCATION_GET_PRODUCTS_SQL_BROADCAST_INTENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_study, container, false);

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
        contactsListView.setAdapter(mEducationProductsAdapter);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i = 0; i < adapterView.getChildCount(); i++) {
                    adapterView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }

                view.setBackground(getResources().getDrawable(R.drawable.educ_let_side_rounded));


                if (mLastChosenPosition != position) {
                    int productId = mEducationProductsAdapter.getCursor().getInt(mEducationProductsAdapter.getCursor().getColumnIndex(DBHelper.EDUCATION_PRODUCTS_SERVER_ID));
                    EducationGetModulesByProgramIdQuery educationGetModulesByProgramIdQuery = new EducationGetModulesByProgramIdQuery();
                    educationGetModulesByProgramIdQuery.params.program_id = productId;
                    ((MainActivity) EducationFragment.this.getActivity()).startHttpIntent(educationGetModulesByProgramIdQuery, HttpIntentService.EDUCATION_GET_PRODUCT_MODULES);
                    String productName = mEducationProductsAdapter.getCursor().getString(mEducationProductsAdapter.getCursor().getColumnIndex(DBHelper.EDUCATION_PRODUCTS_NAME));


                    final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.education_module_container, EducationModuleFragment.newInstance(productName, productId));
                    ft.commit();
                    mLastChosenPosition = position;
                }
            }
        });

        BackAwareEditText mSearchFieldEditText = (BackAwareEditText) v.findViewById(R.id.study_fragment_search_editText);
        mSearchFieldEditText.setBackPressedListener(new BackAwareEditText.BackPressedListener() {
            @Override
            public void onImeBack(BackAwareEditText editText) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideNavBar();
                }
            }
        });

        mSearchFieldEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEducationProductsAdapter.getFilter().filter(s.toString());
            }
        });


        return v;
    }

    private Cursor fetchProductByName(String search) {
        Cursor mCursor;
        if (search == null || search.length() == 0) {
            mCursor = mDatabase.rawQuery(mSelectQuery, null);
        } else {
            LOGE(TAG, search);
            mCursor = mDatabase.rawQuery("SELECT * FROM "
                    + DBHelper.EDUCATION_PRODUCTS_TABLE_NAME + " where " + "name_lc" + " like '%" + search
                    + "%'", null);
        }

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

   }
