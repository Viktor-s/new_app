package me.justup.upme.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "upme.db";
    private static final int DATABASE_VERSION = 1;

    public static final String BASE_TABLE_NAME = "base_table";
    public static final String BASE_ID = BaseColumns._ID;
    public static final String BASE_PROJECT_ID = "project_id";
    public static final String BASE_START_DATE = "start_date";

    public static final String SHORT_NEWS_TABLE_NAME = "short_news_table";
    public static final String SHORT_NEWS_ID = BaseColumns._ID;
    public static final String SHORT_NEWS_SERVER_ID = "server_id";
    public static final String SHORT_NEWS_TITLE = "title";
    public static final String SHORT_NEWS_SHORT_DESCR = "short_descr";
    public static final String SHORT_NEWS_THUMBNAIL = "thumbnail";
    public static final String SHORT_NEWS_POSTED_AT = "posted_at";

    private static final String CREATE_TABLE_BASE = "CREATE TABLE "
            + BASE_TABLE_NAME + "("
            + BASE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BASE_PROJECT_ID + " INTEGER, "
            + BASE_START_DATE + " TEXT);";

    private static final String CREATE_TABLE_SHORT_NEWS = "CREATE TABLE "
            + SHORT_NEWS_TABLE_NAME + "("
            + SHORT_NEWS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SHORT_NEWS_SERVER_ID + " INTEGER, "
            + SHORT_NEWS_TITLE + " TEXT, "
            + SHORT_NEWS_SHORT_DESCR + " TEXT, "
            + SHORT_NEWS_THUMBNAIL + " TEXT, "
            + SHORT_NEWS_POSTED_AT + " TEXT" + ")";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_BASE);
        database.execSQL(CREATE_TABLE_SHORT_NEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BASE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SHORT_NEWS_TABLE_NAME);

        onCreate(db);
    }

}
