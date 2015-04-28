package me.justup.upme.launcher;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;

public class ApplicationInfo extends ItemInfo {
    private static final String TAG = ApplicationInfo.class.getSimpleName();

    public Intent intent = null;
    public Bitmap iconBitmap = null;
    public long firstInstallTime;
    public ComponentName componentName = null;

    public static final int DOWNLOADED_FLAG = 1;
    public static final int UPDATED_SYSTEM_APP_FLAG = 2;

    public int flags = 0;

    public ApplicationInfo() {
        itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT;
    }

    /**
     * Must not hold the Context.
     */
    public ApplicationInfo(PackageManager pm, ResolveInfo info, IconCache iconCache, HashMap<Object, CharSequence> labelCache) {
        final String packageName = info.activityInfo.applicationInfo.packageName;

        this.componentName = new ComponentName(packageName, info.activityInfo.name);
        this.container = ItemInfo.NO_ID;
        this.setActivity(componentName, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        try {
            int appFlags = pm.getApplicationInfo(packageName, 0).flags;
            if ((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
                flags |= DOWNLOADED_FLAG;

                if ((appFlags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    flags |= UPDATED_SYSTEM_APP_FLAG;
                }
            }
            firstInstallTime = pm.getPackageInfo(packageName, 0).firstInstallTime;
        } catch (NameNotFoundException e) {
            LOGE(TAG, "PackageManager.getApplicationInfo failed for " + packageName);
        }

        iconCache.getTitleAndIcon(this, info, labelCache);
    }

    public ApplicationInfo(ApplicationInfo info) {
        super(info);
        componentName = info.componentName;
        title = info.title.toString();
        intent = new Intent(info.intent);
        flags = info.flags;
        firstInstallTime = info.firstInstallTime;
    }

    /**
     * Returns the package name that the shortcut's intent will resolve to, or
     * an empty string if none exists.
     */
    public String getPackageName() {
        return super.getPackageName(intent);
    }

    /**
     * 建立app的intent. Sets {@link #itemType} to
     * {@link LauncherSettings.BaseLauncherColumns#ITEM_TYPE_APPLICATION}.
     *
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    public final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
        itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION;
    }

    @Override
    public String toString() {
        return "ApplicationInfo(title=" + title.toString() + ")";
    }

    public static void dumpApplicationInfoList(String tag, String label,
                                               ArrayList<ApplicationInfo> list) {
        LOGD(tag, label + " size=" + list.size());
        for (ApplicationInfo info : list) {
            LOGD(tag, "   title=\"" + info.title + "\" iconBitmap="
                    + info.iconBitmap + " firstInstallTime="
                    + info.firstInstallTime);
        }
    }

    public ShortcutInfo makeShortcut() {
        return new ShortcutInfo(this);
    }
}
