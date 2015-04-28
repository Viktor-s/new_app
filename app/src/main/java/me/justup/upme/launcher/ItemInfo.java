package me.justup.upme.launcher;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static me.justup.upme.utils.LogUtils.LOGE;

public class ItemInfo {
    private static final String TAG = ItemInfo.class.getSimpleName();

    public static final int NO_ID = -1;

    /**
     * The id in the settings database for this item
     */
    public long id = NO_ID;

    public int itemType;

    public long container = NO_ID;

    public int screen = -1;

    public int cellX = -1;

    public int cellY = -1;

    public int spanX = 1;

    public int spanY = 1;

    public int minSpanX = 1;

    public int minSpanY = 1;

    boolean requiresDbUpdate = false;

    public CharSequence title;

    public int[] dropPos = null;

    public ItemInfo() { }

    public ItemInfo(ItemInfo info) {
        id = info.id;
        cellX = info.cellX;
        cellY = info.cellY;
        spanX = info.spanX;
        spanY = info.spanY;
        screen = info.screen;
        itemType = info.itemType;
        container = info.container;

        LauncherModel.checkItemInfo(this);
    }

    static String getPackageName(Intent intent) {
        if (intent != null) {
            String packageName = intent.getPackage();
            if (packageName == null && intent.getComponent() != null) {
                packageName = intent.getComponent().getPackageName();
            }
            if (packageName != null) {
                return packageName;
            }
        }

        return "";
    }

    void onAddToDatabase(ContentValues values) {
        values.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, itemType);
        values.put(LauncherSettings.Favorites.CONTAINER, container);
        values.put(LauncherSettings.Favorites.SCREEN, screen);
        values.put(LauncherSettings.Favorites.CELLX, cellX);
        values.put(LauncherSettings.Favorites.CELLY, cellY);
        values.put(LauncherSettings.Favorites.SPANX, spanX);
        values.put(LauncherSettings.Favorites.SPANY, spanY);
    }

    void updateValuesWithCoordinates(ContentValues values, int cellX, int cellY) {
        values.put(LauncherSettings.Favorites.CELLX, cellX);
        values.put(LauncherSettings.Favorites.CELLY, cellY);
    }

    static byte[] flattenBitmap(Bitmap bitmap) {
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);

        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            return out.toByteArray();
        } catch (IOException e) {
            LOGE(TAG, "Could not write icon : " + e.getMessage());

            return null;
        }
    }

    static void writeBitmap(ContentValues values, Bitmap bitmap) {
        if (bitmap != null) {
            byte[] data = flattenBitmap(bitmap);
            values.put(LauncherSettings.Favorites.ICON, data);
        }
    }

    /**
     * It is very important that sub-classes implement this if they contain any
     * references to the activity (anything in the view hierarchy etc.). If not,
     * leaks can result since ItemInfo objects persist across rotation and can
     * hence leak by holding stale references to the old view hierarchy /
     * activity.
     */
    void unbind() { }

    @Override
    public String toString() {
        return "ItemInfo{" +
                "id=" + id +
                ", itemType=" + itemType +
                ", container=" + container +
                ", screen=" + screen +
                ", cellX=" + cellX +
                ", cellY=" + cellY +
                ", spanX=" + spanX +
                ", spanY=" + spanY +
                ", minSpanX=" + minSpanX +
                ", minSpanY=" + minSpanY +
                ", requiresDbUpdate=" + requiresDbUpdate +
                ", title=" + title +
                ", dropPos=" + Arrays.toString(dropPos) +
                '}';
    }
}
