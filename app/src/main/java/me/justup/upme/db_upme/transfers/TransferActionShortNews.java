package me.justup.upme.db_upme.transfers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import me.justup.upme.api_rpc.response_object.NewsObject;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;
import me.justup.upme.entity.ArticlesGetShortDescriptionResponse;

import static me.justup.upme.db.DBHelper.SHORT_NEWS_POSTED_AT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_SERVER_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_SHORT_DESCR;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_THUMBNAIL;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_TITLE;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionShortNews implements SyncAdapterMetaData {
    private static final String TAG = TransferActionShortNews.class.getSimpleName();

    private Uri uriShortNews = null;
    private Uri uriOneShortNews = null;
    private Uri uriShortNewsList = null;

    public TransferActionShortNews(CharSequence name) {
        uriShortNews = Uri.parse("content://" + MetaData.AUTHORITY_SHORT_NEWS + "/" + ShortNewsConstance.URI_PATH2_SHORT_NEWS.replace("#", name));
        uriOneShortNews = Uri.parse("content://" + MetaData.AUTHORITY_SHORT_NEWS + "/" + ShortNewsConstance.URI_PATH2_SHORT_NEWS.replace("#", name) + "/" + 2);
        uriShortNewsList = Uri.parse("content://" + MetaData.AUTHORITY_SHORT_NEWS + "/" + ShortNewsConstance.URI_PATH2_SHORT_NEWS.replace("#", name) + "/" + 1);
    }

    public void insertShortNews(Context context, NewsObject newsObject) {
        Log.i(TAG, "NewsObject : " + newsObject.toString() + ", Insert Result : " + context.getContentResolver().insert(uriOneShortNews, newsToContentValues(newsObject)));
    }

    public void insertShortNewsOld(Context context, ArticlesGetShortDescriptionResponse.ResultList resultList) {
        Log.i(TAG, "ResultList : " + resultList.toString() + ", Insert Result : " + context.getContentResolver().insert(uriOneShortNews, newsToContentValues(resultList)));
    }

    public void insertShortNewsList(Context context, ArrayList<NewsObject> newsObjectArrayList) {
        Log.i(TAG, "ArrayList<NewsObject> : " + newsObjectArrayList.toString() + ", Insert Result : " + context.getContentResolver().bulkInsert(uriShortNewsList, newsListToContentValues(newsObjectArrayList)));
    }

    public Cursor getCursorOfShortNews(Context context){
        String sql = "SELECT * FROM " + ShortNewsConstance.TABLE_SHORT_NEWS;

        return context.getContentResolver().query(uriShortNews, null, sql, null, null);
    }

    /**
     * Convert NewsObject to ContentValues
     */
    public ContentValues newsToContentValues(@NonNull ArticlesGetShortDescriptionResponse.ResultList resultList) {
        LOGD(TAG, "ResultList : " + resultList.toString());

        ContentValues cv = new ContentValues();
        cv.put(SHORT_NEWS_SERVER_ID, resultList.id);
        cv.put(SHORT_NEWS_TITLE, resultList.title);
        cv.put(SHORT_NEWS_SHORT_DESCR, resultList.short_descr);
        cv.put(SHORT_NEWS_THUMBNAIL, resultList.thumbnail);
        cv.put(SHORT_NEWS_POSTED_AT, resultList.posted_at);

        LOGD(TAG, "Content Value : " + cv.toString());

        return cv;
    }

    /**
     * Convert NewsObject to ContentValues
     */
    public ContentValues newsToContentValues(@NonNull NewsObject newsObject) {
        LOGD(TAG, "NewsObject : " + newsObject.toString());

        ContentValues cv = new ContentValues();
        cv.put(SHORT_NEWS_SERVER_ID, newsObject.getId());
        cv.put(SHORT_NEWS_TITLE, newsObject.getTitle());
        cv.put(SHORT_NEWS_SHORT_DESCR, newsObject.getShort_descr());
        cv.put(SHORT_NEWS_THUMBNAIL, newsObject.getThumbnail());
        cv.put(SHORT_NEWS_POSTED_AT, newsObject.getPosted_at());

        LOGD(TAG, "Content Value : " + cv.toString());

        return cv;
    }

    /**
     * Convert NewsObject List to ContentValues
     */
    public ContentValues[] newsListToContentValues(@NonNull ArrayList<NewsObject> newsObjectArrayList) {

        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(newsObjectArrayList.size());

        for (int i = 0; i < newsObjectArrayList.size(); i++) {
            // Get One Object
            NewsObject newsObject = newsObjectArrayList.get(i);

            // Init ContentValues
            ContentValues cv = new ContentValues();

            cv.put(SHORT_NEWS_SERVER_ID, newsObject.getId());
            cv.put(SHORT_NEWS_TITLE, newsObject.getTitle());
            cv.put(SHORT_NEWS_SHORT_DESCR, newsObject.getShort_descr());
            cv.put(SHORT_NEWS_THUMBNAIL, newsObject.getThumbnail());
            cv.put(SHORT_NEWS_POSTED_AT, newsObject.getPosted_at());

            // Add to List
            contentValuesArrayList.add(cv);
        }


        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
