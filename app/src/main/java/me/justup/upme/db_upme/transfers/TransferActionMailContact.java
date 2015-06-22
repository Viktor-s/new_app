package me.justup.upme.db_upme.transfers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.justup.upme.api_rpc.response_object.sub_object.Referral;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;
import me.justup.upme.entity.GetAllContactsResponse;

import static me.justup.upme.db.DBHelper.MAIL_CONTACT_DATE_ADD;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_IMG;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_IN_SYSTEM;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_JABBER_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_LATITUDE;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_LEVEL;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_LOGIN;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_LONGITUDE;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_NAME;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_NAME_LOWER_CASE;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_PARENT_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_PHONE;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_SERVER_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_TOTAL_SUM;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionMailContact implements SyncAdapterMetaData {
    private static final String TAG = TransferActionMailContact.class.getSimpleName();

    private Uri uriMailContact = null;
    private Uri uriOneMailContact = null;
    private Uri uriListMailContact = null;

    public TransferActionMailContact(CharSequence name) {
        uriMailContact = Uri.parse("content://" + MetaData.AUTHORITY_MAIL_CONTACT + "/" + MailContactConstance.URI_PATH2_MAIL_CONTACT_TABLE.replace("#", name));
        uriOneMailContact = Uri.parse("content://" + MetaData.AUTHORITY_MAIL_CONTACT + "/" + MailContactConstance.URI_PATH2_MAIL_CONTACT_TABLE.replace("#", name) + "/" + 2);
        uriListMailContact = Uri.parse("content://" + MetaData.AUTHORITY_MAIL_CONTACT + "/" + MailContactConstance.URI_PATH2_MAIL_CONTACT_TABLE.replace("#", name) + "/" + 1);
    }

    public void insertContact(Context context, Referral referral) {
        ContentValues contentValues = referralToContentValues(referral);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneMailContact, contentValues);
        }
    }

    public void insertContactList(Context context, ArrayList<Referral> referralList) {
        ContentValues[] contentValues = contactListToContentValues(referralList);

        if(contentValues!=null) {
            context.getContentResolver().bulkInsert(uriListMailContact, contentValues);
        }
    }

    public void insertContactListOld(Context context, List<GetAllContactsResponse.Result.Parents> referralList) {
        ContentValues[] contentValues = contactListToContentValuesOld(referralList);

        if(contentValues!=null) {
            context.getContentResolver().bulkInsert(uriListMailContact, contentValues);
        }
    }

    public Cursor getCursorOfMailContact(Context context){
        String sql = "SELECT * FROM " + MailContactConstance.TABLE_NAME_MAIL_CONTACT;

        return context.getContentResolver().query(uriMailContact, null, sql, null, null);
    }

    public Cursor getCursorOfMailContactWithStatus(Context context){
        String sql = "SELECT * FROM " + MailContactConstance.TABLE_NAME_MAIL_CONTACT + " WHERE " + MailContactConstance.MAIL_CONTACT_STATUS + " = 1 ";

        return context.getContentResolver().query(uriMailContact, null, sql, null, null);
    }

    public Cursor getCursorOfMailContactWithSearch(Context context, String search){
        String sql = "SELECT * FROM "
                + MailContactConstance.TABLE_NAME_MAIL_CONTACT + " where " + "name_lc" + " like '%" + search
                + "%'";

        return context.getContentResolver().query(uriMailContact, null, sql, null, null);
    }

    /**
     * Convert Referral to ContentValues
     */
    private ContentValues referralToContentValues(@NonNull Referral referral) {
        ContentValues cv = new ContentValues();

        cv.put(MAIL_CONTACT_SERVER_ID, referral.getId());
        cv.put(MAIL_CONTACT_PARENT_ID, referral.getParent_id());
        cv.put(MAIL_CONTACT_NAME, referral.getName());
        cv.put(MAIL_CONTACT_NAME_LOWER_CASE, referral.getName().toLowerCase());
        cv.put(MAIL_CONTACT_JABBER_ID, referral.getJabber_id());
        cv.put(MAIL_CONTACT_LOGIN, referral.getLogin());
        cv.put(MAIL_CONTACT_DATE_ADD, referral.getDateAdd());
        cv.put(MAIL_CONTACT_PHONE, referral.getPhone());
        cv.put(MAIL_CONTACT_IMG, referral.getImg());
        cv.put(MAIL_CONTACT_LATITUDE, referral.getLatitude());
        cv.put(MAIL_CONTACT_LONGITUDE, referral.getLongitude());
        cv.put(MAIL_CONTACT_LEVEL, referral.getLevel());
        cv.put(MAIL_CONTACT_IN_SYSTEM, referral.getIn_system());
        cv.put(MAIL_CONTACT_TOTAL_SUM, referral.getTotal_sum());

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ArrayList<Referral> to ContentValues[]
     */
    private ContentValues[] contactListToContentValues(@NonNull ArrayList<Referral> referralList) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(referralList.size());

        for (int i = 0; i < referralList.size(); i++) {
            // Get One Object
            Referral referral = referralList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(MAIL_CONTACT_SERVER_ID, referral.getId());
            cv.put(MAIL_CONTACT_PARENT_ID, referral.getParent_id());
            cv.put(MAIL_CONTACT_NAME, referral.getName());
            cv.put(MAIL_CONTACT_NAME_LOWER_CASE, referral.getName().toLowerCase());
            cv.put(MAIL_CONTACT_JABBER_ID, referral.getJabber_id());
            cv.put(MAIL_CONTACT_LOGIN, referral.getLogin());
            cv.put(MAIL_CONTACT_DATE_ADD, referral.getDateAdd());
            cv.put(MAIL_CONTACT_PHONE, referral.getPhone());
            cv.put(MAIL_CONTACT_IMG, referral.getImg());
            cv.put(MAIL_CONTACT_LATITUDE, referral.getLatitude());
            cv.put(MAIL_CONTACT_LONGITUDE, referral.getLongitude());
            cv.put(MAIL_CONTACT_LEVEL, referral.getLevel());
            cv.put(MAIL_CONTACT_IN_SYSTEM, referral.getIn_system());
            cv.put(MAIL_CONTACT_TOTAL_SUM, referral.getTotal_sum());

            // Add to List
            contentValuesArrayList.add(cv);
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }

    /**
     * Convert List<GetAllContactsResponse.Result.Parents> to ContentValues[]
     */
    private ContentValues[] contactListToContentValuesOld(@NonNull List<GetAllContactsResponse.Result.Parents> referralList) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(referralList.size());

        for (int i = 0; i < referralList.size(); i++) {
            // Get One Object
            GetAllContactsResponse.Result.Parents referral = referralList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(MAIL_CONTACT_SERVER_ID, referral.id);
            cv.put(MAIL_CONTACT_PARENT_ID, referral.parent_id);
            cv.put(MAIL_CONTACT_NAME, referral.name);
            cv.put(MAIL_CONTACT_NAME_LOWER_CASE, referral.name.toLowerCase());
            cv.put(MAIL_CONTACT_JABBER_ID, referral.jabber_id);
            cv.put(MAIL_CONTACT_LOGIN, referral.login);
            cv.put(MAIL_CONTACT_DATE_ADD, referral.dateAdd);
            cv.put(MAIL_CONTACT_PHONE, referral.phone);
            cv.put(MAIL_CONTACT_IMG, referral.img);
            cv.put(MAIL_CONTACT_LATITUDE, referral.latitude);
            cv.put(MAIL_CONTACT_LONGITUDE, referral.longitude);
            cv.put(MAIL_CONTACT_LEVEL, referral.level);
            cv.put(MAIL_CONTACT_IN_SYSTEM, referral.in_system);
            cv.put(MAIL_CONTACT_TOTAL_SUM, referral.total_sum);

            // Add to List
            contentValuesArrayList.add(cv);
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
