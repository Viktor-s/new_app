package me.justup.upme.db_upme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGI;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private String mParamsSqlCreate = "CREATE TABLE IF NOT EXISTS %TABLE% (%SQL_CREATE_PARAMS%);";
    private String mTableName = null;

    public DatabaseHelper(Context context, String tableName, String paramsSqlCreate) {
        super(context, MetaData.getDatabaseName(context), null, MetaData.DB_VERSION);

        LOGD(TAG, "DB Name : " + MetaData.getDatabaseName(context) + "\nTable Name : " + tableName + "\nParam sql : " + paramsSqlCreate);

        this.mTableName = tableName;
        this.mParamsSqlCreate = paramsSqlCreate;
    }

    public DatabaseHelper(Context context, String dbName, String tableName, String paramsSqlCreate) {
        super(context, dbName, null, MetaData.DB_VERSION);

        LOGD(TAG, "DB Name : " + dbName + "\nTable Name : " + tableName + "\nParam sql : " + paramsSqlCreate);

        this.mTableName = tableName;
        this.mParamsSqlCreate = paramsSqlCreate;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL(mParamsSqlCreate);
        super.onOpen(db);

        LOGD(TAG, "DB Open : " + mParamsSqlCreate);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LOGD(TAG, "Try create db : " + mParamsSqlCreate);

        db.execSQL(mParamsSqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGI(TAG, "Upgrading database from version '" + oldVersion + "' , to '" + newVersion + "', which will destroy all old data.");

        db.execSQL("DROP TABLE IF EXISTS " + mTableName);
        LOGI(TAG, "Upgrade database, table name is " + mTableName);

        onCreate(db);
    }

}
