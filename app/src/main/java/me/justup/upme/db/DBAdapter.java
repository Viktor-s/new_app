package me.justup.upme.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.R;
import me.justup.upme.entity.ArticlesGetShortDescriptionResponse;
import me.justup.upme.entity.ContactEntity;
import me.justup.upme.entity.NewsCommentEntity;
import me.justup.upme.entity.NewsFeedEntity;
import me.justup.upme.entity.UserEntity;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.db.DBHelper.*;

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
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    private String[] BASE_TABLE_COLUMNS = {BASE_ID, BASE_PROJECT_ID, BASE_START_DATE};
    private String[] SHORT_NEWS_TABLE_COLUMNS = {SHORT_NEWS_ID, SHORT_NEWS_SERVER_ID, SHORT_NEWS_TITLE, SHORT_NEWS_SHORT_DESCR, SHORT_NEWS_THUMBNAIL, SHORT_NEWS_POSTED_AT};


    public DBAdapter(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void saveShortNews(ArticlesGetShortDescriptionResponse entity) {

        // maybe needed foreach

        ContentValues values = new ContentValues();
        values.put(SHORT_NEWS_SERVER_ID, entity.result.testId);
        values.put(SHORT_NEWS_TITLE, entity.result.test);
        values.put(SHORT_NEWS_SHORT_DESCR, entity.result.test);
        values.put(SHORT_NEWS_THUMBNAIL, entity.result.test);
        values.put(SHORT_NEWS_POSTED_AT, entity.result.test);

        database.insert(SHORT_NEWS_TABLE_NAME, null, values);
    }

    // load example
    /*
        or:
        String selectQuery = "SELECT * FROM " + BASE_TABLE_NAME;
        Cursor cursor = database.rawQuery(selectQuery, null);

        td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
        td.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
        td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
     */
    public long openTimer(int projId) {
        long saveDate = 0;

        Cursor cursor = database.query(BASE_TABLE_NAME, BASE_TABLE_COLUMNS, BASE_PROJECT_ID + " = " + projId, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            saveDate = cursor.getLong(cursor.getColumnIndex(BASE_START_DATE));
        }

        cursor.close();
        return saveDate;
    }

    // delete example
    public void deleteTimer(int projId) {
        database.delete(BASE_TABLE_NAME, BASE_PROJECT_ID + " = " + projId, null);
    }

    public List<NewsFeedEntity> getNewsModelsTestList() {
        List<NewsFeedEntity> mNewsFeedEntityList = new ArrayList<>();
        List<NewsCommentEntity> mNewsCommentEntityList = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            NewsCommentEntity newsCommentEntity = new NewsCommentEntity();
            newsCommentEntity.setCommentTitle("MR. ANDROID 11:00 20 ЯНВАРЯ 2015");
            newsCommentEntity.setCommentText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, ut labore et");
            newsCommentEntity.setCommentImage(AppContext.getAppContext().getResources().getDrawable(R.drawable.ic_launcher));
            mNewsCommentEntityList.add(newsCommentEntity);
        }
        for (int i = 0; i < 10; i++) {
            NewsFeedEntity newsFeedEntity = new NewsFeedEntity();
            newsFeedEntity.setNewsDate("01:30 03 СЕНТЯБРЯ 2014");
            newsFeedEntity.setNewsTitle("ИЗ ПОДМАСТЕРЬЕВ В МИЛЛИАРДЕРЫ" + " " + i);
            newsFeedEntity.setNewsText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore.");
            newsFeedEntity.setNewsImage(AppContext.getAppContext().getResources().getDrawable(R.drawable.news_image_test));
            newsFeedEntity.setNewsCommentEntityList(mNewsCommentEntityList);
            mNewsFeedEntityList.add(newsFeedEntity);
        }
        return mNewsFeedEntityList;
    }

    public List<NewsFeedEntity> getNextPackOfNewsModelsTestList() {
        List<NewsFeedEntity> mNewsFeedEntityList = new ArrayList<>();
        List<NewsCommentEntity> mNewsCommentEntityList = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            NewsCommentEntity newsCommentEntity = new NewsCommentEntity();
            newsCommentEntity.setCommentTitle("MR. ANDROID 11:00 20 ЯНВАРЯ 2015");
            newsCommentEntity.setCommentText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, ut labore et");
            newsCommentEntity.setCommentImage(AppContext.getAppContext().getResources().getDrawable(R.drawable.ic_launcher));
            mNewsCommentEntityList.add(newsCommentEntity);
        }
        for (int i = 0; i < 10; i++) {
            NewsFeedEntity newsFeedEntity = new NewsFeedEntity();
            newsFeedEntity.setNewsDate("01:30 03 СЕНТЯБРЯ 2014");
            newsFeedEntity.setNewsTitle("ИЗ ПОДМАСТЕРЬЕВ В МИЛЛИАРДЕРЫ" + " " + i);
            newsFeedEntity.setNewsText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore.");
            newsFeedEntity.setNewsImage(AppContext.getAppContext().getResources().getDrawable(R.drawable.news_image_test));
            newsFeedEntity.setNewsCommentEntityList(mNewsCommentEntityList);
            mNewsFeedEntityList.add(newsFeedEntity);
        }
        return mNewsFeedEntityList;
    }


    public UserEntity getUserEntity() {
        UserEntity mUserEntity = new UserEntity();
        List<ContactEntity> mContactEntitiesList = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            ContactEntity mContactEntity = new ContactEntity();
            mContactEntity.setmContactName("MR. ANDROID " + i);
            mContactEntity.setmContactImage(AppContext.getAppContext().getResources().getDrawable(R.drawable.ic_launcher));
            mContactEntitiesList.add(mContactEntity);
        }
        mUserEntity.setmContactEntityList(mContactEntitiesList);
        return mUserEntity;
    }


}
