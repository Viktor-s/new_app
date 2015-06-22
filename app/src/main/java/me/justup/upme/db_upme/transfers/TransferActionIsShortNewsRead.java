package me.justup.upme.db_upme.transfers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;

import static me.justup.upme.db.DBHelper.IS_SHORT_NEWS_READ_ARTICLE_ID;
import static me.justup.upme.db.DBHelper.IS_SHORT_NEWS_READ_VALUE;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionIsShortNewsRead implements SyncAdapterMetaData {
    private static final String TAG = TransferActionNewsComments.class.getSimpleName();

    private Uri uriIsShortNewsRead = null;
    private Uri uriIsOneShortNewsRead = null;
    private Uri uriIsShortNewsReadList = null;

    public TransferActionIsShortNewsRead(CharSequence name) {
        uriIsShortNewsRead = Uri.parse("content://" + MetaData.AUTHORITY_IS_SHORT_NEWS_READ + "/" + IsShortNewsReadConstance.URI_PATH2_IS_SHORT_NEWS_READ_TABLE.replace("#", name));
        uriIsOneShortNewsRead = Uri.parse("content://" + MetaData.AUTHORITY_IS_SHORT_NEWS_READ + "/" + IsShortNewsReadConstance.URI_PATH2_IS_SHORT_NEWS_READ_TABLE.replace("#", name) + "/" + 2);
        uriIsShortNewsReadList = Uri.parse("content://" + MetaData.AUTHORITY_IS_SHORT_NEWS_READ + "/" + IsShortNewsReadConstance.URI_PATH2_IS_SHORT_NEWS_READ_TABLE.replace("#", name) + "/" + 1);
    }

    public void insertNewsReadValue(Activity activity, int articleId) {
        activity.getContentResolver().insert(uriIsOneShortNewsRead, toContentValues(articleId));
    }

    public Cursor getCursorOfIsShortNewsRead(Context context){
        String sql = "SELECT * FROM " + IsShortNewsReadConstance.TABLE_NAME_IS_SHORT_NEWS_READ;

        return context.getContentResolver().query(uriIsShortNewsRead, null, sql, null, null);
    }

    /**
     * Convert articleId to ContentValues
     */
    public ContentValues toContentValues(int articleId) {
        ContentValues cv = new ContentValues();
        cv.put(IS_SHORT_NEWS_READ_ARTICLE_ID, articleId);
        cv.put(IS_SHORT_NEWS_READ_VALUE, 1);

        LOGD(TAG, "Content Value : " + cv.toString());

        return cv;
    }
}
