package me.justup.upme.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import me.justup.upme.R;
import me.justup.upme.adapter.MailContactsAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.db.DBHelper;
import me.justup.upme.entity.SendNotificationQuery;
import me.justup.upme.services.PushIntentService;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppPreferences;

import static me.justup.upme.db.DBHelper.MAIL_CONTACT_TABLE_NAME;


public class MailFragment extends Fragment {
    private DBAdapter mDBAdapter;
    private int lastChosenPosition = -1;
    private DBHelper mDBHelper;
    private MailContactsAdapter mMailContactsAdapter;
    private String selectQuery;
    private BroadcastReceiver receiver;

    public static final int JABBER = 1;
    public static final int WEBRTC = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBHelper = new DBHelper(AppContext.getAppContext());
        mDBAdapter = new DBAdapter(AppContext.getAppContext());
        mDBAdapter.open();
        selectQuery = "SELECT * FROM " + MAIL_CONTACT_TABLE_NAME;
        Cursor cursor = mDBHelper.getWritableDatabase().rawQuery(selectQuery, null);
        mMailContactsAdapter = new MailContactsAdapter(this, AppContext.getAppContext(), cursor, 0);
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(MailFragment.this.getActivity()).unregisterReceiver(receiver);
        mDBAdapter.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Cursor cursor = mDBHelper.getWritableDatabase().rawQuery(selectQuery, null);
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

        ListView contactsListView = (ListView) view.findViewById(R.id.mail_contacts_ListView);
        contactsListView.setAdapter(mMailContactsAdapter);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (lastChosenPosition != position) {
                    String friendName = mMailContactsAdapter.getCursor().getString(mMailContactsAdapter.getCursor().getColumnIndex(DBHelper.MAIL_CONTACT_NAME));
                    String yourName = new AppPreferences(AppContext.getAppContext()).getUserName();
                    int userId = mMailContactsAdapter.getCursor().getInt(mMailContactsAdapter.getCursor().getColumnIndex(DBHelper.MAIL_CONTACT_SERVER_ID));

                    AppPreferences appPreferences = new AppPreferences(getActivity());
                    int ownerId = appPreferences.getUserId();
                    String ownerName = appPreferences.getUserName();

                    startNotificationIntent(userId, ownerId, ownerName, JABBER, 0);

                    final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.mail_messages_container_frameLayout, MailMessagesFragment.newInstance(yourName, friendName));
                    ft.commit();
                    lastChosenPosition = position;
                }
            }
        });
        return view;
    }

    public void startNotificationIntent(int userId, int ownerId, String ownerName, int connectionType, int roomNumber) {
        SendNotificationQuery push = new SendNotificationQuery();
        push.params.user_id = userId;
        push.params.data.owner_id = ownerId;
        push.params.data.owner_name = ownerName;
        push.params.data.connection_type = connectionType;
        push.params.data.room = roomNumber;

        Bundle bundle = new Bundle();
        bundle.putSerializable(PushIntentService.PUSH_INTENT_QUERY_EXTRA, push);

        Intent intent = new Intent(getActivity(), PushIntentService.class);
        getActivity().startService(intent.putExtras(bundle));
    }

}
