package me.justup.upme.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "upme.db";
    private static final int DATABASE_VERSION = 2;

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
    public static final String SHORT_NEWS_IS_READED = "is_readed";

    public static final String FULL_NEWS_TABLE_NAME = "full_news_table";
    public static final String FULL_NEWS_ID = BaseColumns._ID;
    public static final String FULL_NEWS_SERVER_ID = "server_id";
    public static final String FULL_NEWS_FULL_DESCR = "full_descr";


    public static final String SHORT_NEWS_COMMENTS_TABLE_NAME = "short_news_comments_table";
    public static final String SHORT_NEWS_COMMENTS_ID = BaseColumns._ID;
    public static final String SHORT_NEWS_COMMENTS_SERVER_ID = "server_id";
    public static final String SHORT_NEWS_COMMENTS_ARTICLE_ID = "article_id";
    public static final String SHORT_NEWS_COMMENTS_CONTENT = "content";
    public static final String SHORT_NEWS_COMMENTS_AUTHOR_ID = "author_id";
    public static final String SHORT_NEWS_COMMENTS_AUTHOR_NAME = "author_name";
    public static final String SHORT_NEWS_COMMENTS_AUTHOR_IMAGE = "author_image";

    public static final String MAIL_CONTACT_TABLE_NAME = "mail_contact_table";
    public static final String MAIL_CONTACT_ID = BaseColumns._ID;
    public static final String MAIL_CONTACT_SERVER_ID = "server_id";
    public static final String MAIL_CONTACT_NAME = "name";
    public static final String MAIL_CONTACT_LOGIN = "login";
    public static final String MAIL_CONTACT_DATE_ADD = "date_add";
    public static final String MAIL_CONTACT_PHONE = "phone";
    public static final String MAIL_CONTACT_IMG = "img";


    private static final String CREATE_TABLE_BASE = "CREATE TABLE "
            + BASE_TABLE_NAME + "("
            + BASE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BASE_PROJECT_ID + " INTEGER, "
            + BASE_START_DATE + " TEXT);";

    protected static final String CREATE_TABLE_SHORT_NEWS_COMMENTS = "CREATE TABLE "
            + SHORT_NEWS_COMMENTS_TABLE_NAME + "("
            + SHORT_NEWS_COMMENTS_ID + " INTEGER, "
            + SHORT_NEWS_COMMENTS_SERVER_ID + " INTEGER PRIMARY KEY, "
            + SHORT_NEWS_COMMENTS_ARTICLE_ID + " INTEGER, "
            + SHORT_NEWS_COMMENTS_CONTENT + " TEXT, "
            + SHORT_NEWS_COMMENTS_AUTHOR_ID + " INTEGER, "
            + SHORT_NEWS_COMMENTS_AUTHOR_NAME + " TEXT, "
            + SHORT_NEWS_COMMENTS_AUTHOR_IMAGE + " TEXT, "
            + SHORT_NEWS_IS_READED + " INTEGER" + ")";

    protected static final String CREATE_TABLE_SHORT_NEWS = "CREATE TABLE "
            + SHORT_NEWS_TABLE_NAME + "("
            + SHORT_NEWS_ID + " INTEGER, "
            + SHORT_NEWS_SERVER_ID + " INTEGER PRIMARY KEY, "
            + SHORT_NEWS_TITLE + " TEXT, "
            + SHORT_NEWS_SHORT_DESCR + " TEXT, "
            + SHORT_NEWS_THUMBNAIL + " TEXT, "
            + SHORT_NEWS_POSTED_AT + " TEXT" + ")";


    protected static final String CREATE_TABLE_FULL_NEWS = "CREATE TABLE "
            + FULL_NEWS_TABLE_NAME + "("
            + FULL_NEWS_ID + " INTEGER, "
            + FULL_NEWS_SERVER_ID + " INTEGER PRIMARY KEY, "
            + FULL_NEWS_FULL_DESCR + " TEXT" + ")";


    protected static final String CREATE_TABLE_MAIL_CONTACT = "CREATE TABLE "
            + MAIL_CONTACT_TABLE_NAME + "("
            + MAIL_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MAIL_CONTACT_SERVER_ID + " INTEGER, "
            + MAIL_CONTACT_NAME + " TEXT, "
            + MAIL_CONTACT_LOGIN + " TEXT, "
            + MAIL_CONTACT_DATE_ADD + " INTEGER, "
            + MAIL_CONTACT_PHONE + " TEXT, "
            + MAIL_CONTACT_IMG + " TEXT" + ")";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_BASE);
        database.execSQL(CREATE_TABLE_SHORT_NEWS);
        database.execSQL(CREATE_TABLE_FULL_NEWS);
        database.execSQL(CREATE_TABLE_MAIL_CONTACT);
        database.execSQL(CREATE_TABLE_SHORT_NEWS_COMMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BASE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SHORT_NEWS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MAIL_CONTACT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SHORT_NEWS_COMMENTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FULL_NEWS_TABLE_NAME);
        onCreate(db);
    }

}
