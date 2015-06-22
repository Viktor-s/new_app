package me.justup.upme.db_upme;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.text.TextUtils;

public class MetaData {
    private static final String TAG = MetaData.class.getSimpleName();

    public static final String DATABASE_NAME = "upme.db";
    public static final Integer DB_VERSION = 1;

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    // Base Table
    public static final String AUTHORITY_BASE = "me.justup.upme.provider.base";
    // Event Calendar Table
    public static final String AUTHORITY_EVENT_CALENDAR = "me.justup.upme.provider.event.calendar";
    // Full News Table
    public static final String AUTHORITY_FULL_NEWS = "me.justup.upme.provider.full.news";
    // Is short news read Table
    public static final String AUTHORITY_IS_SHORT_NEWS_READ = "me.justup.upme.provider.is.short.news.read";
    // Mail Contact Table
    public static final String AUTHORITY_MAIL_CONTACT = "me.justup.upme.provider.mail.contact";
    // Product Brand Table
    public static final String AUTHORITY_PRODUCT_BRAND = "me.justup.upme.provider.product.brand";
    // Product Categories Table
    public static final String AUTHORITY_PRODUCT_CATEGORIES = "me.justup.upme.provider.product.categories";
    // Product HTML Table
    public static final String AUTHORITY_PRODUCT_HTML = "me.justup.upme.provider.product.html";
    // Products Product Table
    public static final String AUTHORITY_PRODUCTS_PRODUCT = "me.justup.upme.provider.products.product";
    // Short news comments Table
    public static final String AUTHORITY_SHORT_NEWS_COMMENTS = "me.justup.upme.provider.short.news.comments";
    // Short news Table
    public static final String AUTHORITY_SHORT_NEWS = "me.justup.upme.provider.short.news";
    // SQLite Sequence Table
    public static final String AUTHORITY_TILE_MENU = "me.justup.upme.provider.tile.menu";
    // Status bar push Table
    public static final String AUTHORITY_STATUS_BAR_PUSH = "me.justup.upme.provider.status.bar.push";
    // Education Product Table
    public static final String AUTHORITY_EDUCATION_PRODUCT = "me.justup.upme.provider.education.product";
    // Education Product Module Table
    public static final String AUTHORITY_EDUCATION_PRODUCT_MODULE = "me.justup.upme.provider.education.product.module";
    // Education Product Module Material Table
    public static final String AUTHORITY_EDUCATION_MODULE_MATERIAL = "me.justup.upme.provider.education.module.material";

    public static String getDatabaseName(Context context) {
        // Get masm user account
        Account[] accounts = AccountManager.get(context).getAccountsByType(Constants.ACCOUNT_TYPE);
        // Get first reg account
        Account account = accounts.length > 0 ? accounts[0] : null;

        if (account == null) {
            return null;
        }

        return account.name.concat(".").concat(DATABASE_NAME); // Example 0637759115.upme.db
    }

    public static String getDatabaseName(String accountName) {
        if (accountName == null) {
            return null;
        }

        return TextUtils.isEmpty(accountName) ? DATABASE_NAME : accountName.concat(".").concat(DATABASE_NAME);
    }
}
