package me.justup.upme.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import static me.justup.upme.db.DBHelper.BASE_TABLE_NAME;
import static me.justup.upme.db.DBHelper.BASE_ID;
import static me.justup.upme.db.DBHelper.BASE_PROJECT_ID;
import static me.justup.upme.db.DBHelper.BASE_START_DATE;

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

}
