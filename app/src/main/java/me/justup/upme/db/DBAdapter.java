package me.justup.upme.db;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.justup.upme.JustUpApplication;
import me.justup.upme.entity.ArticleFullResponse;
import me.justup.upme.entity.ArticlesGetShortDescriptionResponse;
import me.justup.upme.entity.CalendarGetEventsResponse;
import me.justup.upme.entity.CommentsArticleFullResponse;
import me.justup.upme.entity.GetAllContactsResponse;
import me.justup.upme.entity.GetProductHtmlByIdResponse;
import me.justup.upme.entity.ProductsGetAllCategoriesResponse;
import me.justup.upme.entity.Push;

import static me.justup.upme.db.DBHelper.CREATE_TABLE_MAIL_CONTACT;
import static me.justup.upme.db.DBHelper.CREATE_TABLE_STATUS_BAR_PUSH;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_DESCRIPTION;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_END_DATETIME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_LOCATION;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_NAME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_OWNER_ID;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_SERVER_ID;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_SHARED_WITH;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_START_DATETIME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_TABLE_NAME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_TYPE;
import static me.justup.upme.db.DBHelper.FULL_NEWS_FULL_DESCR;
import static me.justup.upme.db.DBHelper.FULL_NEWS_SERVER_ID;
import static me.justup.upme.db.DBHelper.FULL_NEWS_TABLE_NAME;
import static me.justup.upme.db.DBHelper.IS_SHORT_NEWS_READ_ARTICLE_ID;
import static me.justup.upme.db.DBHelper.IS_SHORT_NEWS_READ_TABLE_NAME;
import static me.justup.upme.db.DBHelper.IS_SHORT_NEWS_READ_VALUE;
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
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_TABLE_NAME;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_TOTAL_SUM;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_BRAND_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_DESCRIPTION;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_CATEGORY_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_FULL_DESCRIPTION;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_IMAGE;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_SERVER_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_TABLE_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_CATEGORIES_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_CATEGORIES_SERVER_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_CATEGORIES_TABLE_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_HTML_CONTENT;
import static me.justup.upme.db.DBHelper.PRODUCTS_HTML_SERVER_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_HTML_TABLE_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_BRAND_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_IMAGE;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_SERVER_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_SHORT_DESCRIPTION;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_TABLE_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_ARTICLE_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_IMAGE;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_CONTENT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_SERVER_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_TABLE_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_POSTED_AT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_SERVER_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_SHORT_DESCR;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_TABLE_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_THUMBNAIL;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_TITLE;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_DATE;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_FILE_NAME;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_FORM_ID;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_ID;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_JABBER;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_LINK;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_PUSH_DESCRIPTION;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_ROOM;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_TABLE_NAME;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_TYPE;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_USER_ID;
import static me.justup.upme.db.DBHelper.STATUS_BAR_PUSH_USER_NAME;

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
    // private static final String TAG = makeLogTag(DBAdapter.class);
    public static final String NEWS_FEED_SQL_BROADCAST_INTENT = "sql_news_feed_broadcast_intent";
    public static final String NEWS_ITEM_SQL_BROADCAST_INTENT = "sql_news_item_broadcast_intent";
    public static final String MAIL_SQL_BROADCAST_INTENT = "mail_sql_broadcast_intent";
    public static final String CALENDAR_SQL_BROADCAST_INTENT = "calendar_sql_broadcast_intent";
    public static final String PRODUCTS_SQL_BROADCAST_INTENT = "products_sql_broadcast_intent";
    public static final String PRODUCT_HTML_SQL_BROADCAST_INTENT = "products_html_sql_broadcast_intent";

    // private DBHelper dbHelper;

    public static final String EMPTY_VALUE = "-";


    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static DBAdapter dbAdapterInstance;
    private static DBHelper mDatabaseHelper;
    private SQLiteDatabase database;


    //  public DBAdapter(Context context) {
    //       dbHelper = new DBHelper(context);
    //  }
    public DBAdapter() {

    }

    public static synchronized void initInstance() {
        if (dbAdapterInstance == null) {
            dbAdapterInstance = new DBAdapter();
            mDatabaseHelper = new DBHelper(JustUpApplication.getApplication().getApplicationContext());
        }
    }

    public static synchronized DBAdapter getInstance() {
        if (dbAdapterInstance == null) {
            throw new IllegalStateException(DBAdapter.class.getSimpleName() +
                    " is not initialized, call initInstance(..) method first.");
        }

        return dbAdapterInstance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            database = mDatabaseHelper.getWritableDatabase();
        }
        return database;
    }

    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            database.close();

        }
    }


//    public void open() {
//        database = dbHelper.getWritableDatabase();
//    }
//
//    public void close() {
//        dbHelper.close();
//    }

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

    public void saveNewsReadValue(int articleId) {
        if (!database.isOpen()) {
            DBAdapter.getInstance().openDatabase();
        }

        ContentValues values = new ContentValues();
        values.put(IS_SHORT_NEWS_READ_ARTICLE_ID, articleId);
        values.put(IS_SHORT_NEWS_READ_VALUE, 1);
        database.insertWithOnConflict(IS_SHORT_NEWS_READ_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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

    /*
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
    */

    public void saveContactsArray(List<GetAllContactsResponse.Result.Parents> userArray) {
        //dropAndCreateTable(MAIL_CONTACT_TABLE_NAME, CREATE_TABLE_MAIL_CONTACT);

        for (GetAllContactsResponse.Result.Parents user : userArray) {
            ContentValues values = new ContentValues();
            values.put(MAIL_CONTACT_SERVER_ID, user.id);
            values.put(MAIL_CONTACT_PARENT_ID, user.parent_id);
            values.put(MAIL_CONTACT_NAME, user.name);
            values.put(MAIL_CONTACT_NAME_LOWER_CASE, user.name.toLowerCase());
            values.put(MAIL_CONTACT_JABBER_ID, user.jabber_id);
            values.put(MAIL_CONTACT_LOGIN, user.login);
            values.put(MAIL_CONTACT_DATE_ADD, user.dateAdd);
            values.put(MAIL_CONTACT_PHONE, user.phone);
            values.put(MAIL_CONTACT_IMG, user.img);
            values.put(MAIL_CONTACT_LATITUDE, user.latitude);
            values.put(MAIL_CONTACT_LONGITUDE, user.longitude);
            values.put(MAIL_CONTACT_LEVEL, user.level);
            values.put(MAIL_CONTACT_IN_SYSTEM, user.in_system);
            values.put(MAIL_CONTACT_TOTAL_SUM, user.total_sum);

            database.insertWithOnConflict(MAIL_CONTACT_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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
            values.put(EVENT_CALENDAR_OWNER_ID, entity.result.get(i).owner_id);
            values.put(EVENT_CALENDAR_START_DATETIME, entity.result.get(i).start_datetime);
            values.put(EVENT_CALENDAR_END_DATETIME, entity.result.get(i).end_datetime);
            values.put(EVENT_CALENDAR_LOCATION, entity.result.get(i).location);
            if (entity.result.get(i).shared_with != null) {
                String strSharedWith = entity.result.get(i).shared_with.toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
                values.put(EVENT_CALENDAR_SHARED_WITH, strSharedWith);
            }
            database.insertWithOnConflict(EVENT_CALENDAR_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        sendBroadcast(CALENDAR_SQL_BROADCAST_INTENT);
    }

    public void saveAllProducts(ProductsGetAllCategoriesResponse entity) {
        for (int i = 0; i < entity.result.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(PRODUCTS_CATEGORIES_SERVER_ID, entity.result.get(i).id);
            values.put(PRODUCTS_CATEGORIES_NAME, entity.result.get(i).name);
            for (int j = 0; j < entity.result.get(i).brandCategories.size(); j++) {
                ContentValues valuesBrand = new ContentValues();
                valuesBrand.put(PRODUCTS_BRAND_CATEGORIES_SERVER_ID, entity.result.get(i).brandCategories.get(j).id);
                valuesBrand.put(PRODUCTS_BRAND_CATEGORIES_NAME, entity.result.get(i).brandCategories.get(j).name);
                valuesBrand.put(PRODUCTS_BRAND_CATEGORIES_IMAGE, entity.result.get(i).brandCategories.get(j).image);
                valuesBrand.put(PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION, entity.result.get(i).brandCategories.get(j).shortDescription);
                valuesBrand.put(PRODUCTS_BRAND_CATEGORIES_FULL_DESCRIPTION, entity.result.get(i).brandCategories.get(j).fullDescription);
                valuesBrand.put(PRODUCTS_BRAND_CATEGORIES_CATEGORY_ID, entity.result.get(i).brandCategories.get(j).categoryId);
                valuesBrand.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ID, entity.result.get(i).brandCategories.get(j).brandId);
                valuesBrand.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_ID, entity.result.get(i).brandCategories.get(j).brand.id);
                valuesBrand.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_NAME, entity.result.get(i).brandCategories.get(j).brand.name);
                valuesBrand.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_DESCRIPTION, entity.result.get(i).brandCategories.get(j).brand.description);
                database.insertWithOnConflict(PRODUCTS_BRAND_CATEGORIES_TABLE_NAME, null, valuesBrand, SQLiteDatabase.CONFLICT_REPLACE);
                for (int k = 0; k < entity.result.get(i).brandCategories.get(j).products.size(); k++) {
                    ContentValues valuesProduct = new ContentValues();
                    valuesProduct.put(PRODUCTS_PRODUCT_SERVER_ID, entity.result.get(i).brandCategories.get(j).products.get(k).id);
                    valuesProduct.put(PRODUCTS_PRODUCT_BRAND_ID, entity.result.get(i).brandCategories.get(j).brandId);
                    valuesProduct.put(PRODUCTS_PRODUCT_NAME, entity.result.get(i).brandCategories.get(j).products.get(k).name);
                    valuesProduct.put(PRODUCTS_PRODUCT_SHORT_DESCRIPTION, entity.result.get(i).brandCategories.get(j).products.get(k).short_description);
                    valuesProduct.put(PRODUCTS_PRODUCT_IMAGE, entity.result.get(i).brandCategories.get(j).products.get(k).img);
                    database.insertWithOnConflict(PRODUCTS_PRODUCT_TABLE_NAME, null, valuesProduct, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
            database.insertWithOnConflict(PRODUCTS_CATEGORIES_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        sendBroadcast(PRODUCTS_SQL_BROADCAST_INTENT);
    }


    public void saveProductHtml(GetProductHtmlByIdResponse entity) {
        ContentValues values = new ContentValues();
        values.put(PRODUCTS_HTML_SERVER_ID, entity.result.id);
        // values.put(PRODUCTS_HTML_VERSION, entity.result.);
        values.put(PRODUCTS_HTML_CONTENT, entity.result.html);
        database.insertWithOnConflict(PRODUCTS_HTML_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        sendBroadcast(PRODUCT_HTML_SQL_BROADCAST_INTENT);
    }

    public void sendBroadcast(String type) {
        Intent intent = new Intent(type);
        LocalBroadcastManager.getInstance(JustUpApplication.getApplication().getApplicationContext()).sendBroadcast(intent);
    }

    public long savePush(final Push push, String date) {
        ContentValues values = new ContentValues();
        values.put(STATUS_BAR_PUSH_TYPE, push.getType());
        values.put(STATUS_BAR_PUSH_USER_ID, push.getUserId());

        if (push.getUserName() != null) {
            values.put(STATUS_BAR_PUSH_USER_NAME, push.getUserName());
        } else {
            values.put(STATUS_BAR_PUSH_USER_NAME, EMPTY_VALUE);
        }

        values.put(STATUS_BAR_PUSH_DATE, date);

        if (push.getLink() != null) {
            values.put(STATUS_BAR_PUSH_LINK, push.getLink());
        } else {
            values.put(STATUS_BAR_PUSH_LINK, EMPTY_VALUE);
        }

        if (push.getJabberId() != null) {
            values.put(STATUS_BAR_PUSH_JABBER, push.getJabberId());
        } else {
            values.put(STATUS_BAR_PUSH_JABBER, EMPTY_VALUE);
        }

        if (push.getFileName() != null) {
            values.put(STATUS_BAR_PUSH_FILE_NAME, push.getFileName());
        } else {
            values.put(STATUS_BAR_PUSH_FILE_NAME, EMPTY_VALUE);
        }

        if (push.getRoom() != null) {
            values.put(STATUS_BAR_PUSH_ROOM, push.getRoom());
        } else {
            values.put(STATUS_BAR_PUSH_ROOM, EMPTY_VALUE);
        }

        if (push.getFormId() != null) {
            values.put(STATUS_BAR_PUSH_FORM_ID, push.getFormId());
        } else {
            values.put(STATUS_BAR_PUSH_FORM_ID, EMPTY_VALUE);
        }

        if (push.getPushDescription() != null) {
            values.put(STATUS_BAR_PUSH_PUSH_DESCRIPTION, push.getPushDescription());
        } else {
            values.put(STATUS_BAR_PUSH_PUSH_DESCRIPTION, EMPTY_VALUE);
        }

        return database.insert(STATUS_BAR_PUSH_TABLE_NAME, null, values);
    }

    public ArrayList loadPushArray() {
        ArrayList<Push> pushArray = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + STATUS_BAR_PUSH_TABLE_NAME;
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Push push = new Push();
                push.setId(cursor.getInt(cursor.getColumnIndex(STATUS_BAR_PUSH_ID)));
                push.setType(cursor.getInt(cursor.getColumnIndex(STATUS_BAR_PUSH_TYPE)));
                push.setUserId(cursor.getInt(cursor.getColumnIndex(STATUS_BAR_PUSH_USER_ID)));
                push.setUserName(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_USER_NAME)));
                push.setDate(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_DATE)));
                push.setLink(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_LINK)));
                push.setJabberId(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_JABBER)));
                push.setFileName(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_FILE_NAME)));
                push.setRoom(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_ROOM)));
                push.setFormId(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_FORM_ID)));
                push.setPushDescription(cursor.getString(cursor.getColumnIndex(STATUS_BAR_PUSH_PUSH_DESCRIPTION)));

                pushArray.add(push);
            }
        }

        cursor.close();
        return pushArray;
    }

    public void deletePush(int pushId) {
        database.delete(STATUS_BAR_PUSH_TABLE_NAME, STATUS_BAR_PUSH_ID + " = " + pushId, null);
    }

    public void deleteAllPush() {
        dropAndCreateTable(STATUS_BAR_PUSH_TABLE_NAME, CREATE_TABLE_STATUS_BAR_PUSH);
    }

}
