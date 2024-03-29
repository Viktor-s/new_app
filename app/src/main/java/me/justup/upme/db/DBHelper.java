package me.justup.upme.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "upme.db";
    private static final int DATABASE_VERSION = 24;

    public static final String BASE_TABLE_NAME = "base_table";
    public static final String BASE_ID = BaseColumns._ID;
    public static final String BASE_PROJECT_ID = "project_id";
    public static final String BASE_START_DATE = "start_date";

    public static final String EVENT_CALENDAR_TABLE_NAME = "event_calendar_table";
    public static final String EVENT_CALENDAR_ID = BaseColumns._ID;
    public static final String EVENT_CALENDAR_SERVER_ID = "server_id";
    public static final String EVENT_CALENDAR_NAME = "name";
    public static final String EVENT_CALENDAR_DESCRIPTION = "description";
    public static final String EVENT_CALENDAR_TYPE = "type";
    public static final String EVENT_CALENDAR_OWNER_ID = "owner_id";
    public static final String EVENT_CALENDAR_START_DATETIME = "start_datetime";
    public static final String EVENT_CALENDAR_END_DATETIME = "end_datetime";
    public static final String EVENT_CALENDAR_LOCATION = "location";
    public static final String EVENT_CALENDAR_SHARED_WITH = "shared_with";

    public static final String FULL_NEWS_TABLE_NAME = "full_news_table";
    public static final String FULL_NEWS_ID = BaseColumns._ID;
    public static final String FULL_NEWS_SERVER_ID = "server_id";
    public static final String FULL_NEWS_FULL_DESCR = "full_descr";

    public static final String IS_SHORT_NEWS_READ_TABLE_NAME = "is_short_news_read_table";
    public static final String IS_SHORT_NEWS_READ_ID = BaseColumns._ID;
    public static final String IS_SHORT_NEWS_READ_ARTICLE_ID = "article_id";
    public static final String IS_SHORT_NEWS_READ_VALUE = "value";

    public static final String MAIL_CONTACT_TABLE_NAME = "mail_contact_table";
    public static final String MAIL_CONTACT_ID = BaseColumns._ID;
    public static final String MAIL_CONTACT_SERVER_ID = "server_id";
    public static final String MAIL_CONTACT_PARENT_ID = "parent_id";
    public static final String MAIL_CONTACT_JABBER_ID = "jabber_id";
    public static final String MAIL_CONTACT_NAME = "name";
    public static final String MAIL_CONTACT_NAME_LOWER_CASE = "name_lc";
    public static final String MAIL_CONTACT_LOGIN = "login";
    public static final String MAIL_CONTACT_DATE_ADD = "date_add";
    public static final String MAIL_CONTACT_PHONE = "phone";
    public static final String MAIL_CONTACT_IMG = "img";
    public static final String MAIL_CONTACT_LATITUDE = "latitude";
    public static final String MAIL_CONTACT_LONGITUDE = "longitude";
    public static final String MAIL_CONTACT_LEVEL = "level";
    public static final String MAIL_CONTACT_IN_SYSTEM = "in_system";
    public static final String MAIL_CONTACT_TOTAL_SUM = "total_sum";
    public static final String MAIL_CONTACT_STATUS = "status";

    public static final String PRODUCTS_BRAND_CATEGORIES_TABLE_NAME = "products_brand_table";
    public static final String PRODUCTS_BRAND_CATEGORIES_ID = BaseColumns._ID;
    public static final String PRODUCTS_BRAND_CATEGORIES_SERVER_ID = "server_id";
    public static final String PRODUCTS_BRAND_CATEGORIES_NAME = "name";
    public static final String PRODUCTS_BRAND_CATEGORIES_IMAGE = "image";
    public static final String PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION = "short_description";
    public static final String PRODUCTS_BRAND_CATEGORIES_FULL_DESCRIPTION = "full_description";
    public static final String PRODUCTS_BRAND_CATEGORIES_CATEGORY_ID = "category_id";
    public static final String PRODUCTS_BRAND_CATEGORIES_BRAND_ID = "brand_id";
    public static final String PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_ID = "brand_item_id";
    public static final String PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_NAME = "brand_item_name";
    public static final String PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_DESCRIPTION = "brand_item_description";

    public static final String PRODUCTS_CATEGORIES_TABLE_NAME = "products_categories_table";
    public static final String PRODUCTS_CATEGORIES_ID = BaseColumns._ID;
    public static final String PRODUCTS_CATEGORIES_SERVER_ID = "server_id";
    public static final String PRODUCTS_CATEGORIES_NAME = "name";

    public static final String PRODUCTS_HTML_TABLE_NAME = "products_html_table";
    public static final String PRODUCTS_HTML_ID = BaseColumns._ID;
    public static final String PRODUCTS_HTML_SERVER_ID = "server_id";
    public static final String PRODUCTS_HTML_VERSION = "version";
    public static final String PRODUCTS_HTML_CONTENT = "content";

    public static final String PRODUCTS_PRODUCT_TABLE_NAME = "products_product_table";
    public static final String PRODUCTS_PRODUCT_ID = BaseColumns._ID;
    public static final String PRODUCTS_PRODUCT_SERVER_ID = "server_id";
    public static final String PRODUCTS_PRODUCT_BRAND_ID = "product_brand_id";
    public static final String PRODUCTS_PRODUCT_NAME = "name";
    public static final String PRODUCTS_PRODUCT_SHORT_DESCRIPTION = "short_description";
    public static final String PRODUCTS_PRODUCT_DESCRIPTION = "description";
    public static final String PRODUCTS_PRODUCT_IMAGE = "image";

    public static final String SHORT_NEWS_COMMENTS_TABLE_NAME = "short_news_comments_table";
    public static final String SHORT_NEWS_COMMENTS_ID = BaseColumns._ID;
    public static final String SHORT_NEWS_COMMENTS_SERVER_ID = "server_id";
    public static final String SHORT_NEWS_COMMENTS_ARTICLE_ID = "article_id";
    public static final String SHORT_NEWS_COMMENTS_CONTENT = "content";
    public static final String SHORT_NEWS_COMMENTS_AUTHOR_ID = "author_id";
    public static final String SHORT_NEWS_COMMENTS_AUTHOR_NAME = "author_name";
    public static final String SHORT_NEWS_COMMENTS_AUTHOR_IMAGE = "author_image";
    public static final String SHORT_NEWS_COMMENTS_POSTED_AT = "posted_at";

    public static final String SHORT_NEWS_TABLE_NAME = "short_news_table";
    public static final String SHORT_NEWS_ID = BaseColumns._ID;
    public static final String SHORT_NEWS_SERVER_ID = "server_id";
    public static final String SHORT_NEWS_TITLE = "title";
    public static final String SHORT_NEWS_SHORT_DESCR = "short_descr";
    public static final String SHORT_NEWS_THUMBNAIL = "thumbnail";
    public static final String SHORT_NEWS_POSTED_AT = "posted_at";
    public static final String SHORT_NEWS_IS_READED = "is_readed";

    public static final String STATUS_BAR_PUSH_TABLE_NAME = "status_bar_push_table";
    public static final String STATUS_BAR_PUSH_ID = BaseColumns._ID;
    public static final String STATUS_BAR_PUSH_TYPE = "type";
    public static final String STATUS_BAR_PUSH_USER_ID = "user_id";
    public static final String STATUS_BAR_PUSH_USER_NAME = "user_name";
    public static final String STATUS_BAR_PUSH_DATE = "date";
    public static final String STATUS_BAR_PUSH_LINK = "link";
    public static final String STATUS_BAR_PUSH_JABBER = "jabber_id";
    public static final String STATUS_BAR_PUSH_FILE_NAME = "file_name";
    public static final String STATUS_BAR_PUSH_ROOM = "room_id";
    public static final String STATUS_BAR_PUSH_FORM_ID = "form_id";
    public static final String STATUS_BAR_PUSH_PUSH_DESCRIPTION = "push_description";

    public static final String EDUCATION_PRODUCTS_TABLE_NAME = "education_products_table";
    public static final String EDUCATION_PRODUCTS_ID = BaseColumns._ID;
    public static final String EDUCATION_PRODUCTS_SERVER_ID = "server_id";
    public static final String EDUCATION_PRODUCTS_NAME = "name";
    public static final String EDUCATION_PRODUCTS_NAME_LOWER_CASE = "name_lc";

    public static final String EDUCATION_PRODUCT_MODULE_TABLE_NAME = "education_product_module_table";
    public static final String EDUCATION_PRODUCT_MODULE_ID = BaseColumns._ID;
    public static final String EDUCATION_PRODUCT_MODULE_SERVER_ID = "server_id";
    public static final String EDUCATION_PRODUCT_MODULE_PROGRAM_ID = "program_id";
    public static final String EDUCATION_PRODUCT_MODULE_NAME = "name";
    public static final String EDUCATION_PRODUCT_MODULE_DESCRIPTION = "description";
    public static final String EDUCATION_PRODUCT_MODULE_CREATED_AT = "created_at";
    public static final String EDUCATION_PRODUCT_MODULE_UPDATED_AT = "updated_at";


    public static final String EDUCATION_MODULES_MATERIAL_TABLE_NAME = "education_modules_material_table";
    public static final String EDUCATION_MODULES_MATERIAL_ID = BaseColumns._ID;
    public static final String EDUCATION_MODULES_MATERIAL_SERVER_ID = "server_id";
    public static final String EDUCATION_MODULES_MATERIAL_MODULE_ID = "module_id";
    public static final String EDUCATION_MODULES_MATERIAL_CONTENT_TYPE = "content_type";
    public static final String EDUCATION_MODULES_MATERIAL_PRIORITY_TYPE = "priority_type";
    public static final String EDUCATION_MODULES_MATERIAL_EXTRA_SOURCE = "extra_source";
    public static final String EDUCATION_MODULES_MATERIAL_EXTRA_LINK = "extra_link";
    public static final String EDUCATION_MODULES_MATERIAL_SORT_WEIGHT = "sort_weight";
    public static final String EDUCATION_MODULES_MATERIAL_CREATED_AT = "created_at";
    public static final String EDUCATION_MODULES_MATERIAL_UPDATED_AT = "updated_at";
    public static final String EDUCATION_MODULES_MATERIAL_NAME = "name";
    public static final String EDUCATION_MODULES_MATERIAL_DESCRIPTION = "description";

    private static final String CREATE_TABLE_BASE = "CREATE TABLE "
            + BASE_TABLE_NAME + "("
            + BASE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BASE_PROJECT_ID + " INTEGER, "
            + BASE_START_DATE + " TEXT);";

    protected static final String CREATE_TABLE_IS_SHORT_NEWS_READ = "CREATE TABLE "
            + IS_SHORT_NEWS_READ_TABLE_NAME + "("
            + IS_SHORT_NEWS_READ_ID + " INTEGER, "
            + IS_SHORT_NEWS_READ_ARTICLE_ID + " INTEGER PRIMARY KEY, "
            + IS_SHORT_NEWS_READ_VALUE + " INTEGER DEFAULT 0" + ")";

    protected static final String CREATE_TABLE_SHORT_NEWS_COMMENTS = "CREATE TABLE "
            + SHORT_NEWS_COMMENTS_TABLE_NAME + "("
            + SHORT_NEWS_COMMENTS_ID + " INTEGER, "
            + SHORT_NEWS_COMMENTS_SERVER_ID + " INTEGER PRIMARY KEY, "
            + SHORT_NEWS_COMMENTS_ARTICLE_ID + " INTEGER, "
            + SHORT_NEWS_COMMENTS_CONTENT + " TEXT, "
            + SHORT_NEWS_COMMENTS_AUTHOR_ID + " INTEGER, "
            + SHORT_NEWS_COMMENTS_AUTHOR_NAME + " TEXT, "
            + SHORT_NEWS_COMMENTS_AUTHOR_IMAGE + " TEXT, "
            + SHORT_NEWS_COMMENTS_POSTED_AT + " TEXT, "
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
            + MAIL_CONTACT_ID + " INTEGER, "
            + MAIL_CONTACT_SERVER_ID + " INTEGER PRIMARY KEY, "
            + MAIL_CONTACT_PARENT_ID + " INTEGER, "
            + MAIL_CONTACT_NAME + " TEXT, "
            + MAIL_CONTACT_NAME_LOWER_CASE + " TEXT, "
            + MAIL_CONTACT_JABBER_ID + " TEXT, "
            + MAIL_CONTACT_LOGIN + " TEXT, "
            + MAIL_CONTACT_DATE_ADD + " INTEGER, "
            + MAIL_CONTACT_PHONE + " TEXT, "
            + MAIL_CONTACT_LATITUDE + " REAL, "
            + MAIL_CONTACT_LONGITUDE + " REAL, "
            + MAIL_CONTACT_LEVEL + " INTEGER, "
            + MAIL_CONTACT_IN_SYSTEM + " TEXT, "
            + MAIL_CONTACT_TOTAL_SUM + " INTEGER, "
            + MAIL_CONTACT_STATUS + " INTEGER, "
            + MAIL_CONTACT_IMG + " TEXT" + ")";

    protected static final String CREATE_TABLE_PRODUCTS_CATEGORIES = "CREATE TABLE "
            + PRODUCTS_CATEGORIES_TABLE_NAME + "("
            + PRODUCTS_CATEGORIES_ID + " INTEGER, "
            + PRODUCTS_CATEGORIES_SERVER_ID + " INTEGER PRIMARY KEY, "
            + PRODUCTS_CATEGORIES_NAME + " TEXT" + ")";

    protected static final String CREATE_TABLE_PRODUCTS_BRAND_CATEGORIES = "CREATE TABLE "
            + PRODUCTS_BRAND_CATEGORIES_TABLE_NAME + "("
            + PRODUCTS_BRAND_CATEGORIES_ID + " INTEGER, "
            + PRODUCTS_BRAND_CATEGORIES_SERVER_ID + " INTEGER PRIMARY KEY, "
            + PRODUCTS_BRAND_CATEGORIES_NAME + " TEXT, "
            + PRODUCTS_BRAND_CATEGORIES_IMAGE + " TEXT, "
            + PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION + " TEXT, "
            + PRODUCTS_BRAND_CATEGORIES_FULL_DESCRIPTION + " TEXT, "
            + PRODUCTS_BRAND_CATEGORIES_CATEGORY_ID + " INTEGER, "
            + PRODUCTS_BRAND_CATEGORIES_BRAND_ID + " INTEGER, "
            + PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_ID + " INTEGER, "
            + PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_NAME + " TEXT, "
            + PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_DESCRIPTION + " TEXT" + ")";

    protected static final String CREATE_TABLE_PRODUCTS_PRODUCT = "CREATE TABLE "
            + PRODUCTS_PRODUCT_TABLE_NAME + "("
            + PRODUCTS_PRODUCT_ID + " INTEGER, "
            + PRODUCTS_PRODUCT_SERVER_ID + " INTEGER PRIMARY KEY, "
            + PRODUCTS_PRODUCT_BRAND_ID + " TEXT, "
            + PRODUCTS_PRODUCT_NAME + " TEXT, "
            + PRODUCTS_PRODUCT_SHORT_DESCRIPTION + " TEXT, "
            + PRODUCTS_PRODUCT_DESCRIPTION + " TEXT, "
            + PRODUCTS_PRODUCT_IMAGE + " TEXT" + ")";

    protected static final String CREATE_TABLE_PRODUCTS_HTML = "CREATE TABLE "
            + PRODUCTS_HTML_TABLE_NAME + "("
            + PRODUCTS_HTML_ID + " INTEGER, "
            + PRODUCTS_HTML_SERVER_ID + " INTEGER PRIMARY KEY, "
            + PRODUCTS_HTML_VERSION + " INTEGER, "
            + PRODUCTS_HTML_CONTENT + " TEXT" + ")";

    protected static final String CREATE_TABLE_EVENT_CALENDAR = "CREATE TABLE "
            + EVENT_CALENDAR_TABLE_NAME + "("
            + EVENT_CALENDAR_ID + " INTEGER, "
            + EVENT_CALENDAR_SERVER_ID + " INTEGER PRIMARY KEY, "
            + EVENT_CALENDAR_NAME + " TEXT, "
            + EVENT_CALENDAR_DESCRIPTION + " TEXT, "
            + EVENT_CALENDAR_TYPE + " TEXT, "
            + EVENT_CALENDAR_OWNER_ID + " INTEGER, "
            + EVENT_CALENDAR_START_DATETIME + " TEXT, "
            + EVENT_CALENDAR_END_DATETIME + " TEXT, "
            + EVENT_CALENDAR_LOCATION + " TEXT, "
            + EVENT_CALENDAR_SHARED_WITH + " TEXT" + ")";

    protected static final String CREATE_TABLE_STATUS_BAR_PUSH = "CREATE TABLE "
            + STATUS_BAR_PUSH_TABLE_NAME + "("
            + STATUS_BAR_PUSH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + STATUS_BAR_PUSH_TYPE + " INTEGER, "
            + STATUS_BAR_PUSH_USER_ID + " INTEGER, "
            + STATUS_BAR_PUSH_USER_NAME + " TEXT, "
            + STATUS_BAR_PUSH_DATE + " TEXT, "
            + STATUS_BAR_PUSH_LINK + " TEXT, "
            + STATUS_BAR_PUSH_JABBER + " TEXT, "
            + STATUS_BAR_PUSH_FILE_NAME + " TEXT, "
            + STATUS_BAR_PUSH_FORM_ID + " TEXT, "
            + STATUS_BAR_PUSH_PUSH_DESCRIPTION + " TEXT, "
            + STATUS_BAR_PUSH_ROOM + " TEXT" + ")";

    protected static final String CREATE_TABLE_EDUCATION_PRODUCTS = "CREATE TABLE "
            + EDUCATION_PRODUCTS_TABLE_NAME + "("
            + EDUCATION_PRODUCTS_ID + " INTEGER, "
            + EDUCATION_PRODUCTS_SERVER_ID + " INTEGER PRIMARY KEY, "
            + EDUCATION_PRODUCTS_NAME_LOWER_CASE + " TEXT, "
            + EDUCATION_PRODUCTS_NAME + " TEXT" + ")";

    protected static final String CREATE_TABLE_EDUCATION_PRODUCT_MODULE = "CREATE TABLE "
            + EDUCATION_PRODUCT_MODULE_TABLE_NAME + "("
            + EDUCATION_PRODUCT_MODULE_ID + " INTEGER, "
            + EDUCATION_PRODUCT_MODULE_SERVER_ID + " INTEGER PRIMARY KEY, "
            + EDUCATION_PRODUCT_MODULE_PROGRAM_ID + " INTEGER, "
            + EDUCATION_PRODUCT_MODULE_NAME + " TEXT, "
            + EDUCATION_PRODUCT_MODULE_DESCRIPTION + " TEXT, "
            + EDUCATION_PRODUCT_MODULE_CREATED_AT + " TEXT, "
            + EDUCATION_PRODUCT_MODULE_UPDATED_AT + " TEXT" + ")";

    protected static final String CREATE_TABLE_EDUCATION_MODULES_MATERIAL = "CREATE TABLE "
            + EDUCATION_MODULES_MATERIAL_TABLE_NAME + "("
            + EDUCATION_MODULES_MATERIAL_ID + " INTEGER, "
            + EDUCATION_MODULES_MATERIAL_SERVER_ID + " INTEGER PRIMARY KEY, "
            + EDUCATION_MODULES_MATERIAL_MODULE_ID + " INTEGER, "
            + EDUCATION_MODULES_MATERIAL_CONTENT_TYPE + " TEXT, "
            + EDUCATION_MODULES_MATERIAL_PRIORITY_TYPE + " TEXT, "
            + EDUCATION_MODULES_MATERIAL_EXTRA_SOURCE + " TEXT, "
            + EDUCATION_MODULES_MATERIAL_EXTRA_LINK + " TEXT, "
            + EDUCATION_MODULES_MATERIAL_SORT_WEIGHT + " INTEGER, "
            + EDUCATION_MODULES_MATERIAL_CREATED_AT + " TEXT, "
            + EDUCATION_MODULES_MATERIAL_NAME + " TEXT, "
            + EDUCATION_MODULES_MATERIAL_DESCRIPTION + " TEXT, "
            + EDUCATION_MODULES_MATERIAL_UPDATED_AT + " TEXT" + ")";

    // Tile Menu
    public static final String TILE_TABLE_NAME = "tile_table";

    public static final String TILE_ID = BaseColumns._ID;

    public static final String TILE_WIDTH = "width";
    public static final String TILE_HEIGHT = "height";
    public static final String TILE_TITLE = "title";
    public static final String TILE_STITLE = "stitle";
    public static final String TILE_RES_ID = "res_id";
    public static final String TILE_BACKGROUND = "background";
    public static final String TILE_IS_ADD_ITEM = "is_plus";
    public static final String TILE_IS_REDACTED = "is_redacted";
    public static final String TILE_IS_IMAGE = "is_img";

    private static final String CREATE_TABLE_TILE_MENU = "CREATE TABLE "
            + TILE_TABLE_NAME + "("
            + TILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TILE_WIDTH + " INTEGER, "
            + TILE_HEIGHT + " INTEGER, "
            + TILE_TITLE + " TEXT, "
            + TILE_STITLE + " TEXT, "
            + TILE_RES_ID + " INTEGER, "
            + TILE_BACKGROUND + " INTEGER, "
            + TILE_IS_ADD_ITEM + " INTEGER, "
            + TILE_IS_REDACTED + " INTEGER, "
            + TILE_IS_IMAGE + " INTEGER" + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_BASE);
        database.execSQL(CREATE_TABLE_SHORT_NEWS);
        database.execSQL(CREATE_TABLE_IS_SHORT_NEWS_READ);
        database.execSQL(CREATE_TABLE_FULL_NEWS);
        database.execSQL(CREATE_TABLE_MAIL_CONTACT);
        database.execSQL(CREATE_TABLE_SHORT_NEWS_COMMENTS);
        database.execSQL(CREATE_TABLE_EVENT_CALENDAR);
        database.execSQL(CREATE_TABLE_PRODUCTS_CATEGORIES);
        database.execSQL(CREATE_TABLE_PRODUCTS_BRAND_CATEGORIES);
        database.execSQL(CREATE_TABLE_PRODUCTS_PRODUCT);
        database.execSQL(CREATE_TABLE_PRODUCTS_HTML);
        database.execSQL(CREATE_TABLE_STATUS_BAR_PUSH);
        database.execSQL(CREATE_TABLE_TILE_MENU);
        database.execSQL(CREATE_TABLE_EDUCATION_PRODUCTS);
        database.execSQL(CREATE_TABLE_EDUCATION_PRODUCT_MODULE);
        database.execSQL(CREATE_TABLE_EDUCATION_MODULES_MATERIAL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BASE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SHORT_NEWS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IS_SHORT_NEWS_READ_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MAIL_CONTACT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SHORT_NEWS_COMMENTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FULL_NEWS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_CALENDAR_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STATUS_BAR_PUSH_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_CATEGORIES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_BRAND_CATEGORIES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_PRODUCT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_HTML_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TILE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EDUCATION_PRODUCTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EDUCATION_PRODUCT_MODULE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EDUCATION_MODULES_MATERIAL_TABLE_NAME);

        onCreate(db);
    }

}
