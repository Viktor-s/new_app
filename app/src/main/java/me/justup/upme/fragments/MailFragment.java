package me.justup.upme.fragments;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.ListView;

import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.adapter.MailContactsAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.db.DBHelper;
import me.justup.upme.entity.Push;
import me.justup.upme.entity.StartChatQuery;
import me.justup.upme.services.PushIntentService;
import me.justup.upme.utils.AppPreferences;
import me.justup.upme.utils.BackAwareEditText;
import me.justup.upme.utils.CommonUtils;

import static me.justup.upme.db.DBHelper.MAIL_CONTACT_TABLE_NAME;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class MailFragment extends Fragment {
    private static final String TAG = makeLogTag(MailFragment.class);

    private int lastChosenPosition = -1;
    private MailContactsAdapter mMailContactsAdapter = null;
    private String mSelectQuery = null;
    private Cursor mCursor = null;
    private BackAwareEditText mSearchFieldEditText = null;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mCursor = mDatabase.rawQuery(mSelectQuery, null);
            mMailContactsAdapter.changeCursor(mCursor);
            mMailContactsAdapter.notifyDataSetChanged();
        }
    };

    public static final int JABBER = 1;
    public static final int WEBRTC = 2;
    public static final int FILE = 3;
    public static final int BREAK_CALL = 4;
    public static final int CALENDAR_NEW_EVENT = 5;
    public static final int ORDER_FORM = 6;
    public static final int ORDER_INFO = 7;

    private SQLiteDatabase mDatabase = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (mDatabase != null) {
            if (!mDatabase.isOpen()) {
                mDatabase = DBAdapter.getInstance().openDatabase();
            }
        } else {
            mDatabase = DBAdapter.getInstance().openDatabase();
        }

        mSelectQuery = "SELECT * FROM " + MAIL_CONTACT_TABLE_NAME;
        mCursor = mDatabase.rawQuery(mSelectQuery, null);

        mMailContactsAdapter = new MailContactsAdapter(this, activity.getApplicationContext(), mCursor, 0);
        mMailContactsAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return fetchContactsByName(constraint.toString().toLowerCase());
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            LocalBroadcastManager.getInstance(MailFragment.this.getActivity()).unregisterReceiver(mReceiver);
        } catch (Exception e) {
            LOGE(TAG, "unregisterReceiver(mReceiver)", e);
            mReceiver = null;
        }
    }

    @Override
    public void onDestroy() {
        if(mMailContactsAdapter.getCursor()!=null){
            mMailContactsAdapter.getCursor().close();
        }

        if (mCursor != null) {
            mCursor.close();
        }

        DBAdapter.getInstance().closeDatabase();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LOGE(TAG, "onReceive mailFragment");
                try {
                    mCursor = mDatabase.rawQuery(mSelectQuery, null);
                }catch (IllegalStateException e){
                    LOGE(TAG, "Attempt to re-open an already-closed object: SQLiteDatabase: /data/data/me.justup.upme/databases/upme.db : \n" + e.getMessage());
                }
                // mMailContactsAdapter = new MailContactsAdapter(this, getActivity().getApplicationContext(), mCursor, 0);
            }
        };

        LocalBroadcastManager.getInstance(MailFragment.this.getActivity()).registerReceiver(mReceiver, new IntentFilter(DBAdapter.MAIL_SQL_BROADCAST_INTENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail, container, false);

        final AppPreferences appPreferences = new AppPreferences(getActivity());

        final Push push = ((MainActivity) getActivity()).getPush();
        if (push != null) {

            if (push.getType() == JABBER) {
                String friendJabberId = push.getJabberId();
                String friendName = push.getUserName();
                String yourJabberId = appPreferences.getJabberId();
                String yourName = appPreferences.getUserName();

                getChildFragmentManager().beginTransaction().replace(R.id.mail_messages_container_frameLayout, MailMessagesFragment.newInstance(yourName, yourJabberId, friendName, friendJabberId, push.getUserId())).commit();

            } else if (push.getType() == WEBRTC) {
                ((MainActivity) getActivity()).prepareAndCallRTC(String.valueOf(push.getRoom()), false, false, 0, 0, "");
            }

            ((MainActivity) getActivity()).setPush(null);
        }

        ListView contactsListView = (ListView) view.findViewById(R.id.mail_contacts_ListView);
        contactsListView.setAdapter(mMailContactsAdapter);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i = 0; i < adapterView.getChildCount(); i++) {
                    adapterView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                view.setBackgroundColor(Color.LTGRAY);

                CommonUtils.hideKeyboard(getActivity());

                if (lastChosenPosition != position) {
                    String friendJabberId = mMailContactsAdapter.getCursor().getString(mMailContactsAdapter.getCursor().getColumnIndex(DBHelper.MAIL_CONTACT_JABBER_ID));
                    String friendName = mMailContactsAdapter.getCursor().getString(mMailContactsAdapter.getCursor().getColumnIndex(DBHelper.MAIL_CONTACT_NAME));
                    String yourJabberId = appPreferences.getJabberId();
                    String yourName = appPreferences.getUserName();
                    int userId = mMailContactsAdapter.getCursor().getInt(mMailContactsAdapter.getCursor().getColumnIndex(DBHelper.MAIL_CONTACT_SERVER_ID));

                    startNotificationIntent(userId);

                    final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.mail_messages_container_frameLayout, MailMessagesFragment.newInstance(yourName, yourJabberId, friendName, friendJabberId, userId));
                    ft.commit();
                    lastChosenPosition = position;
                }
            }
        });

        mSearchFieldEditText = (BackAwareEditText) view.findViewById(R.id.mail_fragment_search_edt);
        mSearchFieldEditText.setBackPressedListener(new BackAwareEditText.BackPressedListener() {
            @Override
            public void onImeBack(BackAwareEditText editText) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideNavBar();
                }
            }
        });

        mSearchFieldEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMailContactsAdapter.getFilter().filter(s.toString());
            }
        });

        ImageButton clearEditTextButton = (ImageButton) view.findViewById(R.id.clear_search_text_button);
        clearEditTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchFieldEditText.setText("");
            }
        });
        return view;
    }

    public void startNotificationIntent(int userId) {
        StartChatQuery push = new StartChatQuery();
        push.params.setUserIds(userId);

        Bundle bundle = new Bundle();
        bundle.putSerializable(PushIntentService.PUSH_INTENT_QUERY_EXTRA, push);

        Intent intent = new Intent(getActivity(), PushIntentService.class);
        getActivity().startService(intent.putExtras(bundle));
    }

    private Cursor fetchContactsByName(String search) {
        Cursor mCursor;
        if (search == null || search.length() == 0) {
            mCursor = mDatabase.rawQuery(mSelectQuery, null);
        } else {
            LOGE(TAG, search);
            mCursor = mDatabase.rawQuery("SELECT * FROM "
                    + DBHelper.MAIL_CONTACT_TABLE_NAME + " where " + "name_lc" + " like '%" + search
                    + "%'", null);

        }

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        // mCursor = new FilterCursorWrapper(mCursor, search, mCursor.getColumnIndex(DBHelper.MAIL_CONTACT_NAME));
        return mCursor;
    }

}
