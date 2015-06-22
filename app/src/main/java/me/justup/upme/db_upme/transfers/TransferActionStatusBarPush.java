package me.justup.upme.db_upme.transfers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import me.justup.upme.api_rpc.response_object.PushObject;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;

import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_DATE;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_FILE_NAME;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_FORM_ID;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_ID;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_JABBER;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_LINK;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_PUSH_DESCRIPTION;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_ROOM;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_TYPE;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_USER_ID;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_USER_NAME;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;

public class TransferActionStatusBarPush implements SyncAdapterMetaData {
    private static final String TAG = TransferActionStatusBarPush.class.getSimpleName();

    private Uri uriStatusBarPush = null;
    private Uri uriOneStatusBarPush = null;
    private Uri uriListStatusBarPush = null;

    public TransferActionStatusBarPush(CharSequence name) {
        uriStatusBarPush = Uri.parse("content://" + MetaData.AUTHORITY_STATUS_BAR_PUSH + "/" + StatusBarPushConstance.URI_PATH2_STATUS_BAR_PUSH.replace("#", name));
        uriOneStatusBarPush = Uri.parse("content://" + MetaData.AUTHORITY_STATUS_BAR_PUSH + "/" + StatusBarPushConstance.URI_PATH2_STATUS_BAR_PUSH.replace("#", name) + "/" + 2);
        uriListStatusBarPush = Uri.parse("content://" + MetaData.AUTHORITY_STATUS_BAR_PUSH + "/" + StatusBarPushConstance.URI_PATH2_STATUS_BAR_PUSH.replace("#", name) + "/" + 1);
    }

    public void insertStatusBarPush(Context context, PushObject pushObject, String date) {
        ContentValues contentValues = pushObjectToContentValues(pushObject, date);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneStatusBarPush, contentValues);
        }
    }

    public void insertListStatusBarPush(Context context, ArrayList<PushObject> pushObjectArrayList, String date) {
        ContentValues[] contentValues = pushObjectListToContentValues(pushObjectArrayList, date);

        if(contentValues!=null) {
            context.getContentResolver().bulkInsert(uriListStatusBarPush, contentValues);
        }
    }

    public ArrayList<PushObject> getListPushObject(Activity activity) {
        ArrayList<PushObject> pushObjectArrayList = new ArrayList<>();

        // Show Column
        String[] columns = new String[]{
                STATUS_BAR_PUSH_TYPE,
                STATUS_BAR_PUSH_USER_ID,
                STATUS_BAR_PUSH_USER_NAME,
                STATUS_BAR_PUSH_DATE,
                STATUS_BAR_PUSH_LINK,
                STATUS_BAR_PUSH_JABBER,
                STATUS_BAR_PUSH_FILE_NAME,
                STATUS_BAR_PUSH_ROOM,
                STATUS_BAR_PUSH_FORM_ID,
                STATUS_BAR_PUSH_PUSH_DESCRIPTION};

        // Get Cursor
        Cursor cursor;
        try{
            cursor = activity.getContentResolver().query(uriListStatusBarPush, columns, null, null, "1", null);
        }catch (NullPointerException e){
            return null;
        }

        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                try {

                    PushObject pushObject = new PushObject();
                    pushObject.setId(cursor.getInt(cursor.getColumnIndex(STATUS_BAR_PUSH_ID)));
                    pushObject.setType(cursor.getInt(cursor.getColumnIndex(STATUS_BAR_PUSH_TYPE)));
                    pushObject.setUserId(cursor.getInt(cursor.getColumnIndex(STATUS_BAR_PUSH_USER_ID)));
                    pushObject.setUserName(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_USER_NAME)));
                    pushObject.setDate(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_DATE)));
                    pushObject.setLink(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_LINK)));
                    pushObject.setJabberId(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_JABBER)));
                    pushObject.setFileName(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_FILE_NAME)));
                    pushObject.setRoom(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_ROOM)));
                    pushObject.setFormId(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_FORM_ID)));
                    pushObject.setPushDescription(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_PUSH_DESCRIPTION)));

                    pushObjectArrayList.add(pushObject);

                } catch (Exception e) {
                    LOGE(TAG, e.getMessage());

                    return null;
                }

                cursor.moveToNext();
            }
        }

        cursor.close();

        return pushObjectArrayList;
    }

    public void deleteOnePush(Activity activity, int pushId) {
        activity.getContentResolver().delete(uriStatusBarPush, StatusBarPushConstance.STATUS_BAR_PUSH_ID + " = " + pushId, null);
    }

    public void deleteAllPush(Activity activity) {
        activity.getContentResolver().delete(uriStatusBarPush, null, null);
    }

    /**
     * Convert PushObject to ContentValues
     */
    public ContentValues pushObjectToContentValues(@NonNull PushObject pushObject, String date) {
        ContentValues cv = new ContentValues();

        cv.put(STATUS_BAR_PUSH_TYPE, pushObject.getType());
        cv.put(STATUS_BAR_PUSH_USER_ID, pushObject.getUserId());

        if (pushObject.getUserName() != null) {
            cv.put(STATUS_BAR_PUSH_USER_NAME, pushObject.getUserName());
        } else {
            cv.put(STATUS_BAR_PUSH_USER_NAME, EMPTY_VALUE);
        }

        if(date!=null) {
            cv.put(STATUS_BAR_PUSH_DATE, date);
        }

        if (pushObject.getLink() != null) {
            cv.put(STATUS_BAR_PUSH_LINK, pushObject.getLink());
        } else {
            cv.put(STATUS_BAR_PUSH_LINK, EMPTY_VALUE);
        }

        if (pushObject.getJabberId() != null) {
            cv.put(STATUS_BAR_PUSH_JABBER, pushObject.getJabberId());
        } else {
            cv.put(STATUS_BAR_PUSH_JABBER, EMPTY_VALUE);
        }

        if (pushObject.getFileName() != null) {
            cv.put(STATUS_BAR_PUSH_FILE_NAME, pushObject.getFileName());
        } else {
            cv.put(STATUS_BAR_PUSH_FILE_NAME, EMPTY_VALUE);
        }

        if (pushObject.getRoom() != null) {
            cv.put(STATUS_BAR_PUSH_ROOM, pushObject.getRoom());
        } else {
            cv.put(STATUS_BAR_PUSH_ROOM, EMPTY_VALUE);
        }

        if (pushObject.getFormId() != null) {
            cv.put(STATUS_BAR_PUSH_FORM_ID, pushObject.getFormId());
        } else {
            cv.put(STATUS_BAR_PUSH_FORM_ID, EMPTY_VALUE);
        }

        if (pushObject.getPushDescription() != null) {
            cv.put(STATUS_BAR_PUSH_PUSH_DESCRIPTION, pushObject.getPushDescription());
        } else {
            cv.put(STATUS_BAR_PUSH_PUSH_DESCRIPTION, EMPTY_VALUE);
        }

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ArrayList<PushObject> to ContentValues[]
     */
    public ContentValues[] pushObjectListToContentValues(@NonNull ArrayList<PushObject> pushObjectArrayList, String date) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(pushObjectArrayList.size());

        for (int i = 0; i < pushObjectArrayList.size(); i++) {
            // Get One Object
            PushObject pushObject = pushObjectArrayList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(STATUS_BAR_PUSH_TYPE, pushObject.getType());
            cv.put(STATUS_BAR_PUSH_USER_ID, pushObject.getUserId());

            if (pushObject.getUserName() != null) {
                cv.put(STATUS_BAR_PUSH_USER_NAME, pushObject.getUserName());
            } else {
                cv.put(STATUS_BAR_PUSH_USER_NAME, EMPTY_VALUE);
            }

            if(date!=null) {
                cv.put(STATUS_BAR_PUSH_DATE, date);
            }

            if (pushObject.getLink() != null) {
                cv.put(STATUS_BAR_PUSH_LINK, pushObject.getLink());
            } else {
                cv.put(STATUS_BAR_PUSH_LINK, EMPTY_VALUE);
            }

            if (pushObject.getJabberId() != null) {
                cv.put(STATUS_BAR_PUSH_JABBER, pushObject.getJabberId());
            } else {
                cv.put(STATUS_BAR_PUSH_JABBER, EMPTY_VALUE);
            }

            if (pushObject.getFileName() != null) {
                cv.put(STATUS_BAR_PUSH_FILE_NAME, pushObject.getFileName());
            } else {
                cv.put(STATUS_BAR_PUSH_FILE_NAME, EMPTY_VALUE);
            }

            if (pushObject.getRoom() != null) {
                cv.put(STATUS_BAR_PUSH_ROOM, pushObject.getRoom());
            } else {
                cv.put(STATUS_BAR_PUSH_ROOM, EMPTY_VALUE);
            }

            if (pushObject.getFormId() != null) {
                cv.put(STATUS_BAR_PUSH_FORM_ID, pushObject.getFormId());
            } else {
                cv.put(STATUS_BAR_PUSH_FORM_ID, EMPTY_VALUE);
            }

            if (pushObject.getPushDescription() != null) {
                cv.put(STATUS_BAR_PUSH_PUSH_DESCRIPTION, pushObject.getPushDescription());
            } else {
                cv.put(STATUS_BAR_PUSH_PUSH_DESCRIPTION, EMPTY_VALUE);
            }

            // Add to List
            contentValuesArrayList.add(cv);
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
