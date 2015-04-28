package me.justup.upme.launcher;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import java.io.IOException;
import java.util.ArrayList;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGE;

/**
 * Takes care of setting initial wallpaper for a user, by selecting the
 * first wallpaper that is not in use by another user.
 */
public class UserInitializeReceiver extends BroadcastReceiver {
    private static final String TAG = UserInitializeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        final Resources resources = context.getResources();
        // Context.getPackageName() may return the "original" package name,
        // com.android.launcher2; Resources needs the real package name,
        // com.android.launcher. So we ask Resources for what it thinks the
        // package name should be.
        final String packageName = resources.getResourcePackageName(R.array.wallpapers);
        ArrayList<Integer> list = new ArrayList<Integer>();
        addWallpapers(resources, packageName, R.array.wallpapers, list);
        addWallpapers(resources, packageName, R.array.extra_wallpapers, list);
        WallpaperManager wpm = (WallpaperManager) context.getSystemService(
                Context.WALLPAPER_SERVICE);
        for (int i=1; i<list.size(); i++) {
            int resid = list.get(i);
            if (!wpm.hasResourceWallpaper(resid)) {
                try {
                    wpm.setResource(resid);
                } catch (IOException e) {
                    LOGE(TAG, e.getMessage());
                }
                return;
            }
        }
    }

    private void addWallpapers(Resources resources, String packageName, int resid,
            ArrayList<Integer> outList) {
        final String[] extras = resources.getStringArray(resid);
        for (String extra : extras) {
            int res = resources.getIdentifier(extra, "drawable", packageName);
            if (res != 0) {
                outList.add(res);
            }
        }
    }
}
