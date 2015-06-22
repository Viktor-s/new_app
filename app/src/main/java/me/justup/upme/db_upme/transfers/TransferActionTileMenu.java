package me.justup.upme.db_upme.transfers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.justup.upme.db.DBHelper;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;
import me.justup.upme.view.dashboard.TileItem;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;

public class TransferActionTileMenu implements SyncAdapterMetaData {
    private static final String TAG = TransferActionTileMenu.class.getSimpleName();

    private Uri uriTileMenu = null;
    private Uri uriOneTile = null;
    private Uri uriListTiles = null;

    public TransferActionTileMenu(CharSequence name) {
        uriTileMenu = Uri.parse("content://" + MetaData.AUTHORITY_TILE_MENU + "/" + TileMenuConstance.URI_PATH2_TILE_MENU.replace("#", name));
        uriOneTile = Uri.parse("content://" + MetaData.AUTHORITY_TILE_MENU + "/" + TileMenuConstance.URI_PATH2_TILE_MENU.replace("#", name) + "/" + 2);
        uriListTiles = Uri.parse("content://" + MetaData.AUTHORITY_TILE_MENU + "/" + TileMenuConstance.URI_PATH2_TILE_MENU.replace("#", name) + "/" + 1);
    }

    public void insertTile(Context context, TileItem tileItem, Integer itemId) {
        ContentValues contentValues = tileItemToContentValues(tileItem, itemId);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneTile, contentValues);
        }
    }

    public void insertTilesList(Context context, List<TileItem> tileItemArrayList) {
        ContentValues[] contentValues = tileItemsListToContentValues(tileItemArrayList);

        if(contentValues!=null) {
            context.getContentResolver().bulkInsert(uriListTiles, contentValues);
        }
    }

    public ArrayList<TileItem> getListTileItems(Context context) {
        ArrayList<TileItem> tileItems = new ArrayList<>();

        // Show Column
        String[] columns = new String[]{
                DBHelper.TILE_WIDTH,
                DBHelper.TILE_HEIGHT,
                DBHelper.TILE_TITLE,
                DBHelper.TILE_STITLE,
                DBHelper.TILE_RES_ID,
                DBHelper.TILE_BACKGROUND,
                DBHelper.TILE_IS_ADD_ITEM,
                DBHelper.TILE_IS_REDACTED,
                DBHelper.TILE_IS_IMAGE };

        // Get Cursor
        Cursor cursor;
        try{
            cursor = context.getContentResolver().query(uriListTiles, columns, null, null, "1", null);
        }catch (NullPointerException e){
            return null;
        }

        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                try {
                    int width = cursor.getInt(cursor.getColumnIndex(DBHelper.TILE_WIDTH));
                    int height = cursor.getInt(cursor.getColumnIndex(DBHelper.TILE_HEIGHT));

                    String title = cursor.getString(cursor.getColumnIndex(DBHelper.TILE_TITLE));
                    String secondTitle = cursor.getString(cursor.getColumnIndex(DBHelper.TILE_STITLE));
                    int resId = cursor.getInt(cursor.getColumnIndex(DBHelper.TILE_RES_ID));
                    int background = cursor.getInt(cursor.getColumnIndex(DBHelper.TILE_BACKGROUND));

                    boolean isAddItemButton = cursor.getInt(cursor.getColumnIndex(DBHelper.TILE_IS_ADD_ITEM)) == 0; // + button
                    boolean isRedacted = cursor.getInt(cursor.getColumnIndex(DBHelper.TILE_IS_REDACTED)) == 0; // Redacted now
                    boolean isImage = cursor.getInt(cursor.getColumnIndex(DBHelper.TILE_IS_IMAGE)) == 0;

                    tileItems.add(new TileItem(height, width, title, secondTitle, resId, background, isAddItemButton, isRedacted, isImage));

                } catch (Exception e) {
                    LOGE(TAG, e.getMessage());

                    return null;
                }

                cursor.moveToNext();
            }
        }

        cursor.close();

        return tileItems;
    }

    /**
     * Convert TileItem to ContentValues
     */
    public ContentValues tileItemToContentValues(@NonNull TileItem tileItem, Integer tileId) {
        ContentValues cv = new ContentValues();

        if(tileId!=null) {
            cv.put(DBHelper.TILE_ID, tileId);
        }

        cv.put(DBHelper.TILE_WIDTH, tileItem.getWSpans());
        cv.put(DBHelper.TILE_HEIGHT, tileItem.getHSpans());
        cv.put(DBHelper.TILE_TITLE, tileItem.getTitle());
        cv.put(DBHelper.TILE_STITLE, tileItem.getSecondTitle());
        cv.put(DBHelper.TILE_RES_ID, tileItem.getResId());
        cv.put(DBHelper.TILE_BACKGROUND, tileItem.getBackground());
        cv.put(DBHelper.TILE_IS_ADD_ITEM, tileItem.isAddItemButton() ? 0 : 1);
        cv.put(DBHelper.TILE_IS_REDACTED, tileItem.isRedacted() ? 0 : 1);
        cv.put(DBHelper.TILE_IS_IMAGE, tileItem.isImage() ? 0 : 1);

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ArrayList<TileItem> to ContentValues[]
     */
    public ContentValues[] tileItemsListToContentValues(@NonNull List<TileItem> tileItemsArrayList) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(tileItemsArrayList.size());

        for (int i = 0; i < tileItemsArrayList.size(); i++) {
            // Get One Object
            ContentValues cv = tileItemToContentValues(tileItemsArrayList.get(i), i);

            // Add to List
            contentValuesArrayList.add(cv);
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
