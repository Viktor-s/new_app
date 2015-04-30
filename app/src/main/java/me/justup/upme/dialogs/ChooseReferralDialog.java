package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.JustUpApplication;
import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.PersonBriefcaseEntity;
import me.justup.upme.fragments.CalendarFragment;
import me.justup.upme.utils.AppPreferences;
import me.justup.upme.utils.BackAwareEditText;

import static me.justup.upme.db.DBHelper.MAIL_CONTACT_IMG;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_NAME;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_PARENT_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_SERVER_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_TABLE_NAME;


public class ChooseReferralDialog extends DialogFragment { // ChooseReferralDialog

    public static final String CHOOSE_REFERRAL = "choose_referral";

//    private SQLiteDatabase database;
//    private List<PersonBriefcaseEntity> listPerson;

    ChooseReferralAdapter chooseReferralAdapter;

    List<PersonBriefcaseEntityExtend> listPerson;
    List<PersonBriefcaseEntityExtend> searchListPerson;


    ArrayList<Integer> listChooseReferralId;

    public static ChooseReferralDialog newInstance(ArrayList<Integer> listIdPerson) {

        Bundle args = new Bundle();
        args.putIntegerArrayList(CHOOSE_REFERRAL, listIdPerson);
        ChooseReferralDialog fragment = new ChooseReferralDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int currentUserId = new AppPreferences(JustUpApplication.getApplication().getApplicationContext()).getUserId();

        SQLiteDatabase database = DBAdapter.getInstance().openDatabase();
        String selectQuery = "SELECT * FROM " + MAIL_CONTACT_TABLE_NAME;
        Cursor mCursor = database.rawQuery(selectQuery, null);
        listPerson = fillPersonsFromCursor(mCursor);

        Log.d("TAG2", listPerson.toString());

        listChooseReferralId = getArguments().getIntegerArrayList(CHOOSE_REFERRAL);
        Log.d("TAG1", listChooseReferralId.toString());
        for (Integer i : listChooseReferralId) {
            for (PersonBriefcaseEntityExtend person : listPerson)
                if (person.getId() == i)
                    person.setSelect(true);
        }
        listChooseReferralId.clear();
        searchListPerson = new ArrayList<>(listPerson);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_choose_referral, null);
        builder.setView(dialogView);
        builder.setTitle(R.string.select_people);
        builder.setCancelable(false);

        final ListView mListviewReferrals = (ListView) dialogView.findViewById(R.id.listview_referrals);
        chooseReferralAdapter = new ChooseReferralAdapter(getActivity(), searchListPerson);
        mListviewReferrals.setAdapter(chooseReferralAdapter);

        final BackAwareEditText mUserName = (BackAwareEditText) dialogView.findViewById(R.id.search_people_by_name);
        mUserName.setBackPressedListener(new BackAwareEditText.BackPressedListener() {
            @Override
            public void onImeBack(BackAwareEditText editText) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideNavBar();
                }
            }
        });

        mUserName.addTextChangedListener(new TextWatcher() {
                                             @Override
                                             public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                             @Override
                                             public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                                 String searchString = mUserName.getText().toString();
                                                 int textLength = searchString.length();
                                                 if (searchListPerson != null) {
                                                     searchListPerson.clear();
                                                     for (int i = 0; i < listPerson.size(); i++) {
                                                         String teamName = listPerson.get(i).getName();
                                                         if (textLength <= teamName.length()) {
                                                             if (teamName.toLowerCase().contains(searchString.toLowerCase())) {
                                                                 searchListPerson.add(listPerson.get(i));
                                                             }
                                                         }
                                                     }
                                                     chooseReferralAdapter.notifyDataSetChanged();
                                                 }
                                             }

                                             @Override
                                             public void afterTextChanged(Editable s) {}
                                         }
        );

        builder.setPositiveButton(R.string.button_select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                for (PersonBriefcaseEntityExtend person : listPerson)
                    if (person.isSelect())
                        listChooseReferralId.add(person.getId());
                Log.d("TAG1_", "-------------------------------------------");
                Log.d("TAG1_listPerson", listPerson.toString());
                Log.d("TAG1_searchListPerson", searchListPerson.toString());
                Log.d("TAG1_listChoose", listChooseReferralId.toString());
                ((CalendarFragment) getParentFragment()).setPersonIdForNewEvent(listChooseReferralId);
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    private List<PersonBriefcaseEntityExtend> fillPersonsFromCursor(Cursor cursorPersons) {
        ArrayList<PersonBriefcaseEntityExtend> personsList = new ArrayList<>();
        for (cursorPersons.moveToFirst(); !cursorPersons.isAfterLast(); cursorPersons.moveToNext()) {
            PersonBriefcaseEntityExtend personBriefcaseEntity = new PersonBriefcaseEntityExtend();
            personBriefcaseEntity.setId(cursorPersons.getInt(cursorPersons.getColumnIndex(MAIL_CONTACT_SERVER_ID)));
            personBriefcaseEntity.setParentId(cursorPersons.getInt(cursorPersons.getColumnIndex(MAIL_CONTACT_PARENT_ID)));
            personBriefcaseEntity.setName(cursorPersons.getString(cursorPersons.getColumnIndex(MAIL_CONTACT_NAME)));
            personBriefcaseEntity.setPhoto(cursorPersons.getString(cursorPersons.getColumnIndex(MAIL_CONTACT_IMG)));
            personsList.add(personBriefcaseEntity);
        }
        if (cursorPersons != null) {
            cursorPersons.close();
        }
        return personsList;
    }

    public class ChooseReferralAdapter extends ArrayAdapter<PersonBriefcaseEntityExtend> {
        private final Activity context;
        private final List<PersonBriefcaseEntityExtend> listReferrals;

        CompoundButton buttonViewGlobal;

        class ViewHolder {
            public TextView text;
            public CheckBox checkbox;
            public ImageView image;
        }

        public ChooseReferralAdapter(Activity context, List<PersonBriefcaseEntityExtend> listReferrals) {
            super(context, R.layout.rowlayout_choose_referral, listReferrals);
            this.context = context;
            this.listReferrals = listReferrals;
        }

        PersonBriefcaseEntityExtend personBriefcaseEntityExtend;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.rowlayout_choose_referral, null);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.referral_item_text_view);
                viewHolder.image = (ImageView) rowView.findViewById(R.id.referral_item_image);

                viewHolder.checkbox = (CheckBox) rowView.findViewById(R.id.referral_checkbox);
                viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        personBriefcaseEntityExtend = (PersonBriefcaseEntityExtend) viewHolder.checkbox.getTag();
                        buttonViewGlobal = buttonView;
                        personBriefcaseEntityExtend.setSelect(buttonViewGlobal.isChecked());
                    }
                });
                rowView.setTag(viewHolder);
                viewHolder.checkbox.setTag(listReferrals.get(position));
            } else {
                rowView = convertView;
                ((ViewHolder) rowView.getTag()).checkbox.setTag(listReferrals.get(position));
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            PersonBriefcaseEntityExtend personB = listReferrals.get(position);

            holder.text.setText(personB.getName());
            holder.checkbox.setChecked(personB.isSelect());

//            String LOGO_URL = (UserSyncInfo.getsUserSyncInfo() != null) ? UserSyncInfo.getsUserSyncInfo().getLogoUrl() : "";
//            Picasso.with(context).load(LOGO_URL + game.getTeamLogo1()).placeholder(R.drawable.no_logo).into(holder.gameListItemImageViewTeam1Logo);
//            Picasso.with(context).load(LOGO_URL + game.getTeamLogo2()).placeholder(R.drawable.no_logo).into(holder.gameListItemImageViewTeam2Logo);

            return rowView;
        }

    }

    private class PersonBriefcaseEntityExtend extends PersonBriefcaseEntity {

        private PersonBriefcaseEntityExtend() {
        }

        private PersonBriefcaseEntityExtend(int id, int parent_id, String name, String photo, boolean select) {
            super(id, parent_id, name, photo);
            this.select = select;
        }

        private boolean select;

        public boolean isSelect() {
            return select;
        }

        public void setSelect(boolean select) {
            this.select = select;
        }

        @Override
        public String toString() {
            return "PersonBriefcaseEntityExtend{" + super.toString() +
                    "select=" + select +
                    '}';
        }
    }

}
