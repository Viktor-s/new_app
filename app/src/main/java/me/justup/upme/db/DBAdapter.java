package me.justup.upme.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

import me.justup.upme.entity.ArticleFullResponse;
import me.justup.upme.entity.ArticlesGetShortDescriptionResponse;
import me.justup.upme.entity.CommentsArticleFullResponse;
import me.justup.upme.entity.CalendarGetEventsResponse;
import me.justup.upme.entity.GetMailContactResponse;
import me.justup.upme.entity.Push;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.db.DBHelper.*;

import static me.justup.upme.utils.LogUtils.makeLogTag;

/**
 * <b>Use:</b>:
 * <p/>
 * private DBAdapter mDBAdapter;
 * <p/>
 * onCreate()<br>
 * mDBAdapter = new DBAdapter(this);<br>
 * mDBAdapter.open();
 * <p/>
 * long saveTimer = mDBAdapter.openTimer(projectId);
 * <p/>
 * onPause()<br>
 * mDBAdapter.close();
 */
public class DBAdapter {
    private static final String TAG = makeLogTag(DBAdapter.class);
    public static final String NEWS_FEED_SQL_BROADCAST_INTENT = "sql_news_feed_broadcast_intent";
    public static final String NEWS_ITEM_SQL_BROADCAST_INTENT = "sql_news_item_broadcast_intent";
    public static final String MAIL_SQL_BROADCAST_INTENT = "mail_sql_broadcast_intent";
    public static final String CALENDAR_SQL_BROADCAST_INTENT = "calendar_sql_broadcast_intent";
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public DBAdapter(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private void dropAndCreateTable(String tableName, String createTableString) {
        database.execSQL("DROP TABLE IF EXISTS " + tableName);
        database.execSQL(createTableString);
    }

    public void saveShortNews(ArticlesGetShortDescriptionResponse entity) {
        for (int i = 0; i < entity.result.result.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(SHORT_NEWS_SERVER_ID, entity.result.result.get(i).id);
            values.put(SHORT_NEWS_TITLE, entity.result.result.get(i).title);
            values.put(SHORT_NEWS_SHORT_DESCR, entity.result.result.get(i).short_descr);
            values.put(SHORT_NEWS_THUMBNAIL, entity.result.result.get(i).thumbnail);
            values.put(SHORT_NEWS_POSTED_AT, entity.result.result.get(i).posted_at);
            for (int j = 0; j < entity.result.result.get(i).comments.size(); j++) {
                ContentValues valuesComments = new ContentValues();
                valuesComments.put(SHORT_NEWS_COMMENTS_SERVER_ID, entity.result.result.get(i).comments.get(j).id);
                valuesComments.put(SHORT_NEWS_COMMENTS_ARTICLE_ID, entity.result.result.get(i).id);
                valuesComments.put(SHORT_NEWS_COMMENTS_CONTENT, entity.result.result.get(i).comments.get(j).content);
                valuesComments.put(SHORT_NEWS_COMMENTS_AUTHOR_ID, entity.result.result.get(i).comments.get(j).author_id);
                valuesComments.put(SHORT_NEWS_COMMENTS_AUTHOR_NAME, entity.result.result.get(i).comments.get(j).author.name);
                valuesComments.put(SHORT_NEWS_COMMENTS_AUTHOR_IMAGE, entity.result.result.get(i).comments.get(j).author.img);
                database.insertWithOnConflict(SHORT_NEWS_COMMENTS_TABLE_NAME, null, valuesComments, SQLiteDatabase.CONFLICT_REPLACE);
            }
            database.insertWithOnConflict(SHORT_NEWS_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        sendBroadcast(NEWS_FEED_SQL_BROADCAST_INTENT);
    }

    public void saveFullNews(ArticleFullResponse entity) {

        ContentValues values = new ContentValues();
        values.put(FULL_NEWS_SERVER_ID, entity.result.result.id);
        values.put(FULL_NEWS_FULL_DESCR, entity.result.result.full_descr);
        for (int j = 0; j < entity.result.result.comments.size(); j++) {
            ContentValues valuesComments = new ContentValues();
            valuesComments.put(SHORT_NEWS_COMMENTS_SERVER_ID, entity.result.result.comments.get(j).id);
            valuesComments.put(SHORT_NEWS_COMMENTS_ARTICLE_ID, entity.result.result.id);
            valuesComments.put(SHORT_NEWS_COMMENTS_CONTENT, entity.result.result.comments.get(j).content);
            valuesComments.put(SHORT_NEWS_COMMENTS_AUTHOR_ID, entity.result.result.comments.get(j).author_id);
            valuesComments.put(SHORT_NEWS_COMMENTS_AUTHOR_NAME, entity.result.result.comments.get(j).author.name);
            valuesComments.put(SHORT_NEWS_COMMENTS_AUTHOR_IMAGE, entity.result.result.comments.get(j).author.img);
            database.insertWithOnConflict(SHORT_NEWS_COMMENTS_TABLE_NAME, null, valuesComments, SQLiteDatabase.CONFLICT_REPLACE);
        }
        database.insertWithOnConflict(FULL_NEWS_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        sendBroadcast(NEWS_ITEM_SQL_BROADCAST_INTENT);
    }


    public void saveArticleFullComments(CommentsArticleFullResponse entity, int article_id) {

        for (int j = 0; j < entity.result.size(); j++) {
            ContentValues valuesComments = new ContentValues();
            valuesComments.put(SHORT_NEWS_COMMENTS_SERVER_ID, entity.result.get(j).id);
            valuesComments.put(SHORT_NEWS_COMMENTS_ARTICLE_ID, article_id);
            valuesComments.put(SHORT_NEWS_COMMENTS_CONTENT, entity.result.get(j).content);
            valuesComments.put(SHORT_NEWS_COMMENTS_AUTHOR_ID, entity.result.get(j).author_id);
            valuesComments.put(SHORT_NEWS_COMMENTS_AUTHOR_NAME, entity.result.get(j).author.name);
            valuesComments.put(SHORT_NEWS_COMMENTS_AUTHOR_IMAGE, entity.result.get(j).author.img);
            database.insertWithOnConflict(SHORT_NEWS_COMMENTS_TABLE_NAME, null, valuesComments, SQLiteDatabase.CONFLICT_REPLACE);
        }

        sendBroadcast(NEWS_ITEM_SQL_BROADCAST_INTENT);
    }


    public void saveMailContacts(GetMailContactResponse entity) {
        dropAndCreateTable(MAIL_CONTACT_TABLE_NAME, CREATE_TABLE_MAIL_CONTACT);

        for (int i = 0; i < entity.result.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(MAIL_CONTACT_SERVER_ID, entity.result.get(i).id);
            values.put(MAIL_CONTACT_PARENT_ID, entity.result.get(i).parentId);
            values.put(MAIL_CONTACT_NAME, entity.result.get(i).name);
            values.put(MAIL_CONTACT_LOGIN, entity.result.get(i).login);
            values.put(MAIL_CONTACT_DATE_ADD, entity.result.get(i).dateAdd);
            values.put(MAIL_CONTACT_PHONE, entity.result.get(i).phone);
            values.put(MAIL_CONTACT_IMG, entity.result.get(i).img);
            database.insert(MAIL_CONTACT_TABLE_NAME, null, values);
        }
        sendBroadcast(MAIL_SQL_BROADCAST_INTENT);
    }

    public void saveEventsCalendar(CalendarGetEventsResponse entity) {
        for (int i = 0; i < entity.result.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(EVENT_CALENDAR_SERVER_ID, entity.result.get(i).id);
            values.put(EVENT_CALENDAR_NAME, entity.result.get(i).name);
            values.put(EVENT_CALENDAR_DESCRIPTION, entity.result.get(i).description);
            values.put(EVENT_CALENDAR_TYPE, entity.result.get(i).type);
            values.put(EVENT_CALENDAR_START_DATETIME, entity.result.get(i).start_datetime);
            values.put(EVENT_CALENDAR_END_DATETIME, entity.result.get(i).end_datetime);
            values.put(EVENT_CALENDAR_LOCATION, entity.result.get(i).location);
            database.insertWithOnConflict(EVENT_CALENDAR_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        sendBroadcast(CALENDAR_SQL_BROADCAST_INTENT);
    }

    public void sendBroadcast(String type) {
        Intent intent = new Intent(type);
        LocalBroadcastManager.getInstance(AppContext.getAppContext()).sendBroadcast(intent);
    }

    public long savePush(int type, int userId, String userName, int room) {
        ContentValues values = new ContentValues();
        values.put(STATUS_BAR_PUSH_TYPE, type);
        values.put(STATUS_BAR_PUSH_USER_ID, userId);
        values.put(STATUS_BAR_PUSH_USER_NAME, userName);
        values.put(STATUS_BAR_PUSH_ROOM, room);

        return database.insert(STATUS_BAR_PUSH_TABLE_NAME, null, values);
    }

    public ArrayList loadPushArray() {
        ArrayList<Push> pushArray = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + STATUS_BAR_PUSH_TABLE_NAME;
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            while (cursor.moveToNext()) {
                Push push = new Push();
                push.setId(cursor.getInt(cursor.getColumnIndex(STATUS_BAR_PUSH_ID)));
                push.setType(cursor.getInt(cursor.getColumnIndex(STATUS_BAR_PUSH_TYPE)));
                push.setUserId(cursor.getInt(cursor.getColumnIndex(STATUS_BAR_PUSH_USER_ID)));
                push.setUserName(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_USER_NAME)));
                push.setRoom(cursor.getInt(cursor.getColumnIndex(STATUS_BAR_PUSH_ROOM)));

                pushArray.add(push);
            }
        }

        cursor.close();
        return pushArray;
    }

    public void deletePush(int pushId) {
        database.delete(STATUS_BAR_PUSH_TABLE_NAME, STATUS_BAR_PUSH_ID + " = " + pushId, null);
    }

}
