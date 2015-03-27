package me.justup.upme.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.justup.upme.LoginActivity;

import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class ApplicationSupervisorService extends Service {
    private static final String TAG = makeLogTag(ApplicationSupervisorService.class);

    private static final String APP_PACKAGE = "me.justup.upme";
    private static final String APP_DOC_PACKAGE = "com.android.documentsui";
    private static final String APP_CAMERA_PACKAGE = "com.android.camera";

    private static final long TIMER_INTERVAL = 10000; // 10 sec
    private Timer mTimer = null;


    @Override
    public void onCreate() {
        super.onCreate();
        LOGI(TAG, "Start ApplicationSupervisorService");

        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new timerJob(), 0, TIMER_INTERVAL);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LOGI(TAG, "Destroy ApplicationSupervisorService");

        mTimer.cancel();
        mTimer = null;
    }

    private class timerJob extends TimerTask {
        @Override
        public void run() {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            @SuppressWarnings("deprecation")
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

            ComponentName componentInfo = taskInfo.get(0).topActivity;
            String appPackage = componentInfo.getPackageName();

            if (!appPackage.equals(APP_PACKAGE) && !appPackage.equals(APP_DOC_PACKAGE) && !appPackage.equals(APP_CAMERA_PACKAGE)) {
                LOGI(TAG, "ping! ... start Application");

                Intent i = new Intent(ApplicationSupervisorService.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
