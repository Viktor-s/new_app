package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.PersonBriefcaseEntity;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppPreferences;

import static me.justup.upme.db.DBHelper.MAIL_CONTACT_IMG;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_NAME;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_PARENT_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_SERVER_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_TABLE_NAME;


public class ChooseReferralDialog extends DialogFragment { // ChooseReferralDialog

    public static final String CHOOSE_REFERRAL = "choose_referral";

//    private SQLiteDatabase database;
//    private List<PersonBriefcaseEntity> listPerson;


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

        SQLiteDatabase database = DBAdapter.getInstance().openDatabase();
        String selectQuery = "SELECT * FROM " + MAIL_CONTACT_TABLE_NAME;
        Cursor mCursor = database.rawQuery(selectQuery, null);
        List<PersonBriefcaseEntity> listPerson = fillPersonsFromCursor(mCursor);

        ArrayList<Integer> listChooseReferralId  = getArguments().getIntegerArrayList(CHOOSE_REFERRAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_choose_referral, null);

//        setCancelable(false);
//
//        TextView mUserName = (TextView) dialogView.findViewById(R.id.break_call_user_name);
//        mUserName.setText(userName);
//
//        builder.setView(dialogView).setTitle(R.string.dialog_video_call)
//                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        getParentFragment();
//                        dialog.dismiss();
//                    }
//                });

        return builder.create();
    }

    private List<PersonBriefcaseEntity> fillPersonsFromCursor(Cursor cursorPersons) {
        ArrayList<PersonBriefcaseEntity> personsList = new ArrayList<>();
        AppPreferences appPreferences = new AppPreferences(AppContext.getAppContext());
        int userId = appPreferences.getUserId();
        String userName = appPreferences.getUserName();
        PersonBriefcaseEntity personBriefcaseEntityUser = new PersonBriefcaseEntity(userId, 0, userName, " ");
        personsList.add(personBriefcaseEntityUser);
        for (cursorPersons.moveToFirst(); !cursorPersons.isAfterLast(); cursorPersons.moveToNext()) {
            PersonBriefcaseEntity personBriefcaseEntity = new PersonBriefcaseEntity();
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

}
