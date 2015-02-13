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
import android.widget.RadioGroup;

import me.justup.upme.R;
import me.justup.upme.adapter.MailContactsAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.db.DBHelper;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.db.DBHelper.MAIL_CONTACT_TABLE_NAME;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class MailFragment extends Fragment {
    private static final String TAG = makeLogTag(MailFragment.class);
    private DBAdapter mDBAdapter;
    private ListView contactsListView;
    private int lastChosenPosition = -1;
    private DBHelper mDBHelper;
    private MailContactsAdapter mMailContactsAdapter;
    private String selectQuery;

    private String yourName = "error";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBHelper = new DBHelper(AppContext.getAppContext());
        mDBAdapter = new DBAdapter(AppContext.getAppContext());
        mDBAdapter.open();
        selectQuery = "SELECT * FROM " + MAIL_CONTACT_TABLE_NAME;
        Cursor cursor = mDBHelper.getWritableDatabase().rawQuery(selectQuery, null);
        mMailContactsAdapter = new MailContactsAdapter(AppContext.getAppContext(), cursor, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDBAdapter.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail, container, false);


        RadioGroup radiogroup = (RadioGroup) view.findViewById(R.id.radioGroup1);
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.user_radioButton:
                        yourName = "test-user-01";
                        break;
                    case R.id.admin_radioButton:
                        yourName = "jab-admin";
                        break;
                    default:
                        break;
                }
                LOGI(TAG, yourName);
            }
        });


        contactsListView = (ListView) view.findViewById(R.id.mail_contacts_ListView);
        contactsListView.setAdapter(mMailContactsAdapter);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (lastChosenPosition != position) {
                    String userName = mMailContactsAdapter.getCursor().getString(mMailContactsAdapter.getCursor().getColumnIndex(DBHelper.MAIL_CONTACT_NAME));

                    final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.mail_messages_container_frameLayout, MailMessagesFragment.newInstance(yourName, userName));
                    ft.commit();
                    lastChosenPosition = position;
                }
            }
        });
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Cursor cursor = mDBHelper.getWritableDatabase().rawQuery(selectQuery, null);
                mMailContactsAdapter.changeCursor(cursor);
                mMailContactsAdapter.notifyDataSetChanged();
            }
        };
        LocalBroadcastManager.getInstance(MailFragment.this.getActivity())
                .registerReceiver(receiver, new IntentFilter(DBAdapter.SQL_BROADCAST_INTENT));
        return view;
    }

}
