package me.justup.upme.db_upme;

import android.net.Uri;
import android.provider.BaseColumns;

public interface SyncAdapterMetaData {

    public static final String EMPTY_VALUE = "-";

    public static class BaseTableConstance {

        // Init Path to SQLite
        public static final String URI_PATH_BASE_TABLE = "base_table";
        public static final String URI_PATH2_BASE_TABLE = "user/#/" + URI_PATH_BASE_TABLE;

        // Create URI
        public static final Uri CONTENT_URI_BASE_TABLE= Uri.parse("content://" + MetaData.AUTHORITY_BASE + "/" + URI_PATH2_BASE_TABLE);

        // Table Name
        public static final String TABLE_NAME_BASE_TABLE = "base_table";

        // Типы данных
        // Набор строк
        public static final String BASE_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_BASE + "." + URI_PATH_BASE_TABLE;
        // Одна строка
        public static final String BASE_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_BASE + "." + URI_PATH_BASE_TABLE;

        // Variable
        public static final String BASE_ID = BaseColumns._ID;
        public static final String BASE_PROJECT_ID = "project_id";
        public static final String BASE_START_DATE = "start_date";

        // Create Table
        public static final String CREATE_TABLE_BASE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_BASE_TABLE + "("
                + BASE_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + BASE_PROJECT_ID + " NUMERIC, "
                + BASE_START_DATE + " text, "
                + "UNIQUE(" + BASE_ID + ") ON CONFLICT IGNORE)";
    }

    public static class EventCalendarConstance {

        // Init Path to SQLite
        public static final String URI_PATH_EVENT_CALENDAR = "event_calendar_table";
        public static final String URI_PATH2_EVENT_CALENDAR = "user/#/" + URI_PATH_EVENT_CALENDAR;

        // Create URI
        public static final Uri CONTENT_URI_EVENT_CALENDAR = Uri.parse("content://" + MetaData.AUTHORITY_EVENT_CALENDAR + "/" + URI_PATH2_EVENT_CALENDAR);

        // Table Name
        public static final String TABLE_NAME_EVENT_CALENDAR = "event_calendar_table";

        // Типы данных
        // Набор строк
        public static final String EVENT_CALENDAR_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_EVENT_CALENDAR + "." + URI_PATH_EVENT_CALENDAR;
        // Одна строка
        public static final String EVENT_CALENDAR_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_EVENT_CALENDAR + "." + URI_PATH_EVENT_CALENDAR;

        // Variable
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

        // Create Table
        public static final String CREATE_TABLE_EVENT_CALENDAR = "CREATE TABLE IF NOT EXISTS"
                + TABLE_NAME_EVENT_CALENDAR + "("
                + EVENT_CALENDAR_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + EVENT_CALENDAR_SERVER_ID + " INTEGER, "
                + EVENT_CALENDAR_NAME + " TEXT, "
                + EVENT_CALENDAR_DESCRIPTION + " TEXT, "
                + EVENT_CALENDAR_TYPE + " TEXT, "
                + EVENT_CALENDAR_OWNER_ID + " INTEGER, "
                + EVENT_CALENDAR_START_DATETIME + " TEXT, "
                + EVENT_CALENDAR_END_DATETIME + " TEXT, "
                + EVENT_CALENDAR_LOCATION + " TEXT, "
                + EVENT_CALENDAR_SHARED_WITH + " TEXT,"
                + "UNIQUE(" + EVENT_CALENDAR_ID + ") ON CONFLICT IGNORE)";
    }

    public static class FullNewsConstance {

        // Init Path to SQLite
        public static final String URI_PATH_FULL_NEWS_TABLE = "full_news_table";
        public static final String URI_PATH2_FULL_NEWS_TABLE = "user/#/" + URI_PATH_FULL_NEWS_TABLE;

        // Create URI
        public static final Uri CONTENT_URI_FULL_NEWS_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_FULL_NEWS + "/" + URI_PATH2_FULL_NEWS_TABLE);

        // Table Name
        public static final String TABLE_NAME_FULL_NEWS = "full_news_table";

        // Типы данных
        // Набор строк
        public static final String FULL_NEWS_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_FULL_NEWS + "." + URI_PATH_FULL_NEWS_TABLE;
        // Одна строка
        public static final String FULL_NEWS_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_FULL_NEWS + "." + URI_PATH_FULL_NEWS_TABLE;

        // Variable
        public static final String FULL_NEWS_ID = BaseColumns._ID;
        public static final String FULL_NEWS_SERVER_ID = "server_id";
        public static final String FULL_NEWS_FULL_DESCR = "full_descr";

        // Create Table
        public static final String CREATE_TABLE_FULL_NEWS = "CREATE TABLE IF NOT EXISTS"
                + TABLE_NAME_FULL_NEWS + "("
                + FULL_NEWS_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + FULL_NEWS_SERVER_ID + " INTEGER, "
                + FULL_NEWS_FULL_DESCR + " TEXT,"
                + "UNIQUE(" + FULL_NEWS_ID + ") ON CONFLICT IGNORE)";
    }

    public static class IsShortNewsReadConstance {

        // Init Path to SQLite
        public static final String URI_PATH_IS_SHORT_NEWS_READ_TABLE = "is_short_news_read_table";
        public static final String URI_PATH2_IS_SHORT_NEWS_READ_TABLE = "user/#/" + URI_PATH_IS_SHORT_NEWS_READ_TABLE;

        // Create URI
        public static final Uri CONTENT_URI_IS_SHORT_NEWS_READ_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_IS_SHORT_NEWS_READ + "/" + URI_PATH2_IS_SHORT_NEWS_READ_TABLE);

        // Table Name
        public static final String TABLE_NAME_IS_SHORT_NEWS_READ = "is_short_news_read_table";

        // Типы данных
        // Набор строк
        public static final String IS_SHORT_NEWS_READ_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_IS_SHORT_NEWS_READ + "." + URI_PATH_IS_SHORT_NEWS_READ_TABLE;
        // Одна строка
        public static final String IS_SHORT_NEWS_READ_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_IS_SHORT_NEWS_READ + "." + URI_PATH_IS_SHORT_NEWS_READ_TABLE;

        // Variable
        public static final String IS_SHORT_NEWS_READ_ID = BaseColumns._ID;
        public static final String IS_SHORT_NEWS_READ_ARTICLE_ID = "article_id";
        public static final String IS_SHORT_NEWS_READ_VALUE = "value";

        // Create Table
        public static final String CREATE_TABLE_IS_SHORT_NEWS_READ = "CREATE TABLE IF NOT EXISTS"
                + TABLE_NAME_IS_SHORT_NEWS_READ + "("
                + IS_SHORT_NEWS_READ_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + IS_SHORT_NEWS_READ_ARTICLE_ID + " INTEGER, "
                + IS_SHORT_NEWS_READ_VALUE + " INTEGER DEFAULT 0"
                + "UNIQUE(" + IS_SHORT_NEWS_READ_ID + ") ON CONFLICT IGNORE)";
    }

    public static class MailContactConstance {

        // Init Path to SQLite
        public static final String URI_PATH_MAIL_CONTACT_TABLE = "mail_contact_table";
        public static final String URI_PATH2_MAIL_CONTACT_TABLE = "user/#/" + URI_PATH_MAIL_CONTACT_TABLE;

        // Create URI
        public static final Uri CONTENT_URI_MAIL_CONTACT_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_MAIL_CONTACT + "/" + URI_PATH2_MAIL_CONTACT_TABLE);

        // Table Name
        public static final String TABLE_NAME_MAIL_CONTACT = "mail_contact_table";

        // Типы данных
        // Набор строк
        public static final String MAIL_CONTACT_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_MAIL_CONTACT + "." + URI_PATH_MAIL_CONTACT_TABLE;
        // Одна строка
        public static final String MAIL_CONTACT_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_MAIL_CONTACT + "." + URI_PATH_MAIL_CONTACT_TABLE;

        // Variable
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

        // Create Table
        public static final String CREATE_TABLE_MAIL_CONTACT = "CREATE TABLE IF NOT EXISTS"
                + TABLE_NAME_MAIL_CONTACT + "("
                + MAIL_CONTACT_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + MAIL_CONTACT_SERVER_ID + " INTEGER, "
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
                + MAIL_CONTACT_IMG + " TEXT"
                + "UNIQUE(" + MAIL_CONTACT_ID + ") ON CONFLICT IGNORE)";
    }

    public static class ProductBrandConstance {

        // Init Path to SQLite
        public static final String URI_PATH_PRODUCT_BRAND = "products_brand_table";
        public static final String URI_PATH2_PRODUCT_BRAND = "user/#/" + URI_PATH_PRODUCT_BRAND;

        // Create URI
        public static final Uri CONTENT_URI_PRODUCT_BRAND_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCT_BRAND + "/" + URI_PATH2_PRODUCT_BRAND);

        // Table Name
        public static final String TABLE_PRODUCT_BRAND = "products_brand_table";

        // Типы данных
        // Набор строк
        public static final String PRODUCT_BRAND_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_PRODUCT_BRAND + "." + URI_PATH_PRODUCT_BRAND;
        // Одна строка
        public static final String PRODUCT_BRAND_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_PRODUCT_BRAND + "." + URI_PATH_PRODUCT_BRAND;

        // Variable
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

        // Create Table
        public static final String CREATE_TABLE_PRODUCTS_BRAND_CATEGORIES = "CREATE TABLE IF NOT EXISTS"
                + TABLE_PRODUCT_BRAND + "("
                + PRODUCTS_BRAND_CATEGORIES_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + PRODUCTS_BRAND_CATEGORIES_SERVER_ID + " INTEGER, "
                + PRODUCTS_BRAND_CATEGORIES_NAME + " TEXT, "
                + PRODUCTS_BRAND_CATEGORIES_IMAGE + " TEXT, "
                + PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION + " TEXT, "
                + PRODUCTS_BRAND_CATEGORIES_FULL_DESCRIPTION + " TEXT, "
                + PRODUCTS_BRAND_CATEGORIES_CATEGORY_ID + " INTEGER, "
                + PRODUCTS_BRAND_CATEGORIES_BRAND_ID + " INTEGER, "
                + PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_ID + " INTEGER, "
                + PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_NAME + " TEXT, "
                + PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_DESCRIPTION + " TEXT"
                + "UNIQUE(" + PRODUCTS_BRAND_CATEGORIES_ID + ") ON CONFLICT IGNORE)";
    }

    public static class ProductCategoriesConstance {

        // Init Path to SQLite
        public static final String URI_PATH_PRODUCT_CATEGORIES = "products_categories_table";
        public static final String URI_PATH2_PRODUCT_CATEGORIES = "user/#/" + URI_PATH_PRODUCT_CATEGORIES;

        // Create URI
        public static final Uri CONTENT_URI_PRODUCT_CATEGORIES_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCT_CATEGORIES + "/" + URI_PATH2_PRODUCT_CATEGORIES);

        // Table Name
        public static final String TABLE_PRODUCT_CATEGORIES = "products_categories_table";

        // Типы данных
        // Набор строк
        public static final String PRODUCT_CATEGORIES_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_PRODUCT_CATEGORIES + "." + URI_PATH_PRODUCT_CATEGORIES;
        // Одна строка
        public static final String PRODUCT_CATEGORIES_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_PRODUCT_CATEGORIES + "." + URI_PATH_PRODUCT_CATEGORIES;

        // Variable
        public static final String PRODUCTS_CATEGORIES_ID = BaseColumns._ID;
        public static final String PRODUCTS_CATEGORIES_SERVER_ID = "server_id";
        public static final String PRODUCTS_CATEGORIES_NAME = "name";

        // Create Table
        public static final String CREATE_TABLE_PRODUCTS_CATEGORIES = "CREATE TABLE IF NOT EXISTS"
                + TABLE_PRODUCT_CATEGORIES + "("
                + PRODUCTS_CATEGORIES_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + PRODUCTS_CATEGORIES_SERVER_ID + " INTEGER PRIMARY KEY, "
                + PRODUCTS_CATEGORIES_NAME + " TEXT"
                + "UNIQUE(" + PRODUCTS_CATEGORIES_ID + ") ON CONFLICT IGNORE)";
    }

    public static class ProductHTMLConstance {

        // Init Path to SQLite
        public static final String URI_PATH_PRODUCT_HTML = "products_html_table";
        public static final String URI_PATH2_PRODUCT_HTML = "user/#/" + URI_PATH_PRODUCT_HTML;

        // Create URI
        public static final Uri CONTENT_URI_PRODUCT_HTML_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCT_HTML + "/" + URI_PATH2_PRODUCT_HTML);

        // Table Name
        public static final String TABLE_PRODUCT_HTML = "products_html_table";

        // Типы данных
        // Набор строк
        public static final String PRODUCT_HTML_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_PRODUCT_HTML + "." + URI_PATH_PRODUCT_HTML;
        // Одна строка
        public static final String PRODUCT_HTML_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_PRODUCT_HTML + "." + URI_PATH_PRODUCT_HTML;

        // Variable
        public static final String PRODUCTS_HTML_ID = BaseColumns._ID;
        public static final String PRODUCTS_HTML_SERVER_ID = "server_id";
        public static final String PRODUCTS_HTML_VERSION = "version";
        public static final String PRODUCTS_HTML_CONTENT = "content";

        // Create Table
        public static final String CREATE_TABLE_PRODUCTS_HTML = "CREATE TABLE IF NOT EXISTS"
                + TABLE_PRODUCT_HTML + "("
                + PRODUCTS_HTML_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + PRODUCTS_HTML_SERVER_ID + " INTEGER, "
                + PRODUCTS_HTML_VERSION + " INTEGER, "
                + PRODUCTS_HTML_CONTENT + " TEXT"
                + "UNIQUE(" + PRODUCTS_HTML_ID + ") ON CONFLICT IGNORE)";
    }

    public static class ProductsProductConstance {

        // Init Path to SQLite
        public static final String URI_PATH_PRODUCTS_PRODUCT = "products_product_table";
        public static final String URI_PATH2_PRODUCTS_PRODUCT = "user/#/" + URI_PATH_PRODUCTS_PRODUCT;

        // Create URI
        public static final Uri CONTENT_URI_PRODUCTS_PRODUCT_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCTS_PRODUCT + "/" + URI_PATH2_PRODUCTS_PRODUCT);

        // Table Name
        public static final String TABLE_PRODUCTS_PRODUCT = "products_product_table";

        // Типы данных
        // Набор строк
        public static final String PRODUCTS_PRODUCT_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_PRODUCTS_PRODUCT + "." + URI_PATH_PRODUCTS_PRODUCT;
        // Одна строка
        public static final String PRODUCTS_PRODUCT_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_PRODUCTS_PRODUCT + "." + URI_PATH_PRODUCTS_PRODUCT;

        // Variable
        public static final String PRODUCTS_PRODUCT_ID = BaseColumns._ID;
        public static final String PRODUCTS_PRODUCT_SERVER_ID = "server_id";
        public static final String PRODUCTS_PRODUCT_BRAND_ID = "product_brand_id";
        public static final String PRODUCTS_PRODUCT_NAME = "name";
        public static final String PRODUCTS_PRODUCT_SHORT_DESCRIPTION = "short_description";
        public static final String PRODUCTS_PRODUCT_DESCRIPTION = "description";
        public static final String PRODUCTS_PRODUCT_IMAGE = "image";

        // Create Table
        public static final String CREATE_TABLE_PRODUCTS_PRODUCT = "CREATE TABLE IF NOT EXISTS"
                + TABLE_PRODUCTS_PRODUCT + "("
                + PRODUCTS_PRODUCT_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + PRODUCTS_PRODUCT_SERVER_ID + " INTEGER, "
                + PRODUCTS_PRODUCT_BRAND_ID + " TEXT, "
                + PRODUCTS_PRODUCT_NAME + " TEXT, "
                + PRODUCTS_PRODUCT_SHORT_DESCRIPTION + " TEXT, "
                + PRODUCTS_PRODUCT_DESCRIPTION + " TEXT, "
                + PRODUCTS_PRODUCT_IMAGE + " TEXT"
                + "UNIQUE(" + PRODUCTS_PRODUCT_ID + ") ON CONFLICT IGNORE)";
    }

    public static class ShortNewsCommentsConstance {

        // Init Path to SQLite
        public static final String URI_PATH_SHORT_NEWS_COMMENTS = "short_news_comments_table";
        public static final String URI_PATH2_SHORT_NEWS_COMMENTS = "user/#/" + URI_PATH_SHORT_NEWS_COMMENTS;

        // Create URI
        public static final Uri CONTENT_URI_SHORT_NEWS_COMMENTS_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_SHORT_NEWS_COMMENTS + "/" + URI_PATH2_SHORT_NEWS_COMMENTS);

        // Table Name
        public static final String TABLE_SHORT_NEWS_COMMENTS = "short_news_comments_table";

        // Типы данных
        // Набор строк
        public static final String SHORT_NEWS_COMMENTS_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_SHORT_NEWS_COMMENTS + "." + URI_PATH_SHORT_NEWS_COMMENTS;
        // Одна строка
        public static final String SHORT_NEWS_COMMENTS_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_SHORT_NEWS_COMMENTS + "." + URI_PATH_SHORT_NEWS_COMMENTS;

        // Variable
        public static final String SHORT_NEWS_COMMENTS_ID = BaseColumns._ID;
        public static final String SHORT_NEWS_COMMENTS_SERVER_ID = "server_id";
        public static final String SHORT_NEWS_COMMENTS_ARTICLE_ID = "article_id";
        public static final String SHORT_NEWS_COMMENTS_CONTENT = "content";
        public static final String SHORT_NEWS_COMMENTS_AUTHOR_ID = "author_id";
        public static final String SHORT_NEWS_COMMENTS_AUTHOR_NAME = "author_name";
        public static final String SHORT_NEWS_COMMENTS_AUTHOR_IMAGE = "author_image";
        public static final String SHORT_NEWS_COMMENTS_POSTED_AT = "posted_at";
        public static final String SHORT_NEWS_IS_READED = "is_readed";

        // Create Table
        public static final String CREATE_TABLE_SHORT_NEWS_COMMENTS = "CREATE TABLE IF NOT EXISTS"
                + TABLE_SHORT_NEWS_COMMENTS + "("
                + SHORT_NEWS_COMMENTS_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + SHORT_NEWS_COMMENTS_SERVER_ID + " INTEGER, "
                + SHORT_NEWS_COMMENTS_ARTICLE_ID + " INTEGER, "
                + SHORT_NEWS_COMMENTS_CONTENT + " TEXT, "
                + SHORT_NEWS_COMMENTS_AUTHOR_ID + " INTEGER, "
                + SHORT_NEWS_COMMENTS_AUTHOR_NAME + " TEXT, "
                + SHORT_NEWS_COMMENTS_AUTHOR_IMAGE + " TEXT, "
                + SHORT_NEWS_COMMENTS_POSTED_AT + " TEXT, "
                + SHORT_NEWS_IS_READED + " INTEGER"
                + "UNIQUE(" + SHORT_NEWS_COMMENTS_ID + ") ON CONFLICT IGNORE)";
    }

    public static class ShortNewsConstance {

        // Init Path to SQLite
        public static final String URI_PATH_SHORT_NEWS = "short_news_table";
        public static final String URI_PATH2_SHORT_NEWS = "user/#/" + URI_PATH_SHORT_NEWS;

        // Create URI
        public static final Uri CONTENT_URI_SHORT_NEWS_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_SHORT_NEWS + "/" + URI_PATH2_SHORT_NEWS);

        // Table Name
        public static final String TABLE_SHORT_NEWS = "short_news_table";

        // Типы данных
        // Набор строк
        public static final String SHORT_NEWS_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_SHORT_NEWS + "." + URI_PATH_SHORT_NEWS;
        // Одна строка
        public static final String SHORT_NEWS_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_SHORT_NEWS + "." + URI_PATH_SHORT_NEWS;

        // Variable
        public static final String SHORT_NEWS_ID = BaseColumns._ID;
        public static final String SHORT_NEWS_SERVER_ID = "server_id";
        public static final String SHORT_NEWS_TITLE = "title";
        public static final String SHORT_NEWS_SHORT_DESCR = "short_descr";
        public static final String SHORT_NEWS_THUMBNAIL = "thumbnail";
        public static final String SHORT_NEWS_POSTED_AT = "posted_at";
        public static final String SHORT_NEWS_IS_READED = "is_readed";

        // Create Table
        public static final String CREATE_TABLE_SHORT_NEWS = "CREATE TABLE IF NOT EXISTS"
                + TABLE_SHORT_NEWS + "("
                + SHORT_NEWS_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + SHORT_NEWS_SERVER_ID + " INTEGER, "
                + SHORT_NEWS_TITLE + " TEXT, "
                + SHORT_NEWS_SHORT_DESCR + " TEXT, "
                + SHORT_NEWS_THUMBNAIL + " TEXT, "
                + SHORT_NEWS_POSTED_AT + " TEXT"
                + "UNIQUE(" + SHORT_NEWS_ID + ") ON CONFLICT IGNORE)";
    }

    public static class StatusBarPushConstance {

        // Init Path to SQLite
        public static final String URI_PATH_STATUS_BAR_PUSH = "status_bar_push_table";
        public static final String URI_PATH2_STATUS_BAR_PUSH = "user/#/" + URI_PATH_STATUS_BAR_PUSH;

        // Create URI
        public static final Uri CONTENT_URI_STATUS_BAR_PUSH_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_STATUS_BAR_PUSH + "/" + URI_PATH2_STATUS_BAR_PUSH);

        // Table Name
        public static final String TABLE_STATUS_BAR_PUSH = "status_bar_push_table";

        // Типы данных
        // Набор строк
        public static final String STATUS_BAR_PUSH_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_STATUS_BAR_PUSH + "." + URI_PATH_STATUS_BAR_PUSH;
        // Одна строка
        public static final String STATUS_BAR_PUSH_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_STATUS_BAR_PUSH + "." + URI_PATH_STATUS_BAR_PUSH;

        // Variable
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

        // Create Table
        public static final String CREATE_TABLE_STATUS_BAR_PUSH = "CREATE TABLE IF NOT EXISTS"
                + TABLE_STATUS_BAR_PUSH + "("
                + STATUS_BAR_PUSH_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + STATUS_BAR_PUSH_TYPE + " INTEGER, "
                + STATUS_BAR_PUSH_USER_ID + " INTEGER, "
                + STATUS_BAR_PUSH_USER_NAME + " TEXT, "
                + STATUS_BAR_PUSH_DATE + " TEXT, "
                + STATUS_BAR_PUSH_LINK + " TEXT, "
                + STATUS_BAR_PUSH_JABBER + " TEXT, "
                + STATUS_BAR_PUSH_FILE_NAME + " TEXT, "
                + STATUS_BAR_PUSH_FORM_ID + " TEXT, "
                + STATUS_BAR_PUSH_PUSH_DESCRIPTION + " TEXT, "
                + STATUS_BAR_PUSH_ROOM + " TEXT"
                + "UNIQUE(" + STATUS_BAR_PUSH_ID + ") ON CONFLICT IGNORE)";
    }

    public static class EducationProductModuleConstance {

        // Init Path to SQLite
        public static final String URI_PATH_EDUCATION_PRODUCT_MODULE = "education_product_module_table";
        public static final String URI_PATH2_EDUCATION_PRODUCT_MODULE = "user/#/" + URI_PATH_EDUCATION_PRODUCT_MODULE;

        // Create URI
        public static final Uri CONTENT_URI_EDUCATION_PRODUCT_MODULE_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_EDUCATION_PRODUCT_MODULE + "/" + URI_PATH2_EDUCATION_PRODUCT_MODULE);

        // Table Name
        public static final String TABLE_EDUCATION_PRODUCT_MODULE = "education_product_module_table";

        // Типы данных
        // Набор строк
        public static final String EDUCATION_PRODUCT_MODULE_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_EDUCATION_PRODUCT_MODULE + "." + URI_PATH_EDUCATION_PRODUCT_MODULE;
        // Одна строка
        public static final String EDUCATION_PRODUCT_MODULE_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_EDUCATION_PRODUCT_MODULE + "." + URI_PATH_EDUCATION_PRODUCT_MODULE;

        // Variable
        public static final String EDUCATION_PRODUCT_MODULE_ID = BaseColumns._ID;
        public static final String EDUCATION_PRODUCT_MODULE_SERVER_ID = "server_id";
        public static final String EDUCATION_PRODUCT_MODULE_PROGRAM_ID = "program_id";
        public static final String EDUCATION_PRODUCT_MODULE_NAME = "name";
        public static final String EDUCATION_PRODUCT_MODULE_DESCRIPTION = "description";
        public static final String EDUCATION_PRODUCT_MODULE_CREATED_AT = "created_at";
        public static final String EDUCATION_PRODUCT_MODULE_UPDATED_AT = "updated_at";

        // Create Table
        public static final String CREATE_TABLE_EDUCATION_PRODUCT_MODULE = "CREATE TABLE IF NOT EXISTS"
                + TABLE_EDUCATION_PRODUCT_MODULE + "("
                + EDUCATION_PRODUCT_MODULE_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + EDUCATION_PRODUCT_MODULE_SERVER_ID + " INTEGER, "
                + EDUCATION_PRODUCT_MODULE_PROGRAM_ID + " INTEGER, "
                + EDUCATION_PRODUCT_MODULE_NAME + " TEXT, "
                + EDUCATION_PRODUCT_MODULE_DESCRIPTION + " TEXT, "
                + EDUCATION_PRODUCT_MODULE_CREATED_AT + " TEXT, "
                + EDUCATION_PRODUCT_MODULE_UPDATED_AT + " TEXT"
                + "UNIQUE(" + EDUCATION_PRODUCT_MODULE_ID + ") ON CONFLICT IGNORE)";
    }

    public static class EducationProductConstance {

        // Init Path to SQLite
        public static final String URI_PATH_EDUCATION_PRODUCT = "education_products_table";
        public static final String URI_PATH2_EDUCATION_PRODUCT = "user/#/" + URI_PATH_EDUCATION_PRODUCT;

        // Create URI
        public static final Uri CONTENT_URI_EDUCATION_PRODUCT_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_EDUCATION_PRODUCT + "/" + URI_PATH2_EDUCATION_PRODUCT);

        // Table Name
        public static final String TABLE_EDUCATION_PRODUCT = "education_products_table";

        // Типы данных
        // Набор строк
        public static final String EDUCATION_PRODUCT_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_EDUCATION_PRODUCT + "." + URI_PATH_EDUCATION_PRODUCT;
        // Одна строка
        public static final String EDUCATION_PRODUCT_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_EDUCATION_PRODUCT + "." + URI_PATH_EDUCATION_PRODUCT;

        // Variable
        public static final String EDUCATION_PRODUCTS_ID = BaseColumns._ID;
        public static final String EDUCATION_PRODUCTS_SERVER_ID = "server_id";
        public static final String EDUCATION_PRODUCTS_NAME = "name";
        public static final String EDUCATION_PRODUCTS_NAME_LOWER_CASE = "name_lc";

        // Create Table
        public static final String CREATE_TABLE_EDUCATION_PRODUCTS = "CREATE TABLE IF NOT EXISTS"
                + TABLE_EDUCATION_PRODUCT + "("
                + EDUCATION_PRODUCTS_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + EDUCATION_PRODUCTS_SERVER_ID + " INTEGER, "
                + EDUCATION_PRODUCTS_NAME_LOWER_CASE + " TEXT, "
                + EDUCATION_PRODUCTS_NAME + " TEXT"
                + "UNIQUE(" + EDUCATION_PRODUCTS_ID + ") ON CONFLICT IGNORE)";
    }

    public static class EducationModulesMaterialConstance {

        // Init Path to SQLite
        public static final String URI_PATH_EDUCATION_MODULES_MATERIAL = "education_modules_material_table";
        public static final String URI_PATH2_EDUCATION_MODULES_MATERIAL = "user/#/" + URI_PATH_EDUCATION_MODULES_MATERIAL;

        // Create URI
        public static final Uri CONTENT_URI_EDUCATION_MODULES_MATERIAL_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_EDUCATION_MODULE_MATERIAL + "/" + URI_PATH2_EDUCATION_MODULES_MATERIAL);

        // Table Name
        public static final String TABLE_EDUCATION_MODULES_MATERIAL = "education_modules_material_table";

        // Типы данных
        // Набор строк
        public static final String EDUCATION_MODULES_MATERIAL_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_EDUCATION_MODULE_MATERIAL + "." + URI_PATH_EDUCATION_MODULES_MATERIAL;
        // Одна строка
        public static final String EDUCATION_MODULES_MATERIAL_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_EDUCATION_MODULE_MATERIAL + "." + URI_PATH_EDUCATION_MODULES_MATERIAL;

        // Variable
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

        // Create Table
        public static final String CREATE_TABLE_EDUCATION_MODULES_MATERIAL = "CREATE TABLE IF NOT EXISTS"
                + TABLE_EDUCATION_MODULES_MATERIAL + "("
                + EDUCATION_MODULES_MATERIAL_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + EDUCATION_MODULES_MATERIAL_SERVER_ID + " INTEGER, "
                + EDUCATION_MODULES_MATERIAL_MODULE_ID + " INTEGER, "
                + EDUCATION_MODULES_MATERIAL_CONTENT_TYPE + " TEXT, "
                + EDUCATION_MODULES_MATERIAL_PRIORITY_TYPE + " TEXT, "
                + EDUCATION_MODULES_MATERIAL_EXTRA_SOURCE + " TEXT, "
                + EDUCATION_MODULES_MATERIAL_EXTRA_LINK + " TEXT, "
                + EDUCATION_MODULES_MATERIAL_SORT_WEIGHT + " INTEGER, "
                + EDUCATION_MODULES_MATERIAL_CREATED_AT + " TEXT, "
                + EDUCATION_MODULES_MATERIAL_NAME + " TEXT, "
                + EDUCATION_MODULES_MATERIAL_DESCRIPTION + " TEXT, "
                + EDUCATION_MODULES_MATERIAL_UPDATED_AT + " TEXT"
                + "UNIQUE(" + EDUCATION_MODULES_MATERIAL_ID + ") ON CONFLICT IGNORE)";
    }

    public static class TileMenuConstance {

        // Init Path to SQLite
        public static final String URI_PATH_TILE_MENU = "tile_table";
        public static final String URI_PATH2_TILE_MENU = "user/#/" + URI_PATH_TILE_MENU;

        // Create URI
        public static final Uri CONTENT_URI_TILE_MENU_TABLE = Uri.parse("content://" + MetaData.AUTHORITY_TILE_MENU + "/" + URI_PATH2_TILE_MENU);

        // Table Name
        public static final String TABLE_TILE_MENU = "tile_table";

        // Типы данных
        // Набор строк
        public static final String TILE_MENU_TABLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MetaData.AUTHORITY_TILE_MENU + "." + URI_PATH_TILE_MENU;
        // Одна строка
        public static final String TILE_MENU_TABLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + MetaData.AUTHORITY_TILE_MENU + "." + URI_PATH_TILE_MENU;

        // Variable
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

        // Create Table
        public static final String CREATE_TABLE_TILE_MENU = "CREATE TABLE IF NOT EXISTS"
                + TABLE_TILE_MENU + "("
                + TILE_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + TILE_WIDTH + " INTEGER, "
                + TILE_HEIGHT + " INTEGER, "
                + TILE_TITLE + " TEXT, "
                + TILE_STITLE + " TEXT, "
                + TILE_RES_ID + " INTEGER, "
                + TILE_BACKGROUND + " INTEGER, "
                + TILE_IS_ADD_ITEM + " INTEGER, "
                + TILE_IS_REDACTED + " INTEGER, "
                + TILE_IS_IMAGE + " INTEGER"
                + "UNIQUE(" + TILE_ID + ") ON CONFLICT IGNORE)";
    }
}
