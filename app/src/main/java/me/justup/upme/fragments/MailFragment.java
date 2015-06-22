package me.justup.upme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import me.justup.upme.JustUpApplication;
import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.adapter.MailContactsAdapter;
import me.justup.upme.api_rpc.response_object.PushObject;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.db.DBHelper;
import me.justup.upme.entity.StartChatQuery;
import me.justup.upme.services.PushIntentService;
import me.justup.upme.utils.BackAwareEditText;
import me.justup.upme.utils.CommonUtils;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class MailFragment extends Fragment {
    private static final String TAG = makeLogTag(MailFragment.class);

    private int mLastChosenPosition = -1;
    private MailContactsAdapter mMailContactsAdapter = null;
    private BackAwareEditText mSearchFieldEditText = null;

    public static final int JABBER = 1;
    public static final int WEBRTC = 2;
    public static final int FILE = 3;
    public static final int BREAK_CALL = 4;
    public static final int CALENDAR_NEW_EVENT = 5;
    public static final int ORDER_FORM = 6;
    public static final int ORDER_INFO = 7;

    private RelativeLayout mContactsContainer = null;
    private FrameLayout mChatContainer = null;

    private View mContentView = null;

    // Instance
    public static MailFragment newInstance() {
        return new MailFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mMailContactsAdapter = new MailContactsAdapter(this, activity.getApplicationContext(), JustUpApplication.getApplication().getTransferActionMailContact().getCursorOfMailContactWithStatus(getActivity().getApplicationContext()), 0);
        mMailContactsAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return fetchContactsByName(constraint.toString().toLowerCase());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_mail, container, false);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init UI
        if (getActivity() != null) {
            initUI();
        }
    }

    private void initUI(){
        mContactsContainer = (RelativeLayout) mContentView.findViewById(R.id.chat_contacts_container);
        mChatContainer = (FrameLayout) mContentView.findViewById(R.id.mail_messages_container_frameLayout);

        final PushObject push = ((MainActivity) getActivity()).getPush();
        if (push != null) {

            if (push.getType() == JABBER) {
                String friendJabberId = push.getJabberId();
                String friendName = push.getUserName();
                String yourJabberId = JustUpApplication.getApplication().getAppPreferences().getJabberId();
                String yourName = JustUpApplication.getApplication().getAppPreferences().getUserName();
                LOGI(TAG, "Mail Fragment : friendJabberId : " + friendJabberId + ", friendName : " + friendName + ", yourJabberId : " + yourJabberId + ", yourName : " + yourName);
                resizeContacts(true);

                getChildFragmentManager().beginTransaction().replace(R.id.mail_messages_container_frameLayout, MailMessagesFragment.newInstance(yourName, yourJabberId, friendName, friendJabberId, push.getUserId())).commit();
            } else if (push.getType() == WEBRTC) {
                ((MainActivity) getActivity()).prepareAndCallRTC(String.valueOf(push.getRoom()), false, false, 0, 0, "");
            }

            ((MainActivity) getActivity()).setPush(null);
        }

        ListView contactsListView = (ListView) mContentView.findViewById(R.id.mail_contacts_ListView);
        contactsListView.setAdapter(mMailContactsAdapter);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i = 0; i < adapterView.getChildCount(); i++) {
                    adapterView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }

                view.setBackgroundColor(Color.LTGRAY);

                CommonUtils.hideKeyboard(getActivity());

                if (mLastChosenPosition != position) {
                    String friendJabberId = mMailContactsAdapter.getCursor().getString(mMailContactsAdapter.getCursor().getColumnIndex(DBHelper.MAIL_CONTACT_JABBER_ID));
                    String friendName = mMailContactsAdapter.getCursor().getString(mMailContactsAdapter.getCursor().getColumnIndex(DBHelper.MAIL_CONTACT_NAME));
                    String yourJabberId = JustUpApplication.getApplication().getAppPreferences().getJabberId();
                    String yourName = JustUpApplication.getApplication().getAppPreferences().getUserName();
                    int userId = mMailContactsAdapter.getCursor().getInt(mMailContactsAdapter.getCursor().getColumnIndex(DBHelper.MAIL_CONTACT_SERVER_ID));

                    startNotificationIntent(userId);
                    resizeContacts(true);

                    final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.mail_messages_container_frameLayout, MailMessagesFragment.newInstance(yourName, yourJabberId, friendName, friendJabberId, userId));
                    ft.commit();
                    mLastChosenPosition = position;
                }
            }
        });

        mSearchFieldEditText = (BackAwareEditText) mContentView.findViewById(R.id.mail_fragment_search_edt);
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
                mMailContactsAdapter.getFilter().filter(s.toString());
            }

        });

        ImageButton clearEditTextButton = (ImageButton) mContentView.findViewById(R.id.clear_search_text_button);
        clearEditTextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSearchFieldEditText.setText("");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                LOGI(TAG, "OnReceive MailFragment");

                try {
                    // mCursor = mDatabase.rawQuery(mSelectQuery, null);
                } catch (IllegalStateException e) {
                    LOGE(TAG, "Attempt to re-open an already-closed object: SQLiteDatabase: /data/data/me.justup.upme/databases/upme.db : \n" + e.getMessage());
                }

                // mMailContactsAdapter = new MailContactsAdapter(this, getActivity().getApplicationContext(), mCursor, 0);
            }
        };

        LocalBroadcastManager.getInstance(MailFragment.this.getActivity()).registerReceiver(mReceiver, new IntentFilter(DBAdapter.MAIL_SQL_BROADCAST_INTENT));
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            LocalBroadcastManager.getInstance(MailFragment.this.getActivity()).unregisterReceiver(mReceiver);
        } catch (Exception e) {
            LOGE(TAG, "UnregisterReceiver(mReceiver)", e);
            mReceiver = null;
        }
    }

    @Override
    public void onDestroy() {
        if (mMailContactsAdapter.getCursor() != null) {
            mMailContactsAdapter.getCursor().close();
        }

        super.onDestroy();
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
            mCursor = JustUpApplication.getApplication().getTransferActionMailContact().getCursorOfMailContact(getActivity().getApplicationContext());
        } else {
            LOGE(TAG, search);
            mCursor = JustUpApplication.getApplication().getTransferActionMailContact().getCursorOfMailContactWithSearch(getActivity().getApplicationContext(), search);

        }

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        // mCursor = new FilterCursorWrapper(mCursor, search, mCursor.getColumnIndex(DBHelper.MAIL_CONTACT_NAME));
        return mCursor;
    }

    public void resizeContacts(boolean isChatVisible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mContactsContainer.getLayoutParams();

        if (isChatVisible) {
            params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics());
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;

            mContactsContainer.setLayoutParams(params);
            mChatContainer.setVisibility(View.VISIBLE);
        } else {
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;

            mContactsContainer.setLayoutParams(params);
            mChatContainer.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mMailContactsAdapter.changeCursor(JustUpApplication.getApplication().getTransferActionMailContact().getCursorOfMailContactWithStatus(getActivity().getApplicationContext()));
            mMailContactsAdapter.notifyDataSetChanged();
        }
    };

}
