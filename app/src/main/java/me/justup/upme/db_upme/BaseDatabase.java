package me.justup.upme.db_upme;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import static me.justup.upme.utils.LogUtils.LOGE;

public class BaseDatabase implements BaseColumns {
    private static final String TAG = BaseDatabase.class.getSimpleName();

    public SQLiteDatabase db = null;
    private DatabaseHelper dbHelper = null;

    public BaseDatabase(Context context, String tableName, String paramsSqlCreate) {
        dbHelper = new DatabaseHelper(context, tableName, paramsSqlCreate);
    }

    public BaseDatabase open() throws SQLException {
        db = dbHelper.getWritableDatabase();

        return this;
    }

    public void exec(String query) {
        db.execSQL(query);
    }

    public void close() {
        try {
            dbHelper.close();
        } catch (Exception e) {
            LOGE(TAG, e.getMessage());
        }
    }
}
