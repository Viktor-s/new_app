package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import me.justup.upme.R;
import me.justup.upme.adapter.MailContactsAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.UserEntity;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class MailFragment extends Fragment {
    private static final String TAG = makeLogTag(MailFragment.class);
    private DBAdapter mDBAdapter;
    private UserEntity mUserEntity;

    private ListView contactsListView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBAdapter = new DBAdapter(AppContext.getAppContext());
        mDBAdapter.open();
        mUserEntity = mDBAdapter.getUserEntity();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDBAdapter.close();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail, container, false);
        contactsListView = (ListView) view.findViewById(R.id.mail_contacts_ListView);
        contactsListView.setAdapter(new MailContactsAdapter(AppContext.getAppContext(), mUserEntity.getmContactEntityList()));
        return view;
    }

}
