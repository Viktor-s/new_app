package me.justup.upme.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.adapter.MailContactsAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.db.DBHelper;
import me.justup.upme.entity.Push;
import me.justup.upme.entity.StartChatQuery;
import me.justup.upme.services.PushIntentService;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppPreferences;
import me.justup.upme.utils.CommonUtils;

import static me.justup.upme.db.DBHelper.MAIL_CONTACT_TABLE_NAME;
import static me.justup.upme.utils.LogUtils.LOGE;


public class MailFragment extends Fragment {
    private int lastChosenPosition = -1;
    private MailContactsAdapter mMailContactsAdapter;
    private String selectQuery;
    private BroadcastReceiver receiver;

    public static final int JABBER = 1;
    public static final int WEBRTC = 2;
    public static final int FILE = 3;
    public static final int BREAK_CALL = 4;
    public static final int CALENDAR_NEW_EVENT = 5;
    public static final int ORDER_FORM = 6;

    private SQLiteDatabase database;
    private Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (database != null) {
            if (!database.isOpen()) {
                database = DBAdapter.getInstance().openDatabase();
            }
        } else {
            database = DBAdapter.getInstance().openDatabase();
        }
        selectQuery = "SELECT * FROM " + MAIL_CONTACT_TABLE_NAME;
        cursor = database.rawQuery(selectQuery, null);
        mMailContactsAdapter = new MailContactsAdapter(this, AppContext.getAppContext(), cursor, 0);
        mMailContactsAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return fetchContactsByName(constraint.toString().toLowerCase());
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(MailFragment.this.getActivity()).unregisterReceiver(receiver);
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
                cursor = database.rawQuery(selectQuery, null);
                mMailContactsAdapter.changeCursor(cursor);
                mMailContactsAdapter.notifyDataSetChanged();

            }
        };
        LocalBroadcastManager.getInstance(MailFragment.this.getActivity())
                .registerReceiver(receiver, new IntentFilter(DBAdapter.MAIL_SQL_BROADCAST_INTENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail, container, false);

        final AppPreferences appPreferences = new AppPreferences(getActivity());

        final Push push = ((MainActivity) getActivity()).getPush();
        if (push != null) {
            ((MainActivity) getActivity()).setPush(null);

            if (push.getType() == JABBER) {
                String friendJabberId = push.getJabberId();
                String friendName = push.getUserName();
                String yourJabberId = appPreferences.getJabberId();
                String yourName = appPreferences.getUserName();

                getChildFragmentManager().beginTransaction()
                        .replace(R.id.mail_messages_container_frameLayout, MailMessagesFragment.newInstance(yourName, yourJabberId, friendName, friendJabberId, push.getUserId())).commit();

            } else if (push.getType() == WEBRTC) {
                final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.mail_messages_container_frameLayout, WebRtcFragment.newInstance(String.valueOf(push.getRoom())));
                ft.commit();
            }
        }

        ListView contactsListView = (ListView) view.findViewById(R.id.mail_contacts_ListView);
        contactsListView.setAdapter(mMailContactsAdapter);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
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
        EditText mSearchFieldEditText = (EditText) view.findViewById(R.id.mail_fragment_search_editText);
        mSearchFieldEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMailContactsAdapter.getFilter().filter(s.toString());
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
            mCursor = database.rawQuery(selectQuery, null);
        } else {
            LOGE("pavel", search);
            mCursor = database.rawQuery("SELECT * FROM "
                    + DBHelper.MAIL_CONTACT_TABLE_NAME + " where " + "name_lc" + " like '%" + search
                    + "%'", null);

        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        //mCursor = new FilterCursorWrapper(mCursor, search, mCursor.getColumnIndex(DBHelper.MAIL_CONTACT_NAME));
        return mCursor;
    }

}
