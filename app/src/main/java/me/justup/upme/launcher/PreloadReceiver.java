package me.justup.upme.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import me.justup.upme.JustUpApplication;

import static me.justup.upme.utils.LogUtils.LOGD;

public class PreloadReceiver extends BroadcastReceiver {
    private static final String TAG = PreloadReceiver.class.getSimpleName();

    public static final String EXTRA_WORKSPACE_NAME = "com.android.launcher.action.EXTRA_WORKSPACE_NAME";

    @Override
    public void onReceive(Context context, Intent intent) {
        final JustUpApplication app = (JustUpApplication) context.getApplicationContext();
        final LauncherProvider provider = app.getLauncherProvider();
        if (provider != null) {
            String name = intent.getStringExtra(EXTRA_WORKSPACE_NAME);
            final int workspaceResId = !TextUtils.isEmpty(name)
                    ? context.getResources().getIdentifier(name, "xml", "com.android.launcher") : 0;

            LOGD(TAG, "Workspace name: " + name + " id: " + workspaceResId);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    provider.loadDefaultFavoritesIfNecessary(workspaceResId);
                }
            }).start();
        }
    }
}
