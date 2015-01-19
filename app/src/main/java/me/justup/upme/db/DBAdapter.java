package me.justup.upme.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.R;
import me.justup.upme.entity.ContactEntity;
import me.justup.upme.entity.NewsCommentEntity;
import me.justup.upme.entity.NewsFeedEntity;
import me.justup.upme.entity.UserEntity;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.db.DBHelper.BASE_ID;
import static me.justup.upme.db.DBHelper.BASE_PROJECT_ID;
import static me.justup.upme.db.DBHelper.BASE_START_DATE;
import static me.justup.upme.db.DBHelper.BASE_TABLE_NAME;

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
    private String[] allColumns = {BASE_ID, BASE_PROJECT_ID, BASE_START_DATE};


    public DBAdapter(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public String getExampleObject() {
        String mObject = "Object";

        return mObject;
    }

    public long saveTimer(int projId, long startDate) {
        ContentValues values = new ContentValues();
        values.put(BASE_PROJECT_ID, projId);
        values.put(BASE_START_DATE, startDate);

        long insertId = database.insert(BASE_TABLE_NAME, null, values);

        return insertId;
    }

    public long openTimer(int projId) {
        long saveDate = 0;

        Cursor cursor = database.query(BASE_TABLE_NAME, allColumns, BASE_PROJECT_ID + " = " + projId, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            saveDate = cursor.getLong(cursor.getColumnIndex(BASE_START_DATE));
        }

        cursor.close();
        return saveDate;
    }

    public void deleteTimer(int projId) {
        database.delete(BASE_TABLE_NAME, BASE_PROJECT_ID + " = " + projId, null);
    }

    public List<NewsFeedEntity> getNewsModelsTestlist() {
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
