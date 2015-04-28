package me.justup.upme.launcher;

import android.content.pm.ActivityInfo;

public class PendingAddShortcutInfo extends PendingAddItemInfo {

    ActivityInfo shortcutActivityInfo = null;

    public PendingAddShortcutInfo(ActivityInfo activityInfo) {
        shortcutActivityInfo = activityInfo;
    }

    @Override
    public String toString() {
        return "Shortcut: " + shortcutActivityInfo.packageName;
    }
}
