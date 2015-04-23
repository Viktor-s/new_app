package me.justup.upme;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import me.justup.upme.utils.LogUtils;


public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LogUtils.DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        super.onCreate(savedInstanceState);
    }

    public void hideNavBar() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void removeCurrentFragment(int layout){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment currentFrag =  getFragmentManager().findFragmentById(layout);

        String fragName = "NONE";

        if (currentFrag!=null) {
            fragName = currentFrag.getClass().getSimpleName();
        }

        if (currentFrag != null) {
            transaction.remove(currentFrag);
        }

        transaction.commit();
    }

    public void replaceFragment (Fragment fragment, int layout){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ // fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(layout, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

}
