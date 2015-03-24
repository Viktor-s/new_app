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
            componentInfo.getPackageName();

            if (!componentInfo.getPackageName().equals(APP_PACKAGE)) {
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
