package me.justup.upme.db_upme.transfers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import me.justup.upme.api_rpc.response_object.CalendarObject;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;
import me.justup.upme.entity.CalendarGetEventsResponse;

import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_DESCRIPTION;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_END_DATETIME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_LOCATION;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_NAME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_OWNER_ID;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_SERVER_ID;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_SHARED_WITH;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_START_DATETIME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_TYPE;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionEventCalendar implements SyncAdapterMetaData {
    private static final String TAG = TransferActionEventCalendar.class.getSimpleName();

    private Uri uriEventCalendar = null;
    private Uri uriOneEventCalendar = null;
    private Uri uriListEventCalendar = null;

    public TransferActionEventCalendar(CharSequence name) {
        uriEventCalendar = Uri.parse("content://" + MetaData.AUTHORITY_EVENT_CALENDAR + "/" + EventCalendarConstance.URI_PATH2_EVENT_CALENDAR.replace("#", name));
        uriOneEventCalendar = Uri.parse("content://" + MetaData.AUTHORITY_EVENT_CALENDAR + "/" + EventCalendarConstance.URI_PATH2_EVENT_CALENDAR.replace("#", name) + "/" + 2);
        uriListEventCalendar = Uri.parse("content://" + MetaData.AUTHORITY_EVENT_CALENDAR + "/" + EventCalendarConstance.URI_PATH2_EVENT_CALENDAR.replace("#", name) + "/" + 1);
    }

    public void insertEventCalendar(Context context, CalendarObject calendarObject) {
        ContentValues contentValues = eventToContentValues(calendarObject);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneEventCalendar, contentValues);
        }
    }

    public void insertEventCalendarOld(Context context, CalendarGetEventsResponse.Result calendarObject) {
        ContentValues contentValues = eventToContentValuesOld(calendarObject);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneEventCalendar, contentValues);
        }
    }

    public void insertEventCalendarList(Context context, ArrayList<CalendarObject> calendarObjectArrayList) {
        ContentValues[] contentValues = contactListToContentValues(calendarObjectArrayList);

        if(contentValues!=null) {
            context.getContentResolver().bulkInsert(uriListEventCalendar, contentValues);
        }
    }

    public Cursor getCursorEventCalendarWithTimeStamp(Context context, String startTime, String endTime){
        String sql = "SELECT * FROM " + EventCalendarConstance.TABLE_NAME_EVENT_CALENDAR + " WHERE start_datetime <= " + endTime + " AND end_datetime >=" + startTime;

        return context.getContentResolver().query(uriEventCalendar, null, sql, null, null);
    }

    public int deleteCalendarEvent(Context context, long serverId){
        return context.getContentResolver().delete(uriEventCalendar, "server_id = " + serverId, null);
    }

    /**
     * Convert CalendarGetEventsResponse.Result to ContentValues
     */
    public ContentValues eventToContentValuesOld(@NonNull CalendarGetEventsResponse.Result calendarObject) {
        ContentValues cv = new ContentValues();

        cv.put(EVENT_CALENDAR_SERVER_ID, calendarObject.id);
        cv.put(EVENT_CALENDAR_NAME, calendarObject.name);
        cv.put(EVENT_CALENDAR_DESCRIPTION, calendarObject.description);
        cv.put(EVENT_CALENDAR_TYPE, calendarObject.type);
        cv.put(EVENT_CALENDAR_OWNER_ID, calendarObject.owner_id);
        cv.put(EVENT_CALENDAR_START_DATETIME, calendarObject.start_datetime);
        cv.put(EVENT_CALENDAR_END_DATETIME, calendarObject.end_datetime);
        cv.put(EVENT_CALENDAR_LOCATION, calendarObject.location);

        if (calendarObject.shared_with != null) {
            String strSharedWith = calendarObject.shared_with.toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
            cv.put(EVENT_CALENDAR_SHARED_WITH, strSharedWith);
        }

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert CalendarObject to ContentValues
     */
    public ContentValues eventToContentValues(@NonNull CalendarObject calendarObject) {
        ContentValues cv = new ContentValues();

        cv.put(EVENT_CALENDAR_SERVER_ID, calendarObject.getId());
        cv.put(EVENT_CALENDAR_NAME, calendarObject.getName());
        cv.put(EVENT_CALENDAR_DESCRIPTION, calendarObject.getDescription());
        cv.put(EVENT_CALENDAR_TYPE, calendarObject.getType());
        cv.put(EVENT_CALENDAR_OWNER_ID, calendarObject.getOwner_id());
        cv.put(EVENT_CALENDAR_START_DATETIME, calendarObject.getStart_datetime());
        cv.put(EVENT_CALENDAR_END_DATETIME, calendarObject.getEnd_datetime());
        cv.put(EVENT_CALENDAR_LOCATION, calendarObject.getLocation());

        if (calendarObject.getShared_with() != null) {
            String strSharedWith = calendarObject.getShared_with().toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
            cv.put(EVENT_CALENDAR_SHARED_WITH, strSharedWith);
        }

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ArrayList<CalendarObject> to ContentValues[]
     */
    public ContentValues[] contactListToContentValues(@NonNull ArrayList<CalendarObject> calendarObjectArrayList) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(calendarObjectArrayList.size());

        for (int i = 0; i < calendarObjectArrayList.size(); i++) {
            // Get One Object
            CalendarObject calendarObject = calendarObjectArrayList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(EVENT_CALENDAR_SERVER_ID, calendarObject.getId());
            cv.put(EVENT_CALENDAR_NAME, calendarObject.getName());
            cv.put(EVENT_CALENDAR_DESCRIPTION, calendarObject.getDescription());
            cv.put(EVENT_CALENDAR_TYPE, calendarObject.getType());
            cv.put(EVENT_CALENDAR_OWNER_ID, calendarObject.getOwner_id());
            cv.put(EVENT_CALENDAR_START_DATETIME, calendarObject.getStart_datetime());
            cv.put(EVENT_CALENDAR_END_DATETIME, calendarObject.getEnd_datetime());
            cv.put(EVENT_CALENDAR_LOCATION, calendarObject.getLocation());

            if (calendarObject.getShared_with() != null) {
                String strSharedWith = calendarObject.getShared_with().toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
                cv.put(EVENT_CALENDAR_SHARED_WITH, strSharedWith);
            }

            // Add to List
            contentValuesArrayList.add(cv);
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
