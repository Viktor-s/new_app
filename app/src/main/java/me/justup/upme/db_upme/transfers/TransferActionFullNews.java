package me.justup.upme.db_upme.transfers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import me.justup.upme.api_rpc.response_object.NewsObject;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;
import me.justup.upme.entity.ArticleFullResponse;

import static me.justup.upme.db.DBHelper.FULL_NEWS_FULL_DESCR;
import static me.justup.upme.db.DBHelper.FULL_NEWS_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionFullNews implements SyncAdapterMetaData {
    private static final String TAG = TransferActionShortNews.class.getSimpleName();

    private Uri uriFullNews = null;
    private Uri uriOneFullNews = null;
    private Uri uriFullNewsList = null;

    public TransferActionFullNews(CharSequence name) {
        uriFullNews = Uri.parse("content://" + MetaData.AUTHORITY_FULL_NEWS + "/" + FullNewsConstance.URI_PATH2_FULL_NEWS_TABLE.replace("#", name));
        uriOneFullNews = Uri.parse("content://" + MetaData.AUTHORITY_FULL_NEWS + "/" + FullNewsConstance.URI_PATH2_FULL_NEWS_TABLE.replace("#", name) + "/" + 2);
        uriFullNewsList = Uri.parse("content://" + MetaData.AUTHORITY_FULL_NEWS + "/" + FullNewsConstance.URI_PATH2_FULL_NEWS_TABLE.replace("#", name) + "/" + 1);
    }

    public void insertFullNews(Activity activity, NewsObject newsObject) {
        activity.getContentResolver().insert(uriOneFullNews, newsToContentValues(newsObject));
    }

    public void insertFullNewsOld(Context context, ArticleFullResponse newsObject) {
        context.getContentResolver().insert(uriOneFullNews, newsToContentValues(newsObject));
    }

    public Cursor getCursorOfFullNewsByServerId(Context context, int serverId){
        String sql = "SELECT * FROM " + FullNewsConstance.TABLE_NAME_FULL_NEWS + " WHERE server_id = " + serverId;

        return context.getContentResolver().query(uriFullNews, null, sql, null, null);
    }

    /**
     * Convert NewsObject to ContentValues
     */
    public ContentValues newsToContentValues(ArticleFullResponse newsObject) {
        ContentValues cv = new ContentValues();
        cv.put(FULL_NEWS_SERVER_ID, newsObject.result.id);
        cv.put(FULL_NEWS_FULL_DESCR, newsObject.result.full_descr);

        LOGD(TAG, "Content Value : " + cv.toString());

        return cv;
    }

    /**
     * Convert NewsObject to ContentValues
     */
    public ContentValues newsToContentValues(NewsObject newsObject) {
        ContentValues cv = new ContentValues();
        cv.put(FULL_NEWS_SERVER_ID, newsObject.getId());
        cv.put(FULL_NEWS_FULL_DESCR, newsObject.getFull_descr());

        LOGD(TAG, "Content Value : " + cv.toString());

        return cv;
    }
}
