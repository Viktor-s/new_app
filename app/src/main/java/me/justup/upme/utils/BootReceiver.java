package me.justup.upme.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.justup.upme.BuildConfig;
import me.justup.upme.LoginActivity;

import static me.justup.upme.utils.LogUtils.LOGI;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent){

        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            LOGI(TAG, "Install Package Name : " + intent.getDataString());
        }

        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            LOGI(TAG, "Removed Package Name : " + intent.getDataString() + "");
        }

        if (intent.getAction().equalsIgnoreCase(Constance.BOOT_ACTION)) {
            LOGI(TAG, "BOOT_ACTION");

            if(BuildConfig.FLAVOR.equals(Constance.APP_FLAVOR_LAUNCHER)){
                Intent openLoginActivity = new Intent(context, LoginActivity.class);
                openLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openLoginActivity);
            }
        }
    }
}
