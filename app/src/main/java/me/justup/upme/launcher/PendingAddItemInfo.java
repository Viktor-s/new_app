package me.justup.upme.launcher;

import android.content.ComponentName;

/**
 * We pass this object with a drag from the customization tray
 */
public class PendingAddItemInfo extends ItemInfo {
    /**
     * The component that will be created.
     */
    public ComponentName componentName = null;
}

