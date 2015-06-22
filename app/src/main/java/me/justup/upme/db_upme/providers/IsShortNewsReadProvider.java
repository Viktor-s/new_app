package me.justup.upme.db_upme.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.HashMap;

import me.justup.upme.db_upme.DatabaseHelper;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;

public class IsShortNewsReadProvider extends ContentProvider implements SyncAdapterMetaData {
    private static final String TAG = IsShortNewsReadProvider.class.getSimpleName();
    private static final String DB_NAME = IsShortNewsReadConstance.TABLE_NAME_IS_SHORT_NEWS_READ;

    private DatabaseHelper mDbHelper = null;
    private SQLiteDatabase db = null;

    // SQLite code
    private static final int OK = 1;
    private static final int FAIL = 0;

    // Type
    private static final int TYPE_LIST_IS_SHORT_NEWS_READ = 1;
    private static final int TYPE_ITEM_IS_SHORT_NEWS_READ = 2;

    private static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(MetaData.AUTHORITY_IS_SHORT_NEWS_READ, IsShortNewsReadConstance.URI_PATH_IS_SHORT_NEWS_READ_TABLE, TYPE_LIST_IS_SHORT_NEWS_READ);
        mUriMatcher.addURI(MetaData.AUTHORITY_IS_SHORT_NEWS_READ, IsShortNewsReadConstance.URI_PATH2_IS_SHORT_NEWS_READ_TABLE, TYPE_LIST_IS_SHORT_NEWS_READ);
        mUriMatcher.addURI(MetaData.AUTHORITY_IS_SHORT_NEWS_READ, IsShortNewsReadConstance.URI_PATH_IS_SHORT_NEWS_READ_TABLE + "/#", TYPE_ITEM_IS_SHORT_NEWS_READ);
        mUriMatcher.addURI(MetaData.AUTHORITY_IS_SHORT_NEWS_READ, IsShortNewsReadConstance.URI_PATH2_IS_SHORT_NEWS_READ_TABLE + "/#", TYPE_ITEM_IS_SHORT_NEWS_READ);
    }

    // Main DB Name
    private String mAppDBName = null;

    // HashMap Is Short News Read
    private static final HashMap<String, String> mIsShortNewsReadMap = new HashMap<String, String>();
    static {
        mIsShortNewsReadMap.put(IsShortNewsReadConstance.IS_SHORT_NEWS_READ_ID, IsShortNewsReadConstance.IS_SHORT_NEWS_READ_ID);
        mIsShortNewsReadMap.put(IsShortNewsReadConstance.IS_SHORT_NEWS_READ_ARTICLE_ID, IsShortNewsReadConstance.IS_SHORT_NEWS_READ_ARTICLE_ID);
        mIsShortNewsReadMap.put(IsShortNewsReadConstance.IS_SHORT_NEWS_READ_VALUE, IsShortNewsReadConstance.IS_SHORT_NEWS_READ_VALUE);
    }

    @Override
    public boolean onCreate() {

        // Init DB
        String appDBName = null;
        try {
            appDBName = MetaData.getDatabaseName(getContext());
        } catch (NullPointerException e) {
            LOGE(TAG, e.getMessage());
        }

        if (appDBName != null) {
            mAppDBName = appDBName;
            mDbHelper = new DatabaseHelper(getContext(), mAppDBName, IsShortNewsReadConstance.CREATE_TABLE_IS_SHORT_NEWS_READ);

            try {
                db = mDbHelper.getWritableDatabase();
            } catch (SQLException e) {
                LOGE(TAG, e.getMessage());
            }

            LOGD(TAG, "App DB Name is '" + mAppDBName + "' \nDBHelper is '" + mDbHelper + "' \nSQLite db is '" + db + "'");

        }

        return false;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        LOGD(TAG, DB_NAME + " Uri insert is : " + uri + ".\n ContentValues [] : " + Arrays.toString(values));

        // Check what Uri need to use
        Uri uriLocal;

        if(values.length>1){
            uriLocal = Uri.parse(uri.toString() + "/" + TYPE_LIST_IS_SHORT_NEWS_READ);
        }else{
            uriLocal = Uri.parse(uri.toString() + "/" + TYPE_ITEM_IS_SHORT_NEWS_READ);
        }

        // Check DB
        switchDatabase(uri);

        // Insert Data in Cycle
        for (ContentValues value : values) {
            insert(uriLocal, value);
        }

        return OK;
    }

    @Override
    public Uri insert(Uri uri, @NonNull ContentValues contentValues) {
        LOGD(TAG, "Uri insert : '" + uri + "' \n ContentValues : " + contentValues.toString());

        // Check DB
        switchDatabase(uri);

        // Get Is short news read ID
        long id;
        try {
            id = (Long) contentValues.get(IsShortNewsReadConstance.IS_SHORT_NEWS_READ_ID);
        } catch (ClassCastException e) {
            id = Long.parseLong((String) contentValues.get(IsShortNewsReadConstance.IS_SHORT_NEWS_READ_ID));
        }

        LOGD(TAG, "Provider " + DB_NAME + " Id = " + id + "\n " + DB_NAME + " db = " + db);

        // DB Path
        String sql = "SELECT " + IsShortNewsReadConstance.IS_SHORT_NEWS_READ_ID + " FROM " + IsShortNewsReadConstance.TABLE_NAME_IS_SHORT_NEWS_READ + " WHERE " + IsShortNewsReadConstance.IS_SHORT_NEWS_READ_ID + " = " + id;
        // Create Cursor
        Cursor cursor = db.rawQuery(sql, new String[]{});
        // Check is Raw Consist
        boolean exists = cursor.getCount() > 0;
        // Close Cursor
        cursor.close();

        Long row = null;

        if (exists) {
            LOGD(TAG, DB_NAME + " DB update : " + contentValues.toString());
            try {
                db.beginTransaction();
                row = (long)db.update(IsShortNewsReadConstance.TABLE_NAME_IS_SHORT_NEWS_READ, contentValues, IsShortNewsReadConstance.IS_SHORT_NEWS_READ_ID + " = " + id, null);
                LOGD(TAG, DB_NAME + " DB updating status '" + row + "' row. \n" + contentValues.toString());

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } else {
            try {
                db.beginTransaction();
                row = db.insertOrThrow(IsShortNewsReadConstance.TABLE_NAME_IS_SHORT_NEWS_READ, null, contentValues);
                LOGD(TAG, DB_NAME + " DB Inserted status '" + row + "' row. \n" + contentValues.toString());

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        Uri resultUri = ContentUris.withAppendedId(uri, row);
        // Send Notify
        getContext().getContentResolver().notifyChange(resultUri, null);

        return resultUri;
    }

    @Override
    public String getType(Uri uri) {
        LOGD(TAG, "Type is " + uri.toString());

        switch (mUriMatcher.match(uri)) {
            case TYPE_LIST_IS_SHORT_NEWS_READ:
                return IsShortNewsReadConstance.IS_SHORT_NEWS_READ_TABLE_CONTENT_TYPE;
            case TYPE_ITEM_IS_SHORT_NEWS_READ:
                return IsShortNewsReadConstance.IS_SHORT_NEWS_READ_TABLE_CONTENT_ITEM_TYPE;
        }

        return null;
    }

    @Override
    public int update(Uri uri, ContentValues cv, String where, String[] strings) {
        LOGD(TAG, DB_NAME + " update uri : " + uri.toString());

        // Check DB
        switchDatabase(uri);

        switch (mUriMatcher.match(uri)) {
            case TYPE_LIST_IS_SHORT_NEWS_READ: {

                LOGD(TAG, DB_NAME + " DB update TYPE_LIST, where : " + where + ", to String : " + cv.toString());
                int row;

                try {
                    db.beginTransaction();
                    row = db.update(IsShortNewsReadConstance.TABLE_NAME_IS_SHORT_NEWS_READ, cv, where, null);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                // Send Notify
                getContext().getContentResolver().notifyChange(uri, null);

                return row;
            }

            case TYPE_ITEM_IS_SHORT_NEWS_READ: {

                // Get Path to Item
                String where1 = IsShortNewsReadConstance.IS_SHORT_NEWS_READ_ID + " = " + uri.getLastPathSegment();
                LOGD(TAG, DB_NAME + " DB update TYPE_ITEM '" + where1 + "', content = " + cv.toString());
                int row;

                try {
                    db.beginTransaction();
                    row = db.update(FullNewsConstance.TABLE_NAME_FULL_NEWS, cv, where1, null);
                } finally {
                    db.endTransaction();
                }

                // Send Notify
                getContext().getContentResolver().notifyChange(uri, null);

                return row;
            }

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String where, String[] params) {
        // Check DB
        switchDatabase(uri);

        int row;

        try {
            db.beginTransaction();
            row = db.delete(IsShortNewsReadConstance.TABLE_NAME_IS_SHORT_NEWS_READ, where, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        // Send Notify
        getContext().getContentResolver().notifyChange(uri, null);

        return row;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        LOGD(TAG, DB_NAME + " Uri : " + uri.toString() + "\n UriMatcher " + DB_NAME + " : " + mUriMatcher.toString());

        // Check DB
        switchDatabase(uri);

        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        LOGD(TAG, "Uri " + DB_NAME + " One Item : " + mUriMatcher.match(uri) + "\n SortOrder = " + sortOrder + "\n Selection = " + selection);


        switch (Integer.parseInt(sortOrder)) {
            case 1:

                builder.setTables(IsShortNewsReadConstance.TABLE_NAME_IS_SHORT_NEWS_READ);
                builder.setProjectionMap(mIsShortNewsReadMap);
                LOGD(TAG, DB_NAME + " TYPE is 'LIST'");

                break;

            case 2:

                builder.setTables(IsShortNewsReadConstance.TABLE_NAME_IS_SHORT_NEWS_READ);
                builder.setProjectionMap(mIsShortNewsReadMap);
                LOGD(TAG, DB_NAME + " TYPE is 'ITEM'");

                break;

            default:
                throw new IllegalArgumentException("Unknown URI : " + uri);
        }

        Cursor queryCursor = builder.query(db, projection, selection, selectionArgs, null, null, null);
        // Make sure that potential listeners are getting notified
        queryCursor.setNotificationUri(getContext().getContentResolver(), uri);

        LOGD(TAG, DB_NAME + " DB query Cursor : " + queryCursor.toString());

        return queryCursor;
    }

    /**
     * Check if DB Created
     */
    private void switchDatabase(Uri uri) {
        LOGD(TAG, DB_NAME + " Switch URI = " + uri.toString() + "\n Uri Path = " + uri.getPathSegments() + "\n Uri Path Size = " + uri.getPathSegments().size());

        if (uri.getPathSegments().size() > 2) {

            // Get DB Name
            String dbName = MetaData.getDatabaseName(uri.getPathSegments().get(1));
            LOGD(TAG, DB_NAME + " DB Name : " + dbName + "\n Old DB Name = " + mAppDBName);

            if (mAppDBName == null && dbName != null || mAppDBName != null && !mAppDBName.equals(dbName)) {
                mAppDBName = dbName;
                mDbHelper = new DatabaseHelper(getContext(), mAppDBName, IsShortNewsReadConstance.TABLE_NAME_IS_SHORT_NEWS_READ, IsShortNewsReadConstance.CREATE_TABLE_IS_SHORT_NEWS_READ);

                try {
                    if (db == null) {
                        db = mDbHelper.getWritableDatabase();
                    }
                } catch (SQLException e) {
                    LOGE(TAG, e.getMessage());
                }

                LOGD(TAG, DB_NAME + " DB : " + dbName + "\n mDbHelper : " + mDbHelper + "\n db : " + db);
            }
        }
    }
}
